# Progress Bar Android
Simple library for creating striped progress bars written in Kotlin

![cookie monster](https://github.com/slaviboy/ProgressBar/blob/master/screens/home.gif)

## About
The library has two available progress bar types
* **ProgressBar**
* **ProgressBarAntiAlias**

[ProgressBar](https://github.com/slaviboy/ProgressBar/wiki#progressbar) uses path clipping     
[ProgressBarAntiAlias](https://github.com/slaviboy/ProgressBar/wiki#progressbarantialias) uses multiple bitmaps, and clips them using [PorterDuff](https://developer.android.com/reference/android/graphics/PorterDuff.Mode) Mode


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
```Kotlin
// set loading percentage to 75
val progressBar : ProgressBar = findViewById(R.id.progress_bar)
progressBar.percentage = 75.0f

```

For creating a custom progress bar check out the [Wiki](https://github.com/slaviboy/ProgressBar/wiki) page
