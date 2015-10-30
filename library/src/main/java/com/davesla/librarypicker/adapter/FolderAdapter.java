package com.davesla.librarypicker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.davesla.librarypicker.R;
import com.davesla.librarypicker.activity.ImagePickerActivity;
import com.davesla.librarypicker.bean.Folder;

import java.util.ArrayList;

/**
 * Created by hwb on 15/6/30.
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {
    private RecyclerView recyclerView;
    private String selectedFolder;
    private ImagePickerActivity context;
    private ArrayList<Folder> folderList = new ArrayList<>();

    public FolderAdapter(ImagePickerActivity context, ArrayList<Folder> folderList, String selectedFolder, RecyclerView recyclerView) {
        this.context = context;
        this.folderList = folderList;
        this.selectedFolder = selectedFolder;
        this.recyclerView = recyclerView;
    }

    @Override
    public FolderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.picker_list_item_folder, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(FolderAdapter.ViewHolder holder, int position) {
        Folder folder = folderList.get(position);
        holder.folderName.setText(folder.name);
        holder.imageCount.setText(folder.count + context.getResources().getString(R.string.picker_unit));
        context.loadLocalImage(holder.cover, folder.coverPath);
        if (folder.name.equals(selectedFolder)) {
            holder.check.setVisibility(View.VISIBLE);
        } else {
            holder.check.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return folderList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView cover, check;
        TextView folderName, imageCount;

        public ViewHolder(View itemView) {
            super(itemView);
            cover = (ImageView) itemView.findViewById(R.id.cover);
            check = (ImageView) itemView.findViewById(R.id.check);
            folderName = (TextView) itemView.findViewById(R.id.tv_folder_name);
            imageCount = (TextView) itemView.findViewById(R.id.tv_image_count);

            check.setColorFilter(context.getColorPrimary());

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = recyclerView.getChildAdapterPosition(v);
            Folder folder = folderList.get(position);
            selectedFolder = folder.name;
            notifyDataSetChanged();
            if (position == 0) {
                context.update(folder.name, true);
            } else {
                context.update(folder.name, false);
            }
        }
    }

}
