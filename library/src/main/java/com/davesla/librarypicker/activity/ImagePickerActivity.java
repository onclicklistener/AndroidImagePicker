package com.davesla.librarypicker.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.davesla.librarypicker.Picker;
import com.davesla.librarypicker.R;
import com.davesla.librarypicker.adapter.FolderAdapter;
import com.davesla.librarypicker.adapter.ImageAdapter;
import com.davesla.librarypicker.bean.Folder;
import com.davesla.librarypicker.bean.Picture;
import com.davesla.librarypicker.utils.DensityUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by hwb on 15/6/29.
 */
public class ImagePickerActivity extends BasePickerActivity {

    private static final int REQUEST_CODE_CROP = 999;
    private static final int REQUEST_CODE_SHOT = 888;

    private Toolbar toolbar;
    private RecyclerView recyclerView, folderRecyclerView;
    private TextView textDate, folderName;
    private ImageAdapter adapter;
    private FrameLayout backLayout;

    private String selectedFolder;

    private static Handler mHandler = new Handler();
    private Map<String, ArrayList<Picture>> folderMap = new LinkedHashMap<>();

    //use static to avoid being restored
    private static String capturedPhotoPath;

    //接收传值
    private int aspectRatioX, aspectRatioY;
    private String savePath;

    @Override
    protected void setContent() {
        setContentView(R.layout.picker_activity_image_picker);
    }

    @Override
    protected void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backLayout = (FrameLayout) findViewById(R.id.back_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        folderRecyclerView = (RecyclerView) findViewById(R.id.recycler_folder);
        folderName = (TextView) findViewById(R.id.tv_folder);
        textDate = (TextView) findViewById(R.id.tv_date);
    }

