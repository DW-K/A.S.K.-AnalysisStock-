package com.gachon.ask.xingapi

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.ebest.api.*
import com.ebest.api.rm.ResourceManager
import com.gachon.ask.datamngr.API_DEFINE
import java.util.ArrayList
import kotlin.system.exitProcess
import com.gachon.ask.R

class s1005 : Fragment() {
    internal var m_nHandle = -1
    internal var handler: ProcMessageHandler? = null
    internal lateinit var manager: SocketManager
    internal inner class ProcMessageHandler : Handler() {

        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.what) {

                // 퍼미션 에러
                API_DEFINE.RECEIVE_PERMISSIONERROR -> {
                    activity?.finishAffinity();             // 해당앱의 루트 액티비티를 종료시킨다.
                    System.runFinalization();               // 현재 작업중인 쓰레드가 종료되면 종료 시키라는 명령어
                    exitProcess(0);                  // 현재 액티비티를 종료시킨다.
                }

                // INITECH 핸드세이킹 에러
                API_DEFINE.RECEIVE_INITECHERROR -> {
                    val strMsg = msg.obj as String
                    Toast.makeText(activity?.applicationContext, strMsg, Toast.LENGTH_SHORT).show()
                }

                // 일반적인 에러
                API_DEFINE.RECEIVE_ERROR -> {
                    val strMsg = msg.obj as String
                    Toast.makeText(activity?.applicationContext, strMsg, Toast.LENGTH_SHORT).show()
                }

                // 서버에서 보내는 시스템 ERROR
                API_DEFINE.RECEIVE_SYSTEMERROR -> {
                    val pMsg = msg.obj as MsgPacket
                    Toast.makeText(
                        activity?.applicationContext,
                        pMsg.strMessageData,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // TR데이타
                API_DEFINE.RECEIVE_DATA -> {
                    val lpDp = msg.obj as DataPacket
                    if (lpDp.strTRCode == "CSPAQ13700") {
                        processCSPAQ13700(lpDp.pData!!)
                        //if(lpDp.strCont.toUpperCase() == "Y"){
                        if(lpDp.strCont == 'Y'.toByte()){
                            m_NextButton.isEnabled = true
                            m_contKey = lpDp.strContKey
                        }
                        else{
                            m_NextButton.isEnabled = false
                            m_contKey = ""
                        }
                    }

                }
                // TR조회 끝
                API_DEFINE.RECEIVE_RELEASE -> {
                    val lpDp = msg.obj as ReleasePacket

                    lpDp.nRqID
                    lpDp.strTrCode
                }

                // REAL데이타
                API_DEFINE.RECEIVE_REALDATA -> {
                    val lpRp = msg.obj as RealPacket
                    if (lpRp.strBCCode == "") {

                    }
                }


                // TR메세지
                API_DEFINE.RECEIVE_MSG -> {
                    val lpMp = msg.obj as MsgPacket
                    Toast.makeText(
                        activity?.applicationContext,
                        lpMp.strTRCode + " " + lpMp.strMsgCode + lpMp.strMessageData,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                // 접속종료 또는 재연결
                API_DEFINE.RECEIVE_DISCONNECT ,  API_DEFINE.RECEIVE_RECONNECT->{
                    mainView.onMessage(msg);
                }
            }
        }
    }

    internal var m_strJongmokCode: String? = null
    lateinit internal var m_editTextAccount: EditText
    lateinit internal var m_editTextJongmok: EditText
    lateinit internal var m_editAccPwd: EditText

    lateinit internal var m_textViewMaedoChaegualAmt: TextView
    lateinit internal var m_textViewMaesuChaegualAmt: TextView
    lateinit internal var m_textViewMaedoChaegualQty: TextView
    lateinit internal var m_textViewMaesuChaegualQty: TextView
    lateinit internal var m_textViewMaedoJumunQty: TextView
    lateinit internal var m_textViewMaesuJumunQty: TextView

    lateinit internal var m_NextButton : Button

    lateinit var m_gridView: GridView
    internal var m_adapter = TableGrid().DataAdapter(R.layout.s1005_item01)

    lateinit var m_combobox: Spinner
    internal var m_dn = ""
    var m_contKey:String = ""

    lateinit var mainView: MainView
    lateinit var root: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.activity_s1005, container, false)
        mainView = (activity as MainView)

        // get SocketManager instance
        handler = ProcMessageHandler()
        manager = (activity?.application as ApplicationManager).getSockInstance()

        m_gridView = root.findViewById<View>(R.id.grid_view) as GridView
        m_gridView.adapter = m_adapter


        m_editTextAccount = root.findViewById<View>(R.id.editTextAccount) as EditText
        m_editTextJongmok = root.findViewById<View>(R.id.editTextJongmok) as EditText
        m_editAccPwd      = root.findViewById<View>(R.id.editAccPwd) as EditText

        m_textViewMaedoChaegualAmt = root.findViewById<View>(R.id.textViewMaedoChaegualAmt) as TextView
        m_textViewMaesuChaegualAmt = root.findViewById<View>(R.id.textViewMaesuChaegualAmt) as TextView
        m_textViewMaedoChaegualQty = root.findViewById<View>(R.id.textViewMaedoChaegualQty) as TextView
        m_textViewMaesuChaegualQty = root.findViewById<View>(R.id.textViewMaesuChaegualQty) as TextView
        m_textViewMaedoJumunQty = root.findViewById<View>(R.id.textViewMaedoJumunQty) as TextView
        m_textViewMaesuJumunQty = root.findViewById<View>(R.id.textViewMaesuJumunQty) as TextView


        m_combobox = root.findViewById(R.id.combo_acc) as Spinner
        val items = getAccountList();
        //val myAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        val myAdapter = ArrayAdapter(root.context, R.layout.spinneritem, items)
        m_combobox.adapter = myAdapter

        m_combobox.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                var t = (parent.getChildAt(0) as TextView)
                //t.setTextColor(Color.BLUE)
                //(parent.getChildAt(0) as TextView).textSize = 10f
                t.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceManager.calcFontSize(t.textSize.toInt()))

