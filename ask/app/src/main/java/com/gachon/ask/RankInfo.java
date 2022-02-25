package com.gachon.ask;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class RankInfo {
    private String category;
//    private Integer uNewRank;
//    private Integer uLastRank;
//    private Integer uRankChange;
    private Integer uLevel;
    private String uNickname;
    private Integer uYield;
    private HashMap uRanks;



    public RankInfo(String category, Integer uLevel,
                    String uNickname, int uYield, HashMap uRanks) {
        this.category = category;
//        this.uNewRank = uNewRank;
//        this.uLastRank = uLastRank;
//        this.uRankChange = uRankChange;
        this.uLevel = uLevel;
        this.uNickname = uNickname;
        this.uYield = uYield;
        this.uRanks = uRanks;

    }

//    public Integer getuNewRank() {
//        return uNewRank;
//    }
//
//    public void setuNewRank(Integer uNewRank) {
//        this.uNewRank = uNewRank;
//    }
//
//    public Integer getuLastRank() {
//        return uLastRank;
//    }
//
//    public void setuLastRank(Integer uLastRank) {
//        this.uLastRank = uLastRank;
//    }
//
//    public Integer getuRankChange() {
//        return uRankChange;
//    }
//
//    public void setuRankChange(Integer uRankChange) {
//        this.uRankChange = uRankChange;
//    }

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


    public HashMap getuRanks() { return uRanks; }

    public void setuRanks(HashMap uRanks) { this.uRanks = uRanks; }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}