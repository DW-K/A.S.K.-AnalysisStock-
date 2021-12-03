package com.gachon.ask;


import com.google.firebase.Timestamp;

import java.util.ArrayList;


public class WriteInfo {
    private String posts_id;
    private String nickname;
//    private String title;
    private String contents;
    private String publisher;
    private String category;
    private Timestamp createdAt;



    public WriteInfo(String posts_id, String nickname, String contents, String publisher,
                     String category, Timestamp createdAt){
        this.posts_id = posts_id;
        this.nickname = nickname;
//        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
        this.category = category;
        this.createdAt = createdAt;

    }


    public String getNickname(){ return this.nickname;}
    public void setNickname(String nickname){ this.nickname = nickname;}
//    public String getTitle(){ return this.title;}
//    public void setTitle(String title){ this.title = title;}
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
}