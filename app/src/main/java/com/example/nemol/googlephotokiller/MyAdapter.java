package com.example.nemol.googlephotokiller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by nemol on 05.10.2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    public interface OnItemClickListener {
        void onItemClick(String item);
    }


    private ArrayList<String> galleryList;
    private final OnItemClickListener listener;


    public MyAdapter(ArrayList<String> galleryList, OnItemClickListener listener) {
        this.galleryList = galleryList;
        this.listener = listener;

    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder viewHolder, int i) {
        viewHolder.bind(galleryList.get(i), listener);
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView img;

        ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.img);
        }

        void bind(final String item, final MyAdapter.OnItemClickListener listener) {
            Picasso.with(itemView.getContext()).load(item).centerCrop().resize(540,540).into(img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }





}