# Mobile Test Automation Project

This project is an automated testing framework for mobile applications, using Java, Maven, Appium, and TestNG. The framework supports parallel testing for both Android and iOS platforms.
The code is made to support :
1. Support automatically switching ON emulators and simulators.
2. Both IOS and Android platforms.
3. Different infrastructure environments - Automation, QA (New envs can be added easily).
4. Different country support is taken care by creating separate directories for every env (Currently UZ is supported).

-----------------------------------------------------------
## Technology: <br>
* Automation Framework: Appium <br>
* Build tool: Maven <br>
* Bundled Tools: TestNG
* Language: Java <br>
* Report: ExtentReport <br>
* Project Structure: Page object Model (POM)<br>

## Prerequisite:
* IntelliJ Idea (aqua) or other IDE
* Java
* npm or other package manager
* Appium Server (2.x) for installation <a href="https://github.com/appium/appium" target="_blank">appium installation</a>
* Appium Inspector [download appium inspector](https://github.com/appium/appium-inspector/releases)
* Android studio Emulator or Real Device
* Xcode (for iOS testing) or Real Device
* Install 'brew install ffmpeg' for ios recording.


## How to execute

----------------------------------------------------------
## Run the Automation Script :
1. When target=local, switch ON emulators or simulators equals to threadCount. (or)
2. When target=runner, Framework will pick emulators or simulators automatically from the machine.
3. Run Appium server.
4. Update secrets in .env file located in 'src/test/resources/.env'. Copy and rename '.env.example' to '.env' file file.
5. Check and update appiumUrl in config.properties before execute it.
6. Execute the testng/testng.xml file(s) or use mvn command
`mvn clean test -Dgroups="insertYourTag" -DplatformName="android or ios" -Dtarget="local or runner" -DthreadCount="threadCountNumber"`.
mvn clean test -DplatformName=android -DthreadCount=1 -Dtarget=local -Denv=automation -Dgroups=Company.Mobile.Module.loginAppTest
7. Video is generated in 'video' folder and logs in 'logs' folder under specific 'platform_uuid' folder.
8. When target=local, will run the test on the local machine. target=runner, will run the test on the remote machine. However, we can use target=runner for local machine and framework will pick devices from machine.

## Test Groups Wiki

The project's **Test Groups** used in `@Test(groups = {...})` annotations are always available in the **Wiki** section.  
This information is automatically updated via **GitHub Actions** on **every pull request**, ensuring that the latest list of test groups is always accessible.

---

### How does the update work?

1. **Whenever a pull request is created or updated**, GitHub Actions automatically scans all `.java` test files for `@Test(groups = {...})` annotations.
2. The extracted **Test Groups** are written to a Markdown file and uploaded to the **Wiki** section.
3. This ensures that the Wiki always contains the most up-to-date list of test groups in the project.


## Project Structure
```plaintext 
.
├── README.md
├── logs                      # Output logs based on device used in execution.
│   └── android_emulator-{id}
│       └── application.log
├── pom.xml                   # Maven build configuration file.
├── reports                   # Output execution reports.
│   ├── AppiumReport_*.html
├── src
│   ├── main
│   │   ├── java
│   │   │   └── company
│   │   │       ├── base
│   │   │       │   └── BasePage.java             # Base class.
│   │   │       ├── driver                        # Appium Driver class.
│   │   │       │   └── DriverFactory.java
│   │   │       ├── pom                           # Page Object Model package. Parent of every module.
│   │   │       │   ├── home                      # Home module.
│   │   │       │   │   └── HomePage.java
│   │   │       │   ├── login                     # Login module.
│   │   │       │   │   ├── Login.java
│   │   │       │   │   └── WelcomePage.java
│   │   │       └── util                          # Utilities package.
│   │   │           ├── APIManager.java           # HTTP helper methods.
│   │   │           ├── AppInteractions.java      # Android and iOS app interaction calls.
│   │   │           ├── CommonGestures.java       # Gestures handling.
│   │   │           ├── DatabaseConnections.java  # Unused DB connection support.
│   │   │           ├── ExtentListeners.java      # Extent Report listeners.
│   │   │           ├── ExtentManager.java        # for managing the ExtentReports instance
│   │   │           ├── IOSGestures.java          # Gestures handling for IOS
│   │   │           ├── JsonReader.java           # provides methods for reading data from a JSON file.
│   │   │           ├── LoggerUtilities.java      # Output log utility.
│   │   │           ├── minioUploader.java        # for uploading screen record files to a MinIO server.
│   │   │           ├── retryanalyzer.java        # implementation of the IRetryAnalyzer interface
│   │   │           ├── ReadProperties.java       # Property reader utility.
│   │   │           └── RecordVideo.java          # Recording utility.
│   │   │           └── TagExtractor.java         # scans a specified directory and extracts test groups.
│   │   └── resources
│   │       └── log4j2.xml
│   └── test
│       ├── java
│       │   └── company
│       │       ├── base
│       │       │   └── BaseTest.java
│       │       ├── login                         # Login Module - Test Scenarios.
│       │       │   └── LoginTest.java
│       └── resources
│           ├── app                               # APK/IPA files for execution.
│           │   ├── automation                    # Automation environment.
│           │   │   ├── zone1
│           │   │   │   └── sample.txt            # Dummy file.
│           │   │   ├── zone2
│           │   │   │   ├── androidApp.apk        # Company Android app.
│           │   │   │   └── sample.txt
│           │   └── qa                            # QA environment.
│           │       ├── zone1
│           │       │   └── sample.txt
│           │       └── zone2
│           │           └── sample.txt
│           ├── config.properties                 # Configuration properties file.
│           ├── testdata                          # Test data parent folder.
│           │   ├── automation                    # Automation environment.
│           │   │   └── zone1
│           │   │       ├── login                 # Module.
│           │   │       │   └── testdata.json     # Test data file (name must be the same as module).
│           │   └── qa
│           │       └── zone1
│           │           └── login
│           │               └── login.json
│           └── testng                            # TestNG files - Test suites.
│               ├── testng.xml                    # testng xml test suite.
└── videos                                        # Output recording videos.
    └── android_emulator-{id}                     # Targeted device folder created at runtime.
        └── folderName                            # Test class folder created at runtime.
            └── testMethodName.mp4                # Test/Method video created at runtime.

```
