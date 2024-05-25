package com.example.readbookapplication.api.callback;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.readbookapplication.api.NovelFetcher;
import com.example.readbookapplication.pojo.Chapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NovelInfoCallback implements Callback<ResponseBody> {

    private final NovelFetcher novelFetcher;
    private final String TAG = "NovelInfoCallback";

    public NovelInfoCallback(NovelFetcher novelFetcher) {
        this.novelFetcher = novelFetcher;
    }

    @Override
    public void onResponse(@NonNull Call<ResponseBody> call, Response<ResponseBody> response) {
        if (response.isSuccessful()) {
            try {
                assert response.body() != null;

                String novelInfoHtml = response.body().string();

                Document document = Jsoup.parse(novelInfoHtml);
                Elements chapterListElements = document.select("div.listmain dl dd a");

                List<Chapter> chapters = new ArrayList<>();

                for (Element chapterElement : chapterListElements) {
                    String chapterUrl = chapterElement.attr("href");
                    String chapterTitle = chapterElement.text();
                    Chapter chapter = new Chapter();
                    chapter.setUrl(chapterUrl);
                    chapter.setTitle(chapterTitle);
                    chapters.add(chapter);
                }

                Log.d(TAG, "已找到小说章节列表，第一章: " + chapters.get(0).getTitle());

                novelFetcher.notifyNovelChapterListSuccess(chapters);
            } catch (IOException e) {
                Log.w(TAG, "获取小说章节列表成功，但处理返回Body时发生了错误: " + e.getMessage());
            }
        } else {
            // 处理请求失败的情况
            Log.w(TAG, "获取小说章节列表失败: " + response.errorBody());
            novelFetcher.notifyFailure("获取小说章节列表失败，请求码：" + response.code());
        }
    }


    @Override
    public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
        // 处理请求失败的情况
        Log.e(TAG, "onFailure: " + t.getMessage());
        novelFetcher.notifyFailure("Request failed. Error: " + t.getMessage());
    }
}