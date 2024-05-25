package com.example.readbookapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.readbookapplication.adapter.NovelInfoChapterAdapter;
import com.example.readbookapplication.api.NovelFetcher;
import com.example.readbookapplication.pojo.Chapter;
import com.example.readbookapplication.pojo.Novel;
import com.example.readbookapplication.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class NovelInfoActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, NovelFetcher.NovelFetchListener {

    private TextView topTitleTextView, titleTextView, authorTextView, descriptionTextView;
    private ImageView bookCoverImageView, novelTopBackImg, novelInfoSortImageView;
    private ListView chapterListView;
    private Button novelInfoStartReadButton, novelInfoAddToBookshelfButton;
    private NovelInfoChapterAdapter novelInfoChapterAdapter;
    private List<Chapter> infoChapterList;

    private boolean nowSort = true;
    private boolean isNovelInBookshelf = false;

    private Novel receivedNovel;

    private NovelFetcher novelFetcher;
    private DatabaseHelper databaseHelper;

    @Override
    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);
        Intent intent = getIntent();
        if (intent.getExtras() == null && intent.getExtras() == null) {
            Toast.makeText(this, "当前书籍信息错误", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            databaseHelper = new DatabaseHelper(this);

            // 获取控件
            novelInfoStartReadButton = findViewById(R.id.novelInfoStartReadButton);
            novelInfoAddToBookshelfButton = findViewById(R.id.novelInfoAddToBookshelfButton);
            novelInfoSortImageView = findViewById(R.id.novelInfoSortImageView);
            chapterListView = findViewById(R.id.novelInfoChapterListView);
            novelTopBackImg = findViewById(R.id.novelTopBackImg);
            bookCoverImageView = findViewById(R.id.novelInfoCoverImageView);
            topTitleTextView = findViewById(R.id.novelInfoTopTitleTextView);
            titleTextView = findViewById(R.id.novelInfoTitleView);
            authorTextView = findViewById(R.id.novelInfoAuthorTextView);
            descriptionTextView = findViewById(R.id.novelInfoDescriptionTextView);


            // 初始化适配器
            novelInfoChapterAdapter = new NovelInfoChapterAdapter(this, new ArrayList<>());
            chapterListView.setOnItemClickListener(this);
            chapterListView.setAdapter(novelInfoChapterAdapter);

            // 监听正序逆序按钮
            novelInfoStartReadButton.setOnClickListener(this);
            novelInfoAddToBookshelfButton.setOnClickListener(this);
            novelInfoSortImageView.setOnClickListener(this);

            Bundle receivedBundle = intent.getExtras();
            // 从 Bundle 中获取 Parcelable 对象
            receivedNovel = receivedBundle.getParcelable("novel");
            novelTopBackImg.setOnClickListener(this);

            // 设置控件内容
            assert receivedNovel != null;
            Glide.with(this).load(Objects.requireNonNull(receivedNovel).getImg_url()).into(bookCoverImageView);
            topTitleTextView.setText(receivedNovel.getTitle());
            titleTextView.setText(receivedNovel.getTitle());
            authorTextView.setText("作者：" + receivedNovel.getAuthor());
            descriptionTextView.setText(receivedNovel.getDescription());

            // 检查是否在书架中
            if (isNovelInDatabase(receivedNovel)) {
                isNovelInBookshelf = true;
                novelInfoAddToBookshelfButton.setText("移除书架");
            }

            // 初始化
            Toast.makeText(this, "正在加载章节列表…", Toast.LENGTH_SHORT).show();
            novelFetcher = new NovelFetcher(this, this);
            novelFetcher.fetchNovelInfo(receivedNovel.getUrl_list());
        }
    }

    private boolean isNovelInDatabase(Novel novel) {
        Novel novelInDatabase = databaseHelper.getNovelById(novel.getUrl_list());
        return novelInDatabase != null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.novelTopBackImg) {
            finish();
        } else if (v.getId() == R.id.novelInfoSortImageView) {
            String nowSortText = nowSort ? "倒序" : "正序";
            Toast.makeText(this, "切换至" + nowSortText, Toast.LENGTH_SHORT).show();
            Collections.reverse(infoChapterList);
            novelInfoChapterAdapter.clear();
            novelInfoChapterAdapter.addAll(infoChapterList);
            if (nowSort) {
                novelInfoSortImageView.setImageResource(R.drawable.normal_sort);
            } else {
                novelInfoSortImageView.setImageResource(R.drawable.back_sort);
            }
            nowSort = !nowSort;
        } else if (v.getId() == R.id.novelInfoStartReadButton) {
            Intent intent = new Intent(this, ChapterActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("chapter", infoChapterList.get(0));
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v.getId() == R.id.novelInfoAddToBookshelfButton) {
            // 添加至书架或移出书架
            Bundle receivedBundle = getIntent().getExtras();
            // 从 Bundle 中获取 Parcelable 对象
            assert receivedBundle != null;
            Novel receivedNovel = receivedBundle.getParcelable("novel");
            assert receivedNovel != null;
            boolean result;
            if (isNovelInBookshelf) {
                result = databaseHelper.removeFromShelf(receivedNovel.getUrl_list());
            } else {
                // 添加至书架
                result = databaseHelper.addToBookshelf(receivedNovel);
            }
            String buttonText = isNovelInBookshelf ? "加入书架" : "移除书架";
            String toastText = isNovelInBookshelf ? "移除书架" : "加入书架";
            if (result) {
                Toast.makeText(this, "成功" + toastText, Toast.LENGTH_SHORT).show();
                isNovelInBookshelf = !isNovelInBookshelf;
                novelInfoAddToBookshelfButton.setText(buttonText);
            } else {
                Toast.makeText(this, toastText + "失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onNovelListSuccess(List<Novel> novels) {
    }

    @Override
    public void onNovelChapterListSuccess(List<Chapter> originalChapterList) {
        // 更新
        infoChapterList = originalChapterList;
        novelInfoChapterAdapter.clear();
        novelInfoChapterAdapter.addAll(originalChapterList);
        novelInfoChapterAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNovelChapterSuccess(Chapter chapter) {

    }

    @Override
    public void onFailure(String errorMessage) {
        Toast.makeText(this, "获取章节列表失败" + errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 获取选中项的数据
        Chapter selectedChapter = (Chapter) parent.getItemAtPosition(position);

        // 获取前后章节url
        int previousPosition = position - 1;
        if (previousPosition >= 0) {
            Chapter previousChapter = (Chapter) parent.getItemAtPosition(previousPosition);
            selectedChapter.setPbPrev(previousChapter.getUrl());
        } else {
            selectedChapter.setPbPrev(receivedNovel.getUrl_list());
        }
        int nextPosition = position + 1;
        if (nextPosition < infoChapterList.size()) {
            Chapter nextChapter = (Chapter) parent.getItemAtPosition(nextPosition);
            selectedChapter.setPbNext(nextChapter.getUrl());
        } else {
            selectedChapter.setPbNext(receivedNovel.getUrl_list());
        }

        Intent intent = new Intent(this, ChapterActivity.class);
        Bundle bundle = new Bundle();
        selectedChapter.setNovelName(receivedNovel.getTitle());
        selectedChapter.setUrl(selectedChapter.getUrl());
        bundle.putParcelable("chapter", selectedChapter);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
