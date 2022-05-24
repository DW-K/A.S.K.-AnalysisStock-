package com.gachon.ask.xingapi

//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.ebest.api.*
import com.gachon.ask.datamngr.API_DEFINE
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList
import com.gachon.ask.R

class s1010 : Fragment() {

    internal            var handler             : ProcMessageHandler? = null
    lateinit internal   var manager             : SocketManager
    internal            var m_nHandle           : Int                 = -1

    internal            var m_strRealKey        : String?             = ""
    internal            var m_strUserID         : String              = ""

    lateinit            var mainView            : MainView
    lateinit            var root                : View

    internal            var m_adapter           = TableGrid().DataAdapter(R.layout.s1010_item01)
    lateinit            var m_gridView          : GridView
    lateinit            var m_combo_list        : Spinner
    lateinit            var m_combo_server      : Spinner
    lateinit            var m_combo_real        : Spinner
    lateinit            var m_textview_file     : TextView
    lateinit            var m_textview_realkey  : TextView

    // 서버저장조건종목검색 InBlock (API)(t1857) ( block,headtype=A )
    class t1857_Inblock(sFlag : String, sSearch : String, sindex : String){

        var sRealFlag   : String  = sFlag    // [1] 0:조회만 1:등록
        var sSearchFlag : String  = sSearch  // [1] F:File방식 S:서버방식
        var query_index : String  = sindex   // [256] F: 파일위치  S:서버 인덱스
        val bAttribute  : Boolean = true     // t1857 은 attr 속성 사용 (DevCenter 참고)

        fun getInbockData() : String {

            if( bAttribute )
                return (String.format("%1s ", sRealFlag) + String.format("%1s ", sSearchFlag) + String.format("%-256s ", query_index))

            return (String.format("%1s", sRealFlag) + String.format("%1s", sSearchFlag) + String.format("%-256s", query_index))
        }
    }

    class t1866_Inblock(user_id : String, gb : String, group_name : String, cont : String, cont_key : String) {

        var sUser_id    : String  = user_id       // [8] 고객아이디
        var sGb         : String  = gb            // [1] 조회구분   0 : 그룹+조건리스트, 1 : 그룹리스트조회, 2 : 그룹명에 속한 조건리스
        var sGroup_Name : String  = group_name    // [40]그룹명트   조회구분이 2일경우 입력
        var sCont       : String  = cont          // [1] 연속여부   0, 1
        var sContkey    : String  = cont_key      // [40]연속
        val bAttribute  : Boolean = false                  // t1857 은 attr 속성 사용안함 (DevCenter 참고)

        fun getInbockData(): String {

            if( bAttribute )
                return  CommonFunction.makeSpace(sUser_id,8) + " " +
                        CommonFunction.makeSpace(sGb,1) + " " +
                        CommonFunction.makeSpace(sGroup_Name,40) + " " +
                        CommonFunction.makeSpace(sCont,1) + " " +
                        CommonFunction.makeSpace(sContkey,40) + " "

            return  CommonFunction.makeSpace(sUser_id,8) +
                    CommonFunction.makeSpace(sGb,1) +
                    CommonFunction.makeSpace(sGroup_Name,40) +
                    CommonFunction.makeSpace(sCont,1) +
                    CommonFunction.makeSpace(sContkey,40)
        }
    }

