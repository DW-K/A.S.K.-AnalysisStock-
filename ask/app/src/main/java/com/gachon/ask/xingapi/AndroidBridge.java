package com.gachon.ask.xingapi;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.ebest.api.DataPacket;
import com.ebest.api.MsgPacket;
import com.ebest.api.RealPacket;
import com.ebest.api.ReleasePacket;
import com.ebest.api.SocketManager;
import org.json.JSONObject;

public class AndroidBridge {

    final int RECEIVE_TIMEOUTERROR = -7;               // TIMEOUT 에러
    final int RECEIVE_INITECHERROR = -6;               // initech 핸드세이킹 에러
    final int RECEIVE_PERMISSIONERROR = -5;               // 퍼미션취소
    final int RECEIVE_ERROR = -4;               // 일반적인 에러
    final int RECEIVE_DISCONNECT = -3;               // SOCKET이 연결종료된 경우
    final int RECEIVE_SYSTEMERROR = -2;               // 서버에서 내려주는 시스템에러
    final int RECEIVE_CONNECTERROR = -1;               // SOCKET 연결에러
    final int RECEIVE_CONNECT = 0;                 // SOCKET 연결완료
    final int RECEIVE_DATA = 1;                // TR데이타 수신
    final int RECEIVE_REALDATA = 2;                // 실시간데이타 수신
    final int RECEIVE_MSG = 3;                // TR메세지 수신
    final int RECEIVE_LOGINCOMPLETE = 4;                // 로그인완료
    final int RECEIVE_RECONNECT = 5;                // SOCKET종료후 재연결 완료
    final int RECEIVE_SIGN = 6;                // 선택한 공인인증서 관련 정보
    final int RECEIVE_RELEASE = 8  ;                 // TR조회 완료

    private Activity m_activity;
    private WebView m_webView;
    private Context m_conText;
    private ProcMessageHandler handler = new ProcMessageHandler();
    private SocketManager manager;
    private int m_nHandle = -1;

    // 생성자
    public AndroidBridge(WebView webView, Context conText, Activity activity ,SocketManager sm) {
        m_webView = webView;
        m_conText = conText;
        m_activity = activity;
        manager = sm;

        // 다음과 같이 WebView의 setWebChromeClient를 이용한다. Javascript의 alert메소드 발생시 이벤트이다.
        /*
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(final WebView view, final String url, final String message, JsResult result) {
                Log.d("JsAlert", "onJsAlert(!" + view + ", " + url + ", " + message + ", " + result + ")");
                Toast.makeText(m_conText, message, Toast.LENGTH_SHORT).show();
                result.confirm();
                return true; // I handled it
            }
        });
        */

    }

    class ProcMessageHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                // 일반적인 에러
                case RECEIVE_ERROR : {
                    String strMsg = (String) msg.obj;
                    //Toast.makeText(m_Activity.getApplicationContext(), strMsg, Toast.LENGTH_SHORT).show();
                    m_webView.loadUrl("javascript:receiveAppMessageData('" + strMsg + "')");        // 자바스크립트 호출
                    break;
                }
                // SOCEKT이 연결이 끊어졌다.
                case RECEIVE_DISCONNECT :
                    break;

                // 서버에서 보내는 시스템 ERROR
                case RECEIVE_SYSTEMERROR :
                    break;

                // SOCKET연결이 실패했다.
                case RECEIVE_CONNECTERROR :
                    break;

                // SOCKET연결이 성공했다.
                case RECEIVE_CONNECT :
                    break;

                // TR데이타
                case RECEIVE_DATA : {
                    DataPacket lpDp = (DataPacket) msg.obj;
                    int nRqID = lpDp.getNRqID();
                    String strCode = lpDp.getStrTRCode();
                    String strBlockName = lpDp.getStrBlockName();
                    byte[] bHeaderType = new byte[1];
                    bHeaderType[0] = lpDp.getBHeaderType();
                    String strHeaderType = new String(bHeaderType);
                    byte[] pData = lpDp.getPData();

                    // 방법1 JSON 객체로 넘기는 경우
                    /*
                    JSONObject json = manager.getJSONValue(strCode, "", pData);
                    if (json == null) {
                        json = manager.getJSONValue(strCode, strBlockName, pData);
                        if (json == null) return;
                    }
                    String strJSONData = json.toString();
                    m_webView.loadUrl("javascript:receiveAppQueryData(" + nRqID + ", '" + strCode + "', '" + strBlockName + "', '" +  strJSONData +  "', '"  + strHeaderType + "')");        // 자바스크립트 호출
                    break;
                    */

                    // 방법2 BASE64로 넘기는 경우
                    /*
                    String strEncodeData = Base64.encodeToString(pData, Base64.NO_WRAP);
                    m_webView.loadUrl("javascript:receiveAppQueryData(" + nRqID + ", '" + strCode + "', '" + strBlockName + "', '" +  strEncodeData +  "', '"  + strHeaderType + "')");        // 자바스크립트 호출
                    break;
                    */

                    // 방법3 JSON + BASE64로 넘기는 경우
                    JSONObject json = manager.getJSONValue(strCode, "", pData);
                    if (json == null) {
                        json = manager.getJSONValue(strCode, strBlockName, pData);
                        if (json == null) return;
                    }
                    String strJSONData = json.toString();

                    String strEncodeData = Base64.encodeToString(strJSONData.getBytes(), Base64.NO_WRAP);
                    m_webView.loadUrl("javascript:receiveAppQueryData(" + nRqID + ", '" + strCode + "', '" + strBlockName + "', '" +  strEncodeData +  "', '"  + strHeaderType + "')");        // 자바스크립트 호출
                    break;

                }

