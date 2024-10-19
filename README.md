# renum-flyway-scripts-eclipse-plugin

Increase or decrease the version numbers of selected flyway scripts in Eclipse with hotkeys.

## Usage

1. Select the Flyway scripts to renum.
2. Press CTRL+ALT+S to increase each script version number by 1.
3. Press CTRL+ALT+W to decrease each script version number by 1.

![renum-flyway-scripts-with-hotkeys](https://github.com/user-attachments/assets/6b0f75d9-3a26-4f63-9fcd-44e63a65ff84)

# Install

Download the .zip from GitHub Releases and add it as an update site in Eclipse.

Blindly trust all Eclipse warnings about unsinged content and enjoy.

# Why

I was working on a Spring Boot web app with Flyway, and I need to squeeze a new SQL Flyway migration script 
in between `V001__some.sql` and `V002__other.sql`, thus having to rename `V002__other.sql` to -> `V003__other.sql` and so on
with all subsequent scripts (like, 3 of them).

So the most sensible thing is, of course, to spend a whole saturday morning 
writing your own first-time-ever Eclipse plugin to do so.

And of course ChatGPT.

