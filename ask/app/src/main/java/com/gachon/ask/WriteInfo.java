package com.gachon.ask;


import com.google.firebase.Timestamp;

import java.util.ArrayList;


public class WriteInfo {
    private String posts_id;
    private String nickname;
    private String contents;
    private String publisher;
    private String category;
    private Timestamp createdAt;
    private int num_heart;
    private int num_comment;
    private ArrayList userlist_heart;


    public WriteInfo(String posts_id, String nickname, String contents, String publisher,
                     String category, Timestamp createdAt, int num_heart, int num_comment, ArrayList userlist_heart){
        this.posts_id = posts_id;
        this.nickname = nickname;
        this.contents = contents;
        this.publisher = publisher;
        this.category = category;
        this.createdAt = createdAt;
        this.num_heart = num_heart;
        this.num_comment = num_comment;
        this.userlist_heart = userlist_heart;

    }


    public String getNickname(){ return this.nickname;}
    public void setNickname(String nickname){ this.nickname = nickname;}
    public String getContents(){ return this.contents;}
    public void setContents(String contents){ this.contents = contents;}
    public String getPublisher(){ return this.publisher;}
    public void setPublisher(String publisher){ this.publisher = publisher;}
    public void setCategory(String category){ this.contents = category;}
    public String getCategory(){ return this.category;}
    public Timestamp getCreatedAt(){ return this.createdAt;}
    public void setCreatedAt(Timestamp createdAt){ this.createdAt = createdAt;}
    public String getPosts_id() { return posts_id; }
    public void setPosts_id(String posts_id) { this.posts_id = posts_id; }

    public int getNum_heart() {
        return num_heart;
    }

    public void setNum_heart(int num_heart) {
        this.num_heart = num_heart;
    }

    public int getNum_comment() {
        return num_comment;
    }

    public void setNum_comment(int num_comment) {
        this.num_comment = num_comment;
    }

    public ArrayList getUserlist_heart() {
        return userlist_heart;
    }

    public void setUserlist_heart(ArrayList userlist_heart) {
        this.userlist_heart = userlist_heart;
    }


}