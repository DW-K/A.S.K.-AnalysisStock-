package com.gachon.ask.util.model;

public class Stock {

    private String stockName; // 종목명
    private String stockPrice;   // 주식 가격 (평균단가)
    private String stockNum;     // 보유하고 있는 해당 주식의 개수 (잔고수량)
    private String stockYield;   // 수익률

    public Stock(){ }

    public Stock(String stockName, String stockPrice, String stockYield, String stockNum) {
        this.stockName = stockName;
        this.stockPrice = stockPrice;
        this.stockNum = stockNum;
        this.stockYield = stockYield;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(String stockPrice) {
        this.stockPrice = stockPrice;
    }

    public String getStockNum() {
        return stockNum;
    }

    public void setStockNum(String stockNum) {
        this.stockNum = stockNum;
    }

    public String getStockYield() { return stockYield; }

    public void setStockYield(String stockYield) { this.stockYield = stockYield; }
}
