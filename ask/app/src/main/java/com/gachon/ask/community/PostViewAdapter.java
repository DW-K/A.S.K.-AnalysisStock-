package com.gachon.ask.community;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.ask.R;
import com.gachon.ask.WriteInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class PostViewAdapter extends RecyclerView.Adapter<PostViewAdapter.ViewHolder> {
    PostViewAdapter postViewAdapter;
    private static final String TAG = "PostViewAdapter";
    ArrayList<WriteInfo> items = new ArrayList<WriteInfo>();
    ArrayList userlist_heart;
    Boolean heart_clicked = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.item_category_post_view, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //bind views
        WriteInfo item = items.get(position);
        holder.setItem(item);
    }



    public void addItem(WriteInfo item){ items.add(item); }
    public void setItems(ArrayList<WriteInfo> items){
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        String post_id;
        String publisher_id;
        TextView vContents;
        TextView vNickname;
        TextView vUploadTime;
        TextView vComment;
        TextView vHeart;
        FirebaseUser user;
        ImageButton heart;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            vContents = itemView.findViewById(R.id.tv_contents);
            vNickname = itemView.findViewById(R.id.tv_nickname);
            vUploadTime = itemView.findViewById(R.id.tv_created_At);
            vComment = itemView.findViewById(R.id.tv_comment);
            vHeart = itemView.findViewById(R.id.tv_heart);
            user = FirebaseAuth.getInstance().getCurrentUser();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent intent;
                    intent = new Intent(context, PostViewActivity.class);
                    intent.putExtra("posts_id", post_id);//포스트 액티비티에 문서 id 전달

                    context.startActivity(intent);
                }
            });
        }

        public void setItem(WriteInfo item) {
            post_id = item.getPosts_id();
            publisher_id = item.getPublisher();

            vNickname.setText(item.getNickname());//작성자 닉네임
            vUploadTime.setText(getTime(item.getCreatedAt()));//업로드 시간
            vContents.setText(item.getContents());//글 내용
            vHeart.setText(String.valueOf(item.getNum_heart()));//하트 개수
            vComment.setText(String.valueOf(item.getNum_comment()));//댓글 개수


            DocumentReference docRef = db.collection("Posts").document(post_id);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            userlist_heart = (ArrayList<String>) document.get("userlist_heart");
                            if (userlist_heart.isEmpty()) {// 하트 리스트가 비어있다면(디폴트)
                                heart.setImageResource(R.drawable.baseline_favorite_border_24);
                                heart_clicked = false;
                            } else if (userlist_heart.contains(user.getUid())) {//현재 유저가 해당 글에 이미 하트를 눌렀다면
                                heart.setImageResource(R.drawable.baseline_favorite_24); //하트 채워두기
                                heart_clicked = true;
                            }

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }

            });


        }

    }

    // getExtra cannot call the type timestamp
    // cast the type timestamp -> String
    static String getTime(Timestamp time) {

        Date date_createdAt = time.toDate();
        SimpleDateFormat formatter = new SimpleDateFormat("yy/MM/dd HH:mm");
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        formatter.setTimeZone(timeZone);

        String txt_createdAt = formatter.format(date_createdAt);

        return txt_createdAt;
    }


}
