package tafm.tt10tt10.mytesttravel.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.bm2_schedule_fragment.*
import tafm.tt10tt10.mytesttravel.R
import tafm.tt10tt10.mytesttravel.model.TravelDetail

class Bm2ScheduleFragment : Fragment() {

    private lateinit var realm: Realm
    private lateinit var include: View
    private var listener: Bm2ScheduleEditOnClickListener? = null
    private var lastOrder = 1
    private var manageId = 1
    private var day = 1
    private var order = 0
    private var travelDays = 1

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context !is Bm2ScheduleEditOnClickListener){
            throw RuntimeException("リスナーを実装せよ")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bm2_schedule_fragment, container, false)
        val argument = arguments
        argument?.apply{
            Log.i("【Bm2ScheduleFragment】", "[onCreateView] $argument")
            manageId = this["manageId"] as Int
            day = this["day"] as Int
            order = this["order"] as Int
            travelDays = this["travelDays"] as Int
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        include = bm2_schedule_include
        goneGuideText()
        realm = Realm.getDefaultInstance()
        val travelDetails = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day)
            .sort("order")
            .findAll()

        for(travelDetail in travelDetails) setTextViews(travelDetail)
        goneLastDestinationVisibility(day, travelDays, lastOrder)
        targetLayoutHighLight()
        //クリックリスナー実装
        view.findViewById<ImageView>(R.id.bm2_schedule_edit).setOnClickListener {
            it.notPressTwice()
            listener = context as? Bm2ScheduleEditOnClickListener
            listener?.onScheduleEditClick()
        }
    }

    //現在表示している部分をハイライトする。
    private fun targetLayoutHighLight() {
        if(day != travelDays && order == lastOrder){
            include.findViewById<View>(R.id.sc2arrivalLayout).setBackgroundColor(Color.argb(30, 2, 252, 41))
        }else {
            when (order) {
                0 -> include.findViewById<View>(R.id.sc2departureLayout).setBackgroundColor(Color.argb(30, 2, 252, 41))
                1 -> include.findViewById<View>(R.id.sc2destination1Layout).setBackgroundColor(Color.argb(30, 2, 252, 41))
                2 -> include.findViewById<View>(R.id.sc2destination2Layout).setBackgroundColor(Color.argb(30, 2, 252, 41))
                3 -> include.findViewById<View>(R.id.sc2destination3Layout).setBackgroundColor(Color.argb(30, 2, 252, 41))
                4 -> include.findViewById<View>(R.id.sc2destination4Layout).setBackgroundColor(Color.argb(30, 2, 252, 41))
                5 -> include.findViewById<View>(R.id.sc2destination5Layout).setBackgroundColor(Color.argb(30, 2, 252, 41))
                6 -> include.findViewById<View>(R.id.sc2destination6Layout).setBackgroundColor(Color.argb(30, 2, 252, 41))
                9 -> include.findViewById<View>(R.id.sc2arrivalLayout).setBackgroundColor(Color.argb(30, 2, 252, 41))
            }
        }
    }

    //何日目かのTextを非表示にする。
    private fun goneGuideText() {
        include.findViewById<TextView>(R.id.sc2DayText).visibility = View.GONE
    }

    //それぞれのLayoutの表示非表示。
    private fun setTextViews(travelDetail: TravelDetail?) {
        Log.i("【Bm2ScheduleFragment】", "$travelDetail")
        when(travelDetail?.order){
            0 -> setTextViewsProperty(travelDetail, R.id.sc2departureLayout
                , R.id.sc2departureTime, R.id.sc2departurePlace
                , R.id.sc2departureMoveTime, R.id.sc2departureMoveTime
                , isOrder0 = true, isOrder9 = false)
            1 -> setTextViewsProperty(travelDetail, R.id.sc2destination1Layout
                , R.id.sc2startTime1, R.id.sc2destination1
                , R.id.sc2requireTime1, R.id.sc2moveTime1
                , isOrder0 = false, isOrder9 = false)
            2 -> setTextViewsProperty(travelDetail, R.id.sc2destination2Layout
                , R.id.sc2startTime2, R.id.sc2destination2
                , R.id.sc2requireTime2, R.id.sc2moveTime2
                , isOrder0 = false, isOrder9 = false)
            3 -> setTextViewsProperty(travelDetail, R.id.sc2destination3Layout
                , R.id.sc2startTime3, R.id.sc2destination3
                , R.id.sc2requireTime3, R.id.sc2moveTime3
                , isOrder0 = false, isOrder9 = false)
            4 -> setTextViewsProperty(travelDetail, R.id.sc2destination4Layout
                , R.id.sc2startTime4, R.id.sc2destination4
                , R.id.sc2requireTime4, R.id.sc2moveTime4
                , isOrder0 = false, isOrder9 = false)
            5 -> setTextViewsProperty(travelDetail, R.id.sc2destination5Layout
                , R.id.sc2startTime5, R.id.sc2destination5
                , R.id.sc2requireTime5, R.id.sc2moveTime5
                , isOrder0 = false, isOrder9 = false)
            6 -> setTextViewsProperty(travelDetail, R.id.sc2destination6Layout
                , R.id.sc2startTime6, R.id.sc2destination6
                , R.id.sc2requireTime6, R.id.sc2moveTime6
                , isOrder0 = false, isOrder9 = false)
            9 -> setTextViewsProperty(travelDetail, R.id.sc2arrivalLayout
                , R.id.sc2ArrivalTime, R.id.sc2ArrivalPlace
                , R.id.sc2ArrivalTime, R.id.sc2ArrivalTime
                , isOrder0 = false, isOrder9 = true)
        }
    }

    //それぞれの目的地の表示設定と値の格納
    private fun setTextViewsProperty(travelDetail: TravelDetail, layoutId: Int, startTimeId: Int
                                     , destinationId: Int, requireTimeId: Int, moveTimeId: Int
                                     , isOrder0: Boolean, isOrder9: Boolean) {
        include.findViewById<View>(layoutId).visibility = View.VISIBLE

        include.findViewById<TextView>(startTimeId).text = travelDetail.startTime
        include.findViewById<TextView>(startTimeId).setBackgroundResource(R.drawable.border)
        include.findViewById<TextView>(destinationId).text = travelDetail.destination
        if (!isOrder9){
            if (!isOrder0){
                include.findViewById<TextView>(requireTimeId).text = travelDetail.requiredTime
                include.findViewById<TextView>(requireTimeId).setBackgroundColor(Color.alpha(0))
            }
            include.findViewById<TextView>(moveTimeId).text = travelDetail.moveTime
            include.findViewById<TextView>(moveTimeId).setBackgroundColor(Color.alpha(0))
            include.findViewById<TextView>(R.id.sc2ArrivalTime).text = travelDetail.startTime
            include.findViewById<TextView>(R.id.sc2ArrivalPlace).text = travelDetail.destination
            lastOrder = travelDetail.order
        }
    }

    //最終日以外の場合、最終目的地のレイアウトを非表示にする。
    private fun goneLastDestinationVisibility(day: Int, travelDays: Int, lastOrder: Int) {
        if (day != travelDays){
            when(lastOrder){
                1 -> lastDestinationLayoutDone(R.id.sc2destination1Layout)
                2 -> lastDestinationLayoutDone(R.id.sc2destination2Layout)
                3 -> lastDestinationLayoutDone(R.id.sc2destination3Layout)
                4 -> lastDestinationLayoutDone(R.id.sc2destination4Layout)
                5 -> lastDestinationLayoutDone(R.id.sc2destination5Layout)
                6 -> lastDestinationLayoutDone(R.id.sc2destination6Layout)
            }
        }
    }

    //最終日以外の場合、最終目的地のレイアウトを非表示にする。
    private fun lastDestinationLayoutDone(layoutId: Int) {
        include.findViewById<View>(layoutId).visibility = View.GONE
    }

    //Bm2ScheduleEditのinterface。
    interface Bm2ScheduleEditOnClickListener {
        fun onScheduleEditClick()
    }

    /**
     * 二度押し防止施策として 1.5 秒間タップを禁止する
     */
    private fun View.notPressTwice() {
        this.isEnabled = false
        this.postDelayed({
            this.isEnabled = true
        }, 1500L)
    }

    //フラグメント削除時にRealmを閉じる。
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}