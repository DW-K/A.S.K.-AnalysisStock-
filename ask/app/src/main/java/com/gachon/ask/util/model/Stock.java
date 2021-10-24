package com.gachon.ask.util.model;

public class Stock {

    private String stockName; // 주식 이름
    private int stockPrice;   // 주식 가격
    private int stockNum;     // 보유하고 있는 해당 주식의 개수

    public Stock(String stockName, int stockPrice, int stockNum) {
        this.stockName = stockName;
        this.stockPrice = stockPrice;
        this.stockNum = stockNum;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public int getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(int stockPrice) {
        this.stockPrice = stockPrice;
    }

    public int getStockNum() {
        return stockNum;
    }

    public void setStockNum(int stockNum) {
        this.stockNum = stockNum;
    }
}
