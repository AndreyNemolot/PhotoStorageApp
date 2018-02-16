package com.example.nemol.googlephotokiller.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nemol.googlephotokiller.BitmapTransform;
import com.example.nemol.googlephotokiller.Model.Album;
import com.example.nemol.googlephotokiller.Model.Photo;
import com.example.nemol.googlephotokiller.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nemol on 05.10.2017.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private final String PHOTO_PATH = "file:///data/data/com.example.nemol.googlephotokiller/cache/";
    private  int MAX_WIDTH = 420;
    private  int MAX_HEIGHT = 420;

    public interface OnItemClickListener {
        void onItemClick(String item);
    }

    public interface OnLongClickListener {
        void onLongClick(Photo item);
    }


    private List<Photo> galleryList; // TODO: 25.12.2017 передавать в адаптер фотки а не названия
    private final OnItemClickListener listener;
    private final OnLongClickListener longListener;

    public PhotoAdapter(List<Photo> galleryList, OnItemClickListener listener, OnLongClickListener longListener) {
        this.galleryList = galleryList;
        this.listener = listener;
        this.longListener = longListener;
    }

    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.photo_cell_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoAdapter.ViewHolder viewHolder, int i) {

        viewHolder.bind(galleryList.get(i), listener, longListener);
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;

        ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.img);
        }

        void bind(final Photo item, final PhotoAdapter.OnItemClickListener listener, final PhotoAdapter.OnLongClickListener longListener) {

            String[] link = item.getPhotoLink().split("//");
            final String name = PHOTO_PATH + link[link.length-1];

            int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));
            Picasso.with(itemView.getContext()).load(name).transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                    .resize(size, size).centerCrop()
                    .into(img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(name);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    longListener.onLongClick(item);
                    return true;
                }
            });
        }
    }
}