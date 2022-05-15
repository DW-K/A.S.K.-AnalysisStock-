package com.gachon.ask;

public class Post {
    private int id, news_count_id, rt_count;


    private String text, date, company, positive, negative; // 트윗 Meta-data
    private String title, press, text_news; // 뉴스 Meta-data
    private String word, count; // 핫 키워드 Meta-data

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
    public String getPositive(){
        return positive;
    }
    public String getNegative(){
        return negative;
    }

    public String getTitle() { return title;}
    public String getPress() { return press;}
    public String getTextNews() { return text_news;}

    public int getNewsCountId() { return news_count_id; }

    public String getWord() { return word; }

    public String getCount() { return count; }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", news_count_id=" + news_count_id +
                ", rt_count=" + rt_count +
                ", text='" + text + '\'' +
                ", date='" + date + '\'' +
                ", company='" + company + '\'' +
                ", positive='" + positive + '\'' +
                ", negative='" + negative + '\'' +
                ", title='" + title + '\'' +
                ", press='" + press + '\'' +
                ", text_news='" + text_news + '\'' +
                ", word='" + word + '\'' +
                ", count='" + count + '\'' +
                '}';
    }
}