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
import java.util.Arrays;
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
    private TextView originalText, companyName, sentimentPercent, predictValue;
    private Button btnTweet, btnNews;
    private Double totalSentiment;
    private int itemCount;
    private double results;
    String url, selected_media="tweet";
    String stockName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentiment_report);
        originalText = findViewById(R.id.tv_original_text);
        sentimentPercent = findViewById(R.id.tv_main_sentiment_percent);
        companyName = findViewById(R.id.tv_company_name);
        RecyclerView_hot_keyword = findViewById(R.id.hot_keyword);
        predictValue = findViewById(R.id.tv_stock_prediction_value);

        // 모의투자에서 받은 intent data
        Intent intent = getIntent();
        stockName = intent.getExtras().getString("stock_name");
        companyName.setText(stockName);

        // get data
        getOriginalData(selected_media, stockName);
        getKeywordData();
        getStockPrediction();


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
        totalSentiment = 0.0;
        String SERVER_URL = BuildConfig.SERVER;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHOlderApi jsonPlaceHOlderApi = retrofit.create(JsonPlaceHOlderApi.class);

        Call<List<Post>> call = jsonPlaceHOlderApi.getNewsCount();
        call = jsonPlaceHOlderApi.getNewsCount();
        myPostList = new ArrayList<>();
        totalSentiment = 0.0;
        itemCount = 0;
        String[] badKeyword = {"국내","기자","업계","최근","서울","뉴시스","시스","제공", "현대차", "기업",
                "21일", "달러", "이번", "이날", "17일", "증권", "그룹", "올해", "지난해", "4일", "5일",
        "7일", "10일", "시장", "13일", "14일", "15일", "18일", "19일", "20일", "22일", "3일", "24일", "26일", "28일", "29일", "때문", "1일"};
        ArrayList<String>  badKeywords = new ArrayList<>(Arrays.asList(badKeyword));
        ArrayList<String>  keywordList = new ArrayList<>();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                myPostList.clear();
                if (!response.isSuccessful()) return;
                List<Post> posts = response.body();

                for ( Post post : posts) {
                    String company = post.getCompany();
                    String keyword = post.getWord();

                    if(!company.equals(stockName)) continue;
                    if(!keywordList.contains(keyword) && !badKeywords.contains(keyword)){
                        myPostList.add(post);
                        Double sentiment = Double.parseDouble(post.getPositive());
                        totalSentiment += sentiment;
                        keywordList.add(keyword);
                        itemCount+=1;
                    }
                    if(itemCount>=10) {
                        break;
                    }
                }
                // adapter
                sentimentReportHotAdapter = new SentimentReportHotAdapter(myPostList);
                // set adapter to recyclerview
                RecyclerView_hot_keyword.setAdapter(sentimentReportHotAdapter);

                int avg = (int)((totalSentiment*100)/itemCount);
                if(avg > 50){
                    sentimentPercent.setText("긍정 " + avg +" %");
                    sentimentPercent.setTextColor(getResources().getColor(R.color.red_up));
                }else{
                    sentimentPercent.setText("부정 "+ avg + "% ");
                    sentimentPercent.setTextColor(getResources().getColor(R.color.blue_down));
                }

            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                System.out.println("실패했습니다.");
            }

        });
    }

    public void getStockPrediction() {
        String SERVER_URL = BuildConfig.SERVER;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHOlderApi jsonPlaceHOlderApi = retrofit.create(JsonPlaceHOlderApi.class);

        Call<List<Post>> call = jsonPlaceHOlderApi.getResult();
        // get data
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                predictValue.setText("");
                if (!response.isSuccessful())
                {
                    predictValue.setText("Code:" + response.code());
                    return;
                }

                List<Post> posts = response.body();

                for ( Post post : posts) {
                    String content ="";


                    String company = post.getCompany();
                    String date = post.getDate();
                    Double result = post.getResult();

                    if(company.equals(stockName) && (date.equals("2022-04-29"))){ // 회사 이름이 일치해야 가져오도록
                        content += "날짜: " + date + "\n\n";
                        content += "result: " + result + "\n\n";

                        results = result;

                        if(result >= 0){
                            predictValue.setText(String.format("%.2f",results) +" % 상승");
                            predictValue.setTextColor(getResources().getColor(R.color.red_up));
                        }else{
                            results *= -1;
                            predictValue.setText(String.format("%.2f",results) + "% 하락");
                            predictValue.setTextColor(getResources().getColor(R.color.blue_down));
                        }
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


    public void getOriginalData(String current_category, String stockName) {
        String SERVER_URL = BuildConfig.SERVER;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHOlderApi jsonPlaceHOlderApi = retrofit.create(JsonPlaceHOlderApi.class);

        Call<List<Post>> call = jsonPlaceHOlderApi.getNews();
        // get tweet data
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

                        if(!compareDate(date)) continue;
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
            //get news data
            call = jsonPlaceHOlderApi.getNews();
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
                        
                        if(!compareDate(date)) continue;
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

        Boolean result = inputCalendar.compareTo(showingDate) == 1; // inputCalendar가 더 클 경우 1 리턴.
        return result;
    }


}