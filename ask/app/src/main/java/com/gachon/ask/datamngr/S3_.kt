package com.gachon.ask.datamngr

import com.ebest.api.datamngr.DataMngrUtil

class S3_ : RealDataMngr()
{
    init {
        setInfo( "S3_", "주식종목조회 API용", 6, DataMngrUtil.ATTR  )
        setFieldData( "chetime   "    , DataMngrUtil.TYPE_STR ,  6 )     // 체결시간
        setFieldData( "sign      "    , DataMngrUtil.TYPE_STR ,  1 )     // 전일대비구분
        setFieldData( "change    "    , DataMngrUtil.TYPE_INT ,  8 )     // 전일대비
        setFieldData( "drate     "    , DataMngrUtil.TYPE_REAL,  6, 2 )     // 등락율
        setFieldData( "price     "    , DataMngrUtil.TYPE_INT ,  8 )     // 현재가
        setFieldData( "opentime  "    , DataMngrUtil.TYPE_STR ,  6 )     // 시가시간
        setFieldData( "open      "    , DataMngrUtil.TYPE_INT ,  8 )     // 시가
        setFieldData( "hightime  "    , DataMngrUtil.TYPE_STR ,  6 )     // 고가시간
        setFieldData( "high      "    , DataMngrUtil.TYPE_INT ,  8 )     // 고가
        setFieldData( "lowtime   "    , DataMngrUtil.TYPE_STR ,  6 )     // 저가시간
        setFieldData( "low       "    , DataMngrUtil.TYPE_INT ,  8 )     // 저가
        setFieldData( "cgubun    "    , DataMngrUtil.TYPE_STR ,  1 )     // 체결구분
        setFieldData( "cvolume   "    , DataMngrUtil.TYPE_INT ,  8 )     // 체결량
        setFieldData( "volume    "    , DataMngrUtil.TYPE_INT , 12 )     // 누적거래량
        setFieldData( "value     "    , DataMngrUtil.TYPE_INT , 12 )     // 누적거래대금
        setFieldData( "mdvolume  "    , DataMngrUtil.TYPE_INT , 12 )     // 매도누적체결량
        setFieldData( "mdchecnt  "    , DataMngrUtil.TYPE_INT ,  8 )     // 매도누적체결건수
        setFieldData( "msvolume  "    , DataMngrUtil.TYPE_INT , 12 )     // 매수누적체결량
        setFieldData( "mschecnt  "    , DataMngrUtil.TYPE_INT ,  8 )     // 매수누적체결건수
        setFieldData( "cpower    "    , DataMngrUtil.TYPE_REAL,  9, 2 )     // 체결강도
        setFieldData( "w_avrg    "    , DataMngrUtil.TYPE_INT ,  8 )     // 가중평균가
        setFieldData( "offerho   "    , DataMngrUtil.TYPE_INT ,  8 )     // 매도호가
        setFieldData( "bidho     "    , DataMngrUtil.TYPE_INT ,  8 )     // 매수호가
        setFieldData( "status    "    , DataMngrUtil.TYPE_STR ,  2 )     // 장정보
        setFieldData( "jnilvolume"    , DataMngrUtil.TYPE_INT , 12 )     // 전일동시간대거래량
        setFieldData( "shcode    "    , DataMngrUtil.TYPE_STR ,  6 )     // 단축코드
    }
}
