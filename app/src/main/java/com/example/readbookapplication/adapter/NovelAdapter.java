package com.example.readbookapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.readbookapplication.R;
import com.example.readbookapplication.pojo.Novel;

import java.util.List;
import java.util.Objects;

public class NovelAdapter extends ArrayAdapter<Novel> {

    private Context context;

    public NovelAdapter(Context context, List<Novel> novels) {
        super(context, 0, novels);
        this.context = context;
    }

    @NonNull
    @Override
    @SuppressLint("SetTextI18n")
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_novel_simple, parent, false);
        }

        Novel currentNovel = getItem(position);

        // 获取视图中的控件
        ImageView coverImageView = itemView.findViewById(R.id.coverImageView);
        TextView titleTextView = itemView.findViewById(R.id.titleTextView);
        TextView authorTextView = itemView.findViewById(R.id.authorTextView);
        TextView descriptionTextView = itemView.findViewById(R.id.descriptionTextView);

        // 设置封面图片（使用Glide等图片加载库）
        Glide.with(context).load(Objects.requireNonNull(currentNovel).getImg_url()).into(coverImageView);

        // 设置标题、作者、简介等信息
        titleTextView.setText(Objects.requireNonNull(currentNovel).getTitle());
        authorTextView.setText("作者：" + currentNovel.getAuthor());
        descriptionTextView.setText(currentNovel.getDescription());

        return itemView;
    }
}
