package com.davesla.librarypicker.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.davesla.librarycroper.CropImageView;
import com.davesla.librarypicker.Picker;
import com.davesla.librarypicker.R;
import com.davesla.librarypicker.utils.ImageUtil;
import com.davesla.librarypicker.utils.MenuColorizer;

/**
 * Created by hwb on 15/7/1.
 */
public class ImageCropActivity extends BasePickerActivity {

    private Toolbar toolbar;
    private CropImageView imageView;

    private Handler mHandler = new Handler();

    private int aspectRatioX, aspectRatioY;
    private String imagePath;
    private String savePath;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picker_crop, menu);
        MenuColorizer.colorMenu(this, menu, getColorControlNormal());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_ok) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bmp = imageView.getCroppedImage();
                    ImageUtil.saveImage(bmp, savePath);
                    Intent intent = new Intent();
                    intent.putExtra(Picker.IMAGEPATH_EXTRA, savePath);
                    setResult(Activity.RESULT_OK, intent);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });

                }
            }).start();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void setContent() {
        setContentView(R.layout.picker_activity_image_crop);
    }

    @Override
    protected void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imageView = (CropImageView) findViewById(R.id.image);
    }

    @Override
    protected void initData() {
        //获取颜色值
        aspectRatioX = getIntent().getIntExtra(Picker.ASPECTRATIOX_EXTRA, 1);
        aspectRatioY = getIntent().getIntExtra(Picker.ASPECTRATIOY_EXTRA, 1);
        imagePath = getIntent().getStringExtra(Picker.IMAGEPATH_EXTRA);
        savePath = getIntent().getStringExtra(Picker.SAVEPATH_EXTRA);

        toolbar.setBackgroundColor(getColorPrimary());

        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle(R.string.picker_crop_title);

        imageView.setAspectRatio(aspectRatioX, aspectRatioY);
        imageView.setFixedAspectRatio(true);
        Bitmap bmp = ImageUtil.getImageThumbnail(imagePath, 720, 720);
        imageView.setImageBitmap(bmp);

    }

}
