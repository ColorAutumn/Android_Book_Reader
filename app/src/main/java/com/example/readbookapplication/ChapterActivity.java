package com.example.readbookapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.readbookapplication.api.NovelFetcher;
import com.example.readbookapplication.pojo.Chapter;
import com.example.readbookapplication.pojo.Novel;
import com.example.readbookapplication.utils.DatabaseHelper;

import java.util.List;

public class ChapterActivity extends AppCompatActivity implements NovelFetcher.NovelFetchListener, View.OnClickListener {

    private ImageView readingTopBackImg;
    private TextView readingChapterTitleTextView, readingContentTextView;
    private ScrollView readingScrollView;
    private Button readingPrevChapterButton, readingNextChapterButton;
    private Chapter receivedChapter;
    private String isPbPrev, isPbNext;
    private NovelFetcher novelFetcher;
    private final int preCacheNum = 2;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        Intent intent = getIntent();
        if (intent.getExtras() == null && intent.getExtras() == null) {
            Toast.makeText(this, "当前书籍信息错误", Toast.LENGTH_SHORT).show();
            finish();

        } else {
            Bundle receivedBundle = intent.getExtras();
            // 从 Bundle 中获取 Parcelable 对象
            receivedChapter = receivedBundle.getParcelable("chapter");
            assert receivedChapter != null;

            readingScrollView = findViewById(R.id.readingScrollView);
            readingTopBackImg = findViewById(R.id.readingTopBackImg);
            readingChapterTitleTextView = findViewById(R.id.readingChapterTitleTextView);
            readingContentTextView = findViewById(R.id.readingContentTextView);
            readingPrevChapterButton = findViewById(R.id.readingPrevChapterButton);
            readingNextChapterButton = findViewById(R.id.readingNextChapterButton);
            databaseHelper = new DatabaseHelper(this);

            readingPrevChapterButton.setOnClickListener(this);
            readingNextChapterButton.setOnClickListener(this);
            readingTopBackImg.setOnClickListener(this);

            Toast.makeText(this, "正在加载章节内容", Toast.LENGTH_SHORT).show();
            novelFetcher = new NovelFetcher(this, this);
            novelFetcher.fetchNovelChapter(receivedChapter, receivedChapter.getUrl(), databaseHelper, false, preCacheNum);
        }
    }

    @Override
    public void onNovelListSuccess(List<Novel> novels) {

    }

    @Override
    public void onNovelChapterListSuccess(List<Chapter> chapterList) {

    }

    @Override
    public void onNovelChapterSuccess(Chapter chapter) {
        readingScrollView.scrollTo(0, 0);
        receivedChapter = chapter;
        readingChapterTitleTextView.setText(chapter.getTitle());
        isPbPrev = chapter.getPbPrev();
        isPbNext = chapter.getPbNext();
        readingContentTextView.setText(Html.fromHtml(chapter.getContent(), Html.FROM_HTML_MODE_LEGACY));
    }

    @Override
    public void onFailure(String errorMessage) {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.readingTopBackImg) {
            finish();
        } else if (v.getId() == R.id.readingPrevChapterButton) {
            if (isPbPrev != null && isPbPrev.endsWith("html")) {
                Toast.makeText(this, "正在加载上一章，请稍等", Toast.LENGTH_SHORT).show();
                novelFetcher.fetchNovelChapter(receivedChapter, receivedChapter.getPbPrev(), databaseHelper, false, preCacheNum);
            } else {
                Toast.makeText(this, "已经是第一章了", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.readingNextChapterButton) {
            if (isPbNext != null && isPbNext.endsWith("html")) {
                Toast.makeText(this, "正在加载下一章，请稍等", Toast.LENGTH_SHORT).show();
                novelFetcher.fetchNovelChapter(receivedChapter, receivedChapter.getPbNext(), databaseHelper, false, preCacheNum);
            } else {
                Toast.makeText(this, "已经是最后一章了", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
