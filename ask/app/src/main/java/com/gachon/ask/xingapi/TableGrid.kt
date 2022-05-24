package com.gachon.ask.xingapi

import com.ebest.api.CommonFunction
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import org.w3c.dom.Text
import java.util.ArrayList


open class TableGrid {

    // cell의 스타일 (배경색,전경색,정렬)

    companion object {
        @kotlin.jvm.JvmField
        val BACKGROUND = "BackGround"
        @kotlin.jvm.JvmField
        val FOREGROUND = "ForeGround"
        @kotlin.jvm.JvmField
        val GRAVITY = "Gravity"
    }


    enum class TYPE { INT,DOUBLE, STRING , DAEBI, ICON }

    internal inner class DataAdapter (private val baseLayOutID : Int) : BaseAdapter() {

        private var colsize = 0
        protected var nMaxCount = 100
        //해당 뷰(리스트나 그리드 등)에 들어갈 데이터의 배열.
        var items = ArrayList< List<Triple<TYPE,Any?,Int>> >()

        //동적으로 새 상품을 추가하는 함수
        fun addItem(item: List<Triple<TYPE,Any?,Int>> ) {

            if(items.size < this.nMaxCount)
                items.add(item)
            else
            {
                items.removeAt(0);
                items.add(item);
            }
        }
        //동적으로 새 상품을 추가하는 함수
        fun addItem(idx : Int,item: List<Triple<TYPE,Any?,Int>> ) {

            if( idx <= 0){
                if(items.size < this.nMaxCount)
                    items.add(0,item)
                else
                {
                    items.removeAt(items.size-1);
                    items.add(0,item);
                }
            }
            else if (idx >= this.nMaxCount){
                this.addItem(item);
            }
            else {
                items.add(idx, item)
            }
        }

        // 데이터 변경
        fun updateItem(idx : Int,item: List<Triple<TYPE,Any?,Int>>){

            if( (idx >= items.size) or (idx < 0)) return
            val temp = items.set(idx,item)
        }
        // 데이터 삭제
        fun delItem(idx : Int)
        {
            if( (idx >= items.size) or (idx < 0)) return
            sortCellStyle(idx)
            items.removeAt(idx)
        }

        fun setMaxCount(cnt: Int)
        {
            if(cnt < 0)
                this.nMaxCount = Int.MAX_VALUE;
            else
                this.nMaxCount = cnt;
        }

        // 특정 cell의 스타일 저장
        protected val cellstyle : MutableMap<Point,Bundle?> = mutableMapOf()
        fun setCellStyle(row : Int, col : Int , style : Bundle?)
        {
            if(items.isEmpty())
                return

            if(style != null) {
                val background = style?.getInt(BACKGROUND, Color.rgb(255, 255, 255)) as Int
                val foreground = style?.getInt(FOREGROUND, Color.rgb(0, 0, 0)) as Int
                val gravity = style?.getInt(GRAVITY, Gravity.END) as Int

                val instyle = Bundle()
                instyle.putInt(BACKGROUND, background)
                instyle.putInt(FOREGROUND, foreground)
                instyle.putInt(GRAVITY, gravity)
                cellstyle.set(Point(row,col),instyle)
            }
            else{
                val instyle = Bundle()
                instyle.putInt(BACKGROUND, Color.rgb(255, 255, 255))
                instyle.putInt(FOREGROUND, Color.rgb(0, 0, 0))
                instyle.putInt(GRAVITY, Gravity.END)
                cellstyle.set(Point(row,col),instyle)
            }
        }
        // cell의 스타일 불러오기 (없을 경우 기본값)
        fun getCellStyle(row : Int, col : Int) : Bundle
        {
            var data = cellstyle.get(Point(row,col))
            if(data == null)
            {
                val temp = Bundle()
                temp.putInt(BACKGROUND, Color.rgb(255, 255, 255))
                temp.putInt(FOREGROUND, Color.rgb(0, 0, 0))
                temp.putInt(GRAVITY, Gravity.END)
                return temp
            }
            else
                return data
        }
        private fun sortCellStyle(deleterow : Int)
        {
            val newarray : ArrayList<ArrayList<Bundle>> = ArrayList()

            for(idx in 0..deleterow-1)
            {
                val first_item : ArrayList<Bundle> = ArrayList()
                var col = 0
                while ( cellstyle.get(Point(idx,col)) != null)
                {
                    val temp = cellstyle.get(Point(idx,col)) as Bundle
                    first_item.add(temp)
                    col = col+1
                }
                newarray.add(first_item)
            }

            val max = items.size
            for(idx in deleterow+1..max-1)
            {
                val second_item : ArrayList<Bundle> = ArrayList()
                var col = 0
                while ( cellstyle.get(Point(idx,col)) != null)
                {
                    val temp = cellstyle.get(Point(idx,col)) as Bundle
                    second_item.add(temp)
                    col = col+1
                }
                newarray.add(second_item)
            }
            resetCellStyle()

            for(row in 0..newarray.size-1){
                 val temp = newarray.get(row)

                for(col in 0..temp.size-1)
                {
                    val temp2 = temp.get(col)
                    cellstyle.set(Point(row,col),temp2)
                }
            }
            Log.e(">>","??")

        }


        // cell스타일 초기화(데이터 리셋시 자동 호출)
        private fun resetCellStyle()
        {
            cellstyle.clear()
        }

        public fun resetItems()
        {
            resetCellStyle()
            items.clear()
        }

        //BaseAdapter는 추상 클래스이므로, 아래 네 함수들을 반드시 정의해줘야 함.
        override fun getCount(): Int {//총 갯수 알기
            return items.size
        }

        override fun getItem(position: Int): Any {//position 번째 아이템 가져오기
            return items[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return super.getItemViewType(position)
        }


        /*
        cell의 스타일 설정을 위한 textview 검색
        */
        @RequiresApi(Build.VERSION_CODES.M)
        private fun getChidrenViews(v : View?) : ArrayList<View?>
        {
            val viewlst : ArrayList<View?> = ArrayList()

            var childviewcnt = 0

            /* view 종류에 따라 형변환 */
            var type = 0
            if( v!! is HorizontalScrollView )
            {
                childviewcnt = (v!! as HorizontalScrollView).childCount
                type = 0
            }
            else if (v!! is ScrollView)
            {
                childviewcnt = (v!! as ScrollView).childCount
                type = 1
            }
            else if (v!! is LinearLayout)
            {
                childviewcnt = (v!! as LinearLayout).childCount
                type = 2
            }

            for(_idx in 0..childviewcnt-1)
            {
                var tempview : View? = null

                /* view 종류에 따라 형변환 */
                when(type)
                {
                    0->{
                        tempview = (v as HorizontalScrollView).getChildAt(_idx)
                    }
                    1->{
                        tempview = (v as ScrollView).getChildAt(_idx)
                    }
                    2->{
                        tempview = (v as LinearLayout).getChildAt(_idx)
                    }
                }

                val addon = "android.widget."
                val name = tempview?.accessibilityClassName.toString().toLowerCase().replace(addon,"")
                when(name)
                {
                    "textview"->{
                        viewlst.add(tempview)
                    }
                    else->{
                        val _templst = getChidrenViews(tempview)
                        viewlst.addAll(_templst)
                    }
                }
            }
            /* textview 목록 반환 */
            return viewlst
        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {//여기서 리턴하는 뷰가 실제로 표시되는 뷰이다.
            var v: View? = convertView

            if (v == null) {
                val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                v = inflater.inflate(baseLayOutID, parent, false)
            }


            val viewlst = getChidrenViews(v!!)

            /* cell 스타일 지정*/
            for(idx in 0..viewlst.size-1)
            {
                val txtview = viewlst.get(idx) as TextView
                val style = getCellStyle(position,idx)

                val backgroundcolor = style.getInt(BACKGROUND)
                val foregroundcolor = style.getInt(FOREGROUND)
                val gravity = style.getInt(GRAVITY)

                txtview.setBackgroundColor(backgroundcolor)
                txtview.setTextColor(foregroundcolor)
                txtview.gravity = gravity
            }

            /* cell data 타입에 따라 분류 */
            val _items = items[position] as List<Triple<TYPE,Any?,Int>>

            val size = _items.size -1
            for(idx in 0..size)
            {
                val item = _items.get(idx)
                val type = item.first as TableGrid.TYPE
                val resid = item.third as Int
                val txt = v!!.findViewById<View>(resid) as TextView

                var data : Any? = null
                when(type){
                    TYPE.INT -> {
                        data = item.second.toString().trim()
                        if(data.toString().length == 0)
                            data = "0"

                        if(data.toString().indexOf(".") > -1)
                        {
                            data = data.toString().substring(0,data.toString().indexOf("."))
                        }
                        data = data.toInt()
                        txt.tag = data
                    }
                    TYPE.DOUBLE -> {
                        data = item.second.toString().trim()
                        if(data.toString().length == 0)
                            data = "0.0"
                        data = data.toDouble()
                        txt.tag = data
                    }
                    TYPE.STRING -> {
                        data = item.second.toString().trim()
                        txt.tag = data.toString()
                    }

                    /* 상하가 수치 색상 정보 */
                    TYPE.DAEBI->{
                        data = item.second.toString().toInt()
                        when(data)
                        {
                            (-64) -> {
                                txt.setTextColor(ContextCompat.getColor(parent.context, android.R.color.black))
                            }
                            (-96) -> {
                                txt.setTextColor(ContextCompat.getColor(parent.context, android.R.color.holo_blue_light))
                            }
                            (-80) -> {
                                txt.setTextColor(ContextCompat.getColor(parent.context, android.R.color.holo_red_light))
                            }
                        }
                        data = txt.tag.toString()
                    }
                    TYPE.ICON->{
                        data = item.second.toString()
                        val icon = CommonFunction.getDaebiGubun(data)
                        when(icon)
                        {
                            "↑"->{
                                txt.setTextColor(ContextCompat.getColor(parent.context, android.R.color.holo_red_light))
                            }
                            "▲"->{
                                txt.setTextColor(ContextCompat.getColor(parent.context, android.R.color.holo_red_light))
                            }
                            ""->{
                                txt.setTextColor(ContextCompat.getColor(parent.context, android.R.color.black))
                            }
                            "↓"->{
                                txt.setTextColor(ContextCompat.getColor(parent.context, android.R.color.holo_blue_light))
                            }
                            "▼"->{
                                txt.setTextColor(ContextCompat.getColor(parent.context, android.R.color.holo_blue_light))
                            }
                        }
                        data = icon
                    }
                }
                txt.setText(data.toString())
            }

            return v
        }
    }
}