                // TR조회 끝
                case RECEIVE_RELEASE: {
                    ReleasePacket lpDp = (ReleasePacket)msg.obj;

                    Integer nRqID = lpDp.getNRqID();
                    String strTrCode = lpDp.getStrTrCode();
                }
                break;
                // REAL데이타
                case RECEIVE_REALDATA : {
                    RealPacket lpRp = (RealPacket) msg.obj;
                    String strCode = lpRp.getStrBCCode();
                    String strKey = lpRp.getStrKeyCode();
                    byte[] pData = lpRp.getPData();

                    // 방법1 JSON 객체로 넘기는 경우
                    /*
                    JSONObject json = manager.getJSONValue(strCode, "", pData);
                    if (json == null) {
                        json = manager.getJSONValue(strCode, "", pData);
                        if (json == null) return;
                    }
                    String strJSONData = json.toString();
                    m_webView.loadUrl("javascript:receiveAppRealData('" + strCode + "', '" + strKey + "', '" + strJSONData + "')");        // 자바스크립트 호출
                    break;
                    */

                    // 방법2 BASE64로 넘기는 경우
                    /*
                    String strEncodeData = Base64.encodeToString(pData, Base64.NO_WRAP);
                    m_webView.loadUrl("javascript:receiveAppRealData('" + strCode + "', '" + strKey + "', '" + strEncodeData + "')");        // 자바스크립트 호출
                    break;
                    */

                    // 방법3 JSON + BASE64로 넘기는 경우
                    JSONObject json = manager.getJSONValue(strCode, "", pData);
                    if (json == null) {
                        json = manager.getJSONValue(strCode, "", pData);
                        if (json == null) return;
                    }
                    String strJSONData = json.toString();

                    String strEncodeData = Base64.encodeToString(strJSONData.getBytes(), Base64.NO_WRAP);
                    m_webView.loadUrl("javascript:receiveAppRealData('" + strCode + "', '" + strKey + "', '" + strEncodeData + "')");        // 자바스크립트 호출
                    break;

                }

                // TR메세지
                case RECEIVE_MSG : {
                    MsgPacket lpMp = (MsgPacket) msg.obj;
                    //Toast.makeText(m_Activity.getApplicationContext(), lpMp.strTRCode + " " + lpMp.strMsgCode + lpMp.strMessageData, Toast.LENGTH_SHORT).show();
                    String strCode = lpMp.getStrTRCode();
                    String strMsg = lpMp.getStrMsgCode() + lpMp.getStrMessageData();
                    m_webView.loadUrl("javascript:receiveAppQueryMessageData('" + strCode + "', '" + strMsg + "')");        // 자바스크립트 호출
                    break;
                }

                // LOGIN이 완료됐다.
                //case RECEIVE_LOGINCOMPLETE : {
                //    //Toast.makeText(m_Activity.getApplicationContext(), "로그인완료", Toast.LENGTH_SHORT).show();
                //    String strCode = "login";
                //    String strMsg = "로그인완료";
                //    m_webView.loadUrl("javascript:receiveAppQueryMessageData('" + strCode + "', '" + strMsg + "')");        // 자바스크립트 호출
                //    break;
                //}

                default:
                    break;
            }
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandle(int nHandle) {
        m_nHandle = nHandle;
    }

    public void loginCompleted() {
        String strCode = "login";
        String strMsg = "로그인완료";
        m_webView.loadUrl("javascript:receiveAppQueryMessageData('" + strCode + "', '" + strMsg + "')");        // 자바스크립트 호출
    }

    // exitApp
    //@JavascriptInterface
    //public void exitApp() {
    //    m_commActivity.exitApp();
    //}

    // LOGIN
    @JavascriptInterface
    public void loginPopup(String strTitle) {
        //manager.loginPopup(m_activity, handler, strTitle, null);
        manager.loginPopupID(m_activity,handler);
    }


    // 조회데이타 요청
    @JavascriptInterface
    public int requestData(final String strCode, final String strData, final boolean bNext, final String strContinueKey, final int nTimeOut) {
        return manager.requestData(m_nHandle, strCode, strData, bNext, strContinueKey, nTimeOut);
    }

    // 조회데이타 요청(JSON)
    @JavascriptInterface
    public int requestDataJSON(final String strCode, final String strData, final boolean bNext, final String strContinueKey, final int nTimeOut) {
        try {
            JSONObject json = new JSONObject(strData);
            return manager.requestData(m_nHandle, strCode, json, bNext, strContinueKey, nTimeOut);
        } catch (Exception e) {

        }

        return -1;
    }

    // 실시간데이타 요청
    @JavascriptInterface
    public boolean addRealData(final String strCode, final String strData, final int nLength) {
        return manager.addRealData(m_nHandle, strCode, strData, nLength);
    }

    // 실시간데이타 요청삭제
    @JavascriptInterface
    public boolean deleteRealData(final String strCode, final String strData, final int nLength) {
        return manager.deleteRealData(m_nHandle, strCode, strData, nLength);
    }

    // 자바스크립트에서 수신한 메세지를 보여준다
    @JavascriptInterface
    public void toast(final String strMsg) {
        Toast.makeText(m_activity, strMsg, Toast.LENGTH_SHORT).show();
    }


}


