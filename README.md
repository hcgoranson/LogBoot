📃 LogBoot
============

This is an IntelliJ plugin to view and change loglevel on a Spring Boot application.

The plugin utilizes the Spring Boot actuator log API.  
Just enter the hostname and port of your Spring Boot application, hit enter and you are ready to go!
![plugin](./readme/logboot.png)
---

## Features
- Fetch current loggers and log level
- Search for a specific logger
- Update log level for a logger

---

## Setup
Make sure Java 11+  and Gradle is installed  
#### 🔨Build
Clone this repo to your desktop and run `./gradlew build` to build the project  
####  🕹 Run
Run IntelliJ in dev mode with `./gradlew runIde`
####  📦 Package
Package a zip file with `./gradlew assemble`  
Zip file can be found in `build/distributions`

---

## Usage
- Install the plugin either manually or via the marketplace
- Open the **LogBoot** dialogue and click on the "refresh" button to fetch the current loggers
- Double click on a logger to op en the update *dialogue*
![plugin](./readme/update.png)
