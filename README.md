# Conway's Game of Life mod
A Minecraft implementation of Conway's Game of Life, where blocks
dynamically change between alive and dead states based on classic
CGOL rules.

## Installation
1. Download and run the [Fabric installer](https://fabricmc.net/use).
    - Note: this step may vary if you aren't using the vanilla launcher
      or an old version of Minecraft.
2. Download the [Fabric API](https://modrinth.com/mod/fabric-api)
   and move it to the mods folder (`.minecraft/mods`).
3. Download CGOL mod from the [releases page](https://github.com/tema5002/cgol-mod/releases)
   and move it to the mods folder (`.minecraft/mods`).

## Contributing
1. Clone the repository
   ```sh
   git clone https://github.com/tema5002/cgol-mod
   cd cgol-mod
   ```
2. Generate the Minecraft source code
   ```sh
   ./gradlew genSources
   ```
    - Note: on Windows, use `gradlew` rather than `./gradlew`.
3. Import the project into your preferred IDE.
    1. If you use IntelliJ (the preferred option), you can simply import the project as a Gradle project.
    2. If you use Eclipse, you need to `./gradlew eclipse` before importing the project as an Eclipse project.
4. Edit the code
5. After testing in the IDE, build a JAR to test whether it works outside the IDE too
   ```sh
   ./gradlew build
   ```
   The mod JAR may be found in the `build/libs` directory
6. [Create a pull request](https://help.github.com/en/articles/creating-a-pull-request)
   so that your changes can be integrated into CGOL mod
    - Note: for large contributions, create an issue before doing all that
      work, to ask whether your pull request is likely to be accepted