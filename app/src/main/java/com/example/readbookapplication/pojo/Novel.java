package com.example.readbookapplication.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Novel implements Parcelable {
    private String url_list;
    @SerializedName("articlename")
    private String title;
    private String author;
    @SerializedName("url_img")
    private String img_url;
    @SerializedName("intro")
    private String description;

    public Novel() {
    }

    protected Novel(Parcel in) {
        url_list = in.readString();
        title = in.readString();
        author = in.readString();
        img_url = in.readString();
        description = in.readString();
    }

    public static final Creator<Novel> CREATOR = new Creator<Novel>() {
        @Override
        public Novel createFromParcel(Parcel in) {
            return new Novel(in);
        }

        @Override
        public Novel[] newArray(int size) {
            return new Novel[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "Novel{" +
                "url_list='" + url_list + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", img_url='" + img_url + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public Novel(String url_list, String title, String author, String img_url, String description) {
        this.url_list = url_list;
        this.title = title;
        this.author = author;
        this.img_url = img_url;
        this.description = description;
    }

    public String getUrl_list() {
        return url_list;
    }

    public void setUrl_list(String url_list) {
        this.url_list = url_list;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(url_list);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(img_url);
        dest.writeString(description);
    }
}