    @Override
    protected void initData() {
        //获取颜色值
        aspectRatioX = getIntent().getIntExtra(Picker.ASPECTRATIOX_EXTRA, 1);
        aspectRatioY = getIntent().getIntExtra(Picker.ASPECTRATIOY_EXTRA, 1);
        savePath = getIntent().getStringExtra(Picker.SAVEPATH_EXTRA);

        toolbar.setBackgroundColor(getColorPrimary());
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle(R.string.picker_select);

        backLayout.getForeground().setAlpha(0);

        final GridLayoutManager manager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(adapter = new ImageAdapter(this, recyclerView));

        folderName.setText(R.string.picker_all);
        folderName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (folderRecyclerView.getVisibility() == View.GONE) {
                    show();
                } else {
                    hide();
                }
            }
        });

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (textDate.getVisibility() == View.VISIBLE) {
                        ObjectAnimator anim = ObjectAnimator.ofFloat(textDate, "alpha", 1.0F, 0.0F).setDuration(300);
                        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float cVal = (Float) animation.getAnimatedValue();
                                if (cVal == 0) {
                                    textDate.setVisibility(View.GONE);
                                }
                            }
                        });
                        anim.start();
                    }
                } else {
                    if (textDate.getVisibility() == View.GONE) {
                        textDate.setVisibility(View.VISIBLE);
                        ObjectAnimator anim = ObjectAnimator.ofFloat(textDate, "alpha", 0.0F, 1.0F).setDuration(300);
                        anim.start();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = manager.findFirstVisibleItemPosition();
                Picture picture = folderMap.get(selectedFolder).get(firstVisibleItem);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
                Date date = new Date(picture.dateAdded * 1000);
                String firstDate = sdf.format(date);
                textDate.setText(firstDate);
            }
        });

        init();
    }

    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<Picture> allPictureList = getImage();
                if (allPictureList == null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showToast("error");
                            finish();
                        }
                    });
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        folderMap.put(getResources().getString(R.string.picker_all), allPictureList);
                        selectedFolder = getResources().getString(R.string.picker_all);

                        for (Picture picture : allPictureList) {
                            String folderName = getFolderName(picture.path);
                            if (folderMap.containsKey(folderName)) {
                                ArrayList<Picture> pictureList = folderMap.get(folderName);
                                pictureList.add(picture);
                            } else {
                                ArrayList<Picture> pictureList = new ArrayList<>();
                                pictureList.add(picture);
                                folderMap.put(folderName, pictureList);
                            }
                        }

                        update(selectedFolder, true);

                        LinearLayoutManager manager = new LinearLayoutManager(ImagePickerActivity.this, LinearLayoutManager.VERTICAL, false);
                        folderRecyclerView.setLayoutManager(manager);


                        if (allPictureList.size() == 0) {
                            return;
                        }
                        ArrayList<Folder> folderList = new ArrayList<>();
                        Set<String> folderSet = folderMap.keySet();
                        for (Iterator it = folderSet.iterator(); it.hasNext(); ) {
                            Folder folder = new Folder();
                            String folderName = (String) it.next();
                            ArrayList<Picture> pictureList = folderMap.get(folderName);

                            Collections.sort(pictureList);

                            folder.name = folderName;
                            folder.coverPath = pictureList.get(0).path;
                            folder.count = pictureList.size();
                            folderList.add(folder);
                        }

                        folderRecyclerView.setAdapter(new FolderAdapter(ImagePickerActivity.this, folderList, selectedFolder, folderRecyclerView));
                    }
                });

            }
        }).start();
    }

    private ArrayList<Picture> getImage() {
        Cursor cursor;
        try {
            ContentResolver cr = getContentResolver();
            cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_ADDED}, null, null, null);
        } catch (Exception e) {
            return null;
        }
        ArrayList<Picture> pictureList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Picture picture = new Picture();
            int path_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int date_added_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
            picture.path = cursor.getString(path_index);
            picture.dateAdded = cursor.getLong(date_added_index);
            pictureList.add(picture);
        }
        cursor.close();
        return pictureList;
    }

    private String getFolderName(String path) {
        String pathArr[] = path.split(File.separator);
        String folderName = pathArr[pathArr.length - 2];
        return folderName;
    }

    public void update(String selectedFolder, boolean containCamera) {
        if (folderRecyclerView.getVisibility() == View.VISIBLE) {
            hide();
        }
        this.selectedFolder = selectedFolder;
        ArrayList<Picture> selectedList = folderMap.get(selectedFolder);
        adapter.update(selectedList, containCamera);
        folderName.setText(selectedFolder);
    }

    private void show() {
        folderRecyclerView.setVisibility(View.VISIBLE);
        ObjectAnimator anim = ObjectAnimator
                .ofFloat(folderRecyclerView, "hwb", 0.0F, 1.0F)
                .setDuration(400);
        anim.start();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float cVal = (Float) animation.getAnimatedValue();
                int height = (int) (cVal * DensityUtil.dip2px(ImagePickerActivity.this, 400));
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
                params.addRule(RelativeLayout.ABOVE, R.id.layout_bottom);
                folderRecyclerView.setLayoutParams(params);
                backLayout.getForeground().setAlpha((int) (200 * cVal));
            }
        });
    }

    private void hide() {
        ObjectAnimator anim = ObjectAnimator
                .ofFloat(folderRecyclerView, "hwb", 1.0F, 0.0F)
                .setDuration(400);
        anim.start();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float cVal = (Float) animation.getAnimatedValue();
                int height = (int) (cVal * DensityUtil.dip2px(ImagePickerActivity.this, 400));
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
                params.addRule(RelativeLayout.ABOVE, R.id.layout_bottom);
                folderRecyclerView.setLayoutParams(params);
                backLayout.getForeground().setAlpha((int) (200 * cVal));
                if (cVal == 0) {
                    folderRecyclerView.setVisibility(View.GONE);
                }
            }

        });
    }

    public void selectDone(String path) {
        //if savePath is null,do not crop
        if (TextUtils.isEmpty(savePath)) {
            Intent intent = new Intent();
            intent.putExtra(Picker.IMAGEPATH_EXTRA, path);
            setResult(RESULT_OK, intent);
            finish();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(Picker.IMAGEPATH_EXTRA, path);
        bundle.putInt(Picker.ASPECTRATIOX_EXTRA, aspectRatioX);
        bundle.putInt(Picker.ASPECTRATIOY_EXTRA, aspectRatioY);
        bundle.putString(Picker.SAVEPATH_EXTRA, savePath);
        Intent intent = new Intent(this, ImageCropActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE_CROP);
    }

    public void callCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String fileName = System.currentTimeMillis() + ".jpg";
        capturedPhotoPath = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + fileName;
        File image_file = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
        Uri imageUri = Uri.fromFile(image_file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CODE_SHOT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CROP) {
            setResult(Activity.RESULT_OK, data);
            finish();
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_SHOT) {
            selectDone(capturedPhotoPath);
        }

    }
}
