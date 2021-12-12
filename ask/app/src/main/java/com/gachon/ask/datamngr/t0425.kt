package com.gachon.ask.datamngr

import com.ebest.api.datamngr.DataMngrUtil

class t0425 : DataMngr()
{
    init {
        setInfo( "t0425", "주식 체결/미체결", DataMngrUtil.HEADER_D, 1, 1, DataMngrUtil.ATTR )

        // t0425InBlock
        setBlockInfo( "t0425InBlock", DataMngrUtil.BLOCK_IN )
        setFieldInfo( "t0425InBlock", "accno    ", DataMngrUtil.TYPE_STR,  11 )           // 계좌번호
        setFieldInfo( "t0425InBlock", "passwd   ", DataMngrUtil.TYPE_STR,   8 )           // 비밀번호
        setFieldInfo( "t0425InBlock", "expcode  ", DataMngrUtil.TYPE_STR,  12 )           // 종목번호
        setFieldInfo( "t0425InBlock", "chegb    ", DataMngrUtil.TYPE_STR,   1 )           // 체결구분
        setFieldInfo( "t0425InBlock", "medosu   ", DataMngrUtil.TYPE_STR,   1 )           // 매매구분
        setFieldInfo( "t0425InBlock", "sortgb   ", DataMngrUtil.TYPE_STR,   1 )           // 정렬순서
        setFieldInfo( "t0425InBlock", "cts_ordno", DataMngrUtil.TYPE_STR,  10 )           // 주문번호

        // t0425OutBlock
        setBlockInfo( "t0425OutBlock", DataMngrUtil.BLOCK_OUT )
        setFieldInfo( "t0425OutBlock", "tqty     ", DataMngrUtil.TYPE_INT, 18 )                // 총주문수량
        setFieldInfo( "t0425OutBlock", "tcheqty  ", DataMngrUtil.TYPE_INT, 18 )                // 총체결수량
        setFieldInfo( "t0425OutBlock", "tordrem  ", DataMngrUtil.TYPE_INT, 18 )                // 총미체결수량
        setFieldInfo( "t0425OutBlock", "cmss     ", DataMngrUtil.TYPE_INT, 18 )                // 추정수수료
        setFieldInfo( "t0425OutBlock", "tamt     ", DataMngrUtil.TYPE_INT, 18 )                // 총주문금액
        setFieldInfo( "t0425OutBlock", "tmdamt   ", DataMngrUtil.TYPE_INT, 18 )                // 총매도체결금액
        setFieldInfo( "t0425OutBlock", "tmsamt   ", DataMngrUtil.TYPE_INT, 18 )                // 총매수체결금액
        setFieldInfo( "t0425OutBlock", "tax      ", DataMngrUtil.TYPE_INT, 18 )                // 추정제세금
        setFieldInfo( "t0425OutBlock", "cts_ordno", DataMngrUtil.TYPE_STR, 10 )                // 주문번호

        // t0425OutBlock1
        setBlockInfo( "t0425OutBlock1", DataMngrUtil.BLOCK_OUT, DataMngrUtil.OCCURS )
        setFieldInfo( "t0425OutBlock1", "ordno     ", DataMngrUtil.TYPE_INT, 10 )                // 주문번호
        setFieldInfo( "t0425OutBlock1", "expcode   ", DataMngrUtil.TYPE_STR, 12 )                // 종목번호
        setFieldInfo( "t0425OutBlock1", "medosu    ", DataMngrUtil.TYPE_STR, 10 )                // 구분
        setFieldInfo( "t0425OutBlock1", "qty       ", DataMngrUtil.TYPE_INT,  9 )                // 주문수량
        setFieldInfo( "t0425OutBlock1", "price     ", DataMngrUtil.TYPE_INT,  9 )                // 주문가격
        setFieldInfo( "t0425OutBlock1", "cheqty    ", DataMngrUtil.TYPE_INT,  9 )                // 체결수량
        setFieldInfo( "t0425OutBlock1", "cheprice  ", DataMngrUtil.TYPE_INT,  9 )                // 체결가격
        setFieldInfo( "t0425OutBlock1", "ordrem    ", DataMngrUtil.TYPE_INT,  9 )                // 미체결잔량
        setFieldInfo( "t0425OutBlock1", "cfmqty    ", DataMngrUtil.TYPE_INT,  9 )                // 확인수량
        setFieldInfo( "t0425OutBlock1", "status    ", DataMngrUtil.TYPE_STR, 10 )                // 상태
        setFieldInfo( "t0425OutBlock1", "orgordno  ", DataMngrUtil.TYPE_INT, 10 )                // 원주문번호
        setFieldInfo( "t0425OutBlock1", "ordgb     ", DataMngrUtil.TYPE_STR, 20 )                // 유형
        setFieldInfo( "t0425OutBlock1", "ordtime   ", DataMngrUtil.TYPE_STR,  8 )                // 주문시간
        setFieldInfo( "t0425OutBlock1", "ordermtd  ", DataMngrUtil.TYPE_STR, 10 )                // 주문매체
        setFieldInfo( "t0425OutBlock1", "sysprocseq", DataMngrUtil.TYPE_INT, 10 )                // 처리순번
        setFieldInfo( "t0425OutBlock1", "hogagb    ", DataMngrUtil.TYPE_STR,  2 )                // 호가유형
        setFieldInfo( "t0425OutBlock1", "price1    ", DataMngrUtil.TYPE_INT,  8 )                // 현재가
        setFieldInfo( "t0425OutBlock1", "orggb     ", DataMngrUtil.TYPE_STR,  2 )                // 주문구분
        setFieldInfo( "t0425OutBlock1", "singb     ", DataMngrUtil.TYPE_STR,  2 )                // 신용구분
        setFieldInfo( "t0425OutBlock1", "loandt    ", DataMngrUtil.TYPE_STR,  8 )                // 대출일자
    }
}