    /**
     * 통신 응답 핸들러
     */
    internal inner class ProcMessageHandler : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.what) {

                // 일반적인 에러
                API_DEFINE.RECEIVE_ERROR -> {
                    val strMsg = msg.obj as String
                    Toast.makeText(activity!!.applicationContext, strMsg, Toast.LENGTH_SHORT).show()
                }

                // TR데이타
                API_DEFINE.RECEIVE_DATA -> {
                    var lpDp = msg.obj as DataPacket
                    
                    if (lpDp.strTRCode == "t1866") {
                        // 서버에 저장된 종목검색 리스트 조회 수신
                        processT1866(lpDp.strBlockName!!, lpDp.pData!!)
                        
                    } else if (lpDp.strTRCode == "t1857") {
                        // 검색된 종목 리스트 조회 수신
                        processT1857(lpDp.strBlockName!!, lpDp.pData!!)
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
                }

                API_DEFINE.RECEIVE_REALDATA_SEARCH ->{
                    val lpRp = msg.obj as RealPacket

                    processRealSearch(lpRp.strKeyCode!!, lpRp.pData)
                }

                // TR메세지
                API_DEFINE.RECEIVE_MSG -> {
                    val lpMp = msg.obj as MsgPacket
                    Toast.makeText(activity!!.applicationContext, lpMp.strTRCode + " " + lpMp.strMsgCode + lpMp.strMessageData, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        root        = inflater.inflate(R.layout.activity_s1010, null)
        mainView    = (activity as MainView)
        handler     = ProcMessageHandler()
        manager     = (activity?.application as ApplicationManager).getSockInstance()

        // 사용자 아이디
        m_strUserID = manager.getUserID();

        // 컨츠롤 초기화
        initControl()
        // 버튼 초기화 및 리스너
        initButtonListener()

        manager.removeServiceAll(m_nHandle);

        return root
    }

    /**
     * 각종 컨트롤 초기화
     */
    fun initControl() {

        m_gridView          = root.findViewById<View>(R.id.grid_view)   as GridView
        m_gridView.adapter  = m_adapter
        m_adapter.setMaxCount(-1);

        m_combo_list        = root!!.findViewById(R.id.combo_list)      as Spinner
        m_combo_real        = root!!.findViewById(R.id.combo_real)      as Spinner
        m_combo_server      = root!!.findViewById(R.id.combo_server)    as Spinner
        m_textview_file     = root!!.findViewById(R.id.text_file)       as TextView
        m_textview_realkey  = root!!.findViewById(R.id.textview_realkey)as TextView

        var items1 = ArrayList<String>()

        items1.add("서버")
        items1.add("파일")
        val adapter_server = ArrayAdapter(root!!.context, R.layout.spinneritem, items1)
        m_combo_server.adapter = adapter_server

        var items2 = ArrayList<String>()

        items2.add("조회")
        items2.add("실시간")
        val adapter_real = ArrayAdapter(root!!.context, R.layout.spinneritem, items2)
        m_combo_real.adapter = adapter_real
    }

    /**
     * 각종 버튼 리스너 등록 및 OnClick 처리
     */
    fun initButtonListener() {

        val btn1 = root.findViewById(R.id.button1) as Button
        val btn2 = root.findViewById(R.id.button2) as Button
        val btn3 = root.findViewById(R.id.button3) as Button

        // 버튼 클릭 리스너
        val btnListener = View.OnClickListener {view->
            when(view.getId()){
                R.id.button1->{
                    // 서버기준 조회(t1857)
                    requestData1857()
                }
                R.id.button2->{
                    // 서버 저장 목록 조회
                    requestData1866()
                }
                R.id.button3->{
                    // 실시간 중지
                    requestRealStop()
                }
            }
        }

        // 버튼이벤트 리스너 등록
        btn1.setOnClickListener(btnListener);
        btn2.setOnClickListener(btnListener);
        btn3.setOnClickListener(btnListener);
    }

    override fun onResume() {
        super.onResume()
        // onResume 경우마다 Activity 를 넣어줘야 한다
        m_nHandle = manager.setHandler(handler as Handler)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()

        requestRealStop()   // 화면 종료시 실시간 종목검색 해제

        manager.removeServiceAll(m_nHandle)
        manager.deleteHandler(m_nHandle)
    }

    /**
     * 종목검색 조회(t1857)
     */
    fun requestData1857() {

        // 기존에 실시간이 등록되어 있다면 중지한다.
        requestRealStop()

        // 서버저장 종목검색 리스트 초기화
        m_adapter.items.clear()
        m_adapter.notifyDataSetChanged()

        // Inblock Data
        var nGubun = m_combo_server.selectedItemPosition as Int
        var nReal  = m_combo_real.selectedItemPosition as Int

        var t1875InBlock = t1857_Inblock("","","")

        if (nGubun == 0){
            // 서버에 저장된 종목검색 키값을 이용한 조회
            var strItem = m_combo_list.selectedItem as String?
            if (strItem == null) {
                Toast.makeText(activity!!.applicationContext, "목록조회를 먼저 진행 하세요", Toast.LENGTH_SHORT).show()
                return;
            }

            t1875InBlock.sSearchFlag = "S"  // 서버기준
            t1875InBlock.query_index = strItem

        }else{
            // HTS [1892] e종목검색 화면에서 API보내기로 파일을 다운받아 이용할 수 있다.
            // 샘플에서는 사용자가 파일을 읽어 파라메터로 전달하는 방식으로 제공
            val strExternalPath: String = android.os.Environment.getExternalStorageDirectory().absolutePath + "/ACF/ConditionToApi.ACF"
            t1875InBlock.sSearchFlag = "F"  // 파일기준
            t1875InBlock.query_index = strExternalPath
        }

        if (nReal == 0){
            t1875InBlock.sRealFlag = "0"    // 조회만
        }else{
            t1875InBlock.sRealFlag = "1"    // 실시간 등록
        }

        var nRqID : Int = 0
        if( t1875InBlock.sSearchFlag == "F" ) {

            val strMediaMount = android.os.Environment.getExternalStorageState()
            if( android.os.Environment.MEDIA_MOUNTED != strMediaMount){
                Toast.makeText(activity!!.applicationContext, "외부 저장소 권한을 확인하세요.", Toast.LENGTH_SHORT).show()
                return
            }

            val strAssetPath: String = "ACF/ConditionToApi.ACF"
            t1875InBlock.query_index = strAssetPath
            var pDataArray = procAssetsFile(strAssetPath)
            //-------------------------------------------------------------------------------------------//
            // 1. 파일 검색은 두가지의 함수를 제공
            //    requestService(nHandle:Int, pszCode:String, pszData:String)
            //    requestService(nHandle:Int, pszCode:String, pszData:String, pACFData:ByteArray?)
            //-------------------------------------------------------------------------------------------//
            // 2. requestService(nHandle:Int, pszCode:String, pszData:String)
            //    파일 검색일 경우 t1857InBlock.query_index 에는 ExternalPath 만 들어 가야 한다.
            // 3. requestService(nHandle:Int, pszCode:String, pszData:String, pACFData:ByteArray?)
            //    t1875InBlock.query_index를 무시하고 사용자가 파일을 직접 읽어서 파일내용을 파라메터로 전달
            //-------------------------------------------------------------------------------------------//
            // 샘플에서는 3번 방식으로 제공
            //-------------------------------------------------------------------------------------------//
            var strInput : String = t1875InBlock.getInbockData()
            nRqID = manager.requestService(m_nHandle, "t1857", strInput, pDataArray)
        }
        else {
            var strInput : String = t1875InBlock.getInbockData()
            nRqID = manager.requestService(m_nHandle, "t1857", strInput)
        }
    }

    /**
     * assets에 있는 파일 읽어오기
     */
    fun procAssetsFile(fileName: String) : ByteArray?{

        val assetManager = activity!!.applicationContext.resources.assets

        var iis: InputStream? = null
        try {
            iis = assetManager.open(fileName)
            return iis.readBytes()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (iis != null) {
                try {
                    iis.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

     /**
     * 서버기준 실시간 해제
     */
    fun requestRealStop() {

        if ( m_strRealKey!!.length > 0 ) {
            manager.removeServiceAll(m_nHandle);
//            val nRqID = manager.removeService(m_nHandle, m_strRealKey!!)
//
//            m_strRealKey = ""
//            m_textview_realkey.text = ""
        }
    }

    /**
     * 서버저장 조건 리스트 조회
     */
    fun requestData1866() {

        var items = ArrayList<String>()
        val myAdapter = ArrayAdapter(root!!.context, R.layout.spinneritem, items)
        m_combo_list.adapter = myAdapter
      //  m_combo_list.notifyDataSetChanged();

        var strInput = t1866_Inblock(m_strUserID, "0", "", "0", "").getInbockData()
        val nRqID = manager.requestData(m_nHandle, "t1866", strInput, false, "", 0)
    }

    /**
     * 서버에 저장된 종목검색 리스트 응답처리
     */
    private fun processT1866(strOutBlockName:String, pData:ByteArray?) {

        if (strOutBlockName == "t1866OutBlock") {
            val nColLen = intArrayOf(5,1,40)
            val bAttributeInData = false
            val strArray = manager.getDataFromByte(pData!!, nColLen, bAttributeInData)      // 전체 데이타 얻어오기
            var nCount = strArray!!.size

            // TR, Block, Field 명으로 데이터 얻어오기
            var sResult_Count = manager!!.getItemData("t1866", "t1866OutBlock", "result_count", pData, 0)
            var sCont         = manager!!.getItemData("t1866", "t1866OutBlock", "cont",         pData, 0)
            var sContKey      = manager!!.getItemData("t1866", "t1866OutBlock", "cont_key",     pData, 0)

        } else if (strOutBlockName == "t1866OutBlock1") {

            //-------- 데이터 얻어 오는 방법1 -------//
            // Array로 얻어오는 방법
            val nColLen = intArrayOf(12,40,40)
            val bAttributeInData = false
            val strArray = manager.getDataFromByte(pData!!, nColLen, bAttributeInData)
            if ( strArray == null )
                return
            var nCount = strArray!!.size

            //-------- 데이터 얻어 오는 방법2 -------//
            // TR, Block, Field 명으로 데이터 얻어오기
            var sBlockCount = manager!!.getValideCount("t1866", "t1866OutBlock1", pData)

            var items = ArrayList<String>()

            for( i in 0..sBlockCount!!.minus(1)) {
                var sQuery_Index = manager!!.getItemData("t1866", "t1866OutBlock1", "query_index", pData, i)
                var sGroup_Name  = manager!!.getItemData("t1866", "t1866OutBlock1", "group_name", pData, i)
                var sQuery_Name  = manager!!.getItemData("t1866", "t1866OutBlock1", "query_name", pData, i)

                items.add(sQuery_Index!!)
            }

            val myAdapter = ArrayAdapter(root!!.context, R.layout.spinneritem, items)
            m_combo_list.adapter = myAdapter
        }
    }

    /**
     * 종목검색 조회에 대한 응답처리
     */
    private fun processT1857(strOutBlockName:String, pData:ByteArray?) {

        if (strOutBlockName == "t1857OutBlock") {

            var sResult_Count  = manager!!.getItemData ("t1857", "t1857OutBlock", "result_count", pData!!, 0)   // 검색종목수
            var sResult_Time   = manager!!.getItemData ("t1857", "t1857OutBlock", "result_time" , pData!!, 0)   // 포착시간
            var sAlertNum      = manager!!.getItemData ("t1857", "t1857OutBlock", "AlertNum"    , pData!!, 0)   // 실시간 키값
            
            m_strRealKey = sAlertNum!!.trim()
            
            m_textview_realkey.text = m_strRealKey

        } else if (strOutBlockName == "t1857OutBlock1") {

            var nCount = manager!!.getValideCount("t1857", "t1857OutBlock1", pData!!)

            for (i in 0..nCount!!.minus(1)){
                var shcode      = manager!!.getItemData ("t1857", "t1857OutBlock1", "shcode" , pData!!, i)
                var hname       = manager!!.getItemData ("t1857", "t1857OutBlock1", "hname"  , pData!!, i)
                var price       = manager!!.getItemData ("t1857", "t1857OutBlock1", "price"  , pData!!, i)
                var sign        = manager!!.getItemData ("t1857", "t1857OutBlock1", "sign"   , pData!!, i)
                var change      = manager!!.getItemData ("t1857", "t1857OutBlock1", "change" , pData!!, i)
                var diff        = manager!!.getItemData ("t1857", "t1857OutBlock1", "diff"   , pData!!, i)
                var volume      = manager!!.getItemData ("t1857", "t1857OutBlock1", "volume" , pData!!, i)
                var jobFlag     = manager!!.getItemData ("t1857", "t1857OutBlock1", "JobFlag", pData!!, i)

                var price_attr  = manager!!.getItemDataAttr ("t1857", "t1857OutBlock1", "price", pData!!, i)
                var change_attr = manager!!.getItemDataAttr ("t1857", "t1857OutBlock1", "diff" , pData!!, i)


                val data_record: List<Triple<TableGrid.TYPE, String?, Int>> = listOf(

                    Triple( TableGrid.TYPE.STRING,                        shcode!!, R.id.view0),
                    Triple( TableGrid.TYPE.STRING,                         hname!!, R.id.view1),
                    Triple( TableGrid.TYPE.STRING,  manager.getCommaValue(price!!), R.id.view2),
                    Triple( TableGrid.TYPE.DAEBI ,                    price_attr!!, R.id.view2),
                    Triple( TableGrid.TYPE.ICON  ,                          sign!!, R.id.view3_1),
                    Triple( TableGrid.TYPE.STRING, manager.getCommaValue(change!!), R.id.view3),
                    Triple( TableGrid.TYPE.DAEBI ,                   change_attr!!, R.id.view3),
                    Triple( TableGrid.TYPE.STRING, manager.getCommaValue(volume!!), R.id.view4),

//                    if ( jobFlag == "N" )       Triple( TableGrid.TYPE.STRING, "신규", R.id.view5)
//                    else if ( jobFlag == "R" )  Triple( TableGrid.TYPE.STRING, "재진입", R.id.view5)
//                    else if ( jobFlag == "O" )  Triple( TableGrid.TYPE.STRING, "이탈", R.id.view5)
//                    else                        Triple( TableGrid.TYPE.STRING, "", R.id.view5)
                    Triple( TableGrid.TYPE.STRING,                      "", R.id.view5)
                )

                m_adapter.addItem(data_record)
            }

            m_adapter.notifyDataSetChanged()   //데이터 갱신을 알린다.
        }
    }

    /**
     *  * 실시간 검색시 처리
     *  t1807 구조로 들어온다
     */
    private fun processRealSearch(strKeyData:String, pData:ByteArray?) {

        // 종목코드  종목명  현재가  전일대비구분  전일대비  등락율  거래량  종목상태
        var shcode      = manager!!.getItemData ("t1857", "t1857OutBlock1", "shcode"  , pData!!, 0)
        var hname       = manager!!.getItemData ("t1857", "t1857OutBlock1", "hname"   , pData!!, 0)
        var price       = manager!!.getItemData ("t1857", "t1857OutBlock1", "price"   , pData!!, 0)
        var sign        = manager!!.getItemData ("t1857", "t1857OutBlock1", "sign"    , pData!!, 0)
        var change      = manager!!.getItemData ("t1857", "t1857OutBlock1", "change"  , pData!!, 0)
        var diff        = manager!!.getItemData ("t1857", "t1857OutBlock1", "diff"    , pData!!, 0)
        var volume      = manager!!.getItemData ("t1857", "t1857OutBlock1", "volume"  , pData!!, 0)
        var jobFlag     = manager!!.getItemData ("t1857", "t1857OutBlock1", "JobFlag" , pData!!, 0)

        var price_attr  = manager!!.getItemDataAttr ("t1857", "t1857OutBlock1", "price", pData!!, 0)
        var change_attr = manager!!.getItemDataAttr ("t1857", "t1857OutBlock1", "diff" , pData!!, 0)

        val data_record: List<Triple<TableGrid.TYPE, String?, Int>> = listOf(

            Triple( TableGrid.TYPE.STRING,                        shcode!!,     R.id.view0),
            Triple( TableGrid.TYPE.STRING,                         hname!!,     R.id.view1),
            Triple( TableGrid.TYPE.STRING,  manager.getCommaValue(price!!),     R.id.view2),
            Triple( TableGrid.TYPE.DAEBI ,                    price_attr!!,     R.id.view2),
            Triple( TableGrid.TYPE.ICON  ,                          sign!!,     R.id.view3_1),
            Triple( TableGrid.TYPE.STRING, manager.getCommaValue(change!!),     R.id.view3),
            Triple( TableGrid.TYPE.DAEBI ,                   change_attr!!,     R.id.view3),
            Triple( TableGrid.TYPE.STRING, manager.getCommaValue(volume!!),     R.id.view4),

            if ( jobFlag == "N" )       Triple( TableGrid.TYPE.STRING, "신규" , R.id.view5)
            else if ( jobFlag == "R" )  Triple( TableGrid.TYPE.STRING, "재진입", R.id.view5)
            else if ( jobFlag == "O" )  Triple( TableGrid.TYPE.STRING, "이탈" , R.id.view5)
            else                        Triple( TableGrid.TYPE.STRING, ""    , R.id.view5)
        )

        var grid_record : List<Triple<TableGrid.TYPE, String?, Int>>? = null
        var field       : Triple<TableGrid.TYPE, String?, Int>?       = null
        var shItemCode  : String? = ""
        var bFind       : Boolean = false

        for(i in 0..m_adapter.getCount() - 1) {

            // 그리드에 있는 데이타를 찾아서
            grid_record = m_adapter.getItem(i) as List<Triple<TableGrid.TYPE, String?, Int>>
            shItemCode  = grid_record.get(0).second

            // 종목코드가 같은게 있으면 update
            if(shItemCode == shcode) {
                m_adapter.updateItem(i, data_record)
                bFind = true
                break
            }
        }

        if(bFind == false)
            // 그리드 내에 종목이 없으면 신규로 들어온 데이타이다.
            m_adapter.addItem(0, data_record)


        m_adapter.notifyDataSetChanged()
    }


}
