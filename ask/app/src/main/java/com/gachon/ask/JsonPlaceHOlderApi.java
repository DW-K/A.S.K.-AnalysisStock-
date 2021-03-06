package com.gachon.ask;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonPlaceHOlderApi {

    @GET("crawl_tweet")
    Call<List<Post>> getTweets();

    @GET("crawl_news")
    Call<List<Post>> getNews();

    @GET("crawl_news_count")
    Call<List<Post>> getNewsCount();

    @GET("result")
    Call<List<Post>> getResult();
}