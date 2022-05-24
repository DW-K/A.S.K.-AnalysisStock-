
function eBest_App()
{
	this.curJongmokCode = '';
	this.curJongmokName = '';
}

var theApp = new eBest_App();


eBest_App.prototype.eBestLoginPopup = function(strTitle)
{
    window.eBestApp.loginPopup(strTitle);
};


eBest_App.prototype.eBestrequestData = function(strCode, strData, bNext, strContinueKey, nTimeOut)
{
    if (bNext == undefined) {
        nNext = false;
        strContinueKey = "";
    }

    if (nTimeOut == undefined)
        nTimeOut = 30;

    return window.eBestApp.requestData(strCode, strData, bNext, strContinueKey, nTimeOut);
};

eBest_App.prototype.eBestrequestDataJSON = function(strCode, json, bNext, strContinueKey, nTimeOut)
{
    if (bNext == undefined) {
        nNext = false;
        strContinueKey = "";
    }

    if (nTimeOut == undefined)
        nTimeOut = 30;

    var strData = JSON.stringify(json);
    return window.eBestApp.requestDataJSON(strCode, strData, bNext, strContinueKey, nTimeOut);
};

eBest_App.prototype.eBestaddRealData = function(strCode, strData, nLength)
{
    return window.eBestApp.addRealData(strCode, strData, nLength);
};

eBest_App.prototype.eBestdeleteRealData = function(strCode, strData, nLength)
{
    return window.eBestApp.deleteRealData(strCode, strData, nLength);
};

eBest_App.prototype.toast = function(strMsg)
{
    return window.eBestApp.toast(strMsg);
};


// 앱에서 호출한다(조회이외의 메세지)
function receiveAppMessageData(strData){
    //alert(strData);
    theApp.toast(strData);
}

// 앱에서 호출한다(조회관련 메세지)
function receiveAppQueryMessageData(strCode, strData){

    // 로그인완료
    if (strCode == "login") {
        //alert(strData);
    } else {
        //alert(strData + "(" + strCode + ")");
        theApp.toast(strData + "(" + strCode + ")");
    }
}


