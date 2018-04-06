# AndroidLogger

AndroidLogger allows you to write logs to file in your Android app, compress logs and send on email. You can set max size of log file, clear 
file when full or rewrite line by line.

To add library to your own project:
###### Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
###### Step 2. Add the dependency
```
dependencies {
	        compile 'com.github.viktord1985:AndroidLogger:v1.3'
	}
```
###### Available functions:
    AndroidLogger.d()           - log debug
    AndroidLogger.e()           - log error
    AndroidLogger.sendLog()     - send log file on email
    AndroidLogger.clearLog()    - clear log file
    AndroidLogger.zipLog()      - compress log file
    AndroidLogger.setFileName() - set log file name
    AndroidLogger.setMaxSize() - set log file max size
See demo app in Example folder.

## Screenshot
![screenshot](https://github.com/viktord1985/AndroidLogger/blob/master/screenshots/Screenshot.png)
