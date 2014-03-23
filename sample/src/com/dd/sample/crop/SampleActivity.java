package com.dd.sample.crop;

import com.dd.crop.CropTextureView;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class SampleActivity extends Activity implements View.OnClickListener,
        ActionBar.OnNavigationListener {

    // Video file url
    private static final String FILE_URL = "http://www.w3schools.com/html/mov_bbb.mp4";
    private CropTextureView mCropTextureView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initView();
        initActionBar();

        if (!isWIFIOn(getBaseContext())) {
            Toast.makeText(getBaseContext(), "You need internet connection to stream video",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void initActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setDisplayShowTitleEnabled(false);

        SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.action_list,
                android.R.layout.simple_spinner_dropdown_item);
        actionBar.setListNavigationCallbacks(mSpinnerAdapter, this);
    }

    private void initView() {
        mCropTextureView = (CropTextureView) findViewById(R.id.cropTextureView);

        findViewById(R.id.btnPlay).setOnClickListener(this);
        findViewById(R.id.btnPause).setOnClickListener(this);
        findViewById(R.id.btnStop).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlay:
                mCropTextureView.play();
                break;
            case R.id.btnPause:
                mCropTextureView.pause();
                break;
            case R.id.btnStop:
                mCropTextureView.stop();
                break;
        }
    }

    final int indexCropCenter = 0;
    final int indexCropTop = 1;
    final int indexCropBottom = 2;

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        switch (itemPosition) {
            case indexCropCenter:
                mCropTextureView.stop();
                mCropTextureView.setScaleType(CropTextureView.ScaleType.CENTER_CROP);
                mCropTextureView.setDataSource(FILE_URL);
                mCropTextureView.play();
                break;
            case indexCropTop:
                mCropTextureView.stop();
                mCropTextureView.setScaleType(CropTextureView.ScaleType.TOP);
                mCropTextureView.setDataSource(FILE_URL);
                mCropTextureView.play();
                break;
            case indexCropBottom:
                mCropTextureView.stop();
                mCropTextureView.setScaleType(CropTextureView.ScaleType.BOTTOM);
                mCropTextureView.setDataSource(FILE_URL);
                mCropTextureView.play();
                break;
        }
        return true;
    }

    public static boolean isWIFIOn(Context context) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return (networkInfo != null && networkInfo.isConnected());
    }
}
