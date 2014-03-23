## Preview

<img src="assets/sample1.png" width="280" />
<img src="assets/sample2.png" width="280" />
<img src="assets/sample3.png" width="280" />

## Description

[`CropTextureView`](/library/src/com/dd/crop/CropTextureView.java) is custom view based on android [`TextureView`](http://developer.android.com/reference/android/view/TextureView.html) which gives you ability to easy play and crop video. This very similar to [`ImageView#setScaleType`](http://developer.android.com/reference/android/widget/ImageView.html#setScaleType(android.widget.ImageView.ScaleType))

Crop modes:

 - TOP
 - CENTER_CROP
 - BOTTOM

## Usage


Include library module to your project or copy [`CropTextureView`](/library/src/com/dd/crop/CropTextureView.java) class to your package.


```xml
<com.dd.crop.CropTextureView
        android:id="@+id/cropTextureView1"
        android:layout_width="fill_parent"
        android:layout_height="100dp"/>
```

```java
CropTextureView cropTextureView = (CropTextureView) findViewById(R.id.cropTextureView);
// Use `setScaleType` method to crop video
cropTextureView.setScaleType(CropTextureView.ScaleType.TOP);
// Use `setDataSource` method to set data source, this could be url, assets folder or path
cropTextureView.setDataSource("http://www.w3schools.com/html/mov_bbb.mp4");
cropTextureView.play();
```

## API

ver 1.0

```java

/**
 * Play or resume video. Video will be played as soon as view is available and media player is
 * prepared.
 *
 * If video is stopped or ended and play() method was called, video will start over.
 */
CropTextureView.play()

/**
 * Pause video. If video is already paused, stopped or ended nothing will happen.
 */
CropTextureView.pause()

/**
 * Stop video (pause and seek to beginning). If video is already stopped or ended nothing will
 * happen.
 */
CropTextureView.stop()

/**
 * Sets the data source (file-path or http/rtsp URL) to use.
 */
CropTextureView.setDataSource(String)

/**
 * Sets the data source as a content Uri.
 */
CropTextureView.setDataSource(Context, Uri)

/**
 * Sets the data source (FileDescriptor) to use.
 */
CropTextureView.setDataSource(AssetFileDescriptor)
 
/**
 * Sets the player to be looping or non-looping.
 */ 
CropTextureView.setLooping(boolean)

/**
 * Seeks to specified time position. (milliseconds)
 */
CropTextureView.seekTo(int)

/**
 * Gets the duration of the file.
 */
CropTextureView.getDuration()

/**
 * Listener trigger 'onVideoPrepared' and `onVideoEnd` events
 */
CropTextureView.setListener(MediaPlayerListener)
```
