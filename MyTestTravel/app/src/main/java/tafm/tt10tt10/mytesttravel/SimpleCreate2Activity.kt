package tafm.tt10tt10.mytesttravel

import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_simple_create2.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.yesButton
import tafm.tt10tt10.mytesttravel.fragment.RequireTimePickerFragment
import tafm.tt10tt10.mytesttravel.fragment.TimePickerFragment
import tafm.tt10tt10.mytesttravel.model.Travel
import tafm.tt10tt10.mytesttravel.model.TravelDetail
import tafm.tt10tt10.mytesttravel.model.TravelPart
import java.lang.StringBuilder


class SimpleCreate2Activity : AppCompatActivity()
    , TimePickerFragment.OnTimeSelectedListener
    , RequireTimePickerFragment.OnTimeSelectedListener {

    private lateinit var realm: Realm
    private var manageId: Int = 0
    private lateinit var temporalyTag: String
    private var temporalyDayFlag = 0
    private var travelDays = 1L

    private val requireTimeText = "所要: ";   private val moveTimeText = "移動: "
    //(1日目)タグ
    private val departureTime10Tag = "departureTime10Tag";    private val departureMoveTime10Tag = "departureMoveTime10Tag"
    private val requireTime11Tag = "requireTime11Tag";   private val moveTime11Tag = "moveTime11Tag"
    private val requireTime12Tag = "requireTime12Tag";   private val moveTime12Tag = "moveTime12Tag"
    private val requireTime13Tag = "requireTime13Tag";   private val moveTime13Tag = "moveTime13Tag"
    private val requireTime14Tag = "requireTime14Tag";   private val moveTime14Tag = "moveTime14Tag"
    private val requireTime15Tag = "requireTime15Tag";   private val moveTime15Tag = "moveTime15Tag"
    private val requireTime16Tag = "requireTime16Tag";   private val moveTime16Tag = "moveTime16Tag"
    //(2日目)タグ
    private val departureTime20Tag = "departureTime20Tag";    private val departureMoveTime20Tag = "departureMoveTime20Tag"
    private val requireTime21Tag = "requireTime21Tag";   private val moveTime21Tag = "moveTime21Tag"
    private val requireTime22Tag = "requireTime22Tag";   private val moveTime22Tag = "moveTime22Tag"
    private val requireTime23Tag = "requireTime23Tag";   private val moveTime23Tag = "moveTime23Tag"
    private val requireTime24Tag = "requireTime24Tag";   private val moveTime24Tag = "moveTime24Tag"
    private val requireTime25Tag = "requireTime25Tag";   private val moveTime25Tag = "moveTime25Tag"
    private val requireTime26Tag = "requireTime26Tag";   private val moveTime26Tag = "moveTime26Tag"
    //(3日目)タグ
    private val departureTime30Tag = "departureTime30Tag";    private val departureMoveTime30Tag = "departureMoveTime30Tag"
    private val requireTime31Tag = "requireTime31Tag";   private val moveTime31Tag = "moveTime31Tag"
    private val requireTime32Tag = "requireTime32Tag";   private val moveTime32Tag = "moveTime32Tag"
    private val requireTime33Tag = "requireTime33Tag";   private val moveTime33Tag = "moveTime33Tag"
    private val requireTime34Tag = "requireTime34Tag";   private val moveTime34Tag = "moveTime34Tag"
    private val requireTime35Tag = "requireTime35Tag";   private val moveTime35Tag = "moveTime35Tag"
    private val requireTime36Tag = "requireTime36Tag";   private val moveTime36Tag = "moveTime36Tag"
    //(4日目)タグ
    private val departureTime40Tag = "departureTime40Tag";    private val departureMoveTime40Tag = "departureMoveTime40Tag"
    private val requireTime41Tag = "requireTime41Tag";   private val moveTime41Tag = "moveTime41Tag"
    private val requireTime42Tag = "requireTime42Tag";   private val moveTime42Tag = "moveTime42Tag"
    private val requireTime43Tag = "requireTime43Tag";   private val moveTime43Tag = "moveTime43Tag"
    private val requireTime44Tag = "requireTime44Tag";   private val moveTime44Tag = "moveTime44Tag"
    private val requireTime45Tag = "requireTime45Tag";   private val moveTime45Tag = "moveTime45Tag"
    private val requireTime46Tag = "requireTime46Tag";   private val moveTime46Tag = "moveTime46Tag"
    //(5日目)タグ
    private val departureTime50Tag = "departureTime50Tag";    private val departureMoveTime50Tag = "departureMoveTime50Tag"
    private val requireTime51Tag = "requireTime51Tag";   private val moveTime51Tag = "moveTime51Tag"
    private val requireTime52Tag = "requireTime52Tag";   private val moveTime52Tag = "moveTime52Tag"
    private val requireTime53Tag = "requireTime53Tag";   private val moveTime53Tag = "moveTime53Tag"
    private val requireTime54Tag = "requireTime54Tag";   private val moveTime54Tag = "moveTime54Tag"
    private val requireTime55Tag = "requireTime55Tag";   private val moveTime55Tag = "moveTime55Tag"
    private val requireTime56Tag = "requireTime56Tag";   private val moveTime56Tag = "moveTime56Tag"
    //(6日目)タグ
    private val departureTime60Tag = "departureTime60Tag";    private val departureMoveTime60Tag = "departureMoveTime60Tag"
    private val requireTime61Tag = "requireTime61Tag";   private val moveTime61Tag = "moveTime61Tag"
    private val requireTime62Tag = "requireTime62Tag";   private val moveTime62Tag = "moveTime62Tag"
    private val requireTime63Tag = "requireTime63Tag";   private val moveTime63Tag = "moveTime63Tag"
    private val requireTime64Tag = "requireTime64Tag";   private val moveTime64Tag = "moveTime64Tag"
    private val requireTime65Tag = "requireTime65Tag";   private val moveTime65Tag = "moveTime65Tag"
    private val requireTime66Tag = "requireTime66Tag";   private val moveTime66Tag = "moveTime66Tag"
    //(7日目)タグ
    private val departureTime70Tag = "departureTime70Tag";    private val departureMoveTime70Tag = "departureMoveTime70Tag"
    private val requireTime71Tag = "requireTime71Tag";   private val moveTime71Tag = "moveTime71Tag"
    private val requireTime72Tag = "requireTime72Tag";   private val moveTime72Tag = "moveTime72Tag"
    private val requireTime73Tag = "requireTime73Tag";   private val moveTime73Tag = "moveTime73Tag"
    private val requireTime74Tag = "requireTime74Tag";   private val moveTime74Tag = "moveTime74Tag"
    private val requireTime75Tag = "requireTime75Tag";   private val moveTime75Tag = "moveTime75Tag"
    private val requireTime76Tag = "requireTime76Tag";   private val moveTime76Tag = "moveTime76Tag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_create2)

        realm = Realm.getDefaultInstance()
        manageId = intent.getIntExtra("MANAGE_ID", 0)
        travelDays = intent.getLongExtra("TRAVEL_DAYS", 1)
        //一日目の出発地設定
        findViewById<View>(R.id.includeSC2Day1).findViewById<TextView>(R.id.departurePlace).text =
            intent.getStringExtra("DEPARTURE_PLACE")

        visibleInclude(travelDays)

        val travelParts = realm.where<TravelPart>().equalTo("manageId", manageId).findAllAsync()

        for(travelPart in travelParts){
            //何日目かチェック。includeするレイアウトを決める。
            when(travelPart?.day){
                1 -> checkDestination(R.id.includeSC2Day1, R.id.includeSC2Day1, travelPart)
                2 -> checkDestination(R.id.includeSC2Day2, R.id.includeSC2Day1, travelPart)
                3 -> checkDestination(R.id.includeSC2Day3, R.id.includeSC2Day2, travelPart)
                4 -> checkDestination(R.id.includeSC2Day4, R.id.includeSC2Day3, travelPart)
                5 -> checkDestination(R.id.includeSC2Day5, R.id.includeSC2Day4, travelPart)
                6 -> checkDestination(R.id.includeSC2Day6, R.id.includeSC2Day5, travelPart)
                7 -> checkDestination(R.id.includeSC2Day7, R.id.includeSC2Day6, travelPart)
            }
        }
        setViewLink(travelDays)
        setDayText()

        //戻るボタンタップ
        sc2backToSC1.setOnClickListener { finish() }
        //決定ボタンタップ
        sc2nextToComplete.setOnClickListener {

            var checkFlag = 0
            for (day in 1..travelDays){
                if(checkEnterDay(day)) checkFlag++
            }

            if(checkFlag >= travelDays){
                realm.executeTransaction {
                    for (day in 1..travelDays) {
                        when (day) {
                            1L -> devideDestination(1, R.id.includeSC2Day1)
                            2L -> devideDestination(2, R.id.includeSC2Day2)
                            3L -> devideDestination(3, R.id.includeSC2Day3)
                            4L -> devideDestination(4, R.id.includeSC2Day4)
                            5L -> devideDestination(5, R.id.includeSC2Day5)
                            6L -> devideDestination(6, R.id.includeSC2Day6)
                            7L -> devideDestination(7, R.id.includeSC2Day7)
                        }
                    }
                    deleteFlagFalse()
                }
                startActivity<CreateCompleteActivity>()
            }else{
                alert ("時間・時刻をすべて入力してください"){ yesButton {  } }.show()
            }
        }
    }

    //入力チェック。日にちチェック
    private fun checkEnterDay(day: Long): Boolean {
        when(day){
            1L -> return checkEnter(R.id.includeSC2Day1)
            2L -> return checkEnter(R.id.includeSC2Day2)
            3L -> return checkEnter(R.id.includeSC2Day3)
            4L -> return checkEnter(R.id.includeSC2Day4)
            5L -> return checkEnter(R.id.includeSC2Day5)
            6L -> return checkEnter(R.id.includeSC2Day6)
            7L -> return checkEnter(R.id.includeSC2Day7)
        }
        return false
    }

    //入力チェック。visibilityを使ってテキストが空かどうか判定する。
    private fun checkEnter(include: Int): Boolean {
        var flag = true
        if(findViewById<View>(include).findViewById<View>(R.id.sc2departureLayout).visibility == View.VISIBLE){
            flag = findViewById<View>(include).findViewById<TextView>(R.id.departureTime).text.isNotEmpty()
            if (flag) flag = findViewById<View>(include).findViewById<TextView>(R.id.departureMoveTime).text.isNotEmpty()
        }
        if(findViewById<View>(include).findViewById<View>(R.id.sc2destination1Layout).visibility == View.VISIBLE){
            if (flag) flag = findViewById<View>(include).findViewById<TextView>(R.id.requireTime1).text.isNotEmpty()
            if (flag) flag = findViewById<View>(include).findViewById<TextView>(R.id.moveTime1).text.isNotEmpty()
        }
        if(findViewById<View>(include).findViewById<View>(R.id.sc2destination2Layout).visibility == View.VISIBLE){
            if (flag) flag = findViewById<View>(include).findViewById<TextView>(R.id.requireTime2).text.isNotEmpty()
            if (flag) flag = findViewById<View>(include).findViewById<TextView>(R.id.moveTime2).text.isNotEmpty()
        }
        if(findViewById<View>(include).findViewById<View>(R.id.sc2destination3Layout).visibility == View.VISIBLE){
            if (flag) flag = findViewById<View>(include).findViewById<TextView>(R.id.requireTime3).text.isNotEmpty()
            if (flag) flag = findViewById<View>(include).findViewById<TextView>(R.id.moveTime3).text.isNotEmpty()
        }
        if(findViewById<View>(include).findViewById<View>(R.id.sc2destination4Layout).visibility == View.VISIBLE){
            if (flag) flag = findViewById<View>(include).findViewById<TextView>(R.id.requireTime4).text.isNotEmpty()
            if (flag) flag = findViewById<View>(include).findViewById<TextView>(R.id.moveTime4).text.isNotEmpty()
        }
        if(findViewById<View>(include).findViewById<View>(R.id.sc2destination5Layout).visibility == View.VISIBLE){
            if (flag) flag = findViewById<View>(include).findViewById<TextView>(R.id.requireTime5).text.isNotEmpty()
            if (flag) flag = findViewById<View>(include).findViewById<TextView>(R.id.moveTime5).text.isNotEmpty()
        }
        if(findViewById<View>(include).findViewById<View>(R.id.sc2destination6Layout).visibility == View.VISIBLE){
            if (flag) flag = findViewById<View>(include).findViewById<TextView>(R.id.requireTime6).text.isNotEmpty()
            if (flag) flag = findViewById<View>(include).findViewById<TextView>(R.id.moveTime6).text.isNotEmpty()
        }
        return flag
    }

    //目的地別に分類
    private fun devideDestination(day: Int, include: Int) {
        createDepartureTravelDetail(day, include)
        var lastDestinationFlag = true
        val lastDayFlag = (day.toLong() == travelDays)

        if(findViewById<View>(include).findViewById<TextView>(R.id.sc2destination6).text.isNotEmpty()){
            createTravelDetail(6, day, include, R.id.sc2destination6, R.id.startTime6, R.id.requireTime6 ,R.id.moveTime6, lastDestinationFlag)
            lastDestinationFlag = false
        }
        if(findViewById<View>(include).findViewById<TextView>(R.id.sc2destination5).text.isNotEmpty()){
            createTravelDetail(5, day, include, R.id.sc2destination5, R.id.startTime5, R.id.requireTime5 ,R.id.moveTime5, lastDestinationFlag)
            lastDestinationFlag = false
        }
        if(findViewById<View>(include).findViewById<TextView>(R.id.sc2destination4).text.isNotEmpty()){
            createTravelDetail(4, day, include, R.id.sc2destination4, R.id.startTime4, R.id.requireTime4 ,R.id.moveTime4, lastDestinationFlag)
            lastDestinationFlag = false
        }
        if(findViewById<View>(include).findViewById<TextView>(R.id.sc2destination3).text.isNotEmpty()){
            createTravelDetail(3, day, include, R.id.sc2destination3, R.id.startTime3, R.id.requireTime3 ,R.id.moveTime3, lastDestinationFlag)
            lastDestinationFlag = false
        }
        if(findViewById<View>(include).findViewById<TextView>(R.id.sc2destination2).text.isNotEmpty()){
            createTravelDetail(2, day, include, R.id.sc2destination2, R.id.startTime2, R.id.requireTime2 ,R.id.moveTime2, lastDestinationFlag)
            lastDestinationFlag = false
        }
        if(findViewById<View>(include).findViewById<TextView>(R.id.sc2destination1).text.isNotEmpty()){
            createTravelDetail(1, day, include, R.id.sc2destination1, R.id.startTime1, R.id.requireTime1 ,R.id.moveTime1, lastDestinationFlag)
        }

        if(lastDayFlag){
            val maxDetailId = realm.where<TravelDetail>().max("id")
            val nextDetailId = (maxDetailId?.toInt() ?: 0) + 1
            val travelDetail = realm.createObject<TravelDetail>(nextDetailId)

            travelDetail.manageId = manageId
            travelDetail.day = day
            travelDetail.order = 9
            travelDetail.destination = findViewById<View>(include).findViewById<TextView>(R.id.arrivalPlace).text.toString()
            travelDetail.startTime = findViewById<View>(include).findViewById<TextView>(R.id.arrivalTime).text.toString()
        }
    }

    //出発のTravelDetail作成
    private fun createDepartureTravelDetail(day: Int, include: Int) {
        val maxDetailId = realm.where<TravelDetail>().max("id")
        val nextDetailId = (maxDetailId?.toInt() ?: 0) + 1
        val travelDetail = realm.createObject<TravelDetail>(nextDetailId)

        travelDetail.manageId = manageId
        travelDetail.day = day
        travelDetail.order = 0
        travelDetail.destination = findViewById<View>(include).findViewById<TextView>(R.id.departurePlace).text.toString()
        travelDetail.startTime = findViewById<View>(include).findViewById<TextView>(R.id.departureTime).text.toString()
        travelDetail.moveTime = findViewById<View>(include).findViewById<TextView>(R.id.departureMoveTime).text.toString()
    }

    //TravelDetailデータベースに値を格納
    private fun createTravelDetail(order: Int, day:Int, include: Int, destinationId: Int
                                   , startId: Int, requireId: Int, moveId: Int, lastDestinationFlag: Boolean){
        val maxDetailId = realm.where<TravelDetail>().max("id")
        val nextDetailId = (maxDetailId?.toInt() ?: 0) + 1
        val travelDetail = realm.createObject<TravelDetail>(nextDetailId)
        travelDetail.manageId = manageId
        travelDetail.day = day
        travelDetail.order = order
        travelDetail.destination = if(!lastDestinationFlag){
            findViewById<View>(include).findViewById<TextView>(destinationId).text.toString()
        }else{
            findViewById<View>(include).findViewById<TextView>(R.id.arrivalPlace).text.toString()
        }
        travelDetail.startTime = findViewById<View>(include).findViewById<TextView>(startId).text.toString()
        travelDetail.requiredTime = findViewById<View>(include).findViewById<TextView>(requireId).text.toString()
        travelDetail.moveTime = findViewById<View>(include).findViewById<TextView>(moveId).text.toString()
    }

    //目的地が空でなければ値をViewにセットする。
    //何日目かを元に出発地と到着地の設定も行う。
    private fun checkDestination(include: Int, preInclude: Int, travelPart: TravelPart) {
        val lastDayFlag = (travelPart.day.toLong() == travelDays)
        var lastDestinationFlag = true
        //目的地が空でなければ、レイアウト・TextView・目的地名を渡す。
        if(travelPart.destination6.isNotEmpty()) {
            setProperty(travelPart.destination6, R.id.sc2destination6Layout, R.id.sc2destination6, include, lastDestinationFlag, lastDayFlag)
            lastDestinationFlag = false
        }
        if(travelPart.destination5.isNotEmpty()) {
            setProperty(travelPart.destination5, R.id.sc2destination5Layout, R.id.sc2destination5, include, lastDestinationFlag, lastDayFlag)
            lastDestinationFlag = false
        }
        if(travelPart.destination4.isNotEmpty()) {
            setProperty(travelPart.destination4, R.id.sc2destination4Layout, R.id.sc2destination4, include, lastDestinationFlag, lastDayFlag)
            lastDestinationFlag = false
        }
        if(travelPart.destination3.isNotEmpty()) {
            setProperty(travelPart.destination3, R.id.sc2destination3Layout, R.id.sc2destination3, include, lastDestinationFlag, lastDayFlag)
            lastDestinationFlag = false
        }
        if(travelPart.destination2.isNotEmpty()) {
            setProperty(travelPart.destination2, R.id.sc2destination2Layout, R.id.sc2destination2, include, lastDestinationFlag, lastDayFlag)
            lastDestinationFlag = false
        }
        if(travelPart.destination1.isNotEmpty()) {
            setProperty(travelPart.destination1, R.id.sc2destination1Layout ,R.id.sc2destination1, include, lastDestinationFlag, lastDayFlag)
        }
        if(lastDayFlag){
            findViewById<View>(include).findViewById<TextView>(R.id.arrivalPlace).text =
                intent.getStringExtra("ARRIVAL_PLACE")
        }
        if(travelPart.day > 1) {
            findViewById<View>(include).findViewById<TextView>(R.id.departurePlace).text =
                findViewById<View>(preInclude).findViewById<TextView>(R.id.arrivalPlace).text
        }
    }

    //レイアウトのvisibilityをVisibleに、目的地をテキストに表示
    private fun setProperty(destinationName: String, layoutId: Int, dViewId: Int, include: Int, lastDestinationFlag: Boolean, lastDayFlag: Boolean) {
        findViewById<View>(include).findViewById<TextView>(dViewId).text = destinationName
        if(lastDestinationFlag && !lastDayFlag){
            //最終目的地かつ最終日ではないとき、その日の到着地を最終目的地に入れる。
            findViewById<View>(include).findViewById<TextView>(R.id.arrivalPlace).text = destinationName
        }else{
            //上記以外の場合、Visibleにする。
            findViewById<View>(include).findViewById<View>(layoutId).visibility = View.VISIBLE
        }
    }

    //includeの表示非表示
    private fun visibleInclude(travelDays: Long) {
        if(travelDays > 1L) findViewById<View>(R.id.includeSC2Day2).visibility = View.VISIBLE
        if(travelDays > 2L) findViewById<View>(R.id.includeSC2Day3).visibility = View.VISIBLE
        if(travelDays > 3L) findViewById<View>(R.id.includeSC2Day4).visibility = View.VISIBLE
        if(travelDays > 4L) findViewById<View>(R.id.includeSC2Day5).visibility = View.VISIBLE
        if(travelDays > 5L) findViewById<View>(R.id.includeSC2Day6).visibility = View.VISIBLE
        if(travelDays > 6L) findViewById<View>(R.id.includeSC2Day7).visibility = View.VISIBLE
    }

    //出発時間をタップ時の処理メソッド（TimePickerフラグメントを呼びだす）
    private fun callDepartureTimePicker(tag: String) {
        val dialog = TimePickerFragment()
        dialog.show(supportFragmentManager, tag)
        temporalyTag = dialog.tag.toString()
    }

    //所要・移動時間をクリックしたときの処理メソッド（RequireTimePickerフラグメントを呼びだす）
    private fun callRequireTimePicker(tag: String) {
        val dialog = RequireTimePickerFragment()
        dialog.show(supportFragmentManager, tag)
        temporalyTag = dialog.tag.toString()
    }

    //出発時間をTextViewにセットするメソッド
    private fun setDepartureTime(textView: TextView, hourOfDay: Int, minute: Int, dayFlag:Int) {
        temporalyDayFlag = dayFlag
        val timeFormat = "%1$02d:%2$02d"
        textView.text = timeFormat.format(hourOfDay, minute)
        if(textView.text != "") textView.background = doneColorChange()
    }

    //所要・移動時間をセットするメソッド
    private fun setRequireOrMoveTime(hourOfDay: Int, minute: Int, textView: TextView, reqOrMove: String, dayFlag:Int) {
        temporalyDayFlag = dayFlag
        val builder = StringBuilder(reqOrMove)
        if (hourOfDay != 0) builder.append(hourOfDay).append(" h ")
        textView.text = builder.append(minute).append(" min ").toString()
        textView.background = doneColorChange()
    }

    //時間自動入力メソッド
    private fun refresh(include: Int) {
        val total00 = timeFirstRefresh(findViewById<View>(include).findViewById(R.id.departureTime))
        val total11 = timeRefresh(total00, null, findViewById<View>(include).findViewById(R.id.departureMoveTime)
            , findViewById<View>(include).findViewById(R.id.startTime1))
        val total12 = timeRefresh(total11, findViewById<View>(include).findViewById(R.id.requireTime1)
            , findViewById<View>(include).findViewById(R.id.moveTime1)
            , findViewById<View>(include).findViewById(R.id.startTime2))
        val total13 = timeRefresh(total12, findViewById<View>(include).findViewById(R.id.requireTime2)
            , findViewById<View>(include).findViewById(R.id.moveTime2)
            , findViewById<View>(include).findViewById(R.id.startTime3))
        val total14 = timeRefresh(total13, findViewById<View>(include).findViewById(R.id.requireTime3)
            , findViewById<View>(include).findViewById(R.id.moveTime3)
            , findViewById<View>(include).findViewById(R.id.startTime4))
        val total15 = timeRefresh(total14, findViewById<View>(include).findViewById(R.id.requireTime4)
            , findViewById<View>(include).findViewById(R.id.moveTime4)
            , findViewById<View>(include).findViewById(R.id.startTime5))
        val total16 = timeRefresh(total15, findViewById<View>(include).findViewById(R.id.requireTime5)
            , findViewById<View>(include).findViewById(R.id.moveTime5)
            , findViewById<View>(include).findViewById(R.id.startTime6))
        timeRefresh(total16, findViewById<View>(include).findViewById(R.id.requireTime6)
            , findViewById<View>(include).findViewById(R.id.moveTime6)
            , findViewById<View>(include).findViewById(R.id.arrivalTime))
    }

    //refreshDay()から呼ばれる。時間を自動入力。出発時間設定
    private fun timeFirstRefresh(startTime: TextView): Long {
        val startTimeArr = startTime.text.split(":")
        val hour = startTimeArr[0].toLong()
        val min = startTimeArr[1].toLong()
        return hour * 60 + min
    }

    //時間を自動入力。合計時間（分）の返却とstartTimeセット。出発時はrequireTime == null
    private fun timeRefresh(sum: Long, requireTime: TextView?, moveTime: TextView, startTime: TextView): Long {
        var total = sum
        if (requireTime is TextView && requireTime.text.isNotEmpty()) total += extractData(requireTime, requireTimeText.length)
        if (moveTime.text.isNotEmpty()) total += extractData(moveTime, moveTimeText.length)
        val timeFormat = "%1$02d:%2$02d"
        startTime.text = timeFormat.format(total / 60, total % 60)
        return total
    }

    //移動時間と所要時間データを抽出して加算したものを返却（1時間以上未満で場合分け）
    private fun extractData(textView: TextView, deleteNum: Int): Long {
        val extractData = StringBuilder(textView.text).delete(textView.text.length-5, textView.text.length-1).delete(0, deleteNum-1)
        val rArr = extractData.split("h")
        return if(rArr.size == 2) {
            rArr[0].trim().toLong() * 60 + rArr[1].trim().toLong()
        }else {
            rArr[0].trim().toLong()
        }
    }

    //入力成功時に色を変える
    @Suppress("DEPRECATION")
    private fun doneColorChange(): Drawable? {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) getDrawable(R.color.timeBackGround)
        else resources.getDrawable(R.color.timeBackGround)
    }

    //Travel、TravelPartデータのデリートフラグを0にする。
    private fun deleteFlagFalse() {
        val updateTravel = realm.where<Travel>().equalTo("manageId", manageId).findFirst()
        updateTravel?.deleteFlag = 0
        updateTravel?.departureTime =  findViewById<View>(R.id.includeSC2Day1).findViewById<TextView>(R.id.departureTime).text.toString()
        updateTravel?.arrivalTime = when(travelDays){
            1L -> findViewById<View>(R.id.includeSC2Day1).findViewById<TextView>(R.id.arrivalTime).text.toString()
            2L -> findViewById<View>(R.id.includeSC2Day2).findViewById<TextView>(R.id.arrivalTime).text.toString()
            3L -> findViewById<View>(R.id.includeSC2Day3).findViewById<TextView>(R.id.arrivalTime).text.toString()
            4L -> findViewById<View>(R.id.includeSC2Day4).findViewById<TextView>(R.id.arrivalTime).text.toString()
            5L -> findViewById<View>(R.id.includeSC2Day5).findViewById<TextView>(R.id.arrivalTime).text.toString()
            6L -> findViewById<View>(R.id.includeSC2Day6).findViewById<TextView>(R.id.arrivalTime).text.toString()
            7L -> findViewById<View>(R.id.includeSC2Day7).findViewById<TextView>(R.id.arrivalTime).text.toString()
            else -> findViewById<View>(R.id.includeSC2Day1).findViewById<TextView>(R.id.arrivalTime).text.toString()
        }

        val updateTravelPart = realm.where<TravelPart>().equalTo("manageId", manageId).findAllAsync()
        for(part in updateTravelPart){
            part?.deleteFlag = 0
        }
    }

    //アクティビティが削除されたときにRealmを閉じる
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    //最初に呼ぶ。何日目かを表示させる。
    private fun setDayText() {
        findViewById<View>(R.id.includeSC2Day1).findViewById<TextView>(R.id.DayText).text = "・1日目"
        findViewById<View>(R.id.includeSC2Day2).findViewById<TextView>(R.id.DayText).text = "・2日目"
        findViewById<View>(R.id.includeSC2Day3).findViewById<TextView>(R.id.DayText).text = "・3日目"
        findViewById<View>(R.id.includeSC2Day4).findViewById<TextView>(R.id.DayText).text = "・4日目"
        findViewById<View>(R.id.includeSC2Day5).findViewById<TextView>(R.id.DayText).text = "・5日目"
        findViewById<View>(R.id.includeSC2Day6).findViewById<TextView>(R.id.DayText).text = "・6日目"
        findViewById<View>(R.id.includeSC2Day7).findViewById<TextView>(R.id.DayText).text = "・7日目"
    }

    //時刻ダイアログ選択後に呼ばれるメソッド
    override fun onSelected(hourOfDay: Int, minute: Int) {
        when(temporalyTag) {
            //1日目
            departureTime10Tag -> setDepartureTime(findViewById<View>(R.id.includeSC2Day1).findViewById(R.id.departureTime)
                , hourOfDay, minute, 1)
            departureMoveTime10Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day1).findViewById(R.id.departureMoveTime), moveTimeText, 1)
            requireTime11Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day1).findViewById(R.id.requireTime1), requireTimeText, 1)
            moveTime11Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day1).findViewById(R.id.moveTime1), moveTimeText, 1)
            requireTime12Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day1).findViewById(R.id.requireTime2), requireTimeText, 1)
            moveTime12Tag -> setRequireOrMoveTime(hourOfDay, minute, findViewById<View>(R.id.includeSC2Day1).findViewById(R.id.moveTime2)
                , moveTimeText, 1)
            requireTime13Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day1).findViewById(R.id.requireTime3), requireTimeText, 1)
            moveTime13Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day1).findViewById(R.id.moveTime3), moveTimeText, 1)
            requireTime14Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day1).findViewById(R.id.requireTime4), requireTimeText, 1)
            moveTime14Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day1).findViewById(R.id.moveTime4), moveTimeText, 1)
            requireTime15Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day1).findViewById(R.id.requireTime5), requireTimeText, 1)
            moveTime15Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day1).findViewById(R.id.moveTime5), moveTimeText, 1)
            requireTime16Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day1).findViewById(R.id.requireTime6), requireTimeText, 1)
            moveTime16Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day1).findViewById(R.id.moveTime6), moveTimeText, 1)
            //2日目
            departureTime20Tag -> setDepartureTime(findViewById<View>(R.id.includeSC2Day2).findViewById(R.id.departureTime)
                , hourOfDay, minute, 2)
            departureMoveTime20Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day2).findViewById(R.id.departureMoveTime), moveTimeText, 2)
            requireTime21Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day2).findViewById(R.id.requireTime1), requireTimeText, 2)
            moveTime21Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day2).findViewById(R.id.moveTime1), moveTimeText, 2)
            requireTime22Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day2).findViewById(R.id.requireTime2), requireTimeText, 2)
            moveTime22Tag -> setRequireOrMoveTime(hourOfDay, minute, findViewById<View>(R.id.includeSC2Day2).findViewById(R.id.moveTime2)
                , moveTimeText, 2)
            requireTime23Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day2).findViewById(R.id.requireTime3), requireTimeText, 2)
            moveTime23Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day2).findViewById(R.id.moveTime3), moveTimeText, 2)
            requireTime24Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day2).findViewById(R.id.requireTime4), requireTimeText, 2)
            moveTime24Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day2).findViewById(R.id.moveTime4), moveTimeText, 2)
            requireTime25Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day2).findViewById(R.id.requireTime5), requireTimeText, 2)
            moveTime25Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day2).findViewById(R.id.moveTime5), moveTimeText, 2)
            requireTime26Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day2).findViewById(R.id.requireTime6), requireTimeText, 2)
            moveTime26Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day2).findViewById(R.id.moveTime6), moveTimeText, 2)
            //3日目
            departureTime30Tag -> setDepartureTime(findViewById<View>(R.id.includeSC2Day3).findViewById(R.id.departureTime)
                , hourOfDay, minute, 3)
            departureMoveTime30Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day3).findViewById(R.id.departureMoveTime), moveTimeText, 3)
            requireTime31Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day3).findViewById(R.id.requireTime1), requireTimeText, 3)
            moveTime31Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day3).findViewById(R.id.moveTime1), moveTimeText, 3)
            requireTime32Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day3).findViewById(R.id.requireTime2), requireTimeText, 3)
            moveTime32Tag -> setRequireOrMoveTime(hourOfDay, minute, findViewById<View>(R.id.includeSC2Day3).findViewById(R.id.moveTime2)
                , moveTimeText, 3)
            requireTime33Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day3).findViewById(R.id.requireTime3), requireTimeText, 3)
            moveTime33Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day3).findViewById(R.id.moveTime3), moveTimeText, 3)
            requireTime34Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day3).findViewById(R.id.requireTime4), requireTimeText, 3)
            moveTime34Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day3).findViewById(R.id.moveTime4), moveTimeText, 3)
            requireTime35Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day3).findViewById(R.id.requireTime5), requireTimeText, 3)
            moveTime35Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day3).findViewById(R.id.moveTime5), moveTimeText, 3)
            requireTime36Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day3).findViewById(R.id.requireTime6), requireTimeText, 3)
            moveTime36Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day3).findViewById(R.id.moveTime6), moveTimeText, 3)
            //4日目
            departureTime40Tag -> setDepartureTime(findViewById<View>(R.id.includeSC2Day4).findViewById(R.id.departureTime)
                , hourOfDay, minute, 4)
            departureMoveTime40Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day4).findViewById(R.id.departureMoveTime), moveTimeText, 4)
            requireTime41Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day4).findViewById(R.id.requireTime1), requireTimeText, 4)
            moveTime41Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day4).findViewById(R.id.moveTime1), moveTimeText, 4)
            requireTime42Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day4).findViewById(R.id.requireTime2), requireTimeText, 4)
            moveTime42Tag -> setRequireOrMoveTime(hourOfDay, minute, findViewById<View>(R.id.includeSC2Day4).findViewById(R.id.moveTime2)
                , moveTimeText, 4)
            requireTime43Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day4).findViewById(R.id.requireTime3), requireTimeText, 4)
            moveTime43Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day4).findViewById(R.id.moveTime3), moveTimeText, 4)
            requireTime44Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day4).findViewById(R.id.requireTime4), requireTimeText, 4)
            moveTime44Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day4).findViewById(R.id.moveTime4), moveTimeText, 4)
            requireTime45Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day4).findViewById(R.id.requireTime5), requireTimeText, 4)
            moveTime45Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day4).findViewById(R.id.moveTime5), moveTimeText, 4)
            requireTime46Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day4).findViewById(R.id.requireTime6), requireTimeText, 4)
            moveTime46Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day4).findViewById(R.id.moveTime6), moveTimeText, 4)
            //5日目
            departureTime50Tag -> setDepartureTime(findViewById<View>(R.id.includeSC2Day5).findViewById(R.id.departureTime)
                , hourOfDay, minute, 5)
            departureMoveTime50Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day5).findViewById(R.id.departureMoveTime), moveTimeText, 5)
            requireTime51Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day5).findViewById(R.id.requireTime1), requireTimeText, 5)
            moveTime51Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day5).findViewById(R.id.moveTime1), moveTimeText, 5)
            requireTime52Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day5).findViewById(R.id.requireTime2), requireTimeText, 5)
            moveTime52Tag -> setRequireOrMoveTime(hourOfDay, minute, findViewById<View>(R.id.includeSC2Day5).findViewById(R.id.moveTime2)
                , moveTimeText, 5)
            requireTime53Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day5).findViewById(R.id.requireTime3), requireTimeText, 5)
            moveTime53Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day5).findViewById(R.id.moveTime3), moveTimeText, 5)
            requireTime54Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day5).findViewById(R.id.requireTime4), requireTimeText, 5)
            moveTime54Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day5).findViewById(R.id.moveTime4), moveTimeText, 5)
            requireTime55Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day5).findViewById(R.id.requireTime5), requireTimeText, 5)
            moveTime55Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day5).findViewById(R.id.moveTime5), moveTimeText, 5)
            requireTime56Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day5).findViewById(R.id.requireTime6), requireTimeText, 5)
            moveTime56Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day5).findViewById(R.id.moveTime6), moveTimeText, 5)
            //6日目
            departureTime60Tag -> setDepartureTime(findViewById<View>(R.id.includeSC2Day6).findViewById(R.id.departureTime)
                , hourOfDay, minute, 6)
            departureMoveTime60Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day6).findViewById(R.id.departureMoveTime), moveTimeText, 6)
            requireTime61Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day6).findViewById(R.id.requireTime1), requireTimeText, 6)
            moveTime61Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day6).findViewById(R.id.moveTime1), moveTimeText, 6)
            requireTime62Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day6).findViewById(R.id.requireTime2), requireTimeText, 6)
            moveTime62Tag -> setRequireOrMoveTime(hourOfDay, minute, findViewById<View>(R.id.includeSC2Day6).findViewById(R.id.moveTime2)
                , moveTimeText, 6)
            requireTime63Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day6).findViewById(R.id.requireTime3), requireTimeText, 6)
            moveTime63Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day6).findViewById(R.id.moveTime3), moveTimeText, 6)
            requireTime64Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day6).findViewById(R.id.requireTime4), requireTimeText, 6)
            moveTime64Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day6).findViewById(R.id.moveTime4), moveTimeText, 6)
            requireTime65Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day6).findViewById(R.id.requireTime5), requireTimeText, 6)
            moveTime65Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day6).findViewById(R.id.moveTime5), moveTimeText, 6)
            requireTime66Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day6).findViewById(R.id.requireTime6), requireTimeText, 6)
            moveTime66Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day6).findViewById(R.id.moveTime6), moveTimeText, 6)
            //7日目
            departureTime70Tag -> setDepartureTime(findViewById<View>(R.id.includeSC2Day7).findViewById(R.id.departureTime)
                , hourOfDay, minute, 7)
            departureMoveTime70Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day7).findViewById(R.id.departureMoveTime), moveTimeText, 7)
            requireTime71Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day7).findViewById(R.id.requireTime1), requireTimeText, 7)
            moveTime71Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day7).findViewById(R.id.moveTime1), moveTimeText, 7)
            requireTime72Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day7).findViewById(R.id.requireTime2), requireTimeText, 7)
            moveTime72Tag -> setRequireOrMoveTime(hourOfDay, minute, findViewById<View>(R.id.includeSC2Day7).findViewById(R.id.moveTime2)
                , moveTimeText, 7)
            requireTime73Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day7).findViewById(R.id.requireTime3), requireTimeText, 7)
            moveTime73Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day7).findViewById(R.id.moveTime3), moveTimeText, 7)
            requireTime74Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day7).findViewById(R.id.requireTime4), requireTimeText, 7)
            moveTime74Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day7).findViewById(R.id.moveTime4), moveTimeText, 7)
            requireTime75Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day7).findViewById(R.id.requireTime5), requireTimeText, 7)
            moveTime75Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day7).findViewById(R.id.moveTime5), moveTimeText, 7)
            requireTime76Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day7).findViewById(R.id.requireTime6), requireTimeText, 7)
            moveTime76Tag -> setRequireOrMoveTime(hourOfDay, minute
                , findViewById<View>(R.id.includeSC2Day7).findViewById(R.id.moveTime6), moveTimeText, 7)
        }
        when(temporalyDayFlag){
            1 -> if(findViewById<View>(R.id.includeSC2Day1).findViewById<TextView>(R.id.departureTime).text != "") refresh(R.id.includeSC2Day1)
            2 -> if(findViewById<View>(R.id.includeSC2Day2).findViewById<TextView>(R.id.departureTime).text != "") refresh(R.id.includeSC2Day2)
            3 -> if(findViewById<View>(R.id.includeSC2Day3).findViewById<TextView>(R.id.departureTime).text != "") refresh(R.id.includeSC2Day3)
            4 -> if(findViewById<View>(R.id.includeSC2Day4).findViewById<TextView>(R.id.departureTime).text != "") refresh(R.id.includeSC2Day4)
            5 -> if(findViewById<View>(R.id.includeSC2Day5).findViewById<TextView>(R.id.departureTime).text != "") refresh(R.id.includeSC2Day5)
            6 -> if(findViewById<View>(R.id.includeSC2Day6).findViewById<TextView>(R.id.departureTime).text != "") refresh(R.id.includeSC2Day6)
            7 -> if(findViewById<View>(R.id.includeSC2Day7).findViewById<TextView>(R.id.departureTime).text != "") refresh(R.id.includeSC2Day7)
        }
    }

    private fun setViewLink(travelDays: Long) {
        //(1日目)タップ処理
        findViewById<View>(R.id.includeSC2Day1).findViewById<TextView>(R.id.departureTime)
            .setOnClickListener { callDepartureTimePicker(departureTime10Tag) }
        findViewById<View>(R.id.includeSC2Day1).findViewById<TextView>(R.id.departureMoveTime)
            .setOnClickListener { callRequireTimePicker(departureMoveTime10Tag) }
        findViewById<View>(R.id.includeSC2Day1).findViewById<TextView>(R.id.requireTime1)
            .setOnClickListener { callRequireTimePicker(requireTime11Tag) }
        findViewById<View>(R.id.includeSC2Day1).findViewById<TextView>(R.id.moveTime1)
            .setOnClickListener { callRequireTimePicker(moveTime11Tag) }
        findViewById<View>(R.id.includeSC2Day1).findViewById<TextView>(R.id.requireTime2)
            .setOnClickListener { callRequireTimePicker(requireTime12Tag) }
        findViewById<View>(R.id.includeSC2Day1).findViewById<TextView>(R.id.moveTime2)
            .setOnClickListener { callRequireTimePicker(moveTime12Tag) }
        findViewById<View>(R.id.includeSC2Day1).findViewById<TextView>(R.id.requireTime3)
            .setOnClickListener { callRequireTimePicker(requireTime13Tag) }
        findViewById<View>(R.id.includeSC2Day1).findViewById<TextView>(R.id.moveTime3)
            .setOnClickListener { callRequireTimePicker(moveTime13Tag) }
        findViewById<View>(R.id.includeSC2Day1).findViewById<TextView>(R.id.requireTime4)
            .setOnClickListener { callRequireTimePicker(requireTime14Tag) }
        findViewById<View>(R.id.includeSC2Day1).findViewById<TextView>(R.id.moveTime4)
            .setOnClickListener { callRequireTimePicker(moveTime14Tag) }
        findViewById<View>(R.id.includeSC2Day1).findViewById<TextView>(R.id.requireTime5)
            .setOnClickListener { callRequireTimePicker(requireTime15Tag) }
        findViewById<View>(R.id.includeSC2Day1).findViewById<TextView>(R.id.moveTime5)
            .setOnClickListener { callRequireTimePicker(moveTime15Tag) }
        findViewById<View>(R.id.includeSC2Day1).findViewById<TextView>(R.id.requireTime6)
            .setOnClickListener { callRequireTimePicker(requireTime16Tag) }
        findViewById<View>(R.id.includeSC2Day1).findViewById<TextView>(R.id.moveTime6)
            .setOnClickListener { callRequireTimePicker(moveTime16Tag) }
        //(2日目)タップ処理
        if(travelDays > 1){
            findViewById<View>(R.id.includeSC2Day2).findViewById<TextView>(R.id.departureTime)
                .setOnClickListener { callDepartureTimePicker(departureTime20Tag) }
            findViewById<View>(R.id.includeSC2Day2).findViewById<TextView>(R.id.departureMoveTime)
                .setOnClickListener { callRequireTimePicker(departureMoveTime20Tag) }
            findViewById<View>(R.id.includeSC2Day2).findViewById<TextView>(R.id.requireTime1)
                .setOnClickListener { callRequireTimePicker(requireTime21Tag) }
            findViewById<View>(R.id.includeSC2Day2).findViewById<TextView>(R.id.moveTime1)
                .setOnClickListener { callRequireTimePicker(moveTime21Tag) }
            findViewById<View>(R.id.includeSC2Day2).findViewById<TextView>(R.id.requireTime2)
                .setOnClickListener { callRequireTimePicker(requireTime22Tag) }
            findViewById<View>(R.id.includeSC2Day2).findViewById<TextView>(R.id.moveTime2)
                .setOnClickListener { callRequireTimePicker(moveTime22Tag) }
            findViewById<View>(R.id.includeSC2Day2).findViewById<TextView>(R.id.requireTime3)
                .setOnClickListener { callRequireTimePicker(requireTime23Tag) }
            findViewById<View>(R.id.includeSC2Day2).findViewById<TextView>(R.id.moveTime3)
                .setOnClickListener { callRequireTimePicker(moveTime23Tag) }
            findViewById<View>(R.id.includeSC2Day2).findViewById<TextView>(R.id.requireTime4)
                .setOnClickListener { callRequireTimePicker(requireTime24Tag) }
            findViewById<View>(R.id.includeSC2Day2).findViewById<TextView>(R.id.moveTime4)
                .setOnClickListener { callRequireTimePicker(moveTime24Tag) }
            findViewById<View>(R.id.includeSC2Day2).findViewById<TextView>(R.id.requireTime5)
                .setOnClickListener { callRequireTimePicker(requireTime25Tag) }
            findViewById<View>(R.id.includeSC2Day2).findViewById<TextView>(R.id.moveTime5)
                .setOnClickListener { callRequireTimePicker(moveTime25Tag) }
            findViewById<View>(R.id.includeSC2Day2).findViewById<TextView>(R.id.requireTime6)
                .setOnClickListener { callRequireTimePicker(requireTime26Tag) }
            findViewById<View>(R.id.includeSC2Day2).findViewById<TextView>(R.id.moveTime6)
                .setOnClickListener { callRequireTimePicker(moveTime26Tag) }
        }
        //(3日目)タップ処理
        if(travelDays > 2){
            findViewById<View>(R.id.includeSC2Day3).findViewById<TextView>(R.id.departureTime)
                .setOnClickListener { callDepartureTimePicker(departureTime30Tag) }
            findViewById<View>(R.id.includeSC2Day3).findViewById<TextView>(R.id.departureMoveTime)
                .setOnClickListener { callRequireTimePicker(departureMoveTime30Tag) }
            findViewById<View>(R.id.includeSC2Day3).findViewById<TextView>(R.id.requireTime1)
                .setOnClickListener { callRequireTimePicker(requireTime31Tag) }
            findViewById<View>(R.id.includeSC2Day3).findViewById<TextView>(R.id.moveTime1)
                .setOnClickListener { callRequireTimePicker(moveTime31Tag) }
            findViewById<View>(R.id.includeSC2Day3).findViewById<TextView>(R.id.requireTime2)
                .setOnClickListener { callRequireTimePicker(requireTime32Tag) }
            findViewById<View>(R.id.includeSC2Day3).findViewById<TextView>(R.id.moveTime2)
                .setOnClickListener { callRequireTimePicker(moveTime32Tag) }
            findViewById<View>(R.id.includeSC2Day3).findViewById<TextView>(R.id.requireTime3)
                .setOnClickListener { callRequireTimePicker(requireTime33Tag) }
            findViewById<View>(R.id.includeSC2Day3).findViewById<TextView>(R.id.moveTime3)
                .setOnClickListener { callRequireTimePicker(moveTime33Tag) }
            findViewById<View>(R.id.includeSC2Day3).findViewById<TextView>(R.id.requireTime4)
                .setOnClickListener { callRequireTimePicker(requireTime34Tag) }
            findViewById<View>(R.id.includeSC2Day3).findViewById<TextView>(R.id.moveTime4)
                .setOnClickListener { callRequireTimePicker(moveTime34Tag) }
            findViewById<View>(R.id.includeSC2Day3).findViewById<TextView>(R.id.requireTime5)
                .setOnClickListener { callRequireTimePicker(requireTime35Tag) }
            findViewById<View>(R.id.includeSC2Day3).findViewById<TextView>(R.id.moveTime5)
                .setOnClickListener { callRequireTimePicker(moveTime35Tag) }
            findViewById<View>(R.id.includeSC2Day3).findViewById<TextView>(R.id.requireTime6)
                .setOnClickListener { callRequireTimePicker(requireTime36Tag) }
            findViewById<View>(R.id.includeSC2Day3).findViewById<TextView>(R.id.moveTime6)
                .setOnClickListener { callRequireTimePicker(moveTime36Tag) }
        }
        //(4日目)タップ処理
        if(travelDays > 3){
            findViewById<View>(R.id.includeSC2Day4).findViewById<TextView>(R.id.departureTime)
                .setOnClickListener { callDepartureTimePicker(departureTime40Tag) }
            findViewById<View>(R.id.includeSC2Day4).findViewById<TextView>(R.id.departureMoveTime)
                .setOnClickListener { callRequireTimePicker(departureMoveTime40Tag) }
            findViewById<View>(R.id.includeSC2Day4).findViewById<TextView>(R.id.requireTime1)
                .setOnClickListener { callRequireTimePicker(requireTime41Tag) }
            findViewById<View>(R.id.includeSC2Day4).findViewById<TextView>(R.id.moveTime1)
                .setOnClickListener { callRequireTimePicker(moveTime41Tag) }
            findViewById<View>(R.id.includeSC2Day4).findViewById<TextView>(R.id.requireTime2)
                .setOnClickListener { callRequireTimePicker(requireTime42Tag) }
            findViewById<View>(R.id.includeSC2Day4).findViewById<TextView>(R.id.moveTime2)
                .setOnClickListener { callRequireTimePicker(moveTime42Tag) }
            findViewById<View>(R.id.includeSC2Day4).findViewById<TextView>(R.id.requireTime3)
                .setOnClickListener { callRequireTimePicker(requireTime43Tag) }
            findViewById<View>(R.id.includeSC2Day4).findViewById<TextView>(R.id.moveTime3)
                .setOnClickListener { callRequireTimePicker(moveTime43Tag) }
            findViewById<View>(R.id.includeSC2Day4).findViewById<TextView>(R.id.requireTime4)
                .setOnClickListener { callRequireTimePicker(requireTime44Tag) }
            findViewById<View>(R.id.includeSC2Day4).findViewById<TextView>(R.id.moveTime4)
                .setOnClickListener { callRequireTimePicker(moveTime44Tag) }
            findViewById<View>(R.id.includeSC2Day4).findViewById<TextView>(R.id.requireTime5)
                .setOnClickListener { callRequireTimePicker(requireTime45Tag) }
            findViewById<View>(R.id.includeSC2Day4).findViewById<TextView>(R.id.moveTime5)
                .setOnClickListener { callRequireTimePicker(moveTime45Tag) }
            findViewById<View>(R.id.includeSC2Day4).findViewById<TextView>(R.id.requireTime6)
                .setOnClickListener { callRequireTimePicker(requireTime46Tag) }
            findViewById<View>(R.id.includeSC2Day4).findViewById<TextView>(R.id.moveTime6)
                .setOnClickListener { callRequireTimePicker(moveTime46Tag) }
        }
        //(5日目)タップ処理
        if(travelDays > 4){
            findViewById<View>(R.id.includeSC2Day5).findViewById<TextView>(R.id.departureTime)
                .setOnClickListener { callDepartureTimePicker(departureTime50Tag) }
            findViewById<View>(R.id.includeSC2Day5).findViewById<TextView>(R.id.departureMoveTime)
                .setOnClickListener { callRequireTimePicker(departureMoveTime50Tag) }
            findViewById<View>(R.id.includeSC2Day5).findViewById<TextView>(R.id.requireTime1)
                .setOnClickListener { callRequireTimePicker(requireTime51Tag) }
            findViewById<View>(R.id.includeSC2Day5).findViewById<TextView>(R.id.moveTime1)
                .setOnClickListener { callRequireTimePicker(moveTime51Tag) }
            findViewById<View>(R.id.includeSC2Day5).findViewById<TextView>(R.id.requireTime2)
                .setOnClickListener { callRequireTimePicker(requireTime52Tag) }
            findViewById<View>(R.id.includeSC2Day5).findViewById<TextView>(R.id.moveTime2)
                .setOnClickListener { callRequireTimePicker(moveTime52Tag) }
            findViewById<View>(R.id.includeSC2Day5).findViewById<TextView>(R.id.requireTime3)
                .setOnClickListener { callRequireTimePicker(requireTime53Tag) }
            findViewById<View>(R.id.includeSC2Day5).findViewById<TextView>(R.id.moveTime3)
                .setOnClickListener { callRequireTimePicker(moveTime53Tag) }
            findViewById<View>(R.id.includeSC2Day5).findViewById<TextView>(R.id.requireTime4)
                .setOnClickListener { callRequireTimePicker(requireTime54Tag) }
            findViewById<View>(R.id.includeSC2Day5).findViewById<TextView>(R.id.moveTime4)
                .setOnClickListener { callRequireTimePicker(moveTime54Tag) }
            findViewById<View>(R.id.includeSC2Day5).findViewById<TextView>(R.id.requireTime5)
                .setOnClickListener { callRequireTimePicker(requireTime55Tag) }
            findViewById<View>(R.id.includeSC2Day5).findViewById<TextView>(R.id.moveTime5)
                .setOnClickListener { callRequireTimePicker(moveTime55Tag) }
            findViewById<View>(R.id.includeSC2Day5).findViewById<TextView>(R.id.requireTime6)
                .setOnClickListener { callRequireTimePicker(requireTime56Tag) }
            findViewById<View>(R.id.includeSC2Day5).findViewById<TextView>(R.id.moveTime6)
                .setOnClickListener { callRequireTimePicker(moveTime56Tag) }
        }
        //(6日目)タップ処理
        if(travelDays > 5){
            findViewById<View>(R.id.includeSC2Day6).findViewById<TextView>(R.id.departureTime)
                .setOnClickListener { callDepartureTimePicker(departureTime60Tag) }
            findViewById<View>(R.id.includeSC2Day6).findViewById<TextView>(R.id.departureMoveTime)
                .setOnClickListener { callRequireTimePicker(departureMoveTime60Tag) }
            findViewById<View>(R.id.includeSC2Day6).findViewById<TextView>(R.id.requireTime1)
                .setOnClickListener { callRequireTimePicker(requireTime61Tag) }
            findViewById<View>(R.id.includeSC2Day6).findViewById<TextView>(R.id.moveTime1)
                .setOnClickListener { callRequireTimePicker(moveTime61Tag) }
            findViewById<View>(R.id.includeSC2Day6).findViewById<TextView>(R.id.requireTime2)
                .setOnClickListener { callRequireTimePicker(requireTime62Tag) }
            findViewById<View>(R.id.includeSC2Day6).findViewById<TextView>(R.id.moveTime2)
                .setOnClickListener { callRequireTimePicker(moveTime62Tag) }
            findViewById<View>(R.id.includeSC2Day6).findViewById<TextView>(R.id.requireTime3)
                .setOnClickListener { callRequireTimePicker(requireTime63Tag) }
            findViewById<View>(R.id.includeSC2Day6).findViewById<TextView>(R.id.moveTime3)
                .setOnClickListener { callRequireTimePicker(moveTime63Tag) }
            findViewById<View>(R.id.includeSC2Day6).findViewById<TextView>(R.id.requireTime4)
                .setOnClickListener { callRequireTimePicker(requireTime64Tag) }
            findViewById<View>(R.id.includeSC2Day6).findViewById<TextView>(R.id.moveTime4)
                .setOnClickListener { callRequireTimePicker(moveTime64Tag) }
            findViewById<View>(R.id.includeSC2Day6).findViewById<TextView>(R.id.requireTime5)
                .setOnClickListener { callRequireTimePicker(requireTime65Tag) }
            findViewById<View>(R.id.includeSC2Day6).findViewById<TextView>(R.id.moveTime5)
                .setOnClickListener { callRequireTimePicker(moveTime65Tag) }
            findViewById<View>(R.id.includeSC2Day6).findViewById<TextView>(R.id.requireTime6)
                .setOnClickListener { callRequireTimePicker(requireTime66Tag) }
            findViewById<View>(R.id.includeSC2Day6).findViewById<TextView>(R.id.moveTime6)
                .setOnClickListener { callRequireTimePicker(moveTime66Tag) }
        }
        //(7日目)タップ処理
        if(travelDays > 6) {
            findViewById<View>(R.id.includeSC2Day7).findViewById<TextView>(R.id.departureTime)
                .setOnClickListener { callDepartureTimePicker(departureTime70Tag) }
            findViewById<View>(R.id.includeSC2Day7).findViewById<TextView>(R.id.departureMoveTime)
                .setOnClickListener { callRequireTimePicker(departureMoveTime70Tag) }
            findViewById<View>(R.id.includeSC2Day7).findViewById<TextView>(R.id.requireTime1)
                .setOnClickListener { callRequireTimePicker(requireTime71Tag) }
            findViewById<View>(R.id.includeSC2Day7).findViewById<TextView>(R.id.moveTime1)
                .setOnClickListener { callRequireTimePicker(moveTime71Tag) }
            findViewById<View>(R.id.includeSC2Day7).findViewById<TextView>(R.id.requireTime2)
                .setOnClickListener { callRequireTimePicker(requireTime72Tag) }
            findViewById<View>(R.id.includeSC2Day7).findViewById<TextView>(R.id.moveTime2)
                .setOnClickListener { callRequireTimePicker(moveTime72Tag) }
            findViewById<View>(R.id.includeSC2Day7).findViewById<TextView>(R.id.requireTime3)
                .setOnClickListener { callRequireTimePicker(requireTime73Tag) }
            findViewById<View>(R.id.includeSC2Day7).findViewById<TextView>(R.id.moveTime3)
                .setOnClickListener { callRequireTimePicker(moveTime73Tag) }
            findViewById<View>(R.id.includeSC2Day7).findViewById<TextView>(R.id.requireTime4)
                .setOnClickListener { callRequireTimePicker(requireTime74Tag) }
            findViewById<View>(R.id.includeSC2Day7).findViewById<TextView>(R.id.moveTime4)
                .setOnClickListener { callRequireTimePicker(moveTime74Tag) }
            findViewById<View>(R.id.includeSC2Day7).findViewById<TextView>(R.id.requireTime5)
                .setOnClickListener { callRequireTimePicker(requireTime75Tag) }
            findViewById<View>(R.id.includeSC2Day7).findViewById<TextView>(R.id.moveTime5)
                .setOnClickListener { callRequireTimePicker(moveTime75Tag) }
            findViewById<View>(R.id.includeSC2Day7).findViewById<TextView>(R.id.requireTime6)
                .setOnClickListener { callRequireTimePicker(requireTime76Tag) }
            findViewById<View>(R.id.includeSC2Day7).findViewById<TextView>(R.id.moveTime6)
                .setOnClickListener { callRequireTimePicker(moveTime76Tag) }
        }
    }
}