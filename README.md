# TWallet Benchmark

The purpose of this repository is to provide the code for the Trusted Applications and App developed to observe and measure the implemented components that are part of the TWallet System.

## Folder structure

This repository is organized as the following:

- App: contains the project of an android application, used to perform all experiments and evaluations of the developed solution.
- Script: contains the script developed to analyze the obtained log files and generate plots.
- Trusted Applications: contains the developed Trusted Applications, used to benchmark some internal operations to the Secure World.

## Pre-requisites

Considering the that Benchmark App executes some of the Secure Components commands, it is necessary to have the [TWallet System components][twallet_system] already installed in your system, before trying to execute all tests available in the application.

Additionally, based on the same pre-requisites described in [TWallet Prototypes][twallet_prototype], its advisable to use Android Studio [[1]](#references) for visualization, edition and compilations.

## Installation Setup

To install the Benchmark Trusted Applications you can just follow the same guide described in the [TWallet System respository][twallet_system]. The process can be done in two ways:
1. You can add the Trusted Application .ta generated file to the AOSP+OP-TEE files and then regenerate the system
2. You can push the .ta file to the system specific folder using ADB, using the commands:
```
adb root
adb remount
adb push [name-of-file].ta /vendor/lib/optee_armtz/
adb reboot
```

To install the Benchmark App, you can just follow the same guide in the [TWallet Prototypes][twallet_prototype], which consists in installing it as a system app. With that in mind, the procedure to achieve it is:
- Connect your Hikey 960 board to your computer through ADB
- Having the Benchmark apk, transfer it into the hikey system with the command
```
adb shell mount -o rw,remount /system
adb push [name-of-apk].apk /system/priv-app/
adb shell chmod 644 /system/priv-app/[name-of-apk].apk
adb shell mount -o remount,ro /
```
- Reboot the system to have it installed in your computer
```
adb reboot
```

## Execution

To execute the Benchmark App you simply need to click on the app icon that appears on your Android Menu. After entering the app, to test some specific component or functionality you need to select the test you intend to execute and wait for the progress bar to be filled

## References

1. Android Developers. Android Studio 2020.3.1. 2021. url: https://developer.android.com/studio/

[twallet_system]: <https://github.com/rafagameiro/TWallet_system>
[twallet_prototype]: <https://github.com/rafagameiro/TWallet_prototypes>