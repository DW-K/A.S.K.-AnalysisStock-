package com.gachon.ask;

public class Post {
    private int id, rt_count;


    private String text, date, company;
    private String title, press, text_news, date_news;

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }
    public int getRT_count() {
        return rt_count;
    }
    public String getDate() {
        return date;
    }
    public String getCompany() {
        return company;
    }

    public String getTitle() { return title;}
    public String getPress() { return press;}
    public String getTextNews() { return text_news;}
    public String getDateNews() { return date_news;}
}