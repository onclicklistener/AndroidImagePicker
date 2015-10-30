package com.davesla.librarypicker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.davesla.librarypicker.R;
import com.davesla.librarypicker.activity.ImagePickerActivity;
import com.davesla.librarypicker.bean.Picture;

import java.util.ArrayList;


public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_IMAGE = 0;
    private static final int TYPE_CAMERA = 1;

    private ArrayList<Picture> pictureList = new ArrayList<>();
    private ImagePickerActivity context;
    private RecyclerView recyclerView;

    //是否允许使用摄像头
    private boolean containCamera = false;

    public ImageAdapter(ImagePickerActivity context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
    }

    public void update(ArrayList<Picture> pictureList, boolean containCamera) {
        this.containCamera = containCamera;
        this.pictureList = pictureList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_IMAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.picker_list_item_gallery, parent, false);
            RecyclerView.ViewHolder holder = new ImageViewHolder(view);
            return holder;
        }
        View view = LayoutInflater.from(context).inflate(R.layout.picker_list_item_gallery_camera, parent, false);
        RecyclerView.ViewHolder holder = new CameraViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_CAMERA ) {
            return;
        }
        String url;
        if (containCamera) {
            url = pictureList.get(position - 1).path;
        } else {
            url = pictureList.get(position).path;
        }
        ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
        context.loadLocalImage(imageViewHolder.image, url);
    }

    @Override
    public int getItemViewType(int position) {
        if (containCamera && position == 0) {
            return TYPE_CAMERA;
        }
        return TYPE_IMAGE;
    }

    @Override
    public int getItemCount() {
        if (containCamera) {
            return pictureList.size() + 1;
        }
        return pictureList.size();
    }

    class CameraViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CameraViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            context.callCamera();
        }
    }

    class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;

        public ImageViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = recyclerView.getChildAdapterPosition(v);
            String path;
            if (containCamera) {
                path = pictureList.get(position - 1).path;
            } else {
                path = pictureList.get(position).path;
            }
            context.selectDone(path);
        }
    }
}