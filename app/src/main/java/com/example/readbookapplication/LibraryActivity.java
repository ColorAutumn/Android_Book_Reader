package com.example.readbookapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.readbookapplication.adapter.NovelAdapter;
import com.example.readbookapplication.api.NovelFetcher;
import com.example.readbookapplication.pojo.Chapter;
import com.example.readbookapplication.pojo.Novel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener, NavigationBarView.OnItemSelectedListener, NovelFetcher.NovelFetchListener {
    private BottomNavigationView bottomNavigationView;
    private ListView novelListView;
    private NovelAdapter novelAdapter;
    private NovelFetcher novelFetcher;

    private int sortId;
    private int page;
    // 添加一个标志来跟踪是否已经加载过数据
    private boolean isLoading = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        // 初始化底部导航栏 BottomNavigationView，并设置监听器
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.libraryMenuItem);

        // 初始化书籍列表 ListView，并设置监听器
        novelListView = findViewById(R.id.libraryListView);
        novelListView.setOnScrollListener(this);
        novelListView.setOnItemClickListener(this);

        Toast.makeText(this, "正在加载书籍", Toast.LENGTH_SHORT).show();

        // 初始化 NovelsListFetcher，并传入 NovelFetchListener
        novelFetcher = new NovelFetcher(this, this);
        // 在书库 Activity 创建时，立即发起网络请求
        page = 1;
        sortId = 7;
        // 搜索
        novelFetcher.fetchNovelsList(sortId, page);

        // 初始化 NovelAdapter
        novelAdapter = new NovelAdapter(this, new ArrayList<>());

        // 将 NovelAdapter 与 ListView 绑定
        novelListView.setAdapter(novelAdapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.bookshelfMenuItem) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.libraryMenuItem) {
            Toast.makeText(this, "你已经在书库页了", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public void onNovelListSuccess(List<Novel> novels) {
        // 更新 NovelAdapter 的数据
        novelAdapter.addAll(novels);
        novelAdapter.notifyDataSetChanged();
        // 更新 isLoading 的状态
        isLoading = false;
    }

    @Override
    public void onNovelChapterListSuccess(List<Chapter> chapterList) {

    }

    @Override
    public void onNovelChapterSuccess(Chapter chapter) {

    }

    @Override
    public void onFailure(String errorMessage) {
        // 显示请求失败的提示
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        isLoading = false;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // 当滚动状态改变时触发
        switch (scrollState) {
            case SCROLL_STATE_IDLE:
                // 滚动停止时触发
                break;
            case SCROLL_STATE_TOUCH_SCROLL:
                // 正在触摸滚动时触发
                break;
            case SCROLL_STATE_FLING:
                // 手指快速滑动时触发
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // 滚动时触发
        int lastVisibleItem = firstVisibleItem + visibleItemCount;
        // 当滑动到底部时，加载更多数据
        if (lastVisibleItem == totalItemCount && totalItemCount > 0 && !isLoading) {
            // 触发加载更多的操作，比如调用一个加载数据的方法
            Toast.makeText(this, "正在加载更多书籍", Toast.LENGTH_SHORT).show();
            page++;
            isLoading = true;
            novelFetcher.fetchNovelsList(sortId, page);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 获取选中项的数据
        Novel selectedNovel = (Novel) parent.getItemAtPosition(position);

        // 创建 Intent，并将书籍 ID 传递给下一个页面
        Intent intent = new Intent(this, NovelInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("novel", selectedNovel);
        intent.putExtras(bundle);

        // 启动下一个页面
        startActivity(intent);
    }
}
