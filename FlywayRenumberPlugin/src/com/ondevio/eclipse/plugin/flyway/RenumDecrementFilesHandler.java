package com.ondevio.eclipse.plugin.flyway;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RenumDecrementFilesHandler extends AbstractHandler implements IStartup {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            List<IFile> files = new ArrayList<>();
            
            for (Object element : structuredSelection.toList()) {
                if (element instanceof IFile) {
                    IFile file = (IFile) element;
                    if (file.getName().matches("^V[0-9]{3}__.*")) {
                        files.add(file);
                    }
                }
            }
            // Call a method to renumber the files (implement renumbering logic here)
            List<IFile> newFiles = renumberFiles(files, false);
            // Re-select the files after renaming them
            IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
            reselectFiles(window, newFiles);
        }
        return null;
    }
    
    private void reselectFiles(IWorkbenchWindow window, List<IFile> files) {
        // Create a new selection with the updated files
        StructuredSelection newSelection = new StructuredSelection(files);

        // Get the active part's selection provider
        if (window.getActivePage() != null && window.getActivePage().getActivePart() != null) {
            window.getActivePage().getActivePart().getSite().getSelectionProvider().setSelection(newSelection);
        }
    }
    
    public List<IFile> renumberFiles(List<IFile> files, boolean increment) {
    	 List<IFile> newFiles = new ArrayList<>();
        // Sort the files by their number (ascending)
        files.sort(Comparator.comparingInt(this::extractNumber));

        // If increment is false, reverse the order for decrementing
        if (!increment) {
            Collections.reverse(files);
        }

        // Monitor for progress, you can use NullProgressMonitor for simplicity
        IProgressMonitor monitor = new NullProgressMonitor();

        // Iterate through the files and rename them
        for (int i = 0; i < files.size(); i++) {
            IFile file = files.get(i);
            int currentNumber = extractNumber(file);
            int newNumber = increment ? currentNumber + 1 : Math.max(currentNumber - 1, 0);
            IFile newFile = renameFile(file, newNumber, monitor);
            if (newFile != null) {
            	newFiles.add(newFile);
            }
        }
        return newFiles;
    }

    // Helper method to extract the number from the file name
    private int extractNumber(IFile file) {
        String fileName = file.getName();
        try {
            // Extract the number (assumes file name format like V001__ or V002__)
            String numberPart = fileName.substring(1, 4);
            return Integer.parseInt(numberPart);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            // Handle any issues with file naming here
            e.printStackTrace();
            return -1; // Return -1 if parsing fails
        }
    }

    // Helper method to rename the file with the new number
    private IFile renameFile(IFile file, int newNumber, IProgressMonitor monitor) {
        String newFileName = String.format("V%03d__%s", newNumber, getMigrationDescription(file.getName()));
        IContainer parent = file.getParent();
        IPath newFilePath = parent.getFullPath().append(newFileName);

        // Check if a file with the new name already exists
        if (parent.getFile(new Path(newFileName)).exists()) {
            System.out.println("Target file already exists: " + newFileName);
            // You could skip the renaming, raise an exception, or handle it in another way
            return null;
        }
        try {
			file.move(newFilePath, true, monitor);
		    // Return the new IFile reference pointing to the new location
		    return parent.getFile(new Path(newFileName));
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Extracts the migration description part of the file name after the number
    private String getMigrationDescription(String fileName) {
        // Assumes format V001__description.sql
        int doubleUnderscoreIndex = fileName.indexOf("__");
        if (doubleUnderscoreIndex != -1) {
            return fileName.substring(doubleUnderscoreIndex + 2);
        }
        return "unknown.sql"; // Fallback if the format is not as expected
    }
    
    @Override
    public void earlyStartup() {
        // This method ensures the plugin is activated at startup
        System.out.println("FlywayRenumberPlugin activated at startup.");
    }
}
