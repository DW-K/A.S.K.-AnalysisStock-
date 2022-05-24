package com.gachon.ask.xingapi

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.ebest.api.*

import com.ebest.api.rm.ResourceManager
import com.gachon.ask.datamngr.API_DEFINE

import java.util.ArrayList
import java.util.HashMap
import com.gachon.ask.R
import com.gachon.ask.util.Firestore

class s1006 : Fragment() {

    private var m_editTextAccount: EditText? = null
    private var m_editTextJongmok: EditText? = null
    private var m_editTextQty: EditText? = null
    private var m_editTextDanga: EditText? = null
    private var m_editAccPwd: EditText? = null

    private var m_textViewJumunBunho: TextView? = null
    private var m_rb1: RadioButton? = null
    private var m_rb2: RadioButton? = null
    private var manager: SocketManager? = null
    private var root: View? = null

    lateinit var m_combobox: Spinner

    internal var m_dn = ""

    @SuppressLint("HandlerLeak")
    private inner class ProcMessageHandler : Handler() {
        override fun handleMessage(msg: Message) {

            val msg_type = msg.what
            when (msg_type) {
                API_DEFINE.RECEIVE_DATA -> {
                    val lpDp = msg.obj as DataPacket
                    val trcode = lpDp.strTRCode

                    if (trcode == "t1102") {
                        processT1102(lpDp.strBlockName!!, lpDp.pData)
                    } else if (trcode!!.contains("CSPAT") || trcode.contains("CFOAT")) {
                        processCSPAT_CFOAT(lpDp.pData, lpDp.strTRCode)
                    }
                }
                // TR조회 끝
                API_DEFINE.RECEIVE_RELEASE -> {
                    val lpDp = msg.obj as ReleasePacket

                    lpDp.nRqID
                    lpDp.strTrCode
                }
                API_DEFINE.RECEIVE_REALDATA -> {
                    val lpRp = msg.obj as RealPacket
                    if (lpRp.strBCCode == "S3_" || lpRp.strBCCode == "K3_") {
                        //processS3_(lpRp.strKeyCode, lpRp.pData);
                    } else if (lpRp.strBCCode == "SC0" || lpRp.strBCCode == "SC1" || lpRp.strBCCode == "SC2" || lpRp.strBCCode == "SC3" || lpRp.strBCCode == "SC4") {
                        var pData = lpRp.pData
                        var nLen = pData!!.size

                        // 주식주문접수
                        if (lpRp.strBCCode == "SC0") {
                            processSC0(pData)

                            // 주식주문체결
                        } else if (lpRp.strBCCode == "SC1") {
                            processSC1(pData)

                            // 주식주문정정
                        } else if (lpRp.strBCCode == "SC2") {
                            processSC2(pData)

                            // 주식주문취소
                        } else if (lpRp.strBCCode == "SC3") {
                            processSC3(pData)

                            // 주식주문거부
                        } else if (lpRp.strBCCode == "SC4") {
                            processSC4(pData)
                        }
                    }
                }
                API_DEFINE.RECEIVE_MSG -> {
                    val lpMp = msg.obj as MsgPacket
                    Toast.makeText(
                        activity!!.applicationContext,
                        lpMp.strTRCode + " " + lpMp.strMsgCode + lpMp.strMessageData,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                // 일반적인 에러
                API_DEFINE.RECEIVE_ERROR -> {
                    val strMsg = msg.obj as String
                    Toast.makeText(activity!!.applicationContext, strMsg, Toast.LENGTH_SHORT).show()
                }

                // 접속종료 또는 재연결
                API_DEFINE.RECEIVE_DISCONNECT, API_DEFINE.RECEIVE_RECONNECT -> run { mainView!!.onMessage(msg) }

                else -> {
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.activity_s1006, null)
        // get SocketManager instance
        mainView = activity as MainView?
        m_handler = ProcMessageHandler()
        manager = (activity?.application as ApplicationManager).getSockInstance()

        m_editTextAccount = root!!.findViewById(R.id.editTextAccount)
        m_editTextJongmok = root!!.findViewById(R.id.editTextJongmok)
        m_editTextQty = root!!.findViewById(R.id.editTextQty)
        m_editTextDanga = root!!.findViewById(R.id.editTextDanga)
        m_editAccPwd = root!!.findViewById(R.id.editAccPwd)
        m_textViewJumunBunho = root!!.findViewById(R.id.textViewJumunBunho)

        //계좌번호
        m_combobox = root!!.findViewById(R.id.combo_acc) as Spinner
        var items = getAccountList();
        //val myAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        val myAdapter = ArrayAdapter(root!!.context, R.layout.spinneritem, items)
        m_combobox.adapter = myAdapter


        m_combobox.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                var t = (parent.getChildAt(0) as TextView)
                //t.setTextColor(Color.BLUE)
                //(parent.getChildAt(0) as TextView).textSize = 10f
                t.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceManager.calcFontSize(t.textSize.toInt()))

                m_dn = m_combobox.getItemAtPosition(position) as String
                m_editTextAccount!!.setText(m_dn);


                //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                when (position) {
                    0 -> {

                    }
                    1 -> {

                    }
                    //...
                    else -> {

                    }


                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


        m_rb1 = root!!.findViewById(R.id.radioButton1)
        m_rb2 = root!!.findViewById(R.id.radioButton2)


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

        root!!.findViewById<Button>(R.id.buttoncancel).setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(view: View) {
                //취소
                OnButtonCancelClicked()
            }
        })




        root!!.findViewById<Button>(R.id.buttonJumunList)
            .setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    //주문내역
                    OnButtonJumunListClicked()
                }
            })


        root!!.findViewById<Button>(R.id.buttonMaedo).setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                //매도
                OnButtonMaedoClicked()
            }
        })

        root!!.findViewById<Button>(R.id.buttonMaesu2).setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                //매수
                OnButtonMaesuClicked()
            }
        })
        root!!.findViewById<Button>(R.id.buttonjungjung)
            .setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    //정정
                    OnButtonJungjungClicked()
                }
            })


        root!!.findViewById<Button>(R.id.buttonSMaesu2).setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                //매수
                requestSeonmulMaemae()
            }
        })
        root!!.findViewById<Button>(R.id.buttonSjungjung)
            .setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    //정정
                    requestSeonmulJungjung()
                }
            })
        root!!.findViewById<Button>(R.id.buttonScancel)
            .setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    //취소
                    requestSeonmulCancel()
                }
            })



        return root
        //return super.onCreateView(inflater, container, savedInstanceState);
    }


    //계좌확인
    fun getAccountList() : ArrayList<String>{
        var temp : ArrayList<String> = arrayListOf();

        if(manager!!.isConnect() == false) return arrayListOf();
        var tempList = manager!!.getAccountList();
        var tempSize = manager!!.getAccountCount() as Int;

        for( i in 0.. tempSize-1){
            temp.add(tempList?.get(i)?.get(0) as String);
        }


        return temp;

    }

    override fun onResume() {
        super.onResume()
        /* 화면 갱신시 핸들 재연결 */
        m_nHandle = manager!!.setHandler(mainView!!, (m_handler as Handler?)!!)

        // 실시간 관련 추가 작업
        var bOK = manager!!.addRealData(m_nHandle, "SC0", "",0)
        bOK = manager!!.addRealData(m_nHandle, "SC1", "",0)
        bOK = manager!!.addRealData(m_nHandle, "SC2", "",0)
        bOK = manager!!.addRealData(m_nHandle, "SC3", "",0)
        bOK = manager!!.addRealData(m_nHandle, "SC4", "",0)

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()

        // 실시간 취소 작업
        var bOK = manager!!.deleteRealData(m_nHandle, "SC0", "",0)
        bOK = manager!!.deleteRealData(m_nHandle, "SC1", "",0)
        bOK = manager!!.deleteRealData(m_nHandle, "SC2", "",0)
        bOK = manager!!.deleteRealData(m_nHandle, "SC3", "",0)
        bOK = manager!!.deleteRealData(m_nHandle, "SC4", "",0)



        /* 해당 화면을 사용하지 않을떄 핸들값 삭제 */
        manager!!.deleteHandler(m_nHandle)
    }


    private fun OnButtonMaesuClicked() {
        requestMaemae("2")
    }

    private fun OnButtonMaedoClicked() {
        requestMaemae("1")
    }

    private fun OnButtonJungjungClicked() {
        requestJungjung()
    }

    private fun OnButtonCancelClicked() {
        requestCancel()
    }

    private fun OnButtonJumunListClicked() {
        val nLen = m_editTextJongmok!!.text.length
        if (nLen > 0) {
            requestData()
        }
    }

    // 주식주문
    // 55501035473 , 55551023962
    private fun requestMaemae(strMaemaeGubun: String) {
        // 계좌번호(20) , 입력비밀번호(8) , 종목번호(12) , 주문수량(16) , 주문가(13.2) , 매매구분(1) , 호가유형코드(2) , 신용거래코드(3) , 대출일(8) , 주문조건구분(1)

        var strAccount = m_editTextAccount!!.text.toString()
        var strJongmok = m_strJongmokCode
        var strQty = m_editTextQty!!.text.toString()
        var strDanga = m_editTextDanga!!.text.toString()
        var strHogaCode = ""
        if (m_rb1!!.isChecked)
            strHogaCode = "00"         // 보통가
        else if (m_rb2!!.isChecked)
            strHogaCode = "03"        // 시장가

        strAccount = manager!!.makeSpace(strAccount, 20)
        strJongmok = manager!!.makeSpace(strJongmok, 12)

        strQty = manager!!.makeZero(strQty, 16)

        if (strDanga.isEmpty()) {
            strDanga = "0"
        }

        strDanga = String.format("%.2f", java.lang.Double.parseDouble(strDanga))
        strDanga = manager!!.makeZero(strDanga, 13)

        var strPass = m_editAccPwd!!.text.toString()
        strPass = manager!!.makeSpace(strPass,8)

        //manager.setHeaderInfo(1, "1");

        val strInBlock =
            strAccount + strPass + strJongmok + strQty + strDanga + strMaemaeGubun + strHogaCode + "000" + "        " + "0"
        //int nRqID = manager.requestDataAccount(m_nHandle, "CSPAT00600", strInBlock, 0, 'B', "", false, false, false, false, "", 30);
        val nRqID = manager!!.requestData(m_nHandle, "CSPAT00600", strInBlock, false, "", 30)
    }

    // 주식정정
    // 55501035473 , 55551023962
    private fun requestJungjung() {
        // 원주문번호(10) , 계좌번호(20) , 입력비밀번호(8) , 종목번호(12) , 주문수량(16) , 호가유형코드(2) , 주문조건구분(1) , 주문가(13.2)

        var strJumunBunho = m_textViewJumunBunho!!.text.toString()
        var strAccount = m_editTextAccount!!.text.toString()
        var strJongmok = m_strJongmokCode
        var strQty = m_editTextQty!!.text.toString()
        var strDanga = m_editTextDanga!!.text.toString()
        var strHogaCode = ""
        if (m_rb1!!.isChecked)
            strHogaCode = "00"       // 보통가
        else if (m_rb2!!.isChecked)
            strHogaCode = "03"        // 시장가

        strJumunBunho = manager!!.makeZero(strJumunBunho, 10)
        strAccount = manager!!.makeSpace(strAccount, 20)
        strJongmok = manager!!.makeSpace(strJongmok, 12)

        strQty = manager!!.makeZero(strQty, 16)

        if (strDanga.length == 0)
            strDanga = "0"

        strDanga = String.format("%.2f", java.lang.Double.parseDouble(strDanga))
        strDanga = manager!!.makeZero(strDanga, 13)
        var strPass = m_editAccPwd!!.text.toString()
        strPass = manager!!.makeSpace(strPass,8);

        val strInBlock =
            strJumunBunho + strAccount + strPass + strJongmok + strQty + strHogaCode + "0" + strDanga
        val nRqID = manager!!.requestData(m_nHandle, "CSPAT00700", strInBlock, false, "", 30)
    }

    // 주식취소
    // 55501035473 , 55551023962
    private fun requestCancel() {
        // 원주문번호(10) , 계좌번호(20) , 입력비밀번호(8) , 종목번호(12) , 주문수량(16)

        var strJumunBunho = m_textViewJumunBunho!!.text.toString()
        var strAccount = m_editTextAccount!!.text.toString()
        var strJongmok = m_strJongmokCode
        var strQty = m_editTextQty!!.text.toString()
        var strPass = m_editAccPwd!!.text.toString()

        strJumunBunho = manager!!.makeZero(strJumunBunho, 10)
        strAccount = manager!!.makeSpace(strAccount, 20)
        strJongmok = manager!!.makeSpace(strJongmok, 12)
        strQty = manager!!.makeZero(strQty, 16)
        strPass = manager!!.makeSpace(strPass,8);

        val strInBlock = strJumunBunho + strAccount + strPass + strJongmok + strQty
        val nRqID = manager!!.requestData(m_nHandle, "CSPAT00800", strInBlock, false, "", 30)
    }

    // 선물주문
    // 55501035473 , 55551023962
    private fun requestSeonmulMaemae() {
        // 계좌번호(20) , 입력비밀번호(8) , 종목번호(12) , 매매구분(1) , 호가유형코드(2) , 주문가격(15.2) , 주문수량(16)

        var strAccount = m_editTextAccount!!.getText().toString();
        var strPass = m_editAccPwd!!.text.toString()
        var strJongmok = "101P9000";
        var strQty = "1";
        var strDanga = "251.00";
        var strHogaCode = "00"  ;       // 보통가

        strAccount = manager!!.makeSpace(strAccount, 20);
        strJongmok = manager!!.makeSpace(strJongmok, 12);

        strQty = manager!!.makeZero(strQty, 16);

        if(strDanga.length ==0);
            strDanga ="0";
        strDanga = String.format("%.2f", java.lang.Double.parseDouble(strDanga));
        strDanga = manager!!.makeZero(strDanga, 15);

        strPass = manager!!.makeSpace(strPass,8);

        var strInBlock = strAccount + strPass + strJongmok + "2" + strHogaCode + strDanga + strQty;
        var nRqID = manager!!.requestData(m_nHandle, "CFOAT00100", strInBlock, false, "", 30);
    }

    // 선물정정
    // 55501035473 , 55551023962
    private fun requestSeonmulJungjung() {
        // 계좌번호(20) , 입력비밀번호(8) , 종목번호(12) , 원주문번호(10) , 호가유형코드(2) , 주문가격(15.2) , 주문수량(16)

        var strJumunBunho = m_textViewJumunBunho!!.getText().toString();
        var strAccount = m_editTextAccount!!.getText().toString();
        var strPass = m_editAccPwd!!.text.toString()
        var strJongmok = "101P9000";
        var strQty = "1";
        var strDanga = "250.95";
        var strHogaCode = "00"      ;   // 보통가


        strAccount = manager!!.makeSpace(strAccount, 20);
        strJongmok = manager!!.makeSpace(strJongmok, 12);
        strJumunBunho = manager!!.makeZero(strJumunBunho, 10);
        strQty = manager!!.makeZero(strQty, 16);

        if(strDanga.length == 0)
            strDanga = "0";

        strDanga = String.format("%.2f", java.lang.Double.parseDouble(strDanga));
        strDanga = manager!!.makeZero(strDanga, 15);
        strPass = manager!!.makeSpace(strPass,8);
        var strInBlock = strAccount + strPass + strJongmok + strJumunBunho + strHogaCode + strDanga + strQty;
        var nRqID = manager!!.requestData(m_nHandle, "CFOAT00200", strInBlock, false, "", 30);
    }

    // 선물취소
    // 55501035473 , 55551023962
    private fun requestSeonmulCancel() {
        // 계좌번호(20) , 입력비밀번호(8) , 종목번호(12) , 원주문번호(10) , 취소수량(16)

        var strJumunBunho = m_textViewJumunBunho!!.getText().toString();
        var strAccount = m_editTextAccount!!.getText().toString();
        var strPass = m_editAccPwd!!.text.toString()
        var strJongmok = "101P9000";
        var strQty = "1";

        strAccount = manager!!.makeSpace(strAccount, 20);
        strJongmok = manager!!.makeSpace(strJongmok, 12);
        strJumunBunho = manager!!.makeZero(strJumunBunho, 10);
        strQty = manager!!.makeZero(strQty, 16);
        strPass = manager!!.makeSpace(strPass,8);
        var strInBlock = strAccount + strPass + strJongmok + strJumunBunho + strQty;
        var nRqID = manager!!.requestData(m_nHandle, "CFOAT00300", strInBlock, false, "", 30);
    }


    private fun requestData() {

        // 현재 수신받고 있는 종목의 실시간정보를 삭제한다.
        var bOK = manager!!.deleteRealData(m_nHandle, "S3_", m_strJongmokCode, 6)
        bOK = manager!!.deleteRealData(m_nHandle, "K3_", m_strJongmokCode, 6)
        m_strJongmokCode = m_editTextJongmok!!.text.toString()
        Log.d("s1006", "temp값 테스트(전) : " + m_strJongmokCode)
        Firestore.getMockCode().addOnSuccessListener { documentSnapshot ->
            val mockMap = documentSnapshot.data
            Log.d("s1001_DM", "mockMap.get(삼성전자) : " + mockMap!![m_strJongmokCode].toString())

            if (mockMap[m_strJongmokCode] != null) {
                m_strJongmokCode = mockMap!![m_strJongmokCode].toString()
            }
            Log.d("s1006", "temp값 테스트(함수 안) : " + m_strJongmokCode)

            Log.d("s1006", "temp값 테스트(후) : " + m_strJongmokCode)

            val nRqID = manager!!.requestData(m_nHandle, "t1102", m_strJongmokCode, false, "", 0)
        }
    }


    private fun processT1102(strBlockName: String, pData: ByteArray?) {
        if (strBlockName == "t1102OutBlock") {
            processT1102OutBlock(pData)
        }
    }

    private fun processT1102OutBlock(pData: ByteArray?) {

        val pArray: Array<ByteArray>

        /*
        방법 1. res 폴더에 TR정보가 담긴 *.res 파일이 있는 경우
        map = manager.getOutBlockDataFromByte("t1102", "t1102OutBlock", pData);
        pArray = manager.getAttributeFromByte("t1102", "t1102OutBlock", pData); // attribute
        */

        /* 방법2. 프로젝트의 TR정보가 담긴 소스(.kt, .java ... *현재 프로젝트의 TRCODE.kt )를 사용하는 경우  */
        val nColLen = intArrayOf( 20, 8, 1, 8, 6, 12, 8, 8, 8, 8, 12, 12, 8, 6, 8, 6, 8, 6, 8, 8, 8, 8, 6, 6, 6, 12, 8, 5, 3, 3, 6, 6, 8, 8, 8, 8, 6, 6, 3, 3, 6, 6, 8, 8, 8, 8, 6, 6, 3, 3, 6, 6, 8, 8, 8, 8, 6, 6, 3, 3, 6, 6, 8, 8, 8, 8, 6, 6, 3, 3, 6, 6, 8, 8, 8, 8, 6, 6, 12, 12, 6, 12, 12, 6, 6, 6, 12, 12, 8, 8, 8, 8, 8, 12, 12, 8, 2, 8, 12, 8, 10, 12, 12, 12, 12, 13, 10, 12, 12, 12, 12, 13, 7, 7, 7, 7, 7, 10, 10, 10, 12, 10, 6, 3, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 18, 18, 8, 8, 8, 1, 8, 1, 8, 10, 8, 8, 1, 1, 8   )
        val bAttributeInData = true
        val map = manager!!.getDataFromByte(pData!!, nColLen, bAttributeInData)
        assert(map != null)

        val jongmokName = root!!.findViewById<TextView>(R.id.textViewName)
        jongmokName.setText(map!![0]?.get(0))

        val danga = root!!.findViewById<EditText>(R.id.editTextDanga)
        danga.setText(map[0]?.get(1))

    }

    private fun processCSPAT_CFOAT(pData: ByteArray?, TRName: String?) {

        var blockname1  = TRName!! + "OutBlock1"
        val blockname2 = TRName!! + "OutBlock2"
        val map: Array<Array<String>>?
        val pArray: Array<ByteArray>

        /*
        //방법 1. res 폴더에 TR정보가 담긴 *.res 파일이 있는 경우
        map = manager.getOutBlockDataFromByte(TRName, blockname, pData);
        pArray = manager.getAttributeFromByte(TRName, blockname, pData); // attribute
        */

        //방법2. 프로젝트의 TR정보가 담긴 소스(.kt, .java ... *현재 프로젝트의 TRCODE.kt등 )를 사용하는 경우.
        var OutBlockName: Array<String>? = null
        OutBlockName = arrayOf(blockname1, blockname2);

        var OutBlockOccursInfo: BooleanArray? = null
        var OutBlockLenInfo: Array<IntArray>? = null
        var hashmap: HashMap<*, *>? = null

        when (TRName!!) {
            "CSPAT00600" -> {
                // ex CSPAT00600.
                //OutBlockName = arrayOf("CSPAT00600OutBlock1", "CSPAT00600OutBlock2")
                OutBlockOccursInfo = booleanArrayOf(false, false)
                OutBlockLenInfo = arrayOf(
                    intArrayOf(5, 20, 8, 12, 16, 13, 1,2,2,1,1,2,3,8,3,1,6,20,10,10,10,10,10,12,1,1),
                    intArrayOf(5, 10, 9, 2, 2, 9, 9, 16, 10, 10, 10, 16, 16, 16, 16, 16, 40, 40)
                )
            }
            "CSPAT00700" -> {
                // ex CSPAT00700.
                //OutBlockName = arrayOf("CSPAT00700OutBlock1", "CSPAT00700OutBlock2")
                OutBlockOccursInfo = booleanArrayOf(false, false)
                OutBlockLenInfo = arrayOf(
                    intArrayOf(5, 20, 8, 12, 16, 2, 1, 13, 2, 6, 20, 10, 10, 10, 10, 10),
                    intArrayOf(5,10,10,9,2,2,9,2,1,1,3,8,1,1,9,16,1,10,10,10,16,16,16,40,40)
                )
            }
            "CSPAT00800" -> {
                // ex CSPAT00800.
                //OutBlockName = arrayOf("CSPAT00800OutBlock1", "CSPAT00800OutBlock2")
                OutBlockOccursInfo = booleanArrayOf(false, false)
                OutBlockLenInfo = arrayOf(
                    intArrayOf(5, 10, 20, 8, 12, 16, 2, 20, 6, 10, 10, 10, 10, 10),
                    intArrayOf(5, 10, 10, 9, 2, 2, 9, 2, 1, 1, 3, 8, 1, 1, 9, 1, 10, 10, 10, 40, 40)
                )
            }
            //선물옵션 정상주문
            "CFOAT00100"->{
                //OutBlockName = arrayOf("CFOAT00100OutBlock1", "CFOAT00100OutBlock2")
                OutBlockOccursInfo = booleanArrayOf(false, false)
                OutBlockLenInfo = arrayOf(
                    intArrayOf(5,2,20,8,12,1,2,2,2,15,16,2,9,20,10,10,10,10,16,12,9,12,10),
                    intArrayOf(5,10,40,40,50,16,16,16,16,16)
                )

            }
            //선물옵션 정정주문
            "CFOAT00200"->{
                OutBlockOccursInfo = booleanArrayOf(false, false)
                OutBlockLenInfo = arrayOf(
                    intArrayOf(5,2,20,8,12,2,10,2,15,16,2,9,20,10,10,10,10,9,12,10,10),
                    intArrayOf(5,10,40,40,50,16,16,16,16,16)
                )
            }
            //선물옵션 취소주문
            "CFOAT00300"->{
                OutBlockOccursInfo = booleanArrayOf(false, false)
                OutBlockLenInfo = arrayOf(
                    intArrayOf(5,2,20,8,12,2,10,16,2,9,20,10,10,10,10,10,9,12,10,10),
                    intArrayOf(5,10,40,40,50,16,16,16,16,16)
                )
            }
        }
        hashmap = manager!!.getDataFromByte(
            pData!!,
            OutBlockName!!,
            OutBlockOccursInfo!!,
            OutBlockLenInfo!!,
            false,
            "",
            "B"
        )
        val o1 = hashmap!![OutBlockName[0]]
        val o2 = hashmap[OutBlockName[1]]
        // OutBlock별 데이터
        val s1: Array<Array<String>>?
        val s2: Array<Array<String>>?
        s1 = (o1 as Array<Array<String>>?)
        s2 = (o2 as Array<Array<String>>?)
        map = s2;


        if (map != null) {
            val strJumunBunho = map[0][1]
            m_textViewJumunBunho!!.text = strJumunBunho

        }

    }

    // 주문접수
    private fun processSC0(pData: ByteArray) {

        val nColLen = intArrayOf(10,11,8,6,1,1,1,3,8,3,16,2,3,9,16,12,12,3,3,8,1,9,4,1,1,4,4,6,1,18,2,2,2,1,4,4,41,2,2,2,10,11,9,8,12,9,40,16,13,1,2,2,1,1,3,8,1,6,20,10,10,10,10,10,
            1,3,1,3,20,1,2,1,20,10,9,10,9,16,16,16,10,16,1,10,10,10,10,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,13,16,16,16,16,16,16,16,16,16)
        val bAttributeInData = false
        val strArray = manager!!.getDataFromByte(pData, nColLen, bAttributeInData)
        val nRowCount = strArray?.size
        val nColCount = nColLen.size
    }

    // 주문체결
    private fun processSC1(pData: ByteArray) {

        val nColLen = intArrayOf(10,11,8,6,1,1,1,3,8,3,16,2,3,9,16,12,12,3,3,8,1,9,4,1,1,4,4,6,1,18,2,2,2,1,4,4,41,2,2,2,3,11,9,40,12,40,10,10,10,16,13,16,13,16,16,16,16,4,10,1,2,
            16,9,12,1,16,16,16,16,13,16,12,12,1,2,3,2,2,8,3,20,3,9,3,20,1,2,7,9,16,16,16,16,16,16,16,16,16,6,20,10,10,10,10,10,16,1,6,1,1,9,9,16,16,16,16,16,16,16,16,13,16,16,16,16,16,16,16,16,16)
        val bAttributeInData = false
        val strArray = manager!!.getDataFromByte(pData, nColLen, bAttributeInData)
        val nRowCount = strArray?.size
        val nColCount = nColLen.size
    }

    // 주문정정
    private fun processSC2(pData: ByteArray) {

        val nColLen = intArrayOf(10,11,8,6,1,1,1,3,8,3,16,2,3,9,16,12,12,3,3,8,1,9,4,1,1,4,4,6,1,18,2,2,2,1,4,4,41,2,2,2,3,11,9,40,12,40,10,10,10,16,13,16,13,16,16,16,16,4,10,1,2,
            16,9,12,1,16,16,16,16,13,16,12,12,1,2,3,2,2,8,3,20,3,9,3,20,1,2,7,9,16,16,16,16,16,16,16,16,16,6,20,10,10,10,10,10,16,1,6,1,1,9,9,16,16,16,16,16,16,16,16,13,16,16,16,16,16,16,16,16,16)
        val bAttributeInData = false
        val strArray = manager!!.getDataFromByte(pData, nColLen, bAttributeInData)
        val nRowCount = strArray?.size
        val nColCount = nColLen.size
    }

    // 주문취소
    private fun processSC3(pData: ByteArray) {

        val nColLen = intArrayOf(10,11,8,6,1,1,1,3,8,3,16,2,3,9,16,12,12,3,3,8,1,9,4,1,1,4,4,6,1,18,2,2,2,1,4,4,41,2,2,2,3,11,9,40,12,40,10,10,10,16,13,16,13,16,16,16,16,4,10,1,2,
            16,9,12,1,16,16,16,16,13,16,12,12,1,2,3,2,2,8,3,20,3,9,3,20,1,2,7,9,16,16,16,16,16,16,16,16,16,6,20,10,10,10,10,10,16,1,6,1,1,9,9,16,16,16,16,16,16,16,16,13,16,16,16,16,16,16,16,16,16)
        val bAttributeInData = false
        val strArray = manager!!.getDataFromByte(pData, nColLen, bAttributeInData)
        val nRowCount = strArray?.size
        val nColCount = nColLen.size
    }

    // 주문거부
    private fun processSC4(pData: ByteArray) {

        val nColLen = intArrayOf(10,11,8,6,1,1,1,3,8,3,16,2,3,9,16,12,12,3,3,8,1,9,4,1,1,4,4,6,1,18,2,2,2,1,4,4,41,2,2,2,3,11,9,40,12,40,10,10,10,16,13,16,13,16,16,16,16,4,10,1,2,
            16,9,12,1,16,16,16,16,13,16,12,12,1,2,3,2,2,8,3,20,3,9,3,20,1,2,7,9,16,16,16,16,16,16,16,16,16,6,20,10,10,10,10,10,16,1,6,1,1,9,9,16,16,16,16,16,16,16,16,13,16,16,16,16,16,16,16,16,16)
        val bAttributeInData = false
        val strArray = manager!!.getDataFromByte(pData, nColLen, bAttributeInData)
        val nRowCount = strArray?.size
        val nColCount = nColLen.size
    }


    companion object {
        private var m_nHandle = -1
        private var m_handler: ProcMessageHandler? = null
        private var m_strJongmokCode = ""
        private var mainView: MainView? = null
    }

}