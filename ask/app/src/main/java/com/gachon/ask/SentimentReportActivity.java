package com.gachon.ask;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SentimentReportActivity extends AppCompatActivity {
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

        // 모의투자에서 받은 intent data
        Intent intent = getIntent();
        String stockName = intent.getExtras().getString("stock_name");
        companyName.setText(stockName);

        getData(selected_media);


        // 버튼 클릭 이벤트
        btnTweet = findViewById(R.id.btn_tweet);
        btnNews = findViewById(R.id.btn_news);


        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundColor(getResources().getColor(R.color.blue_down));
                btnNews.setBackgroundColor(getResources().getColor(R.color.skyblue_background));
                selected_media = "tweet";
                getData(selected_media);
            }

        });
        btnNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundColor(getResources().getColor(R.color.blue_down));
                btnTweet.setBackgroundColor(getResources().getColor(R.color.skyblue_background));
                selected_media = "news";
                getData(selected_media);
            }
        });





    }

    public void getData(String current_category) {
        String SERVER_URL = BuildConfig.SERVER;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHOlderApi jsonPlaceHOlderApi = retrofit.create(JsonPlaceHOlderApi.class);

        Call<List<Post>> call = jsonPlaceHOlderApi.getTweets();
        if(current_category.equals("tweet")) {
            call = jsonPlaceHOlderApi.getTweets();
        }
//        else{
//            call = jsonPlaceHOlderApi.getNews();
//        }

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {

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
                    if(!company.equals(stockName)) continue;
                    if(!compareDate(date)) continue;

                    content += "" + post.getText() + "\n";
                    content += "날짜: " + date + "\n\n";



                    originalText.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                System.out.println("실패했습니다.");
                originalText.setText(t.getMessage());
            }
        });

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