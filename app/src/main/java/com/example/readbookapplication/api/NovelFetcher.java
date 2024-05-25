package com.example.readbookapplication.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.readbookapplication.api.callback.NovelChapterCallback;
import com.example.readbookapplication.api.callback.NovelInfoCallback;
import com.example.readbookapplication.api.callback.NovelsListCallback;
import com.example.readbookapplication.pojo.Chapter;
import com.example.readbookapplication.pojo.Novel;
import com.example.readbookapplication.utils.DatabaseHelper;

import java.util.List;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NovelFetcher {

    public interface NovelFetchListener {
        void onNovelListSuccess(List<Novel> novels);

        void onNovelChapterListSuccess(List<Chapter> chapterList);

        void onNovelChapterSuccess(Chapter chapter);

        void onFailure(String errorMessage);
    }

    private final NovelFetchListener listener;
    private final Context context;
    private final Handler mainHandler;

    public NovelFetcher(NovelFetchListener listener, Context context) {
        this.listener = listener;
        this.context = context;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    private Retrofit createRetrofitInstance() {

        int cacheSize = 10 * 1024 * 1024; // 10 MB
        Cache cache = new Cache(context.getCacheDir(), cacheSize);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        return new Retrofit.Builder()
                .baseUrl("https://www.biqg.cc/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private NovelService createApiService() {
        return createRetrofitInstance().create(NovelService.class);
    }

    public void fetchNovelsList(int sortId, int page) {
        NovelService novelService = createApiService();
        Call<List<Novel>> call = novelService.getNovels(sortId, page);
        call.enqueue(new NovelsListCallback(this));
    }

    public void fetchNovelInfo(String novelId) {
        NovelService novelService = createApiService();
        Call<ResponseBody> call = novelService.getNovelInfo(novelId);
        call.enqueue(new NovelInfoCallback(this));
    }

    public void notifyNovelsListSuccess(final List<Novel> novels) {
        mainHandler.post(() -> {
            if (listener != null) {
                listener.onNovelListSuccess(novels);
            }
        });
    }

    public void notifyNovelChapterListSuccess(final List<Chapter> chapterList) {
        mainHandler.post(() -> {
            if (listener != null) {
                listener.onNovelChapterListSuccess(chapterList);
            }
        });
    }

    public void notifyNovelChapterSuccess(final Chapter chapter) {
        mainHandler.post(() -> {
            if (listener != null) {
                listener.onNovelChapterSuccess(chapter);
            }
        });
    }

    public void notifyFailure(final String errorMessage) {
        mainHandler.post(() -> {
            if (listener != null) {
                listener.onFailure(errorMessage);
            }
        });
    }

    public void fetchNovelChapterByUrl(String chapterUrl, DatabaseHelper databaseHelper, boolean preCache, int preCacheNum) {
        NovelService novelService = createApiService();
        Call<ResponseBody> call = novelService.getChapterContent(chapterUrl);
        call.enqueue(new NovelChapterCallback(this, chapterUrl, databaseHelper, preCache, preCacheNum));
    }

    public void fetchNovelChapter(Chapter chapter, String chapterUrl, DatabaseHelper databaseHelper, boolean preCache, int preCacheNum) {
        if (databaseHelper.isChapterInDatabase(chapterUrl)) {
            Chapter chapterFromDatabase = databaseHelper.getNovelChapter(chapterUrl);
            Log.d("NovelFetcher", "《" + chapter.getNovelName() + "》 的章节 《" + chapterFromDatabase.getTitle() + "》 已缓存，从数据库中读取");
            notifyNovelChapterSuccess(chapterFromDatabase);
            if (preCacheNum > 0 && preCache) {
                preCacheNum--;
                if (chapterFromDatabase.getPbPrev().endsWith(".html") && !databaseHelper.isChapterInDatabase(chapterFromDatabase.getPbPrev())) {
                    Log.d("NovelFetcher", "当前章节 《" + chapterFromDatabase.getTitle() + "》 的上一章节未缓存，正在缓存章节内容");
                    fetchNovelChapterByUrl(chapterFromDatabase.getPbPrev(), databaseHelper, true, preCacheNum);
                }
                if (chapterFromDatabase.getPbNext().endsWith(".html") && !databaseHelper.isChapterInDatabase(chapterFromDatabase.getPbNext())) {
                    Log.d("NovelFetcher", "当前章节 《" + chapterFromDatabase.getTitle() + "》 的下一章节未缓存，正在缓存章节内容");
                    fetchNovelChapterByUrl(chapterFromDatabase.getPbNext(), databaseHelper, true, preCacheNum);
                }
            }
        } else {
            Log.d("NovelFetcher", "该章节未缓存，正在请求章节内容");
            fetchNovelChapterByUrl(chapterUrl, databaseHelper, preCache, preCacheNum);
        }
    }
}