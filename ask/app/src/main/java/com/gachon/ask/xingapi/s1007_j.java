package com.gachon.ask.xingapi;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ebest.api.DataPacket;
import com.ebest.api.MsgPacket;
import com.ebest.api.RealPacket;
import com.ebest.api.ReleasePacket;
import com.ebest.api.SocketManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import kotlin.Triple;
import com.gachon.ask.R;

public class s1007_j extends Fragment {

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
    class ProcMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            int msg_type = (int)msg.what;
            switch (msg_type)
            {
                case RECEIVE_DATA:
                {
                    DataPacket lpDp = (DataPacket) msg.obj;
                    if (lpDp.getStrTRCode().equals("CSPAQ12300")) {
                        processCSPAQ12300(lpDp.getPData());
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
    private TableGrid.DataAdapter m_adapter = new TableGrid().new DataAdapter(R.layout.s1007_item01);
    private GridView m_gridView ;
    private MainView mainView;
    private SocketManager manager;
    private View root = null;

    private EditText m_editTextAccount;
    private TextView m_textViewAmt;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.activity_s1007, null);

        // get SocketManager instance
        mainView = (MainView)getActivity();
        m_handler = new ProcMessageHandler();
        manager = ((ApplicationManager) getActivity().getApplication()).getSockInstance();

        m_gridView = (GridView)root.findViewById(R.id.gridViewList) ;
        m_gridView.setAdapter(m_adapter);


        m_editTextAccount = root.findViewById(R.id.editTextAccount);
        m_textViewAmt = root.findViewById(R.id.textViewAmt);

        root.findViewById(R.id.buttonQuery).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                m_adapter.resetItems();
                m_adapter.notifyDataSetChanged();

                // 레코드갯수(5) , 계좌번호(20) , 입력비밀번호(8) , 잔고생성구분(1) , 수수료적용구분(1) , D2잔고기준조회구분(1) , 단가구분(1)
                String strRecord = "00001";
                String strAccount = m_editTextAccount.getText().toString();
                strAccount = manager.makeSpace(strAccount, 20);

                String strInBlock = strRecord + strAccount + "0000    " + "0" + "0" + "0" + "0";
                //int nRqID = manager.requestDataAccount(m_nHandle, "CSPAQ12300", strInBlock, 0, 'B', "", false, false, false, false, "", 30);
                int nRqID = manager.requestData(m_nHandle, "CSPAQ12300", strInBlock, false, "", 30);
             }
          }
        );


        return root;
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

    // 주식잔고
    private void processCSPAQ12300(byte[] pData) {

        if (pData.length == 0)
            return;

        String[][] s1;
        String[][] s2;
        String[][] s3;

        /*
        //방법 1. res 폴더에 TR정보가 담긴 *.res 파일이 있는 경우
        // 1-1. 특정 OutBlock 데이타만 get하는 경우
        String[][] map3 = manager.getOutBlockDataFromByte("CSPAQ12300", "CSPAQ12300OutBlock2", pData);
        String[][] map4 = manager.getOutBlockDataFromByte("CSPAQ12300", "CSPAQ12300OutBlock3", pData);

        // 1-2. 전체 OutBlock 데이타 get하는 경우 ==> TR정보를 API모듈에서 읽어와 처리하는 경우
        HashMap<?, ?> map2 = manager.getTrStructDataFromByte("CSPAQ12300", pData);
        if (map2 != null) {
            Object o11 = map2.get("CSPAQ12300OutBlock1");
            Object o22 = map2.get("CSPAQ12300OutBlock2");
            Object o33 = map2.get("CSPAQ12300OutBlock3");

            // OutBlock별 데이터
            s1 = (String[][])o11;
            s2 = (String[][])o22;
            s3 = (String[][])o33;
        }
        */

        // 방법2. 전체 OutBlock 데이타 get하는 경우 ==> TR정보를 화면에서 직접 setting하는 경우
        String[] OutBlockName = new String[]{"CSPAQ12300OutBlock1", "CSPAQ12300OutBlock2", "CSPAQ12300OutBlock3"};
        boolean[] OutBlockOccursInfo = new boolean[]{false, false, true};
        int[][] OutBlockLenInfo = new int[][]{
                new int[]{5, 20, 8, 1, 1, 1, 1},
                new int[]{5, 40, 40, 16, 16, 16, 16, 16, 16, 16, 16, 16, 18, 20, 16, 16, 16, 16, 16, 8, 16, 16, 16, 16, 16, 16, 16, 15, 16, 19, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16},
                new int[]{12, 40, 2, 40, 16, 16, 16, 16, 21, 21, 16, 18, 15, 16, 8, 13, 16, 13, 16, 8, 13, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 15, 16, 2, 2, 16}
        };

        HashMap<?, ?>  map;
        map = manager.getDataFromByte(pData, OutBlockName, OutBlockOccursInfo, OutBlockLenInfo, false, "", "B");
        if (map == null)
            return;

        Object o1 = map.get(OutBlockName[0]);
        Object o2 = map.get(OutBlockName[1]);
        Object o3 = map.get(OutBlockName[2]);

        /* OutBlock별 데이터 */
        s1 = (String[][])o1;
        s2 = (String[][])o2;
        s3 = (String[][])o3;



        m_textViewAmt.setText(manager.getCommaValue((s2[0][4])));

        if( s3 != null) {
            for (int i = 0; i < s3.length; i++) {

                // 종목명 잔고수량 평균단가 현재가 평가손익
                List<Triple<TableGrid.TYPE, Object, Integer>> tempdata = Arrays.asList(
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.STRING,
                                s3[i][1],
                                R.id.textView1),
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.INT,
                                s3[i][4],
                                R.id.textView2),
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.DOUBLE,
                                s3[i][20],
                                R.id.textView3),
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.DOUBLE,
                                s3[i][12],
                                R.id.textView4),
                        new kotlin.Triple<TableGrid.TYPE, Object, Integer>(
                                TableGrid.TYPE.INT,
                                s3[i][28],
                                R.id.textView5)
                );


                m_adapter.addItem(tempdata);  //아이템을 추가한다.

                Bundle style = new Bundle();
                style.putInt(TableGrid.GRAVITY, Gravity.START);
                m_adapter.setCellStyle(i,0,style);

                style.putInt(TableGrid.GRAVITY, Gravity.END);
                m_adapter.setCellStyle(i,1,style);
                m_adapter.setCellStyle(i,2,style);
                m_adapter.setCellStyle(i,3,style);
                m_adapter.setCellStyle(i,4,style);
            }
            m_adapter.notifyDataSetChanged();   //데이터 갱신을 알린다.

        /* JSON 사용시
        JSONObject json = manager.getJSONValue("CSPAQ12300", pData);
        JSONObject json2 = manager.getJSONValue("CSPAQ12300", "CSPAQ12300OutBlock3", pData);
        JSONObject json3 = manager.getJSONValue("CSPAQ12300", "CSPAQ12300OutBlock2", pData);

        byte[] pEncodeData = Base64.encode(json.toString().getBytes(), Base64.NO_WRAP);
        */
        }
    }


}

