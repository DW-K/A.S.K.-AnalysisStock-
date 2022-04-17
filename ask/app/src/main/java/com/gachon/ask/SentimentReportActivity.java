package com.gachon.ask;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SentimentReportActivity extends AppCompatActivity {
    private TextView originalText;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentiment_report);

        String SERVER_URL = BuildConfig.SERVER;
        originalText = findViewById(R.id.tv_original_text);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHOlderApi jsonPlaceHOlderApi = retrofit.create(JsonPlaceHOlderApi.class);

        Call<List<Post>> call = jsonPlaceHOlderApi.getPosts();

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

                    content += "company : " + post.getCompany() + "\n\n";
                    content += "rt_count : " + post.getRT_count() + "\n";
                    content += "text : " + post.getText() + "\n\n";
                    content += "date : " + post.getDate() + "\n\n";

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



    // 로딩화면 아직 미구현
}