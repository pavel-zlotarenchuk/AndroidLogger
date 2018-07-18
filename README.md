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
	        compile 'com.github.viktord1985:AndroidLogger:v1.4'
	}
```
###### Step 3. Usage

```
AndroidLogger.activateLogger(this, new String[]{"exampleemail@gmail.com"}, 5);
```
- Context need for permission
- List of emails
- Maximum log file size

###### Available functions:
    AndroidLogger.log() - for your tag
    AndroidLogger.v() - for verbose
    AndroidLogger.d() - for debug
    AndroidLogger.i() - for info
    AndroidLogger.w() - for warn
    AndroidLogger.e() - for error
    AndroidLogger.sendLog()     - send log file on email
    AndroidLogger.clearLog()    - clear log file
    AndroidLogger.setFileName() - set log file name
    AndroidLogger.setMaxSize() - set log file max size
See demo app in Example folder.

## Screenshot
![screenshot](https://github.com/viktord1985/AndroidLogger/blob/master/screenshots/Screenshot.png)
