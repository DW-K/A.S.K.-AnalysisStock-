package com.gachon.ask.xingapi;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ebest.api.DataPacket;
import com.ebest.api.MsgPacket;
import com.ebest.api.RealPacket;
import com.ebest.api.ReleasePacket;
import com.ebest.api.SocketManager;

import java.util.ArrayList;
import java.util.HashMap;
import com.gachon.ask.R;
import com.gachon.ask.util.Firestore;

// 자바 관련은 2020.02 이후로 수정하지 않았음.

public class s1006_j extends Fragment {

    private static final int RECEIVE_INITECHERROR = -6;              // initech 핸드세이킹 에러
    private static final int RECEIVE_PERMISSIONERROR = -5;           // 퍼미션취소
    private static final int RECEIVE_ERROR = -4;                     // 일반적인 에러
    private static final int RECEIVE_SYSTEMERROR = -2;               // 서버에서 내려주는 시스템에러
    private static final int RECEIVE_DATA = 1;                       // TR데이타 수신
    private static final int RECEIVE_REALDATA = 2;                   // 실시간데이타 수신
    private static final int RECEIVE_MSG = 3;                        // TR메세지 수신
    private static final int RECEIVE_RECONNECT = 5  ;              // SOCKET종료후 재연결 완료
    private static final int RECEIVE_RELEASE = 8  ;                 // TR조회 완료
    private static final int RECEIVE_DISCONNECT = -3;               // SOCKET이 연결종료된 경우

    private static int m_nHandle = -1;

    private static ProcMessageHandler m_handler = null;

    @SuppressLint("HandlerLeak")
    private class ProcMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            int msg_type = (int) msg.what;
            switch (msg_type) {
                case RECEIVE_DATA: {
                    DataPacket lpDp = (DataPacket) msg.obj;
                    String trcode = lpDp.getStrTRCode();

                    if (trcode.equals("t1102")) {
                        processT1102(lpDp.getStrBlockName(), lpDp.getPData());
                    }
                    else if ((trcode.contains("CSPAT")) || (trcode.contains("CFOAT"))) {
                        processCSPAT_CFOAT(lpDp.getPData(), lpDp.getStrTRCode());
                    }
                }
                break;
                // TR조회 끝
                case RECEIVE_RELEASE: {
                    ReleasePacket lpDp = (ReleasePacket)msg.obj;

                    Integer nRqID = lpDp.getNRqID();
                    String strTrCode = lpDp.getStrTrCode();
                }
                break;
                case RECEIVE_REALDATA: {
                    RealPacket lpRp = (RealPacket) msg.obj;
                    if (lpRp.getStrBCCode().equals("")) {
                        //processSK3_(lpRp.getStrBCCode(), lpRp.getStrKeyCode(), lpRp.getPData());
                    }
                }
                break;
                case RECEIVE_MSG: {
                    MsgPacket lpMp = (MsgPacket) msg.obj;
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            lpMp.getStrTRCode() + " " + lpMp.getStrMsgCode() + lpMp.getStrMessageData(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
                break;
                // 일반적인 에러
                case RECEIVE_ERROR: {
                    String strMsg = (String) msg.obj;
                    Toast.makeText(getActivity().getApplicationContext(), strMsg, Toast.LENGTH_SHORT).show();
                }
                break;

                // 접속종료 또는 재연결
                case RECEIVE_DISCONNECT:
                case RECEIVE_RECONNECT: {
                    mainView.onMessage(msg);
                }
                default:
                    break;
            }
        }

    }

    private static String m_strJongmokCode = "";
    private EditText m_editTextAccount;
    private EditText m_editTextJongmok;
    private EditText m_editTextQty;
    private EditText m_editTextDanga;
    private TextView m_textViewJumunBunho;
    private RadioButton m_rb1;
    private RadioButton m_rb2;

