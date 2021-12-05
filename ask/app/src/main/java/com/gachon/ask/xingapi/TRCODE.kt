package com.gachon.ask.xingapi

public object TRCODE {

    /* 8407 */
    // 종목코드(6) 종목명(20) 현재가(8) 전일대비구분(1) 전일대비(8) 등락율(6.2) 누적거래량(12) 매도호가(8) 매수호가(8)
    // 체결수량(8) 체결강도(9.2) 시가(8) 고가(8) 저가(8) 거래대금(12) 우선매도전량(12) 우선매수전량(12) 총매도잔량(12)
    // 총매수전량(12) 전일종가(8) 상한가(8) 하한가(8)
    enum class T8407 {
        CHCODE,
        HNAME,
        PRICE,
        SIGN,
        CHANGE,
        DIFF,
        VOLUME,
        OFFERHO,
        BIDHO,
        CVOLUME,
        CHDEGREE,
        OPEN,
        HIGH,
        LOW,
        VALUE,
        OFFERREM,
        BIDREM,
        TOTOOFFERREM,
        TOTBIDREM,
        JNILCLOSE,
        UPLMTPRICE,
        DNLMTPRICE
    }

    /* 1301 */
    //0~6   시간(10) , 현재가(8) , 전일대비구분(1) , 전일대비(8) , 등락율(6) , 체결수량(12) , 체결강도(8)
    //7~12  거래량(12) , 매도체결수량(12) , 매도체결건수(8) , 매수체결수량(12) , 매수체결건수(8) , 순체결량(12)
    //13    순체결건수(8)
    enum class T1301(){
        CHETIME,
        PRICE,
        SIGN,
        CHANGE,
        DIFF,
        CVOLUME,
        CHDEGREE,
        VOLUME,
        MDVOLUME,
        MDCHECNT,
        MSVOLUME,
        MSCHECNT,
        REVOLUNM,
        RECHENCNT
    }
    @kotlin.jvm.JvmField
    val n1301col: IntArray
     = intArrayOf(10, 8, 1, 8, 6, 12, 8,
        12, 12, 8, 12, 8, 12,
        8)


    /* 1302 */
    enum class T1302 {CHETIME,CLOSE,SIGN,CHANGE,DIFF,CHDEGREE,MDVOLUME,MSVOLUME,
        REVOLIME,MDCHECNT,MSCHECNT,RECHECNT,VOLUME,OPEN,HIGH,LOW,
        CVOLUME,MDCHECNTTM,MSCHECNTTM,TOTOFFERREM,TOTBIDREM,MDVOLUMETM,MSVOLUMETM}
    @kotlin.jvm.JvmField
    val n1302col = intArrayOf(
        6,8,1,8,6,8,12,12,12,8,8,8,12,8,8,8,12,8,8,12,12,12,12
    )

    /* 1305 */
    enum class T1305{
        DATE,OPEN,HIGH,LOW,CLOSE,SIGN,CHANGE,DIFF,VOLUME,DIFF_VOL,CHDEGREE,SOJINRATE,CHANGERATE,FPVOLUME,COVOLUME,SHCODE,
        VALUE,PPVOLUME,O_SIGN,O_CHANGE,O_DIFF,H_SIGN,H_CHANGE,H_DIFF,L_SIGN,L_CHANGE,L_DIFF,MARKETCAP}
    @kotlin.jvm.JvmField
    val n1305col = intArrayOf(8, 8, 8, 8, 8, 1, 8,
        6, 12, 10, 6, 6, 6,12,12,6,
        12,12,1,8,6,1,8,6,1,8,6,12)


    /* T0424 */
    enum class T0424OUTBLOCK{
        SUNAMT,DTSINIK,MAMT,SUNAMT1,CTS_EXPCODE,TAPPAMT,TDTSUNIK,                      //0~6
    }
    //    enum class T0424OUTBLOCK1{
//        EXPCODE,JANGB,JANGQTY,MDPOSQT,PAMT,MAMT,SINAMT,                      //0~6
//        LASTDT,JANGB,JANGQTY,MDPOSQT,PAMT,MAMT,SINAMT,                      //0~6
//        EXPCODE,JANGB,JANGQTY,MDPOSQT,PAMT,MAMT,SINAMT,                      //0~6
//        EXPCODE,JANGB,JANGQTY,MDPOSQT,PAMT,MAMT,SINAMT,                      //0~6
//    }
    @kotlin.jvm.JvmField
    val nT0424OUTBLOCK = intArrayOf(18,18,18,18,22,18,18)



