package com.davesla.librarypicker.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

import com.davesla.librarypicker.cache.ImageFileCache;
import com.davesla.librarypicker.cache.ImageMemoryCache;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hwb on 15/4/21.
 */
public class ImageLoader {

    private ExecutorService threadPool = Executors.newFixedThreadPool(2);

    public static final ImageLoader loader = new ImageLoader();
    public static Handler mainThread = new Handler();

    private ImageFileCache fileCache;
    private ImageMemoryCache memoryCache;

    private LinkedList<Task> taskList;
    private Context context;

    private ImageLoader() {

    }

    public void init(Context context) {
        this.context = context;
        fileCache = new ImageFileCache(context);
        memoryCache = new ImageMemoryCache(context);
        taskList = new LinkedList<>();
    }

    public void loadImageThumb(ImageView imageView, String path) {
        synchronized (taskList) {
            taskList.addLast(new Task(imageView, path));
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
        String path;

        public Task(ImageView imageView, String path) {
            this.imageView = imageView;
            this.path = path;
        }

        @Override
        public void run() {
            //获取缩略图
            Bitmap bitmap = memoryCache.getBitmapFromCache(path);
            if (bitmap == null) {
                bitmap = fileCache.getImage(path);
                if (bitmap == null) {
                    bitmap =  getImageThumbnail(context, path);
                    fileCache.saveBitmap(bitmap, path);
                    memoryCache.addBitmapToCache(path, bitmap);
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

    public static Bitmap getImageThumbnail(Context context, String path) {
        Bitmap bitmap = null;

        return bitmap;
    }


}
