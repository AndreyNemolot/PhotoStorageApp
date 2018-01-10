package com.example.nemol.googlephotokiller.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nemol.googlephotokiller.Model.Album;
import com.example.nemol.googlephotokiller.R;

import java.util.ArrayList;

/**
 * Created by nemol on 24.12.2017.
 */

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder>{



    public interface OnItemClickListener {
        void onItemClick(Album item);
    }

    public interface OnLongClickListener {
        void onLongClick(Album item);
    }


    private ArrayList<Album> albumList;
    private final AlbumAdapter.OnItemClickListener listener;
    private final AlbumAdapter.OnLongClickListener longListener;

    public AlbumAdapter(ArrayList<Album> albumList, AlbumAdapter.OnItemClickListener listener, AlbumAdapter.OnLongClickListener longListener) {
        this.albumList = albumList;
        this.listener = listener;
        this.longListener = longListener;

    }

    @Override
    public AlbumAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.album_cell_layout, viewGroup, false);
        return new AlbumAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumAdapter.ViewHolder viewHolder, int i) {
        viewHolder.bind(albumList.get(i), listener, longListener);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTitle;

        ViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
        }

        void bind(final Album item, final AlbumAdapter.OnItemClickListener listener, final AlbumAdapter.OnLongClickListener longListener) {
            tvTitle.setText(item.getAlbumTitle());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return false;
                }
            });
        }
    }
}