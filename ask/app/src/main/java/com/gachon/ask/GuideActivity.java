package com.gachon.ask;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


public class GuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String guide_info = intent.getStringExtra("guide_info");
        System.out.println("guide_info:"+guide_info);

        if (guide_info.equals("slang")) {
            setContentView(R.layout.activity_guide_stock_slang);
        } else setContentView(R.layout.activity_guide_stock_term);



    }
}
