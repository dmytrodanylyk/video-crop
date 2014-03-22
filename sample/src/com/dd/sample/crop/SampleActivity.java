package com.dd.sample.crop;

import com.dd.crop.CropTextureView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

public class SampleActivity extends Activity implements View.OnClickListener {

    // Video file url
    private static final String FILE_URL = "http://www.w3schools.com/html/mov_bbb.mp4";
    private CropTextureView mCropTextureView1;
    private CropTextureView mCropTextureView2;
    private CropTextureView mCropTextureView3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initView();
    }

    private void initView() {
        mCropTextureView1 = (CropTextureView) findViewById(R.id.cropTextureView1);
        mCropTextureView1.setScaleType(CropTextureView.ScaleType.TOP);
        mCropTextureView1.setDataSource(FILE_URL);
//        mCropTextureView1.play();

//        mCropTextureView2 = (CropTextureView) findViewById(R.id.cropTextureView2);
//        mCropTextureView2.setScaleType(CropTextureView.ScaleType.CENTER_CROP);
//        mCropTextureView2.setDataSource(FILE_URL);
//        mCropTextureView2.play();

//        mCropTextureView3 = (CropTextureView) findViewById(R.id.cropTextureView3);
//        mCropTextureView3.setScaleType(CropTextureView.ScaleType.BOTTOM);
//        mCropTextureView3.setDataSource(FILE_URL);
//        mCropTextureView3.play();

        findViewById(R.id.btnPlay).setOnClickListener(this);
        findViewById(R.id.btnPause).setOnClickListener(this);
        findViewById(R.id.btnStop).setOnClickListener(this);

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlay:
                mCropTextureView1.play();
//                mCropTextureView2.play();
//                mCropTextureView3.play();
                break;
            case R.id.btnPause:
                mCropTextureView1.pause();
//                mCropTextureView2.pause();
//                mCropTextureView3.pause();
                break;
            case R.id.btnStop:
                mCropTextureView1.stop();
//                mCropTextureView2.stop();
//                mCropTextureView3.stop();
                break;
        }
    }
}
