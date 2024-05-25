package com.example.readbookapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.readbookapplication.R;
import com.example.readbookapplication.pojo.Chapter;

import java.util.List;

public class NovelInfoChapterAdapter extends ArrayAdapter<Chapter> {
    private Context context;

    public NovelInfoChapterAdapter(Context context, List<Chapter> novels) {
        super(context, 0, novels);
        this.context = context;
    }

    @NonNull
    @Override
    @SuppressLint("SetTextI18n")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_novel_info_chapter, parent, false);
        }

        Chapter currentChapter = getItem(position);

        TextView novelInfoChapterTitleTextView = itemView.findViewById(R.id.novelInfoChapterTitleTextView);

        assert currentChapter != null;
        novelInfoChapterTitleTextView.setText(currentChapter.getTitle());
        return itemView;
    }
}
