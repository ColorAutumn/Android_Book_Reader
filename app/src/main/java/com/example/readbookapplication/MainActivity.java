package com.example.readbookapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.readbookapplication.adapter.NovelAdapter;
import com.example.readbookapplication.pojo.Novel;
import com.example.readbookapplication.utils.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, NavigationBarView.OnItemSelectedListener {
    private BottomNavigationView bottomNavigationView;
    private ListView bookshelfListView;
    private TextView emptyShelfText;
    private NovelAdapter novelAdapter;
    private List<Novel> novels;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.bookshelfMenuItem);

        emptyShelfText = findViewById(R.id.emptyShelfText);
        dbHelper = new DatabaseHelper(this);
        bookshelfListView = findViewById(R.id.bookshelfListView);

        // 读取数据
        novels = dbHelper.getAllNovels();

        // 使用 ArrayAdapter 将数据设置到 ListView
        novelAdapter = new NovelAdapter(this, new ArrayList<>());
        bookshelfListView.setAdapter(novelAdapter);
        bookshelfListView.setOnItemClickListener(this);

        visibility();
    }

    private void visibility() {
        if (novels.isEmpty()) {
            // 书架为空，显示提示
            emptyShelfText.setVisibility(View.VISIBLE);
            bookshelfListView.setVisibility(View.GONE);
        } else {
            // 书架不为空，显示数据
            emptyShelfText.setVisibility(View.GONE);
            bookshelfListView.setVisibility(View.VISIBLE);
            novelAdapter.clear();
            novelAdapter.addAll(novels);
            novelAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 读取数据
        bottomNavigationView.setSelectedItemId(R.id.bookshelfMenuItem);
        novels = dbHelper.getAllNovels();
        visibility();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.libraryMenuItem) {
            Intent intent = new Intent(this, LibraryActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.bookshelfMenuItem) {
            Toast.makeText(this, "你已经在书架页了", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
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
