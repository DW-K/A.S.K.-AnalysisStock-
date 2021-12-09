package com.gachon.ask.xingapi;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ebest.api.DataPacket;
import com.ebest.api.LinkData;
import com.ebest.api.MsgPacket;
import com.ebest.api.RealPacket;
import com.ebest.api.ReleasePacket;
import com.ebest.api.SocketManager;
//import com.whykeykey.briefsign.WKKException;
//import com.whykeykey.briefsign.WKKHex;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import kotlin.Triple;

import com.gachon.ask.R;


public class s1001_j extends Fragment {

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

    static int m_nHandle = -1;
    static ProcMessageHandler m_handler = null;
    class ProcMessageHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {

            int msg_type = (int)msg.what;
            switch (msg_type)
            {
                case RECEIVE_DATA:
                {
                    DataPacket lpDp = (DataPacket) msg.obj;
                    if (lpDp.getStrTRCode().equals("t1301")) {
                        processT1301(lpDp.getStrBlockName(), lpDp.getPData());
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
                case RECEIVE_REALDATA:
                {
                    RealPacket lpRp = (RealPacket)msg.obj;
                    if (lpRp.getStrBCCode().equals("S3_") || lpRp.getStrBCCode().equals("K3_"))
                    {
                        processSK3_(lpRp.getStrBCCode(), lpRp.getStrKeyCode(), lpRp.getPData() );
                    }
                }
                break;
                case RECEIVE_MSG : {
                    MsgPacket lpMp = (MsgPacket)msg.obj;
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            lpMp.getStrTRCode() + " " + lpMp.getStrMsgCode() + lpMp.getStrMessageData(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
                break;
                // 일반적인 에러
                case  RECEIVE_ERROR : {
                    String strMsg = (String)msg.obj;
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

    private String m_strJongmokCode = "";
    private TableGrid.DataAdapter m_adapter = new TableGrid().new DataAdapter(R.layout.s1001_item01);
    private GridView m_gridView ;
    private MainView mainView;
    private SocketManager manager;
    private View root = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.activity_s1001,null);


        root.findViewById(R.id.button).setOnClickListener(new Button.OnClickListener() {
             @Override
             public void onClick(View view) {
                 onClickQueryButton();
             }
          }
        );

        m_gridView = (GridView)root.findViewById(R.id.grid_view) ;
        m_gridView.setAdapter(m_adapter);

        // get SocketManager instance
        mainView = (MainView)getActivity();
        m_handler = new ProcMessageHandler();
        manager = ((ApplicationManager)getActivity().getApplication()).getSockInstance();



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

    private void onClickQueryButton()
    {
        EditText edit = (EditText) root.findViewById(R.id.editText2);

        String temp =  edit.getText().toString();
        if(temp.length() < 6)
        {
            Toast.makeText(
                    getActivity().getApplicationContext(),
                    "종목코드를 확인해 주십시오.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        /* 초기화 및 실시간 등록 해제*/
        m_adapter.resetItems();
        m_adapter.notifyDataSetChanged();
        boolean bOK = manager.deleteRealData(m_nHandle, "S3_", m_strJongmokCode, 6);

        /* 신규 등록 */
        m_strJongmokCode = temp;
        manager.requestData(m_nHandle, "t1301", m_strJongmokCode, false, "", 30);

    }

    private void processT1301(String strBlockName , byte[] pData) {
        if (strBlockName.equals("t1301OutBlock1")) {
            processT1301OutBlock1(pData);
            boolean bOK = manager.addRealData(m_nHandle, "S3_", m_strJongmokCode, 6);
        }
    }
    private void processT1301OutBlock1(byte[] pData) {

        String[][] map;
        byte[][] pArray;

        /*
        방법 1. res 폴더에 TR정보가 담긴 *.res 파일이 있는 경우
        map = manager.getOutBlockDataFromByte("t1301", "t1301OutBlock1", pData);
        pArray = manager.getAttributeFromByte("t1301", "t1301OutBlock1", pData); // attribute
        */

        /* 방법2. 프로젝트의 TR정보가 담긴 소스(.kt, .java ... *현재 프로젝트의 TRCODE.kt )를 사용하는 경우  */
        Boolean bAttributeInData = true;
        /* TRCODE.kt에서 데이터 구조 확인 */
        map = manager.getDataFromByte(pData, TRCODE.n1301col, bAttributeInData);
        pArray = manager.getAttributeFromByte(pData, TRCODE.n1301col);// attribute


        assert pArray != null;
        /* 읽어온 데이터 처리 */
        if( map != null) {
            for (int i = 0; i < map.length; i++) {

                List<Triple<TableGrid.TYPE, Object, Integer>> tempdata = Arrays.asList(
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.STRING,
                                manager.getTimeFormat(map[i][TRCODE.T1301.CHETIME.ordinal()]),
                                R.id.view1),
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.STRING,
                                manager.getCommaValue(map[i][TRCODE.T1301.PRICE.ordinal()]),
                                R.id.view2),
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.DAEBI,
                                pArray[i][TRCODE.T1301.PRICE.ordinal()],
                                R.id.view2),
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.ICON,
                                map[i][TRCODE.T1301.SIGN.ordinal()],
                                R.id.view3_1),
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.STRING,
                                manager.getCommaValue(map[i][TRCODE.T1301.CHANGE.ordinal()]),
                                R.id.view3_2),
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.DAEBI,
                                (pArray[i][TRCODE.T1301.CHANGE.ordinal()]),
                                R.id.view3_2),
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.DOUBLE,
                                (map[i][TRCODE.T1301.CVOLUME.ordinal()]),
                                R.id.view4),
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.DAEBI,
                                (pArray[i][TRCODE.T1301.CVOLUME.ordinal()]),
                                R.id.view4),
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.STRING,
                                manager.getCommaValue(map[i][TRCODE.T1301.VOLUME.ordinal()]),
                                R.id.view5)
                );

                m_adapter.addItem(tempdata);
            }

