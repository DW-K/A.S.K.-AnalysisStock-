package com.gachon.ask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class StockActivity extends AppCompatActivity {
    ImageButton btn_back;
    Button btn_sentiment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

    }

    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.activity_stock);
        btn_back = findViewById(R.id.btn_back);
        btn_sentiment = findViewById(R.id.button_sentiment);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_sentiment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SentimentReportActivity.class);
                startActivity(intent);

            }
        });
    }
}
