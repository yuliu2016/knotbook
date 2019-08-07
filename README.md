# Knotbook

![Knotbook Icon](assets/knot-small.png)

**Knotbook** is a **JavaFX** data viewing/analysis app for FRC. It succeeds RT

It is explicitly written with JDK 11 with modules and uses OpenJFX 12 and Kotlin 1.3 frameworks.

To build: `gradlew build`

To run application: `gradlew run`

To generate a runtime image: `gradlew jlink` or `gradlew jlinkZip`

ControlsFX SpreadsheetView is not supported since JDK 9. 
This project uses a custom table Control that does not depend on VirtualFlow
and is more efficient at data tables: instead of creating a SpreadsheetCell object
for each cell, it does a layout pass for the entire table. 
