package com.davesla.imagepickerdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.davesla.librarypicker.Picker;

import java.io.File;
import java.util.UUID;

public class MainActivity extends ActionBarActivity {
    private static final int REQUEST_CODE = 200;

    private ImageView image;
    private Button btnPick, btnCrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = (ImageView) findViewById(R.id.image);
        btnPick = (Button) findViewById(R.id.btn_pick);
        btnCrop = (Button) findViewById(R.id.btn_crop);

        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picker.pick(MainActivity.this, REQUEST_CODE);
            }
        });
        btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + "picker" + File.separator;
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdir();
                }
                String savePath = path + UUID.randomUUID().toString() + ".jpg";
                Picker.pickAndCrop(MainActivity.this, REQUEST_CODE, 1, 1, savePath);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            String path = data.getStringExtra(Picker.IMAGEPATH_EXTRA);
            image.setImageBitmap(BitmapFactory.decodeFile(path));
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
