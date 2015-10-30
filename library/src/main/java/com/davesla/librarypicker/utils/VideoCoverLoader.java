package com.davesla.librarypicker.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.davesla.librarypicker.R;
import com.davesla.librarypicker.cache.ImageFileCache;
import com.davesla.librarypicker.cache.ImageMemoryCache;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hwb on 15/4/21.
 */
public class VideoCoverLoader {

    private ExecutorService threadPool = Executors.newFixedThreadPool(2);

    public static final VideoCoverLoader loader = new VideoCoverLoader();
    public static Handler mainThread = new Handler();

    private ImageFileCache fileCache;
    private ImageMemoryCache memoryCache;

    private LinkedList<Task> taskList;
    private Context context;

    private VideoCoverLoader() {

    }

    public void init(Context context) {
        this.context = context;
        fileCache = new ImageFileCache(context);
        memoryCache = new ImageMemoryCache(context);
        taskList = new LinkedList<>();
    }

    public void loadVideoThumb(ImageView imageView, long id) {
        synchronized (taskList) {
            taskList.addLast(new Task(imageView, id));
        }
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                Task task;
                synchronized (taskList) {
                    task = taskList.removeLast();
                }
                task.run();
            }
        });
    }

    class Task implements Runnable {
        ImageView imageView;
        long id;

        public Task(ImageView imageView, long id) {
            this.imageView = imageView;
            this.id = id;
        }

        @Override
        public void run() {
            //获取缩略图
            Bitmap bitmap = memoryCache.getBitmapFromCache(id + "");
            if (bitmap == null) {
                bitmap = fileCache.getImage(id + "");
                if (bitmap == null) {
                    bitmap =  getVideoThumbnail(context, id);
                    fileCache.saveBitmap(bitmap, id + "");
                    memoryCache.addBitmapToCache(id + "", bitmap);
                }
            }
            final Bitmap finalBitmap = bitmap;
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(finalBitmap);
                }
            });
        }
    }

    public static Bitmap getVideoThumbnail(Context context, long id) {
        Bitmap bitmap = null;
        bitmap = MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(), id,
                MediaStore.Video.Thumbnails.MINI_KIND, null);
        if (bitmap == null)
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.picker_bg_default_gray);


        return bitmap;
    }


}
