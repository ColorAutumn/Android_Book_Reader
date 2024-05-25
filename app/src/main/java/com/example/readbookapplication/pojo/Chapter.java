package com.example.readbookapplication.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Chapter implements Parcelable {
    private String title;
    private String novelName;
    private String url;
    private String content;
    private String pbPrev;
    private String pbNext;

    public Chapter() {
    }

    public Chapter(String title, String novelName, String url, String content, String pbPrev, String pbNext) {
        this.title = title;
        this.novelName = novelName;
        this.url = url;
        this.content = content;
        this.pbPrev = pbPrev;
        this.pbNext = pbNext;
    }

    protected Chapter(Parcel in) {
        title = in.readString();
        novelName = in.readString();
        url = in.readString();
        content = in.readString();
        pbPrev = in.readString();
        pbNext = in.readString();
    }

    public static final Creator<Chapter> CREATOR = new Creator<Chapter>() {
        @Override
        public Chapter createFromParcel(Parcel in) {
            return new Chapter(in);
        }

        @Override
        public Chapter[] newArray(int size) {
            return new Chapter[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNovelName() {
        return novelName;
    }

    public void setNovelName(String novelName) {
        this.novelName = novelName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPbPrev() {
        return pbPrev;
    }

    public void setPbPrev(String pbPrev) {
        this.pbPrev = pbPrev;
    }

    public String getPbNext() {
        return pbNext;
    }

    public void setPbNext(String pbNext) {
        this.pbNext = pbNext;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "title='" + title + '\'' +
                ", novelName='" + novelName + '\'' +
                ", url='" + url + '\'' +
                ", pbPrev='" + pbPrev + '\'' +
                ", pbNext='" + pbNext + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(novelName);
        dest.writeString(url);
        dest.writeString(content);
        dest.writeString(pbPrev);
        dest.writeString(pbNext);
    }
}
