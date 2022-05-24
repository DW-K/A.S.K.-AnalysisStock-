package com.gachon.ask.util.model;

public class StockReport {

    private String stockName; // 주식 이름
    private int stockPrice;   // 주식 가격
    private int stockNum;     // 보유했던 주식 수
    private boolean isSell;   // 1: 매도, 0: 매수

    public StockReport(String stockName, int stockPrice, int stockNum, boolean isSell) {
        this.stockName = stockName;
        this.stockPrice = stockPrice;
        this.stockNum = stockNum;
        this.isSell = isSell;
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

    public boolean isSell() {
        return isSell;
    }

    public void setSell(boolean sell) {
        isSell = sell;
    }
}
