package com.example.nemol.googlephotokiller.Model;

/**
 * Created by nemol on 23.12.2017.
 */

public class Photo {

    private int photoId;
    private int albumId;
    private String photoLink;

    public Photo(int photoId, int albumId, String photoLink) {
        this.photoId = photoId;
        this.albumId = albumId;
        this.photoLink = photoLink;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }


}
