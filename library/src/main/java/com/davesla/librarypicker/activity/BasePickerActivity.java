package com.davesla.librarypicker.activity;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.davesla.librarypicker.R;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

/**
 * Created by hwb on 15/7/1.
 */
public abstract class BasePickerActivity extends ActionBarActivity {
    public ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initImageLoader();
        setContent();
        initView();
        initData();
    }

    protected abstract void setContent();

    protected abstract void initView();

    protected abstract void initData();

    private void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheExtraOptions(200, 200)
                .diskCacheExtraOptions(200, 200, null).threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .denyCacheImageMultipleSizesInMemory().memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(20 * 1024 * 1024).memoryCacheSizePercentage(13)
                .diskCacheSize(50 * 1024 * 1024).diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .imageDownloader(new BaseImageDownloader(this))
                .imageDecoder(new BaseImageDecoder(true))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .build();
        imageLoader.init(config);
    }

    protected void colorStatusBar(int color) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    public int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
        int colorPrimary = a.getColor(0, getResources().getColor(R.color.default_primary_color));
        a.recycle();
        return colorPrimary;
    }

    protected int getColorPrimaryDark() {
        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimaryDark});
        int colorPrimary = a.getColor(0, getResources().getColor(R.color.default_primary_dark_color));
        a.recycle();
        return colorPrimary;
    }

    protected int getColorControlNormal() {
        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorControlNormal});
        int colorPrimary = a.getColor(0, getResources().getColor(R.color.default_control_normal_color));
        a.recycle();
        return colorPrimary;
    }

    public void showToast(String message) {
        if (message == null || TextUtils.isEmpty(message)) {
            return;
        }
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void loadLocalImage(ImageView view, String url) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.picker_bg_default_gray)
                .cacheInMemory(true)
                .considerExifParams(true)
                .cacheOnDisk(true).build();
        ImageLoader.getInstance().displayImage("file://" + url, view, options);

    }

}
