# Getting Started

## VS Code Users
1) Install the Java Extension Pack in VS Code.
2) Open this repository folder.
3) Run the app:
   - Use the Run/Debug button and choose "Run JavaFX App (Gradle)", or
   - In the terminal: `./gradlew run` (Git Bash/WSL) or `gradlew.bat run` (PowerShell).
4) First run will download Gradle/JavaFX dependencies.

Notes:
- If prompted for a JDK, install Java 21+ and let Gradle handle the rest.
- If the Run button does not appear, reopen the folder and wait for Java extensions to finish loading.

## IntelliJ Users
1) Open the project as a Gradle project (import from `settings.gradle`).
2) Let IntelliJ sync Gradle.
3) Run with Gradle: `app -> Tasks -> application -> run` (or use `./gradlew run`).

Notes:
- IntelliJ should auto-detect the Gradle Wrapper.
- If it asks for a JDK, select Java 21+.
