package com.example.junior.hotelsignature.views


import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.Surface
import android.view.TextureView
import com.example.junior.hotelsignature.views.TextureVideoView.ScaleType.*
import java.io.IOException

@Suppress("unused")
class TextureVideoView : TextureView, TextureView.SurfaceTextureListener {

    private var mMediaPlayer: MediaPlayer? = null

    private var mVideoHeight: Float = 0f
    private var mVideoWidth: Float = 0f

    private var mIsDataSourceSet: Boolean = false
    private var mIsViewAvailable: Boolean = false
    private var mIsVideoPrepared: Boolean = false
    private var mIsPlayCalled: Boolean = false

    private var mScaleType: ScaleType? = null
    private var mState: State? = null

    private var onPreparedListener: (() -> Unit)? = null
    private var onCompletionListener: (() -> Unit)? = null
    private var onErrorListener: ((mp: MediaPlayer, err: Int, desc: Int) -> Boolean)? = null

    /**
     * @see android.media.MediaPlayer.getDuration
     */
    val duration: Int?
        get() = mMediaPlayer?.duration

    enum class ScaleType {
        CENTER_CROP, TOP, BOTTOM
    }

    enum class State {
        UNINITIALIZED, PLAY, STOP, PAUSE, END
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initView()
    }

    private fun initView() {
        initPlayer()
        setScaleType(CENTER_CROP)
        surfaceTextureListener = this
    }

    fun setScaleType(scaleType: ScaleType) {
        mScaleType = scaleType
    }

    private fun updateTextureViewSize() {
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        var scaleX = 1f
        var scaleY = 1f

        when {
            mVideoWidth > viewWidth && mVideoHeight > viewHeight -> {
                scaleX = mVideoWidth / viewWidth
                scaleY = mVideoHeight / viewHeight
            }
            mVideoWidth < viewWidth && mVideoHeight < viewHeight -> {
                scaleY = viewWidth / mVideoWidth
                scaleX = viewHeight / mVideoHeight
            }
            viewWidth > mVideoWidth ->
                scaleY = (viewWidth / mVideoWidth) / (viewHeight / mVideoHeight)
            viewHeight > mVideoHeight ->
                scaleX = (viewHeight / mVideoHeight) / (viewWidth / mVideoWidth)
        }

        // Calculate pivot points, in our case crop from center
        val pivotPointX: Int
        val pivotPointY: Int

        when (mScaleType) {
            TOP -> {
                pivotPointX = 0
                pivotPointY = 0
            }
            BOTTOM -> {
                pivotPointX = (viewWidth).toInt()
                pivotPointY = (viewHeight).toInt()
            }
            CENTER_CROP -> {
                pivotPointX = (viewWidth / 2).toInt()
                pivotPointY = (viewHeight / 2).toInt()
            }
            else -> {
                pivotPointX = (viewWidth / 2).toInt()
                pivotPointY = (viewHeight / 2).toInt()
            }
        }

        val matrix = Matrix()
        matrix.setScale(scaleX, scaleY, pivotPointX.toFloat(), pivotPointY.toFloat())

        setTransform(matrix)
    }

    private fun initPlayer() {
        if (mMediaPlayer == null)
            mMediaPlayer = MediaPlayer()
        else
            mMediaPlayer?.reset()

        mMediaPlayer?.setOnErrorListener { mp, err, desc ->
            onErrorListener?.invoke(mp, err, desc) ?: false
        }

        mIsVideoPrepared = false
        mIsPlayCalled = false
        mState = State.UNINITIALIZED
    }

    /**
     * @see android.media.MediaPlayer.setDataSource
     */
    fun setDataSource(path: String) {
        initPlayer()

        try {
            mMediaPlayer?.setDataSource(path)
            mIsDataSourceSet = true
            prepare()
        } catch (e: IOException) {
            Log.d(TAG, "${e.message}")
        }

    }

    /**
     * @see android.media.MediaPlayer.setDataSource
     */
    fun setDataSource(context: Context, uri: Uri) {
        initPlayer()

        try {
            mMediaPlayer?.setDataSource(context, uri)
            mIsDataSourceSet = true
            prepare()
        } catch (e: IOException) {
            Log.d(TAG, "${e.message}")
        }

    }

    /**
     * @see android.media.MediaPlayer.setDataSource
     */
    fun setDataSource(afd: AssetFileDescriptor) {
        initPlayer()

        try {
            val startOffset = afd.startOffset
            val length = afd.length

            mMediaPlayer?.setDataSource(afd.fileDescriptor, startOffset, length)
            mIsDataSourceSet = true
            prepare()
        } catch (e: IOException) {
            Log.d(TAG, "${e.message}")
        }

    }

