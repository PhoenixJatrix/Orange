package com.nullinnix.orange;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class AudioModel implements Parcelable {
    String path;
    String title;
    String duration;
    String album;
    String displayName;
    String documentID;

    public AudioModel(String path, String title, String duration, String album, String displayName, String documentID) {
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.album = album;
        this.displayName = displayName;
        this.documentID = documentID;
    }

    protected AudioModel(Parcel in) {
        path = in.readString();
        title = in.readString();
        duration = in.readString();
        album = in.readString();
        displayName = in.readString();
        documentID = in.readString();
    }

    public static final Creator<AudioModel> CREATOR = new Creator<AudioModel>() {
        @Override
        public AudioModel createFromParcel(Parcel in) {
            return new AudioModel(in);
        }

        @Override
        public AudioModel[] newArray(int size) {
            return new AudioModel[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAlbum(){
        return album;
    }

    public void setAlbum(){
        this.album = album;
    }

    public String getDisplayName(){
        return displayName;
    }

    public String getDocumentID(){
        return documentID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(title);
        dest.writeString(duration);
        dest.writeString(album);
        dest.writeString(displayName);
        dest.writeString(documentID);
    }
}