                m_dn = m_combobox.getItemAtPosition(position) as String
                m_editTextAccount.setText(m_dn);


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



        root.findViewById<Button>(R.id.buttonQuery).setOnClickListener {
            m_contKey = ""
            m_adapter.items.clear()
            m_adapter.notifyDataSetChanged()

            // 레코드갯수(5) , 계좌번호(20) , 입력비밀번호(8) , 주문시장코드(2) , 매매구분(1) , 종목번호(12) , 체결여부(1) , 주문일(8) , 시작주문번호(10) , 역순구분(1) , 주문유형코드(2)
            val strRecord = "00001"

            var strAccount = m_editTextAccount.text.toString()

            /* 종목 코드 입력 시
               주식 : A+종목코드
               ELW : J+종목코드
            */
            var strJongmok = m_editTextJongmok.text.toString()

            strAccount = manager.makeSpace(strAccount, 20)
            strJongmok = manager.makeSpace(strJongmok, 12)

            var strPass = m_editAccPwd.text.toString()
            strPass = manager.makeSpace(strPass,8)

            val strServerIlja = manager.serverIlja
            val strInBlock = strRecord + strAccount + strPass + "00" + "0" + strJongmok + "0" + strServerIlja + "0000000000" + "0" + "00"

            //int nRqID = manager.requestDataAccount(m_nHandle, "CSPAQ13700", strInBlock, 0, 'B', "", false, false, false, false, "", 30);
            val nRqID = manager.requestData(m_nHandle, "CSPAQ13700", strInBlock, false, "", 30)
        }

        m_NextButton = root.findViewById<Button>(R.id.buttonNext);
        m_NextButton.isEnabled = false;

        m_NextButton.setOnClickListener {
            m_adapter.items.clear()
            m_adapter.notifyDataSetChanged()

            // 레코드갯수(5) , 계좌번호(20) , 입력비밀번호(8) , 주문시장코드(2) , 매매구분(1) , 종목번호(12) , 체결여부(1) , 주문일(8) , 시작주문번호(10) , 역순구분(1) , 주문유형코드(2)
            val strRecord = "00001"
            var strAccount = m_editTextAccount.text.toString()
            var strJongmok = m_editTextJongmok.text.toString()
            strAccount = manager.makeSpace(strAccount, 20)
            strJongmok = manager.makeSpace(strJongmok, 12)

            var strPass = m_editAccPwd.text.toString()
            strPass = manager.makeSpace(strPass,8);

            val strServerIlja = manager.serverIlja
            val strInBlock = strRecord + strAccount + strPass + "00" + "0" + strJongmok + "0" + strServerIlja + "+999999999" + "0" + "00"
            val nRqID = manager.requestData(m_nHandle, "CSPAQ13700", strInBlock, true, m_contKey, 30)
        }

