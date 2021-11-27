package com.gachon.ask.community;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//import com.gachon.ask.pushNoti.SendMessage;
import com.gachon.ask.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ListDetailActivity extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String posts_id;
    TextView peopleNum;
    TextView status;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Intent intent = getIntent();

        TextView title = (TextView) findViewById(R.id.title);
        TextView nickname = (TextView) findViewById(R.id.nickname);
        TextView created_at = (TextView) findViewById(R.id.created_At);
        TextView contents = (TextView) findViewById(R.id.contents);
        EditText host_comment = findViewById(R.id.host_comment);


        String txt_title = intent.getExtras().getString("title");
        String txt_nickname = intent.getExtras().getString("nickname");
        String txt_contents = intent.getExtras().getString("contents");
        String txt_publisher = intent.getExtras().getString("publisher");
        String txt_createdAt = intent.getExtras().getString("created_at");
        posts_id = intent.getExtras().getString("posts_id");

        DocumentReference postRef = db.collection("Posts").document(posts_id);


        title.setText(txt_title);
        nickname.setText(txt_nickname);
        created_at.setText(txt_createdAt);
        contents.setText(txt_contents);



    }


    @Override
    public void onBackPressed() {
        finish();
    }


    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}