// 앱에서 호출한다(조회데이타 수신)
function receiveAppQueryData(nRqID, strCode, strBlockName, strData, strHeaderType){

    //alert(nRqID + "/" + strCode + "/" + strBlockName + "/" + strHeaderType + "/" + strData);
    theApp.toast(nRqID + "/" + strCode + "/" + strBlockName + "/" + strHeaderType + "/" + strData);

    // 방법1 JSON객체로 넘어온 경우
    /*
    if (strBlockName == "t1301OutBlock1") {
        var strOutBlockData = JSON.parse(strData);
        var nCount = strOutBlockData[strBlockName].length;
        var strText = "";
        for (var i=0; i<=nCount-1; i++) {
            var ob = strOutBlockData[strBlockName];
            var strTime = ob[i].chetime;        // 체결시간
            var strPrice = ob[i].price;         // 현재가
            var strSign = ob[i].sign;           // 전일대비구분
            var strChange = ob[i].change;       // 전일대비
            var strDiff = ob[i].diff;           // 등락율
            var strCvolume = ob[i].cvolume;     // 체결수량
            var strVolume = ob[i].volume;       // 누적수량
            strText = strText + strTime + "," + strPrice + "," + strSign + "," + strChange + "," + strDiff + "," + strCvolume + "," + strVolume + "\n";
        }
        document.form1.textquery.value = strText;
        theApp.eBestaddRealData("S3_", theApp.curJongmokCode, theApp.curJongmokCode.length);
    }
    */

    // 방법2 BASE64로 넘어온 경우
    /*
    if (strBlockName == "t1301OutBlock1") {
        var buf = new Uint8Array(10000);
        var size = Base64.atobArray(strData, buf);
        var nRowCount = size / (125+14);     // 139 : t1301OutBlock size
        var offset = 0;
        var strText = "";
        for (var i=0; i<=nRowCount-1; i++) {
            var nLen = 10;
            var strTime = getString(buf, offset, nLen);      // 체결시간
            offset = offset + nLen;
            if (strHeaderType == "A") {
                offset = offset + 1;
            }

            nLen = 8;
            var strPrice = getString(buf, offset, nLen);     // 현재가
            offset = offset + nLen;
            if (strHeaderType == "A")
                offset = offset + 1;


            nLen = 1;
            var strSign = getString(buf, offset, nLen);     // 전일대비구분
            offset = offset + nLen;
            if (strHeaderType == "A")
                offset = offset + 1;


            nLen = 8;
            var strChange = getString(buf, offset, nLen);     // 전일대비
            offset = offset + nLen;
            if (strHeaderType == "A")
                offset = offset + 1;


            nLen = 6;
            var strDiff = getString(buf, offset, nLen);     // 등락율
            offset = offset + nLen;
            if (strHeaderType == "A")
                offset = offset + 1;


            nLen = 12;
            var strCvolume = getString(buf, offset, nLen);     // 체결수량
            offset = offset + nLen;
            if (strHeaderType == "A")
                offset = offset + 1;

            offset = offset + 8;                            // 체결강도
            if (strHeaderType == "A")
                offset = offset + 1;

            nLen = 12;
            var strVolume = getString(buf, offset, nLen);     // 누적수량
            offset = offset + nLen;
            if (strHeaderType == "A")
                offset = offset + 1;

            offset = offset + 60;                           // 매도체결수량 , 매도체결건수 , 매수체결수량 , 매수체결건수 , 순체결량 , 순체결건수
            if (strHeaderType == "A")
                offset = offset + 6;

            strText = strText + strTime + "," + strPrice + "," + strSign + "," + strChange + "," + strDiff + "," + strCvolume + "," + strVolume + "\n";
        }
        document.form1.textquery.value = strText;
        theApp.eBestaddRealData("S3_", theApp.curJongmokCode, theApp.curJongmokCode.length);
    }
    */

    // 방법3 JSON + BASE64로 넘어온 경우
    if (strBlockName == "t1301OutBlock1") {
        var buf = Base64.atob(strData);
        var strOutBlockData = JSON.parse(buf);
        var nCount = strOutBlockData[strBlockName].length;
        var strText = "";
        for (var i=0; i<=nCount-1; i++) {
            var ob = strOutBlockData[strBlockName];
            var strTime = ob[i].chetime;        // 체결시간
            var strPrice = ob[i].price;         // 현재가
            var strSign = ob[i].sign;           // 전일대비구분
            var strChange = ob[i].change;       // 전일대비
            var strDiff = ob[i].diff;           // 등락율
            var strCvolume = ob[i].cvolume;     // 체결수량
            var strVolume = ob[i].volume;       // 누적수량
            strText = strText + strTime + "," + strPrice + "," + strSign + "," + strChange + "," + strDiff + "," + strCvolume + "," + strVolume + "\n";
        }
        document.form1.textquery.value = strText;
        theApp.eBestaddRealData("S3_", theApp.curJongmokCode, theApp.curJongmokCode.length);

    }
}

