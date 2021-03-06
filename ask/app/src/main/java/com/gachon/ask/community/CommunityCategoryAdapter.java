package com.gachon.ask.community;


import static com.gachon.ask.community.CommunityCategoryActivity.getTime;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.ask.R;
import com.gachon.ask.util.CloudStorage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class CommunityCategoryAdapter extends RecyclerView.Adapter<CommunityCategoryAdapter.ViewHolder>{
    ArrayList<PostInfo> items = new ArrayList<PostInfo>();
    private static final String TAG = "CC Adapter";
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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        PostInfo item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        String post_id;
        String publisher_id;
        TextView vContents;
        TextView vNickname;
        TextView vUploadTime;
        TextView vComment;
        TextView vHeart;
        FirebaseUser user;
        ImageView heart;
        ImageView iv_profile;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            vContents = itemView.findViewById(R.id.tv_contents);
            vNickname = itemView.findViewById(R.id.tv_nickname);
            vUploadTime = itemView.findViewById(R.id.tv_created_At);
            vComment = itemView.findViewById(R.id.tv_comment);
            vHeart = itemView.findViewById(R.id.tv_heart);
            heart = (ImageView)itemView.findViewById(R.id.ic_heart);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            user = FirebaseAuth.getInstance().getCurrentUser();


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent intent;
                    intent = new Intent(context, PostViewActivity.class);
                    intent.putExtra("post_id", post_id);//????????? ??????????????? ?????? id ??????
                    Log.d(TAG, "post_id: " + post_id);
                    context.startActivity(intent);
                }
            });
        }

        public void setItem(PostInfo item){
            post_id = item.getPost_id();
            publisher_id = item.getPublisher();

            vNickname.setText(item.getNickname());//????????? ?????????
            vUploadTime.setText(getTime(item.getCreatedAt()));//????????? ??????
            vContents.setText(item.getContents());//??? ??????
            vHeart.setText(String.valueOf(item.getNum_heart()));//?????? ??????
            vComment.setText(String.valueOf(item.getNum_comment()));//?????? ??????
            CloudStorage.getImageFromURL(String.valueOf(item.getuProfileImgURL())).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    if(task.isSuccessful()) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                        iv_profile.setImageBitmap(bitmap);
                    }
                }
            });


            DocumentReference docRef = db.collection("Posts").document(post_id);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            userlist_heart = (ArrayList<String>) document.get("userlist_heart");
                            if(userlist_heart.isEmpty()){// ?????? ???????????? ???????????????(?????????)
                                heart.setImageResource(R.drawable.baseline_favorite_border_24);
                                heart_clicked=false;
                            }
                            else if(userlist_heart.contains(user.getUid())){//?????? ????????? ?????? ?????? ?????? ????????? ????????????
                                heart.setImageResource(R.drawable.baseline_favorite_24); //?????? ????????????
                                heart_clicked=true;
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
    public void addItem(PostInfo item){
        items.add(item);
    }
    public void setItems(ArrayList<PostInfo> items){
        this.items = items;
    }

    public PostInfo getItem(int position){
        return items.get(position);
    }
    public void setItem(int position, PostInfo item){
        items.set(position, item);
    }
}
