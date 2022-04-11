package com.gachon.ask;


public class RankInfo {
    private Integer uRank;
    private Integer uLevel;
    private String uNickname;
    private Integer uYield;


    public RankInfo(Integer uRank, Integer uLevel, String uNickname, int uYield) {
        this.uRank = uRank;
        this.uLevel = uLevel;
        this.uNickname = uNickname;
        this.uYield = uYield;

    }

    public Integer getuRank() {
        return uRank;
    }

    public void setuRank(Integer uRank) {
        this.uRank = uRank;
    }

    public Integer getuLevel() {
        return uLevel;
    }

    public void setuLevel(Integer uLevel) {
        this.uLevel = uLevel;
    }

    public String getuNickname() {
        return uNickname;
    }

    public void setuNickname(String uNickname) {
        this.uNickname = uNickname;
    }

    public Integer getuYield() {
        return uYield;
    }

    public void setuYield(Integer uYield) {
        this.uYield = uYield;
    }


}