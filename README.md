# IN DEVELOPMENT

![asd](https://raw.githubusercontent.com/dmytrodanylyk/android-video-crop/master/sample.png)

### Description
----------
[`CropTextureView`](/library/src/com/dd/crop/CropTextureView.java) is custom view based on android [`TextureView`](http://developer.android.com/reference/android/view/TextureView.html) which gives you ability to easy play and crop video. This very similar to [`ImageView#setScaleType`](http://developer.android.com/reference/android/widget/ImageView.html#setScaleType(android.widget.ImageView.ScaleType))

Crop modes:

 - TOP
 - CENTER_CROP
 - BOTTOM

### Usage
----------

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