    private static MainView mainView;
    private SocketManager manager;
    private View root = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.activity_s1006,null);     
        // get SocketManager instance
        mainView = (MainView)getActivity();
        m_handler = new ProcMessageHandler();
        manager = ((ApplicationManager)getActivity().getApplication()).getSockInstance();

        m_editTextAccount = root.findViewById(R.id.editTextAccount);
        m_editTextJongmok = root.findViewById(R.id.editTextJongmok);
        m_editTextQty = root.findViewById(R.id.editTextQty);
        m_editTextDanga = root.findViewById(R.id.editTextDanga);
        m_textViewJumunBunho = root.findViewById(R.id.textViewJumunBunho);

        m_rb1 = root.findViewById(R.id.radioButton1);
        m_rb2 = root.findViewById(R.id.radioButton2);



//        m_editTextJongmok.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//            // 입력되는 텍스트에 변화가 있을 때
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                int nLen = m_editTextJongmok.getText().length();
//                if (nLen == 6) {
//                    requestData();
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

        root.findViewById(R.id.buttoncancel).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //취소
                OnButtonCancelClicked();
            }
        });

        root.findViewById(R.id.buttonjungjung).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //정정
                OnButtonJungjungClicked();
            }
        });


        root.findViewById(R.id.buttonJumunList).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //주문내역
                OnButtonJumunListClicked();
            }
        });


        root.findViewById(R.id.buttonMaedo).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //매도
                OnButtonMaedoClicked();
            }
        });

        root.findViewById(R.id.buttonMaesu2).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //매수
                OnButtonMaesuClicked();
            }
        });
        return root;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        /* 화면 갱신시 핸들 재연결 */
        m_nHandle = manager.setHandler(mainView, (Handler)m_handler);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /* 해당 화면을 사용하지 않을떄 핸들값 삭제 */
        manager.deleteHandler(m_nHandle);
    }


    private void OnButtonMaesuClicked() {
        requestMaemae("2");
    }

    private void OnButtonMaedoClicked() {
        requestMaemae("1");
    }

    private void OnButtonJungjungClicked() {
        requestJungjung();
    }

    private void OnButtonCancelClicked() {
        requestCancel();
    }

    private void OnButtonJumunListClicked()
    {
        int nLen = m_editTextJongmok.getText().length();
        if (nLen == 6) {
            requestData();
        }
    }

    // 주식주문
    // 55501035473 , 55551023962
    private void requestMaemae(String strMaemaeGubun) {
        // 계좌번호(20) , 입력비밀번호(8) , 종목번호(12) , 주문수량(16) , 주문가(13.2) , 매매구분(1) , 호가유형코드(2) , 신용거래코드(3) , 대출일(8) , 주문조건구분(1)

        String strAccount = m_editTextAccount.getText().toString();
        String strJongmok = m_strJongmokCode;
        String strQty = m_editTextQty.getText().toString();
        String strDanga = m_editTextDanga.getText().toString();
        String strHogaCode = "";
        if (m_rb1.isChecked())
            strHogaCode = "00";         // 보통가
        else if (m_rb2.isChecked())
            strHogaCode = "03" ;        // 시장가

        strAccount = manager.makeSpace(strAccount, 20);
        strJongmok = manager.makeSpace(strJongmok, 12);

        strQty = manager.makeZero(strQty, 16);

        if( strDanga.length() == 0)
            strDanga ="0";

        strDanga = String.format("%.2f", java.lang.Double.parseDouble(strDanga));
        strDanga = manager.makeZero(strDanga, 13);

        //manager.setHeaderInfo(1, "1");
        String strInBlock = strAccount + "0000    " + strJongmok + strQty + strDanga + strMaemaeGubun + strHogaCode + "000" + "        " + "0";
        //int nRqID = manager.requestDataAccount(m_nHandle, "CSPAT00600", strInBlock, 0, 'B', "", false, false, false, false, "", 30);
        int nRqID = manager.requestData(m_nHandle, "CSPAT00600", strInBlock, false, "", 30);
    }

    // 주식정정
    // 55501035473 , 55551023962
    private void requestJungjung() {
        // 원주문번호(10) , 계좌번호(20) , 입력비밀번호(8) , 종목번호(12) , 주문수량(16) , 호가유형코드(2) , 주문조건구분(1) , 주문가(13.2)

        String strJumunBunho = m_textViewJumunBunho.getText().toString();
        String strAccount = m_editTextAccount.getText().toString();
        String strJongmok = m_strJongmokCode;
        String strQty = m_editTextQty.getText().toString();
        String strDanga = m_editTextDanga.getText().toString();
        String strHogaCode = "";
        if (m_rb1.isChecked())
            strHogaCode = "00"  ;       // 보통가
        else if (m_rb2.isChecked())
            strHogaCode = "03" ;        // 시장가

        strJumunBunho = manager.makeZero(strJumunBunho, 10);
        strAccount = manager.makeSpace(strAccount, 20);
        strJongmok = manager.makeSpace(strJongmok, 12);

        strQty = manager.makeZero(strQty, 16);

        if(strDanga.length() == 0)
            strDanga = "0";

        strDanga = String.format("%.2f", java.lang.Double.parseDouble(strDanga));
        strDanga = manager.makeZero(strDanga, 13);

        String strInBlock = strJumunBunho + strAccount + "0000    " + strJongmok + strQty + strHogaCode + "0" + strDanga;
        int nRqID = manager.requestData(m_nHandle, "CSPAT00700", strInBlock, false, "", 30);
    }

    // 주식취소
    // 55501035473 , 55551023962
    private void requestCancel() {
        // 원주문번호(10) , 계좌번호(20) , 입력비밀번호(8) , 종목번호(12) , 주문수량(16)

        String strJumunBunho = m_textViewJumunBunho.getText().toString();
        String strAccount = m_editTextAccount.getText().toString();
        String strJongmok = m_strJongmokCode;
        String strQty = m_editTextQty.getText().toString();

        strJumunBunho = manager.makeZero(strJumunBunho, 10);
        strAccount = manager.makeSpace(strAccount, 20);
        strJongmok = manager.makeSpace(strJongmok, 12);
        strQty = manager.makeZero(strQty, 16);

        String strInBlock = strJumunBunho + strAccount + "0000    " + strJongmok + strQty;
        int nRqID = manager.requestData(m_nHandle, "CSPAT00800", strInBlock, false, "", 30);
    }

    /*
    // 선물주문
    // 55501035473 , 55551023962
    private void requestSeonmulMaemae() {
        // 계좌번호(20) , 입력비밀번호(8) , 종목번호(12) , 매매구분(1) , 호가유형코드(2) , 주문가격(15.2) , 주문수량(16)

        String strAccount = m_editTextAccount.getText().toString();
        String strJongmok = "101P9000";
        String strQty = "1";
        String strDanga = "251.00";
        String strHogaCode = "00"  ;       // 보통가

        strAccount = manager.makeSpace(strAccount, 20);
        strJongmok = manager.makeSpace(strJongmok, 12);

        strQty = manager.makeZero(strQty, 16);

        if(strDanga.length() ==0);
            strDanga ="0";
        strDanga = String.format("%.2f", java.lang.Double.parseDouble(strDanga));
        strDanga = manager.makeZero(strDanga, 15);

        String strInBlock = strAccount + "0000    " + strJongmok + "2" + strHogaCode + strDanga + strQty;
        int nRqID = manager.requestData(m_nHandle, "CFOAT00100", strInBlock, false, "", 30);
    }

    // 선물정정
    // 55501035473 , 55551023962
    private void requestSeonmulJungjung() {
        // 계좌번호(20) , 입력비밀번호(8) , 종목번호(12) , 원주문번호(10) , 호가유형코드(2) , 주문가격(15.2) , 주문수량(16)

        String strJumunBunho = m_textViewJumunBunho.getText().toString();
        String strAccount = m_editTextAccount.getText().toString();
        String strJongmok = "101P9000";
        String strQty = "1";
        String strDanga = "250.95";
        String strHogaCode = "00"      ;   // 보통가

        strAccount = manager.makeSpace(strAccount, 20);
        strJongmok = manager.makeSpace(strJongmok, 12);
        strJumunBunho = manager.makeZero(strJumunBunho, 10);
        strQty = manager.makeZero(strQty, 16);

        if(strDanga.length() == 0)
            strDanga = "0";

        strDanga = String.format("%.2f", java.lang.Double.parseDouble(strDanga));
        strDanga = manager.makeZero(strDanga, 15);

        String strInBlock = strAccount + "0000    " + strJongmok + strJumunBunho + strHogaCode + strDanga + strQty;
        int nRqID = manager.requestData(m_nHandle, "CFOAT00200", strInBlock, false, "", 30);
    }

    // 선물취소
    // 55501035473 , 55551023962
    private void requestSeonmulCancel() {
        // 계좌번호(20) , 입력비밀번호(8) , 종목번호(12) , 원주문번호(10) , 취소수량(16)

        String strJumunBunho = m_textViewJumunBunho.getText().toString();
        String strAccount = m_editTextAccount.getText().toString();
        String strJongmok = "101P9000";
        String strQty = "1";

        strAccount = manager.makeSpace(strAccount, 20);
        strJongmok = manager.makeSpace(strJongmok, 12);
        strJumunBunho = manager.makeZero(strJumunBunho, 10);
        strQty = manager.makeZero(strQty, 16);

        String strInBlock = strAccount + "0000    " + strJongmok + strJumunBunho + strQty;
        int nRqID = manager.requestData(m_nHandle, "CFOAT00300", strInBlock, false, "", 30);
    }
    */


    private void requestData() {

        // 현재 수신받고 있는 종목의 실시간정보를 삭제한다.
        boolean bOK = manager.deleteRealData(m_nHandle, "S3_", m_strJongmokCode, 6);
        bOK = manager.deleteRealData(m_nHandle, "K3_", m_strJongmokCode, 6);
        m_strJongmokCode = m_editTextJongmok.getText().toString();
        int nRqID = manager.requestData(m_nHandle, "t1102", m_strJongmokCode, false, "", 0);
    }
    
    
    private void processT1102(String strBlockName, byte[] pData) {
        if (strBlockName.equals("t1102OutBlock"))
        {
            processT1102OutBlock(pData);
        }
    }

    private void processT1102OutBlock(byte[] pData) {


        String[][] map;
        byte[][] pArray;

        /*
        방법 1. res 폴더에 TR정보가 담긴 *.res 파일이 있는 경우
        map = manager.getOutBlockDataFromByte("t1102", "t1102OutBlock", pData);
        pArray = manager.getAttributeFromByte("t1102", "t1102OutBlock", pData); // attribute
        */

        /* 방법2. 프로젝트의 TR정보가 담긴 소스(.kt, .java ... *현재 프로젝트의 TRCODE.kt )를 사용하는 경우  */
        int[] nColLen = new int[]{20, 8, 1, 8, 6, 12, 8, 8, 8, 8, 12, 12, 8, 6, 8, 6, 8, 6, 8, 8, 8, 8, 6, 6, 6, 12, 8, 5, 3, 3, 6, 6, 8, 8, 8, 8, 6, 6, 3, 3, 6, 6, 8, 8, 8, 8, 6, 6, 3, 3, 6, 6, 8, 8, 8, 8, 6, 6, 3, 3, 6, 6, 8, 8, 8, 8, 6, 6, 3, 3, 6, 6, 8, 8, 8, 8, 6, 6, 12, 12, 6, 12, 12, 6, 6, 6, 12, 12, 8, 8, 8, 8, 8, 12, 12, 8, 2, 8, 12, 8, 10, 12, 12, 12, 12, 13, 10, 12, 12, 12, 12, 13, 7, 7, 7, 7, 7, 10, 10, 10, 12, 10, 6, 3, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 18, 18, 8, 8, 8, 1, 8, 1, 8, 10, 8, 8, 1, 1, 8};
        boolean bAttributeInData = true;
        map = manager.getDataFromByte(pData, nColLen, bAttributeInData);
        assert map != null;

        TextView jongmokName = root.findViewById(R.id.textViewName);
        jongmokName.setText(map[0][0].toString());

        EditText danga = root.findViewById(R.id.editTextDanga);
        danga.setText(map[0][1].toString());

    }
    private void processCSPAT_CFOAT(byte[] pData, String TRName) {

        String blockname = TRName + "OutBlock2";
        String[][] map;
        byte[][] pArray;

        /*
        //방법 1. res 폴더에 TR정보가 담긴 *.res 파일이 있는 경우
        map = manager.getOutBlockDataFromByte(TRName, blockname, pData);
        pArray = manager.getAttributeFromByte(TRName, blockname, pData); // attribute
        */

        //방법2. 프로젝트의 TR정보가 담긴 소스(.kt, .java ... *현재 프로젝트의 TRCODE.kt등 )를 사용하는 경우.
        String[] OutBlockName = null;
        boolean[] OutBlockOccursInfo = null;
        int[][] OutBlockLenInfo = null;
        HashMap<?, ?> hashmap = null;

        switch (blockname)
        {
            case "CSPAT00600OutBlock2":
            {
                // ex CSPAT00600.
                OutBlockName = new String[]{"CSPAT00600OutBlock1", "CSPAT00600OutBlock2"};
                OutBlockOccursInfo = new boolean[]{false, false};
                OutBlockLenInfo = new int[][]{
                new int[]{5, 20, 8, 12, 16, 13, 1,2,2,1,1,2,3,8,3,1,6,20,10,10,10,10,10,12,1,1},
                new int[]{5,10,9,2,2,9,9,16,10,10,10,16,16,16,16,16,40,40}
                };
            }
            break;
            case "CSPAT00700OutBlock2":
            {
                // ex CSPAT00700.
                OutBlockName = new String[]{"CSPAT00700OutBlock1", "CSPAT00700OutBlock2"};
                OutBlockOccursInfo = new boolean[]{false, false};
                OutBlockLenInfo = new int[][]{
                new int[]{5, 20, 8, 12, 16, 2,1,13,2,6,20,10,10,10,10,10},
                new int[]{5,10,10,9,2,2,9,2,1,1,3,8,1,1,9,16,1,10,10,10,16,16,16,40,40}
                };
            }
            break;
            case "CSPAT00800OutBlock2":
            {
                // ex CSPAT00800.
                OutBlockName = new String[]{"CSPAT00800OutBlock1", "CSPAT00800OutBlock2"};
                OutBlockOccursInfo = new boolean[]{false, false};
                OutBlockLenInfo = new int[][]{
                new int[]{5,10,20,8,12,16,2,20,6,10,10,10,10,10},
                new int[]{5,10,10,9,2,2,9,2,1,1,3,8,1,1,9,1,10,10,10,40,40}
                };

            }
            break;
        }
        hashmap = manager.getDataFromByte(pData, OutBlockName, OutBlockOccursInfo, OutBlockLenInfo, false, "", "B");
        Object o1 = hashmap.get(OutBlockName[0]);
        Object o2 = hashmap.get(OutBlockName[1]);
        // OutBlock별 데이터
        String[][] s1;
        String[][] s2;
        s1 = (String[][])o1;
        s2 = (String[][])o2;
        map = s2;


        if(map != null) {
            String strJumunBunho = map[0][1].toString();
            m_textViewJumunBunho.setText(strJumunBunho);

        }

    }

}