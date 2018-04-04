package com.example.nemol.googlephotokiller.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nemol.googlephotokiller.Model.Album;
import com.example.nemol.googlephotokiller.R;

/**
 * Created by nemol on 02.03.2018.
 */

public class AlbumListCursorAdapter extends CursorRecyclerViewAdapter<AlbumListCursorAdapter.ViewHolder> {

    private Listener listener;


    public interface Listener {
        void onClick(Album album);

        void onLongClick(Album album);
    }

    public AlbumListCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        CardView cardView;

        ViewHolder(View view) {
            super(view);
            mTextView = view.findViewById(R.id.tvTitle);
            cardView = view.findViewById(R.id.card_view);
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_cell_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        cursor.moveToPosition(viewHolder.getAdapterPosition());
        CardView cardView = viewHolder.cardView;
        Album myListItem = Album.fromCursor(cursor);
        viewHolder.mTextView.setText(myListItem.getAlbumTitle());
        final Album album = new Album();
        album.setAlbumId(Integer.parseInt(cursor.getString(0)));
        album.setAlbumTitle(cursor.getString(1));
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClick(album);
                }
            }
        });

        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (listener != null) {
                    listener.onLongClick(album);
                }
                return true;
            }
        });

    }
}