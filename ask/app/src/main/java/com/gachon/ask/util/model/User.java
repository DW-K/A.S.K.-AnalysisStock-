package com.gachon.ask.util.model;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class User {

    private String userEmail; // 유저 이메일
    private String userNickName; // 유저 닉네임
    private Timestamp registerTime; // 가입 시간
    private int userLevel;        // 레벨
    private int userExp;          // 경험치
    private int userMoney;        // 내 자산 (주식, 주문 가능 금액)
    private int postAnalysisNum;  // 분석글 수
    private int postQuestionNum;  // 질문 수
    private int postAnswerNum;    // 답변 수
    private float profitRate;         // 수익률
    private ArrayList<Stock> myStock; // 보유 주식
    private ArrayList<StockReport> myStockReport; // 매매 기록
    private ArrayList<Integer> challenges;        // 달성한 업적

    public User(){ }

    public User(String userEmail, String userNickName, Timestamp registerTime, int userLevel, int userExp, int userMoney, int postAnalysisNum, int postQuestionNum, int postAnswerNum, float profitRate, ArrayList<Stock> myStock, ArrayList<StockReport> myStockReport, ArrayList<Integer> challenges) {
        this.userEmail = userEmail;
        this.userNickName = userNickName;
        this.registerTime = registerTime;
        this.userLevel = userLevel;
        this.userExp = userExp;
        this.userMoney = userMoney;
        this.postAnalysisNum = postAnalysisNum;
        this.postQuestionNum = postQuestionNum;
        this.postAnswerNum = postAnswerNum;
        this.profitRate = profitRate;
        this.myStock = myStock;
        this.myStockReport = myStockReport;
        this.challenges = challenges;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public Timestamp getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Timestamp registerTime) {
        this.registerTime = registerTime;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public int getUserExp() {
        return userExp;
    }

    public void setUserExp(int userExp) {
        this.userExp = userExp;
    }

    public int getUserMoney() {
        return userMoney;
    }

    public void setUserMoney(int userMoney) {
        this.userMoney = userMoney;
    }

    public int getPostAnalysisNum() {
        return postAnalysisNum;
    }

    public void setPostAnalysisNum(int postAnalysisNum) {
        this.postAnalysisNum = postAnalysisNum;
    }

    public int getPostQuestionNum() {
        return postQuestionNum;
    }

    public void setPostQuestionNum(int postQuestionNum) {
        this.postQuestionNum = postQuestionNum;
    }

    public int getPostAnswerNum() {
        return postAnswerNum;
    }

    public void setPostAnswerNum(int postAnswerNum) {
        this.postAnswerNum = postAnswerNum;
    }

    public float getProfitRate() {
        return profitRate;
    }

    public void setProfitRate(float profitRate) {
        this.profitRate = profitRate;
    }

    public ArrayList<Stock> getMyStock() {
        return myStock;
    }

    public void setMyStock(ArrayList<Stock> myStock) {
        this.myStock = myStock;
    }

    public ArrayList<StockReport> getMyStockReport() {
        return myStockReport;
    }

    public void setMyStockReport(ArrayList<StockReport> myStockReport) {
        this.myStockReport = myStockReport;
    }

    public ArrayList<Integer> getChallenges() {
        return challenges;
    }

    public void setChallenges(ArrayList<Integer> challenges) {
        this.challenges = challenges;
    }
}