        return root
    }

    //계좌확인
    fun getAccountList() : ArrayList<String> {
        var temp : ArrayList<String> = arrayListOf();

        if(!manager.isConnect()) {
            return arrayListOf()
        }

        val tempList = manager.getAccountList()
        var tempSize = manager.getAccountCount() as Int;

        for( i in 0.. tempSize-1){
            temp.add(tempList?.get(i)?.get(0) as String)
            var strName = tempList?.get(i)?.get(1) as String;
        }

        return temp
    }


    override fun onResume() {
        super.onResume()
        m_nHandle = manager.setHandler(mainView, handler as Handler)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.deleteHandler(m_nHandle)
    }

    // 주식주문내역
    private fun processCSPAQ13700(pData: ByteArray) {
        //방법 1. res 폴더에 TR정보가 담긴 *.res 파일이 있는 경우
        /*
        var s1 = manager!!.getOutBlockDataFromByte("CSPAQ13700", "CSPAQ13700OutBlock1", pData!!) as Array<Array<String>>
        var s2 = manager!!.getOutBlockDataFromByte("CSPAQ13700", "CSPAQ13700OutBlock2", pData!!) as Array<Array<String>>
        var s3 = manager!!.getOutBlockDataFromByte("CSPAQ13700", "CSPAQ13700OutBlock3", pData!!) as Array<Array<String>>
        //방법1
        */

        // 방법2. 프로젝트의 TR정보가 담긴 소스(.kt, .java ... *현재 프로젝트의 TRCODE.kt )를 사용하는 경우
        /*

        val OutBlockName = arrayOf("CSPAQ13700OutBlock1", "CSPAQ13700OutBlock2", "CSPAQ13700OutBlock3")
        val OutBlockOccursInfo = booleanArrayOf(false, false, true)
        val OutBlockLenInfo = arrayOf(intArrayOf(5, 20, 8, 2, 1, 12, 1, 8, 10, 1, 2),
            intArrayOf(5, 16, 16, 16, 16, 16, 16),
            intArrayOf(8, 3, 2, 10, 10, 12, 40, 1, 10, 2, 40, 9, 50, 1, 10, 16, 16, 16, 15, 16, 15, 9, 9, 2, 40, 1, 16, 2, 40, 3, 1, 8, 9, 12, 16))

*/

        val OutBlockName = arrayOf("CSPAQ13700OutBlock1", "CSPAQ13700OutBlock2", "CSPAQ13700OutBlock3")
        val OutBlockOccursInfo = booleanArrayOf(false, false, true)
        val OutBlockLenInfo = arrayOf(intArrayOf(5, 20, 8, 2, 1, 12, 1, 8, 10, 1, 2), intArrayOf(5, 16, 16, 16, 16, 16, 16), intArrayOf(8, 3, 2, 10, 10, 12, 40, 1, 10, 2, 40, 9, 50, 1, 10, 16, 16, 16, 15, 16, 15, 9, 9, 2, 40, 1, 16, 2, 40, 3, 1, 8, 9, 12, 16))

        val map = manager.getDataFromByte(pData, OutBlockName, OutBlockOccursInfo, OutBlockLenInfo, false, "", "B")
        if (map == null){
            return
        }

        val s1 = map[OutBlockName[0]] as Array<Array<String>>?
        val s2 = map[OutBlockName[1]] as Array<Array<String>>?
        val s3 = map[OutBlockName[2]] as Array<Array<String>>?
        // 방법2

        var nOutCount1 = manager.getValideCount("CSPAQ13700", "CSPAQ13700OutBlock1", pData)
        var nOutCount2 = manager.getValideCount("CSPAQ13700", "CSPAQ13700OutBlock2", pData)
        var nOutCount3 = manager.getValideCount("CSPAQ13700", "CSPAQ13700OutBlock3", pData)

        var AcntNo = manager.getItemData("CSPAQ13700", "CSPAQ13700OutBlock1", "AcntNo", pData, 0)
        var SellExecAmt = manager.getItemData("CSPAQ13700", "CSPAQ13700OutBlock2", "SellExecAmt", pData, 0)
        var IsNum = manager.getItemData("CSPAQ13700", "CSPAQ13700OutBlock3", "IsuNm", pData, 0)

        IsNum = "1234"

        if (s2 == null){
            return
        }

        m_textViewMaedoChaegualAmt.text = manager.getCommaValue(s2[0][1])
        m_textViewMaesuChaegualAmt.text = manager.getCommaValue(s2[0][2])
        m_textViewMaedoChaegualQty.text = manager.getCommaValue(s2[0][3])
        m_textViewMaesuChaegualQty.text = manager.getCommaValue(s2[0][4])
        m_textViewMaedoJumunQty.text = manager.getCommaValue(s2[0][5])
        m_textViewMaesuJumunQty.text = manager.getCommaValue(s2[0][6])

        if (s3 != null) {
            for (i in s3.indices) {

                val data_record: List<Triple<TableGrid.TYPE, Any?, Int>> = listOf(
                    Triple(TableGrid.TYPE.STRING, s3[i][0], R.id.textView1),
                    Triple(TableGrid.TYPE.STRING, s3[i][6], R.id.textView2),
                    Triple(TableGrid.TYPE.STRING, s3[i][3], R.id.textView3),
                    Triple(TableGrid.TYPE.STRING, s3[i][10], R.id.textView4),
                    Triple(TableGrid.TYPE.STRING, s3[i][17], R.id.textView5),
                    Triple(TableGrid.TYPE.STRING, s3[i][18], R.id.textView6)
                )
                m_adapter.addItem(data_record)
                //m_adapter.addItem(DataItem(s3[i][0], s3[i][6], s3[i][3], s3[i][10], s3[i][17], s3[i][18]))    //아이템을 추가한다.
            }
        }
        m_adapter.notifyDataSetChanged()   //데이터 갱신을 알린다.

    }

}