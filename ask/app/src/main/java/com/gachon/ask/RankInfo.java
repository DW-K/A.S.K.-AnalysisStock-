package com.gachon.ask;


public class RankInfo {
    private Integer userRank;
    private Integer userLevel;
    private String nickname;
    private Integer userMoney;


    public RankInfo(Integer userRank, Integer userLevel, String nickname, int userMoney){
        this.userRank = userRank;
        this.userLevel = userLevel;
        this.nickname = nickname;
        this.userMoney = userMoney;

    }


    public String getNickname(){ return this.nickname;}
    public void setNickname(String nickname){ this.nickname = nickname;}
    public Integer getUserRank() {
        return userRank;
    }
    public void setUserRank(Integer userRank) {
        this.userRank = userRank;
    }
    public Integer getUserLevel() {
        return userLevel;
    }
    public void setUserLevel(Integer userLevel) {
        this.userLevel = userLevel;
    }
    public Integer getUserMoney() {
        return userMoney;
    }
    public void setUserMoney(Integer userMoney) {
        this.userMoney = userMoney;
    }


}