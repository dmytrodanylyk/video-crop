package com.dd.crop;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.io.IOException;

public class CropTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    private boolean mIsPlayCalled;

    public enum ScaleType {
        CENTER_CROP, TOP, BOTTOM;
    }

    // Log tag
    private static final String TAG = CropTextureView.class.getName();

    private MediaPlayer mMediaPlayer;
    private SurfaceTexture mSurfaceTexture;

    private float mVideoHeight;
    private float mVideoWidth;

    private boolean mIsDataSourceSet;
    private boolean mIsViewAvailable;

    private ScaleType mScaleType;

    public CropTextureView(Context context) {
        super(context);
        initView();
    }

    public CropTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CropTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        mMediaPlayer = new MediaPlayer();
        setScaleType(ScaleType.CENTER_CROP);
        setSurfaceTextureListener(this);
    }

    public void setScaleType(ScaleType scaleType) {
        mScaleType = scaleType;
    }

    private void updateTextureViewSize() {
        float viewWidth = getWidth();
        float viewHeight = getHeight();

        float scaleX = 1.0f;
        float scaleY = 1.0f;

        if (mVideoWidth > viewWidth && mVideoHeight > viewHeight) {
            scaleX = mVideoWidth / viewWidth;
            scaleY = mVideoHeight / viewHeight;
        } else if (mVideoWidth < viewWidth && mVideoHeight < viewHeight) {
            scaleY = viewWidth / mVideoWidth;
            scaleX = viewHeight / mVideoHeight;
        } else if (viewWidth > mVideoWidth) {
            scaleY = (viewWidth / mVideoWidth) / (viewHeight / mVideoHeight);
        } else if (viewHeight > mVideoHeight) {
            scaleX = (viewHeight / mVideoHeight) / (viewWidth / mVideoWidth);
        }

        // Calculate pivot points, in our case crop from center
        int pivotPointX;
        int pivotPointY;

        switch (mScaleType) {
            case TOP:
                pivotPointX = 0;
                pivotPointY = 0;
                break;
            case BOTTOM:
                pivotPointX = (int) (viewWidth);
                pivotPointY = (int) (viewHeight);
                break;
            case CENTER_CROP:
                pivotPointX = (int) (viewWidth / 2);
                pivotPointY = (int) (viewHeight / 2);
                break;
            default:
                pivotPointX = (int) (viewWidth / 2);
                pivotPointY = (int) (viewHeight / 2);
                break;
        }

        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY, pivotPointX, pivotPointY);

        setTransform(matrix);
    }

    public void setDataSource(String path) {
        try {
            mMediaPlayer.setDataSource(path);
            mIsDataSourceSet = true;
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void setDataSource(Context context, Uri uri) {
        try {
            mMediaPlayer.setDataSource(context, uri);
            mIsDataSourceSet = true;
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void setDataSource(AssetFileDescriptor afd) {
        try {
            long startOffset = afd.getStartOffset();
            long length = afd.getLength();
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), startOffset, length);
            mIsDataSourceSet = true;
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void play() {
        if (mMediaPlayer.isPlaying()) {
            Log.w(TAG, "play() was called but video is already playin.");
            return;
        }

        mIsPlayCalled = true;
        if (!mIsDataSourceSet) {
            Log.w(TAG, "play() was called but data source was not set.");
            return;
        }

        if (!mIsViewAvailable) {
            Log.d(TAG, "play() was called but view is not available yet, waiting.");
            return;
        }

        Surface surface = new Surface(mSurfaceTexture);
        try {

            mMediaPlayer.setSurface(surface);
            mMediaPlayer.setLooping(true);

            mMediaPlayer.setOnVideoSizeChangedListener(
                    new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                            mVideoWidth = width;
                            mVideoHeight = height;
                            updateTextureViewSize();
                        }
                    }
            );

            // don't forget to call MediaPlayer.prepareAsync() method when you use constructor for
            // creating MediaPlayer
            mMediaPlayer.prepareAsync();

            // Play video when the media source is ready for playback.
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });

        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.getMessage());
        } catch (SecurityException e) {
            Log.d(TAG, e.getMessage());
        } catch (IllegalStateException e) {
            Log.d(TAG, e.toString());
        }
    }

    public void pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void stop() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    public void resume() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        mSurfaceTexture = surfaceTexture;
        mIsViewAvailable = true;
        if (mIsDataSourceSet && mIsPlayCalled) {
            Log.d(TAG, "View is available and play() was called.");
            play();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
