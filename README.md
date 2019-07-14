
# ANDROID CARD SCANNER

## Synopsis ##

This repository contains an activity that can scan a card to a simple model, containing: card number, expiratio date,
issue date and security code
## Basic Instructions ##

To use this package, you need to add this in project gradle
```
allprojects {
    repositories {
        ...
        maven {
            url "https://jitpack.io"
            credentials { username authToken }
        }
        ...
    }
}
```
The credential must be put in gradle.properties:
```
authToken=jp_2bfkj8gscb8bpi2jbdbnvltunnl
```

And add this gradle dependencies

```
dependencies {
    ...
    implementation 'com.github.mobile2you:m2ycdt:0.1.1'
    ...
}
```

To start the activity, you just need to call the intent creator function.
```
            startActivityForResult(createCardScannerIntent(object : CardScannerConfig(){
                /*
                *  You can override some properties. Please, read the comments in the CardScannerConfig file
                * */
            }), /* Code for Result*/)

```

You can use the FirebaseMLManager to handle other texts, and also the CardBuilder, if you wish
to user other activities, fotos, camera, etc for builing the CardModel. Read the comments in
the files of these cards to learn so.

## Camera View ##

It uses a dependency to help using the camera of device: CameraView

https://github.com/natario1/CameraView

## Language ##
Kotlin

## Settings ##

```
  minSdkVersion = 17
  targetSdkVersion = 27
  compileSdkVersion = 27
  buildToolsVersion = '27.0.1'
  kotlinVersion = '1.3.41'
  ktxVersion = '0.3'
```



