package com.example.readbookapplication.api;

import com.example.readbookapplication.pojo.Novel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NovelService {
    @GET("json")
    @Headers("Cache-Control: max-age=180")
    Call<List<Novel>> getNovels(@Query("sortid") int sortId, @Query("page") int page);

    @GET("{novelId}")
    @Headers("Cache-Control: max-age=600")
    Call<ResponseBody> getNovelInfo(@Path("novelId") String novelId);

    // 获取章节内容
    @GET("{chapterId}")
    @Headers("Cache-Control: max-age=600")
    Call<ResponseBody> getChapterContent(@Path("chapterId") String chapterId);
}