    /* T1101 */
    enum class T1101{
        HNAME,PRICE,SIGN,CHANGE,DIFF,VOLUME,JNILCLOSE,                      //0~6
        OFFERHO_1,BIDHO_1,OFFERREM_1,BIDREM_1,PREOFFERCHA_1,PREBIDCHA_1,    //7~12
        OFFERHO_2,BIDHO_2,OFFERREM_2,BIDREM_2,PREOFFERCHA_2,PREBIDCHA_2,    //13~18
        OFFERHO_3,BIDHO_3,OFFERREM_3,BIDREM_3,PREOFFERCHA_3,PREBIDCHA_3,    //19~24
        OFFERHO_4,BIDHO_4,OFFERREM_4,BIDREM_4,PREOFFERCHA_4,PREBIDCHA_4,    //25~30
        OFFERHO_5,BIDHO_5,OFFERREM_5,BIDREM_5,PREOFFERCHA_5,PREBIDCHA_5,    //31~36
        OFFERHO_6,BIDHO_6,OFFERREM_6,BIDREM_6,PREOFFERCHA_6,PREBIDCHA_6,    //37~42
        OFFERHO_7,BIDHO_7,OFFERREM_7,BIDREM_7,PREOFFERCHA_7,PREBIDCHA_7,    //43~48
        OFFERHO_8,BIDHO_8,OFFERREM_8,BIDREM_8,PREOFFERCHA_8,PREBIDCHA_8,    //49~54
        OFFERHO_9,BIDHO_9,OFFERREM_9,BIDREM_9,PREOFFERCHA_9,PREBIDCHA_9,    //55~60
        OFFERHO_10,BIDHO_10,OFFERREM_10,BIDREM_10,PREOFFERCHA_10,PREBIDCHA_10,//61~66
        OFFER,BID,PREOFFERCHA,PREBIDCHA,HOTIME,                             //67~71
        YEPRICE,YEVOLUME,YESIGN,YECHANGE,YEDIFF,                            //72~76
        TMOFFER,TMBID,HO_STATUS,                                            //77~79
        SHCODE,UPLMTPRICE,DNLMTPRICE,OPEN,HIGH,LOW                          //80~85
    }
    @kotlin.jvm.JvmField
    val n1101col = intArrayOf(20,8,1,8,6,12,8,
        8,8,12,12,12,12,
        8,8,12,12,12,12,
        8,8,12,12,12,12,
        8,8,12,12,12,12,
        8,8,12,12,12,12,
        8,8,12,12,12,12,
        8,8,12,12,12,12,
        8,8,12,12,12,12,
        8,8,12,12,12,12,
        8,8,12,12,12,12,
        12,12,12,12,8,
        8,12,1,8,6,
        12,12,1,
        6,8,8,8,8,8)


    @kotlin.jvm.JvmField
    val n_T1102_col =
    intArrayOf(20, 8, 1, 8, 6, 12, 8, 8, 8, 8, 12, 12, 8, 6, 8,
        6, 8, 6, 8, 8, 8, 8, 6, 6, 6, 12, 8, 5, 3, 3, 6, 6,
        8, 8, 8, 8, 6, 6, 3, 3, 6, 6, 8, 8, 8, 8, 6,
        6, 3, 3, 6, 6, 8, 8, 8, 8, 6, 6, 3, 3, 6, 6, 8,
        8, 8, 8, 6, 6, 3, 3, 6, 6, 8, 8, 8, 8, 6, 6, 12,
        12, 6, 12, 12, 6, 6, 6, 12, 12, 8, 8, 8, 8, 8, 12,
        12, 8, 2, 8, 12, 8, 10, 12, 12, 12, 12, 13, 10, 12,
        12, 12, 12, 13, 7, 7, 7, 7, 7, 10, 10, 10, 12, 10, 6,
        3, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 8, 8, 8, 8,
        8, 8, 8, 8, 8, 8, 18, 18, 8, 8, 8, 1, 8, 1, 8, 10, 8, 8, 1, 1, 8)

