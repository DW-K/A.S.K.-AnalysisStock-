package com.gachon.ask.community;

public class PostInfo {
    String publisher_id;
    String post_id;
    String nickname;
    String time;
    String contents;
    int num_heart;
    int num_comment;

    public PostInfo(String publisher_id, String post_id, String nickname, String time, String contents, int num_heart, int num_comment) {
        this.publisher_id = publisher_id;
        this.post_id = post_id;
        this.nickname = nickname;
        this.time = time;
        this.contents = contents;
        this.num_heart = num_heart;
        this.num_comment = num_comment;
    }

    public String getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(String publisher_id) {
        this.publisher_id = publisher_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

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
