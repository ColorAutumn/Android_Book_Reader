package com.example.readbookapplication.api.callback;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.readbookapplication.api.NovelFetcher;
import com.example.readbookapplication.pojo.Chapter;
import com.example.readbookapplication.utils.DatabaseHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NovelChapterCallback implements Callback<ResponseBody> {

    private final NovelFetcher novelFetcher;
    private final String chapterUrl;
    private final DatabaseHelper databaseHelper;
    private boolean preCache;
    private int preCacheNum;
    private final String TAG = "NovelChapterCallback";

    public NovelChapterCallback(NovelFetcher novelFetcher, String chapterUrl, DatabaseHelper databaseHelper, boolean preCache, int preCacheNum) {
        this.novelFetcher = novelFetcher;
        this.chapterUrl = chapterUrl;
        this.databaseHelper = databaseHelper;
        this.preCache = preCache;
        this.preCacheNum = preCacheNum;
    }


    @Override
    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
        if (response.isSuccessful()) {
            assert response.body() != null;
            try {
                String novelChapterHtml = response.body().string();
                Document document = Jsoup.parse(novelChapterHtml);

                Elements chapterTitleElements = document.select("#read > div.book.reader > div.content > h1");
                Elements novelNameElements = document.select("#read > div.book.reader > div.path.wap_none > a:nth-child(2)");

                String pbPrev = Objects.requireNonNull(document.getElementById("pb_prev")).attr("href");
                String pbNext = Objects.requireNonNull(document.getElementById("pb_next")).attr("href");

                Element chapterContentElement = document.getElementById("chaptercontent");

                assert chapterContentElement != null;

                String novelName = novelNameElements.get(0).text();
                String title = chapterTitleElements.get(0).text();
                String content = chapterContentElement.toString();
                content = content.replaceAll("<p class=\"readinline\">(.*?)</p>", "");

                Chapter chapter = new Chapter();
                chapter.setUrl(chapterUrl);
                chapter.setNovelName(novelName);
                chapter.setTitle(title);
                chapter.setContent(content);
                chapter.setPbPrev(pbPrev);
                chapter.setPbNext(pbNext);

                Log.d(TAG, "已找到 《" + novelName + "》 的章节内容，标题：《" + title + "》");

                // 判断是否缓存章节内容
                if (!databaseHelper.isChapterInDatabase(chapterUrl)) {
                    Log.d(TAG, "当前章节 《" + title + "》 未缓存，正在缓存章节内容");
                    boolean cacheResult = databaseHelper.addChapter(chapter);
                    if (!cacheResult) {
                        Log.e(TAG, "当前章节 《" + title + "》 缓存失败");
                    }
                }

                if (!preCache) {
                    Log.d(TAG, "当前章节 《" + title + "》 不是预缓存章节，正在通知阅读页面更新内容");
                    novelFetcher.notifyNovelChapterSuccess(chapter);
                    preCache = !preCache;
                }

                if (preCacheNum > 0 && preCache) {
                    preCacheNum--;
                    if (pbPrev.endsWith(".html") && !databaseHelper.isChapterInDatabase(pbPrev)) {
                        Log.d(TAG, "当前章节 《" + title + "》 的上一章节未缓存，正在缓存章节内容");
                        novelFetcher.fetchNovelChapterByUrl(pbPrev, databaseHelper, preCache, preCacheNum);
                    }
                    if (pbNext.endsWith(".html") && !databaseHelper.isChapterInDatabase(pbNext)) {
                        Log.d(TAG, "当前章节 《" + title + "》 的下一章节未缓存，正在缓存章节内容");
                        novelFetcher.fetchNovelChapterByUrl(pbNext, databaseHelper, preCache, preCacheNum);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            // 处理请求失败的情况
            Log.w(TAG, "获取小说章节内容失败: " + response.errorBody());
            novelFetcher.notifyFailure("获取小说章节内容失败，请求码：" + response.code());
        }
    }

    @Override
    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
        // 处理请求失败的情况
        Log.e(TAG, "onFailure: " + t.getMessage());
        novelFetcher.notifyFailure("Request failed. Error: " + t.getMessage());
    }
}
