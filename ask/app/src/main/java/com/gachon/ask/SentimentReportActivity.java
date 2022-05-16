package com.gachon.ask;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SentimentReportActivity extends AppCompatActivity {
    private static final String TAG = "SentimentReportActivity";
    private ArrayList<Post> myPostList;
    private SentimentReportHotAdapter sentimentReportHotAdapter;
    private RecyclerView RecyclerView_hot_keyword;
    private TextView originalText, companyName;
    private Button btnTweet, btnNews;
    String url, selected_media="tweet";
    String stockName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentiment_report);
        originalText = findViewById(R.id.tv_original_text);
        companyName = findViewById(R.id.tv_company_name);
        RecyclerView_hot_keyword = findViewById(R.id.hot_keyword);


        // 모의투자에서 받은 intent data
        Intent intent = getIntent();
        stockName = intent.getExtras().getString("stock_name");
        companyName.setText(stockName);

        getOriginalData(selected_media, stockName);
        getKeywordData();


        // 버튼 클릭 이벤트
        btnTweet = findViewById(R.id.btn_tweet);
        btnNews = findViewById(R.id.btn_news);


        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundColor(getResources().getColor(R.color.blue_down));
                btnNews.setBackgroundColor(getResources().getColor(R.color.skyblue_background));
                selected_media = "tweet";
                getOriginalData(selected_media, stockName);
            }

        });
        btnNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundColor(getResources().getColor(R.color.blue_down));
                btnTweet.setBackgroundColor(getResources().getColor(R.color.skyblue_background));
                selected_media = "news";
                getOriginalData(selected_media, stockName);
            }
        });



    }

    public void getKeywordData() {
        String SERVER_URL = BuildConfig.SERVER;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHOlderApi jsonPlaceHOlderApi = retrofit.create(JsonPlaceHOlderApi.class);

        Call<List<Post>> call = jsonPlaceHOlderApi.getNewsCount();
        call = jsonPlaceHOlderApi.getNewsCount();
        myPostList = new ArrayList<>();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                myPostList.clear();
                if (!response.isSuccessful()) return;
                //myPostList = new ArrayList<>();
                List<Post> posts = response.body();

                // 상위 5개만 보여주기 위해 뒤의 5개 데이터는 지움
                for(int index = 23; index > 4; index--){
                    posts.remove(index);
                }
                myPostList.addAll(posts); // 상위 5개의 post만 저장

                for ( Post post : posts) {
                    String content ="";

                    String company = post.getCompany();
//                    String date = post.getDate();

                    if(!company.equals(stockName)) continue;
                    //if(!compareDate(date)) continue;

                    Log.d(TAG, "keyword : "+post.getWord());
                    Log.d(TAG, "news_count_id : "+post.getNewsCountId());
                    Log.d(TAG, "\n");

                }
                // adapter
                sentimentReportHotAdapter = new SentimentReportHotAdapter(myPostList);
                // set adapter to recyclerview
                RecyclerView_hot_keyword.setAdapter(sentimentReportHotAdapter);
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                System.out.println("실패했습니다.");
            }

        });
    }

    public void getOriginalData(String current_category, String stockName) {
        String SERVER_URL = BuildConfig.SERVER;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHOlderApi jsonPlaceHOlderApi = retrofit.create(JsonPlaceHOlderApi.class);

        Call<List<Post>> call = jsonPlaceHOlderApi.getNews();
        if(current_category.equals("tweet")) {
            call = jsonPlaceHOlderApi.getTweets();
            call.enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    originalText.setText("");
                    if (!response.isSuccessful())
                    {
                        originalText.setText("Code:" + response.code());
                        return;
                    }

                    List<Post> posts = response.body();

                    for ( Post post : posts) {
                        String content ="";


                        String company = post.getCompany();
                        String date = post.getDate();

                        //if(!company.equals(stockName)) continue;
                        //if(!compareDate(date)) continue;

                        if(company.equals(stockName)){ // 회사 이름이 일치해야 가져오도록
                            content += "" + post.getText() + "\n";
                            content += "날짜: " + date + "\n\n";

                            originalText.append(content);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Post>> call, Throwable t) {
                    System.out.println("실패했습니다.");
                    originalText.setText(t.getMessage());
                }
            });
        }
        else{
            call = jsonPlaceHOlderApi.getNews();
            //call = jsonPlaceHOlderApi.getTweets();
            call.enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    originalText.setText("");
                    if (!response.isSuccessful())
                    {
                        originalText.setText("Code:" + response.code());
                        return;
                    }

                    List<Post> posts = response.body();

                    for ( Post post : posts) {
                        String content ="";
                        Log.d("SentimentReport","TEST");

                        String company = post.getCompany();
                        String date = post.getDate();
                        //if(!company.equals(stockName)) continue;
                        //if(!compareDate(date)) continue;

                        if(company.equals(stockName)){ // 회사 이름이 일치해야 가져오도록
                            content += "" + post.getTitle() + "\n";
                            content += "날짜: " + date + "\n\n";

                            originalText.append(content);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Post>> call, Throwable t) {
                    System.out.println("실패했습니다.");
                    originalText.setText(t.getMessage());
                }
            });
        }
    }

    public boolean compareDate(String inputDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Calendar inputCalendar = Calendar.getInstance();
        try {
            Date date = format.parse(inputDate);
            inputCalendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar showingDate = Calendar.getInstance();
        showingDate.setTime(new Date());
        showingDate.add(Calendar.DATE, -30);

        Boolean result = inputCalendar.compareTo(showingDate) == 1;
        return result;
    }


}