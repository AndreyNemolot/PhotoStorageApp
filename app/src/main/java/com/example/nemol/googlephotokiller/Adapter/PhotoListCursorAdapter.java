package com.example.nemol.googlephotokiller.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.nemol.googlephotokiller.BitmapTransform;
import com.example.nemol.googlephotokiller.Model.Photo;
import com.example.nemol.googlephotokiller.R;
import com.squareup.picasso.Picasso;

/**
 * Created by nemol on 02.03.2018.
 */

public class PhotoListCursorAdapter extends CursorRecyclerViewAdapter<PhotoListCursorAdapter.ViewHolder> {

    private PhotoListCursorAdapter.Listener listener;
    private final String PHOTO_PATH = "file:///sdcard/GooglePhotoKiller/";
    private  int MAX_WIDTH = 420;
    private  int MAX_HEIGHT = 420;


    public interface Listener {
        void onClick(Photo photo);

        void onLongClick(Photo photo);
    }

    public PhotoListCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private CardView cardView;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.img);
            cardView = view.findViewById(R.id.card_view);
        }
    }

    public void setListener(PhotoListCursorAdapter.Listener listener) {
        this.listener = listener;
    }

    @Override
    public PhotoListCursorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_cell_layout, parent, false);
        return new PhotoListCursorAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PhotoListCursorAdapter.ViewHolder viewHolder, Cursor cursor) {
        cursor.moveToPosition(viewHolder.getAdapterPosition());
        CardView cardView = viewHolder.cardView;
        Photo myListItem = Photo.fromCursor(cursor);

        final String name = myListItem.getPhotoLink();
        int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));
        Picasso.with(viewHolder.imageView.getContext()).load(name).transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                .resize(size, size).centerCrop()
                .into(viewHolder.imageView);

        final Photo photo = new Photo();
        photo.setPhotoId(Integer.parseInt(cursor.getString(0)));
        photo.setPhotoLink(cursor.getString(1));
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClick(photo);
                }
            }
        });

        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (listener != null) {
                    listener.onLongClick(photo);
                }
                return false;
            }
        });
    }
}