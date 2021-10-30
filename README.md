# TWallet Benchmark

The purpose of this repository is to provide the code for the Trusted Applications and App developed to observe and measure the implemented components that are part of the TWallet System.

## Installation Setup

To install the Benchmark Trusted Applications you can just follow the same guide described in the [TWallet System respository][twallet_system]. The process can be done in two ways:
1. You can add the Trusted Application .ta generated file to the AOSP+OP-TEE files and then regenerate the system
2. You can push the .ta file to the system specific folder using ADB, using the commands:
```
adb root
adb remount
adb push [name-of-apk].ta /vendor/optee-armtz/
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

[twallet_system]: <https://github.com/rafagameiro/TWallet_system>
[twallet_prototype]: <https://github.com/rafagameiro/TWallet_prototypes>