// 앱에서 호출한다(실시간 데이타 수신)
function receiveAppRealData(strCode, strKey, strData){
    //alert(strCode + "/" + strKey + "/" + strData);
    //theApp.toast(strCode + "/" + strKey + "/" + strData);

    // 방법1 JSON객체로 넘어온 경우
    /*
    if (strCode == "S3_") {
        var strOutBlockData = JSON.parse(strData);
        //var nCount = strOutBlockData["OutBlock"].length;
        var nCount = 1;
        var strText = "";
        for (var i=0; i<=nCount-1; i++) {
            var ob = strOutBlockData["OutBlock"];
            var strTime = ob[i].chetime;        // 체결시간
            var strPrice = ob[i].price;         // 현재가
            var strSign = ob[i].sign;           // 전일대비구분
            var strChange = ob[i].change;       // 전일대비
            var strDiff = ob[i].drate;          // 등락율
            var strCvolume = ob[i].cvolume;     // 체결수량
            var strVolume = ob[i].volume;       // 누적수량
            strText = strText + strTime + "," + strPrice + "," + strSign + "," + strChange + "," + strDiff + "," + strCvolume + "," + strVolume + "\n";
        }
        document.form1.textreal.value = strText;
    }
    */

    // 방법2 BASE64로 넘어온 경우
    if (strCode == "S3_") {
        var buf = new Uint8Array(10000);
        var size = Base64.atobArray(strData, buf);
        var nRowCount = 1;
        var offset = 0;
        var strText = "";

        for (var i=0; i<=nRowCount-1; i++) {
            var nLen = 6;
            var strTime = getString(buf, offset, nLen);      // 체결시간
            offset = offset + nLen;
            offset = offset + 1;

            nLen = 1;
            var strSign = getString(buf, offset, nLen);     // 전일대비구분
            offset = offset + nLen;
            offset = offset + 1;


            nLen = 8;
            var strChange = getString(buf, offset, nLen);     // 전일대비
            offset = offset + nLen;
            offset = offset + 1;


            nLen = 6;
            var strDiff = getString(buf, offset, nLen);     // 등락율
            offset = offset + nLen;
            offset = offset + 1;

            nLen = 8;
            var strPrice = getString(buf, offset, nLen);     // 현재가
            offset = offset + nLen;
            offset = offset + 1;

            offset = offset + 6;                            // 시가시간
            offset = offset + 1;

            offset = offset + 8;                            // 시가
            offset = offset + 1;

            offset = offset + 6;                            // 고가시간
            offset = offset + 1;

            offset = offset + 8;                            // 고가
            offset = offset + 1;

            offset = offset + 6;                            // 저가시간
            offset = offset + 1;

            offset = offset + 8;                            // 저가
            offset = offset + 1;

            offset = offset + 1;                            // 체결구분
            offset = offset + 1;

            nLen = 8;
            var strCvolume = getString(buf, offset, nLen);     // 체결수량
            offset = offset + nLen;
            offset = offset + 1;

            nLen = 12;
            var strVolume = getString(buf, offset, nLen);     // 누적수량
            offset = offset + nLen;
            offset = offset + 1;

            offset = offset + 105;                           // 누적거래대금 , 매도누적체결량 , 매도누적체결건수 , 매수누적체결량 , 매수누적체결건수 , 체결강도 , 가중평균가 , 매도호가 , 매수호가 , 장정보 , 전일동시간대거래량 , 단축코드
            offset = offset + 12;

            strText = strText + strTime + "," + strPrice + "," + strSign + "," + strChange + "," + strDiff + "," + strCvolume + "," + strVolume + "\n";
        }
        document.form1.textreal.value = strText;
    }

    // 방법3 JSON + BASE64로 넘어온 경우
    if (strCode == "S3_") {
        var buf = Base64.atob(strData);
        var strOutBlockData = JSON.parse(buf);
        var nCount = 1;
        var strText = "";
        for (var i=0; i<=nCount-1; i++) {
            var ob = strOutBlockData["OutBlock"];
            var strTime = ob[i].chetime;        // 체결시간
            var strPrice = ob[i].price;         // 현재가
            var strSign = ob[i].sign;           // 전일대비구분
            var strChange = ob[i].change;       // 전일대비
            var strDiff = ob[i].drate;          // 등락율
            var strCvolume = ob[i].cvolume;     // 체결수량
            var strVolume = ob[i].volume;       // 누적수량
            strText = strText + strTime + "," + strPrice + "," + strSign + "," + strChange + "," + strDiff + "," + strCvolume + "," + strVolume + "\n";
        }
        document.form1.textreal.value = strText;
    }

}


function getString(buf, offset, size)
{
	var ret = '';

    for(var i=0; i<size; i++)
    {
        if(buf[offset+i]==0)
            break;
        else
            ret += String.fromCharCode(buf[offset+i]);
    }

	return ret;
}
