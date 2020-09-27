# Progress Bar Android
Simple library for creating striped progress bars written in Kotlin

![cookie monster](https://github.com/slaviboy/ProgressBar/blob/master/screens/home.png)

## About
The library has two available progress bar classes ProgressBar and ProgressBarAntiAlias. The anti-alias one uses PorterDuff mode with multiple bitmap, for smooth clipping, the result is better but it tend to be slower. For creating custom progress bar check out the official [wiki](https://github.com/slaviboy/ProgressBar/wiki) page.

* [**ProgressBar**](https://github.com/slaviboy/ProgressBar/wiki#progressbar) class is using path clipping 
* [**ProgressBarAntiAlias**](https://github.com/slaviboy/ProgressBar/wiki#progressbarantialias) class is using multiple bitmaps, and clips them using [PorterDuff](https://developer.android.com/reference/android/graphics/PorterDuff.Mode) Mode
 
[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![Download](https://img.shields.io/badge/version-0.1.0-blue)](https://github.com/slaviboy/ProgressBar/releases/tag/v0.1.0)

## Add to your project
Add the jitpack maven repository
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
``` 
Add the dependency
```
dependencies {
  implementation 'com.github.slaviboy:ProgressBar:v0.1.0'
}
```

## How to use
 
To create a ProgressBar just include the code below in your main activity layout file.
```xml
<com.slaviboy.progressbar.ProgressBar
     android:id="@+id/progress_bar"
     android:layout_width="200dp"
     android:layout_height="wrap_content"
     android:adjustViewBounds="true"
     android:scaleType="centerCrop"
     android:layout_marginBottom="10dp"
     app:corner_radius="100dp"
     android:padding="2dp"
     app:layout_constraintLeft_toLeftOf="parent"
     app:layout_constraintTop_toTopOf="parent"
     app:percentage="50"
     app:start_animation="true"
     app:srcCompat="@drawable/green_progress_bar" />
```

   
To change the loading percentage in real time, use the percentage property.
```kotlin
// set loading percentage to 75
val progressBar : ProgressBar = findViewById(R.id.progress_bar)
progressBar.percentage = 75.0f

```

