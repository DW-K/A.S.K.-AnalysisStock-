package com.gachon.ask.community;

public class CommentInfo {
    String comment;
    String nickname;
    String post_id;
    String publisher;
//    String profile_image;
    String time;

    public CommentInfo(String comment, String nickname, String post_id, String publisher, String time) {
        this.comment = comment;
        this.nickname = nickname;
        this.post_id = post_id;
//        this.profile_image = profile_image;
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

//    public String getProfile_image() {
//        return profile_image;
//    }
//
//    public void setProfile_image(String profile_image) {
//        this.profile_image = profile_image;
//    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
