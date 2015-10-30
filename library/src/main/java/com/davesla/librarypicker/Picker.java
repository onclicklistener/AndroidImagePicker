package com.davesla.librarypicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.davesla.librarypicker.activity.ImagePickerActivity;

/**
 * Created by hwb on 15/7/7.
 */
public class Picker {
    public static final String IMAGEPATH_EXTRA = "IMAGEPATH_EXTRA";

    public static final String ASPECTRATIOX_EXTRA = "ASPECTRATIOX_EXTRA";
    public static final String ASPECTRATIOY_EXTRA = "ASPECTRATIOY_EXTRA";
    public static final String SAVEPATH_EXTRA = "SAVEPATH_EXTRA";

    /**
     * just select a picture,not crop,with default color of actionbar and statusbar
     *
     * @param activity    the activity which start the pick action
     * @param requestCode
     */
    public static void pick(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * pick and crop a image with aspect ratio,not specify the color of actionbar and statusbar
     * @param activity the activity which start the pick action
     * @param requestCode
     * @param aspectRatioX
     * @param aspectRatioY
     * @param savePath path the cropped image save to
     */
    public static void pickAndCrop(Activity activity, int requestCode,int aspectRatioX ,int aspectRatioY,String savePath) {
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(ASPECTRATIOX_EXTRA, aspectRatioX);
        bundle.putInt(ASPECTRATIOY_EXTRA, aspectRatioY);
        bundle.putString(SAVEPATH_EXTRA,savePath);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, requestCode);
    }

    //TODO
    public static void pickImages() {

    }

    //TODO
    public static void pickVideo() {

    }

    //TODO
    public static void pickVideos() {

    }
}
