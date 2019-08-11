# Knotbook [![Build Status](https://dev.azure.com/yuliu2016/knotbook/_apis/build/status/yuliu2016.knotbook?branchName=master)](https://dev.azure.com/yuliu2016/knotbook/_build/latest?definitionId=1&branchName=master)


![Knotbook Icon](assets/knot-small.png)

**Knotbook** is a **JavaFX** data viewing/analysis app for FRC. It succeeds RT

It is written with JDK 12 with modules and uses OpenJFX 12

To build: `gradlew build`

To run application: `gradlew run`

To generate a runtime image: `gradlew jlink` or `gradlew jlinkZip`

ControlsFX SpreadsheetView is not supported since JDK 9. 
This project uses a custom table Control that does not depend on VirtualFlow
and is more efficient at data tables: instead of creating a SpreadsheetCell object
for each cell, it does a layout pass for the entire table. 

##### Special Notes: 
The webcam-capture library is unable to load the native files in the modules in development mode,
likely due to Java 9+ modularization. As a workaround, we need to find `OpenIMAJGrabber.dll`
(or another extension for another system) and put it on the PATH so that BridJ can find it.
It seems to work in the jlink image