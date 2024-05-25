package com.example.readbookapplication.api.callback;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.readbookapplication.api.NovelFetcher;
import com.example.readbookapplication.pojo.Novel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NovelsListCallback implements Callback<List<Novel>> {

    private final NovelFetcher novelFetcher;
    private final String TAG = "NovelCallback";

    public NovelsListCallback(NovelFetcher novelFetcher) {
        this.novelFetcher = novelFetcher;
    }

    @Override
    public void onResponse(@NonNull Call<List<Novel>> call, Response<List<Novel>> response) {
        if (response.isSuccessful()) {
            // 处理请求成功的情况
            List<Novel> novels = response.body();
            // 日志输出第一本小说名字
            assert novels != null;
            Log.d(TAG, "已加载小说列表，第一本小说：《" + novels.get(0).getTitle() + "》");
            novelFetcher.notifyNovelsListSuccess(novels);
        } else {
            // 处理请求失败的情况
            Log.w(TAG, "onResponse: " + response.errorBody());
            novelFetcher.notifyFailure("Request failed. Code: " + response.code());
        }
    }

    @Override
    public void onFailure(@NonNull Call<List<Novel>> call, Throwable t) {
        // 处理请求失败的情况
        Log.e(TAG, "onFailure: " + t.getMessage());
        novelFetcher.notifyFailure("Request failed. Error: " + t.getMessage());
    }
}