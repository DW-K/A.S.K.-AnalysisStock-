package com.gachon.ask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        try{

            Thread.sleep(2000);


        }catch (InterruptedException e) {

            e.printStackTrace();

        }


        Intent intent=new Intent(this, LoginActivity.class);

        startActivity(intent);

        finish();




    }

}
