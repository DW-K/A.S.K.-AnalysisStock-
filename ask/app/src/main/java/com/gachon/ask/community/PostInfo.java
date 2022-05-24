package com.gachon.ask.community;

import com.google.firebase.Timestamp;

public class PostInfo {
    private String post_id;
    private String nickname;
    private String contents;
    private String publisher;
    private String category;
    private String profileImgURL;
    private Timestamp createdAt;
    private int num_heart;
    private int num_comment;

    public PostInfo(String post_id, String nickname, String contents, String publisher,
                     String category, String profileImgURL ,Timestamp createdAt, int num_heart, int num_comment){
        this.post_id = post_id;
        this.nickname = nickname;
        this.contents = contents;
        this.publisher = publisher;
        this.category = category;
        this.profileImgURL = profileImgURL;
        this.createdAt = createdAt;
        this.num_heart = num_heart;
        this.num_comment = num_comment;

    }

    public String getNickname(){ return this.nickname;}
    public void setNickname(String nickname){ this.nickname = nickname;}
    public String getContents(){ return this.contents;}
    public void setContents(String contents){ this.contents = contents;}
    public String getPublisher(){ return this.publisher;}
    public void setPublisher(String publisher){ this.publisher = publisher;}
    public void setCategory(String category){ this.contents = category;}
    public String getCategory(){ return this.category;}
    public String getuProfileImgURL() { return profileImgURL; }
    public void setuProfileImgURL(String profileImgURL) { this.profileImgURL = profileImgURL; }
    public Timestamp getCreatedAt(){ return this.createdAt;}
    public void setCreatedAt(Timestamp createdAt){ this.createdAt = createdAt;}
    public String getPost_id() { return post_id; }
    public void setPost_id(String post_id) { this.post_id = post_id; }

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




}