    /* H1 */
    enum class H1_ {
        HOTIME,
        OFFERHO1,BIDHO1,OFFERREM1,BIDREM1,
        OFFERHO2,BIDHO2,OFFERREM2,BIDREM2,
        OFFERHO3,BIDHO3,OFFERREM3,BIDREM3,
        OFFERHO4,BIDHO4,OFFERREM4,BIDREM4,
        OFFERHO5,BIDHO5,OFFERREM5,BIDREM5,
        OFFERHO6,BIDHO6,OFFERREM6,BIDREM6,
        OFFERHO7,BIDHO7,OFFERREM7,BIDREM7,
        OFFERHO8,BIDHO8,OFFERREM8,BIDREM8,
        OFFERHO10,BIDHO10,OFFERREM10,BIDREM10,
        TOTOFFERREM,TOTBIDREM,DONSIGUBUN,SHCODE,ALLOCGUBUN
    }
    @kotlin.jvm.JvmField
    val nH1_col = intArrayOf(
        6,
        7,7,9,9,
        7,7,9,9,
        7,7,9,9,
        7,7,9,9,
        7,7,9,9,
        7,7,9,9,
        7,7,9,9,
        7,7,9,9,
        7,7,9,9,
        7,7,9,9,
        9,9,1,6,1
    )

    /* S3,K3 */
    // 체결시간 , 전일대비구분 , 전일대비 , 등락율 , 현재가 , 시가시간 , 시가 , 고가시간 , 고가 , 저가시간 , 저가 , 체결구분 , 체결량 , 누적거래량 , 누적거래대금 , 매도누적체결량 , 매도누적체결건수
    // 매수누적체결량 , 매수누적체결건수 , 체결강도 , 가중평균가 , 매도호가 , 매수호가 , 장정보 , 전일동시간대거래량 , 단축코드
    enum class S_K_3_ {
        CHETIME,
        SIGN,
        CHANGE,
        DRATE,
        PRICE,
        OPENTIME,
        OPEN,
        HIGHTIME,
        HIGH,
        LOWTIME,
        LOW,
        CGUBUN,
        CVOLUME,
        VOLUME,
        VALUE,
        MDVOLUME,
        MDCHECNT,
        MSVOLUME,
        MSCHECNT,
        CPOWER,
        W_AVRG,
        OFFERHO,
        BIDHO,
        STATUS,
        JNILVOLUME,
        SHCODE
    }
    @kotlin.jvm.JvmField
    val nS_K_3col = intArrayOf(6, 1, 8, 6, 8, 6, 8, 6, 8, 6, 8, 1, 8, 12, 12, 12, 8, 12, 8, 9, 8, 8, 8, 2, 12, 6)



    public fun getBlockSize(block: IntArray) : Int{

        var size = 0;
        for( i in 0..block.size-1){
            size += block[i];
        }
        return size;
    }

    fun makeNullString( data : String ,len : Int) : String{

        var temp = "";

        for(i in 0..((len-data.length)-1))
        {
            temp += " ";
        }

        return temp+data;

    }

    public fun makeInblock(inblock : ArrayList<String> , idx : Int = -1, data : String = "" , datasize : Int = -1 ) : String{

        if(idx < 0){
            return inblock.joinToString(" ");
        }

        var _data = data;
        if( (datasize > 0) and (data.length < datasize) ){
            _data = makeNullString(data, datasize);
        }


        if ( inblock.size-1 > idx){
            inblock.set(idx,_data);
        }
        else{

            val gap = idx - inblock.size + 1;
            for(i in 0..gap-1){
                inblock.add("");
            }
            inblock.set(idx,_data);
        }

        return "";
    }

}