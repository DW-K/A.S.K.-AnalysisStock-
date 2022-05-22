package com.gachon.ask;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {


    CheckBox question1_a, question1_b, question1_c, question1_d;
    RadioGroup question1;
    RadioButton question1_answer;
    RadioGroup question2;
    RadioButton question2_answer;
    RadioGroup question3;
    RadioButton question3_answer;
    RadioGroup question4;
    RadioButton question4_answer;
    RadioGroup question5;
    RadioButton question5_answer;
    int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        question1 = findViewById(R.id.radiogrp_1);
        question1_answer = findViewById(R.id.radio_question_1_b);
        question2 = findViewById(R.id.radiogrp_2);
        question2_answer = findViewById(R.id.radio_question_2_b);
        question3 = findViewById(R.id.radiogrp_3);
        question3_answer = findViewById(R.id.radio_question_3_c);
        question4 = findViewById(R.id.radiogrp_4);
        question4_answer = findViewById(R.id.radio_question_4_a);
        question5 = findViewById(R.id.radiogrp_5);
        question5_answer = findViewById(R.id.radio_question_5_b);


    }

    //Reset methods will clear all options when RESET button pressed
    public void reset(View view) {
        question1.clearCheck();
        question2.clearCheck();
        question3.clearCheck();
        question4.clearCheck();
        question5.clearCheck();
        score = 0;
    }

    public void submit(View view) {
        if (question1_answer.isChecked()) {
            score++;
        }
        if (question2_answer.isChecked()) {
            score++;
        }
        if (question3_answer.isChecked()) {
            score++;
        }
        if (question4_answer.isChecked()) {
            score++;
        }
        if (question5_answer.isChecked()) {
            score++;
        }
        //    Intent shareIntent = new Intent(MainActivity.this, QuizShareActivity.class);
        //    shareIntent.putExtra("score", score);
        //    startActivity(shareIntent);
    }
}