    private fun prepare() {
        try {
            mMediaPlayer?.setOnVideoSizeChangedListener { _, width, height ->
                mVideoWidth = width.toFloat()
                mVideoHeight = height.toFloat()
                updateTextureViewSize()
            }
            mMediaPlayer?.setOnCompletionListener {
                mState = State.END
                log("Video has ended.")

                onCompletionListener?.invoke()
            }

            // don't forget to call MediaPlayer.prepareAsync() method when you use constructor for
            // creating MediaPlayer
            mMediaPlayer?.prepareAsync()

            // Play video when the media source is ready for playback.
            mMediaPlayer?.setOnPreparedListener {
                mIsVideoPrepared = true
                if (mIsPlayCalled && mIsViewAvailable) {
                    log("Player is prepared and play() was called.")
                    play()
                }

                onPreparedListener?.invoke()
            }

        } catch (e: IllegalArgumentException) {
            Log.w(TAG, e.message)
        } catch (e: SecurityException) {
            Log.w(TAG, e.message)
        } catch (e: IllegalStateException) {
            Log.w(TAG, e.toString())
        }

    }

    /**
     * Play or resume video. Video will be played as soon as view is available and media player is
     * prepared.
     *
     * If video is stopped or ended and play() method was called, video will start over.
     */
    fun play() {
        if (!mIsDataSourceSet) {
            log("play() was called but data source was not set.")
            return
        }

        mIsPlayCalled = true

        if (mIsVideoPrepared) {
            if (!mIsViewAvailable) {
                log("play() was called but view is not available yet, waiting.")
                return
            }

            if (mState == State.PLAY) {
                log("play() was called but video is already playing.")
                return
            }

            if (mState == State.PAUSE) {
                log("play() was called but video is paused, resuming.")
                mState = State.PLAY
                mMediaPlayer?.start()
                return
            }

            if (mState == State.END || mState == State.STOP) {
                log("play() was called but video already ended, starting over.")
                mState = State.PLAY
                mMediaPlayer?.seekTo(0)
                mMediaPlayer?.start()
                return
            }

            mState = State.PLAY
            mMediaPlayer?.start()
            return
        }
        log("play() was called but video is not prepared yet, waiting.")
    }

    /**
     * Pause video. If video is already paused, stopped or ended nothing will happen.
     */
    fun pause() {
        if (mState == State.PAUSE) {
            log("pause() was called but video already paused.")
            return
        }

        if (mState == State.STOP) {
            log("pause() was called but video already stopped.")
            return
        }

        if (mState == State.END) {
            log("pause() was called but video already ended.")
            return
        }

        mState = State.PAUSE
        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
            mMediaPlayer?.pause()
        }
    }

    /**
     * Stop video (pause and seek to beginning). If video is already stopped or ended nothing will
     * happen.
     */
    fun stop() {
        if (mState == State.STOP) {
            log("stop() was called but video already stopped.")
            return
        }

        if (mState == State.END) {
            log("stop() was called but video already ended.")
            return
        }

        mState = State.STOP
        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
            mMediaPlayer?.pause()
            mMediaPlayer?.seekTo(0)
        }
    }

    fun release() {
        mMediaPlayer?.release()
    }

    /**
     * @see android.media.MediaPlayer.setLooping
     */
    fun setLooping(looping: Boolean) {
        mMediaPlayer?.isLooping = looping
    }

    /**
     * @see android.media.MediaPlayer.seekTo
     */
    fun seekTo(milliseconds: Int) {
        mMediaPlayer?.seekTo(milliseconds)
    }

    fun setOnCompletionListener(listener: () -> Unit) {
        onCompletionListener = listener
    }


    fun setOnPreparedListener(listener: () -> Unit) {
        onPreparedListener = listener
    }

    fun setOnErrorListener(listener: (mp: MediaPlayer, err: Int, desc: Int) -> Boolean) {
        onErrorListener = listener
    }


    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        val surface = Surface(surfaceTexture)
        mMediaPlayer?.setSurface(surface)
        mIsViewAvailable = true
        if (mIsDataSourceSet && mIsPlayCalled && mIsVideoPrepared) {
            log("View is available and play() was called.")
            play()
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = false

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

    companion object {

        // Indicate if logging is on
        private const val LOG_ON = false

        // Log tag
        private val TAG = TextureVideoView::class.java.name

        internal fun log(message: String) {
            @Suppress("ConstantConditionIf")
            if (LOG_ON) {
                Log.v(TAG, message)
            }
        }
    }
}