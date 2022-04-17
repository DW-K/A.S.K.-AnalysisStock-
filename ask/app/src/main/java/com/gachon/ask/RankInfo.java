package com.gachon.ask;

public class RankInfo {
    private String uID;
    private Integer uRank;
    private Integer uLevel;
    private String uNickname;
    private String uProfileImgURL;
    private Integer uYield;

    public RankInfo(String uID, Integer uRank, Integer uLevel,
            String uNickname, String uProfileImgURL, int uYield) {
        this.uID = uID;
        this.uRank = uRank;
        this.uLevel = uLevel;
        this.uNickname = uNickname;
        this.uProfileImgURL = uProfileImgURL;
        this.uYield = uYield;

    }

    public String getuID() { return uID; }

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

    public String getuProfileImgURL() {
        return uProfileImgURL;
    }

    public void setuProfileImgURL(String uProfileImgURL) {
        this.uProfileImgURL = uProfileImgURL;
    }

    public Integer getuYield() {
        return uYield;
    }

    public void setuYield(Integer uYield) {
        this.uYield = uYield;
    }

}