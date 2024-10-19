package com.ondevio.eclipse.plugin.flyway;

import static org.mockito.Mockito.*;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class RenumberFilesHandlerTest {

    private RenumIncrementFilesHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RenumIncrementFilesHandler();
    }

    @Test
    void testRenumberFiles() throws CoreException {
        // Create mock files using Mockito
        List<IFile> files = new ArrayList<>();
        files.add(createMockIFile("V001__initial_setup.sql"));
        files.add(createMockIFile("V002__add_column.sql"));
        files.add(createMockIFile("V003__remove_table.sql"));

        // Call the renumberFiles method to renumber files incrementally
        handler.renumberFiles(files, true);

        // Verify that the file names have been changed correctly
        verify(files.get(0)).move(eq(new Path("/test/V002__initial_setup.sql")), eq(true), any());
        verify(files.get(1)).move(eq(new Path("/test/V003__add_column.sql")), eq(true), any());
        verify(files.get(2)).move(eq(new Path("/test/V004__remove_table.sql")), eq(true), any());
    }

    private IFile createMockIFile(String name) {
        // Create a mock IFile
        IFile mockFile = mock(IFile.class);

        // Create a mock IContainer for the parent
        IContainer mockParent = mock(IContainer.class);
        when(mockParent.getFullPath()).thenReturn(new Path("/test"));

        // Set up the behavior for getName(), getFullPath(), and getParent()
        when(mockFile.getName()).thenReturn(name);
        when(mockFile.getFullPath()).thenReturn(new Path("/test/" + name));
        when(mockFile.getParent()).thenReturn(mockParent);

        // Mock the move method to simulate file renaming
        try {
            doNothing().when(mockFile).move(any(IPath.class), eq(true), any());
        } catch (CoreException e) {
            e.printStackTrace();
        }

        return mockFile;
    }
}