            /* JSON 전환 */
            org.json.JSONObject json = manager.getJSONValue("t1301", pData);
            org.json.JSONObject json2 = manager.getJSONValue("t1301", "t1301OutBlock1", pData);

            m_adapter.notifyDataSetChanged();
        }
    }

    private void processSK3_(String strTrCode, String strKeyCode, byte[] pData)
    {
        String[][] map;
        byte[][] pArray;

        /* 방법 1. res 폴더에 TR정보가 담긴 *.res 파일이 있는 경우
        map = manager.getOutBlockDataFromByte(strTrCode, "OutBlock", pData);
        pArray = manager.getAttributeFromByte(strTrCode, "OutBlock", pData); // attribute
        */

        /* 방법2. 프로젝트의 TR정보가 담긴 소스(.kt, .java ... *현재 프로젝트의 TRCODE.kt )를 사용하는 경우  */
        boolean bAttributeInData = true;
        map = manager.getDataFromByte(pData, TRCODE.nS_K_3col, bAttributeInData);
        pArray = manager.getAttributeFromByte(pData, TRCODE.nS_K_3col); // attribute

        assert pArray != null;

        /* 데이터 처리 */
        if( map != null) {
            for (int i = 0; i < map.length; i++) {

                List<Triple<TableGrid.TYPE, Object, Integer>> tempdata = Arrays.asList(
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.STRING,
                                manager.getTimeFormat(map[i][TRCODE.S_K_3_.CHETIME.ordinal()]),
                                R.id.view1),
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.STRING,
                                manager.getCommaValue(map[i][TRCODE.S_K_3_.PRICE.ordinal()]),
                                R.id.view2),
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.DAEBI,
                                pArray[i][TRCODE.S_K_3_.PRICE.ordinal()],
                                R.id.view2),
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.ICON,
                                manager.getCommaValue(map[i][TRCODE.S_K_3_.SIGN.ordinal()]),
                                R.id.view3_1),
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.STRING,
                                manager.getCommaValue(map[i][TRCODE.S_K_3_.CHANGE.ordinal()]),
                                R.id.view3_2),
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.DAEBI,
                                pArray[i][TRCODE.S_K_3_.CHANGE.ordinal()],
                                R.id.view3_2),
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.DOUBLE,
                                map[i][TRCODE.S_K_3_.CVOLUME.ordinal()],
                                R.id.view4),
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.DAEBI,
                                pArray[i][TRCODE.S_K_3_.CVOLUME.ordinal()],
                                R.id.view4),
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.STRING,
                                manager.getCommaValue(map[i][TRCODE.S_K_3_.VOLUME.ordinal()]),
                                R.id.view5)

                );

                m_adapter.addItem(0, tempdata);
                m_adapter.notifyDataSetChanged();   //데이터 갱신을 알린다.
            }
        }
    }
}
