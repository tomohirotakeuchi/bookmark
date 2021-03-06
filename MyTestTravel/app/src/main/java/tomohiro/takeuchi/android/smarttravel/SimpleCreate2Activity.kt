package tomohiro.takeuchi.android.smarttravel

import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_simple_create2.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.yesButton
import tomohiro.takeuchi.android.smarttravel.fragment.RequireTimePickerFragment
import tomohiro.takeuchi.android.smarttravel.fragment.TimePickerFragment
import tomohiro.takeuchi.android.smarttravel.model.Travel
import tomohiro.takeuchi.android.smarttravel.model.TravelDetail
import tomohiro.takeuchi.android.smarttravel.model.TravelPart
import java.lang.StringBuilder

class SimpleCreate2Activity : AppCompatActivity()
    , TimePickerFragment.OnTimeSelectedListener
    , RequireTimePickerFragment.OnTimeSelectedListener {

    private lateinit var realm: Realm
    private var manageId: Int = 0
    private lateinit var includeDay1: View
    private lateinit var includeDay2: View
    private lateinit var includeDay3: View
    private lateinit var includeDay4: View
    private lateinit var includeDay5: View
    private lateinit var includeDay6: View
    private lateinit var includeDay7: View
    private lateinit var temporalyTag: String
    private var temporalyDayFlag = 0
    private var travelDays = 1L

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
        includeDay1 = findViewById(R.id.includeSC2Day1)
        includeDay2 = findViewById(R.id.includeSC2Day2)
        includeDay3 = findViewById(R.id.includeSC2Day3)
        includeDay4 = findViewById(R.id.includeSC2Day4)
        includeDay5 = findViewById(R.id.includeSC2Day5)
        includeDay6 = findViewById(R.id.includeSC2Day6)
        includeDay7 = findViewById(R.id.includeSC2Day7)
        manageId = intent.getIntExtra("MANAGE_ID", 0)
        travelDays = intent.getLongExtra("TRAVEL_DAYS", 1)
        //一日目の出発地設定
        includeDay1.findViewById<TextView>(R.id.sc2departurePlace).text =
            intent.getStringExtra("DEPARTURE_PLACE")

        visibleInclude(travelDays)

        val travelParts = realm.where<TravelPart>().equalTo("manageId", manageId).findAllAsync()

        for(travelPart in travelParts){
            //何日目かチェック。includeするレイアウトを決める。
            when(travelPart?.day){
                1 -> checkDestination(includeDay1, includeDay1, travelPart)
                2 -> checkDestination(includeDay2, includeDay1, travelPart)
                3 -> checkDestination(includeDay3, includeDay2, travelPart)
                4 -> checkDestination(includeDay4, includeDay3, travelPart)
                5 -> checkDestination(includeDay5, includeDay4, travelPart)
                6 -> checkDestination(includeDay6, includeDay5, travelPart)
                7 -> checkDestination(includeDay7, includeDay6, travelPart)
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
                            1L -> devideDestination(1, includeDay1)
                            2L -> devideDestination(2, includeDay2)
                            3L -> devideDestination(3, includeDay3)
                            4L -> devideDestination(4, includeDay4)
                            5L -> devideDestination(5, includeDay5)
                            6L -> devideDestination(6, includeDay6)
                            7L -> devideDestination(7, includeDay7)
                        }
                    }
                    deleteFlagFalse()
                }
                startActivity<CreateCompleteActivity>()
            }else{
                alert (resources.getString(R.string.enterAllTimes)){ yesButton {  } }.show()
            }
        }
    }

    //入力チェック。日にちチェック
    private fun checkEnterDay(day: Long): Boolean {
        when(day){
            1L -> return checkEnter(includeDay1)
            2L -> return checkEnter(includeDay2)
            3L -> return checkEnter(includeDay3)
            4L -> return checkEnter(includeDay4)
            5L -> return checkEnter(includeDay5)
            6L -> return checkEnter(includeDay6)
            7L -> return checkEnter(includeDay7)
        }
        return false
    }

    //入力チェック。visibilityを使ってテキストが空かどうか判定する。
    private fun checkEnter(include: View): Boolean {
        var flag = true
        if(include.findViewById<View>(R.id.sc2departureLayout).visibility == View.VISIBLE){
            flag = include.findViewById<TextView>(R.id.sc2departureTime).text.isNotEmpty()
            if (flag) flag = include.findViewById<TextView>(R.id.sc2departureMoveTime).text.isNotEmpty()
        }
        if(include.findViewById<View>(R.id.sc2destination1Layout).visibility == View.VISIBLE){
            if (flag) flag = include.findViewById<TextView>(R.id.sc2requireTime1).text.isNotEmpty()
            if (flag) flag = include.findViewById<TextView>(R.id.sc2moveTime1).text.isNotEmpty()
        }
        if(include.findViewById<View>(R.id.sc2destination2Layout).visibility == View.VISIBLE){
            if (flag) flag = include.findViewById<TextView>(R.id.sc2requireTime2).text.isNotEmpty()
            if (flag) flag = include.findViewById<TextView>(R.id.sc2moveTime2).text.isNotEmpty()
        }
        if(include.findViewById<View>(R.id.sc2destination3Layout).visibility == View.VISIBLE){
            if (flag) flag = include.findViewById<TextView>(R.id.sc2requireTime3).text.isNotEmpty()
            if (flag) flag = include.findViewById<TextView>(R.id.sc2moveTime3).text.isNotEmpty()
        }
        if(include.findViewById<View>(R.id.sc2destination4Layout).visibility == View.VISIBLE){
            if (flag) flag = include.findViewById<TextView>(R.id.sc2requireTime4).text.isNotEmpty()
            if (flag) flag = include.findViewById<TextView>(R.id.sc2moveTime4).text.isNotEmpty()
        }
        if(include.findViewById<View>(R.id.sc2destination5Layout).visibility == View.VISIBLE){
            if (flag) flag = include.findViewById<TextView>(R.id.sc2requireTime5).text.isNotEmpty()
            if (flag) flag = include.findViewById<TextView>(R.id.sc2moveTime5).text.isNotEmpty()
        }
        if(include.findViewById<View>(R.id.sc2destination6Layout).visibility == View.VISIBLE){
            if (flag) flag = include.findViewById<TextView>(R.id.sc2requireTime6).text.isNotEmpty()
            if (flag) flag = include.findViewById<TextView>(R.id.sc2moveTime6).text.isNotEmpty()
        }
        return flag
    }

    //目的地別に分類
    private fun devideDestination(day: Int, include: View) {
        createDepartureTravelDetail(day, include)
        val lastDayFlag = (day.toLong() == travelDays)

        if(include.findViewById<TextView>(R.id.sc2destination6).text.isNotEmpty()){
            createTravelDetail(6, day, include, R.id.sc2destination6, R.id.sc2startTime6, R.id.sc2requireTime6 ,R.id.sc2moveTime6)
        }
        if(include.findViewById<TextView>(R.id.sc2destination5).text.isNotEmpty()){
            createTravelDetail(5, day, include, R.id.sc2destination5, R.id.sc2startTime5, R.id.sc2requireTime5 ,R.id.sc2moveTime5)
        }
        if(include.findViewById<TextView>(R.id.sc2destination4).text.isNotEmpty()){
            createTravelDetail(4, day, include, R.id.sc2destination4, R.id.sc2startTime4, R.id.sc2requireTime4 ,R.id.sc2moveTime4)
        }
        if(include.findViewById<TextView>(R.id.sc2destination3).text.isNotEmpty()){
            createTravelDetail(3, day, include, R.id.sc2destination3, R.id.sc2startTime3, R.id.sc2requireTime3 ,R.id.sc2moveTime3)
        }
        if(include.findViewById<TextView>(R.id.sc2destination2).text.isNotEmpty()){
            createTravelDetail(2, day, include, R.id.sc2destination2, R.id.sc2startTime2, R.id.sc2requireTime2 ,R.id.sc2moveTime2)
        }
        if(include.findViewById<TextView>(R.id.sc2destination1).text.isNotEmpty()){
            createTravelDetail(1, day, include, R.id.sc2destination1, R.id.sc2startTime1, R.id.sc2requireTime1 ,R.id.sc2moveTime1)
        }

        //最終日なら到着地を最終目的地（order=9)として新しくDetail作成。
        if(lastDayFlag){
            val maxDetailId = realm.where<TravelDetail>().max("id")
            val nextDetailId = (maxDetailId?.toInt() ?: 0) + 1
            val travelDetail = realm.createObject<TravelDetail>(nextDetailId)

            travelDetail.manageId = manageId
            travelDetail.day = day
            travelDetail.order = 9
            travelDetail.destination = include.findViewById<TextView>(R.id.sc2ArrivalPlace).text.toString()
            travelDetail.startTime = include.findViewById<TextView>(R.id.sc2ArrivalTime).text.toString()

            val travel = realm.where<Travel>()
                .equalTo("manageId", manageId)
                .findFirst()
            if (travel is Travel){
                travelDetail.latitude = travel.arrivalLatitude
                travelDetail.longitude = travel.arrivalLongitude
            }
        }
    }

    //出発のTravelDetail作成
    private fun createDepartureTravelDetail(day: Int, include: View) {
        val maxDetailId = realm.where<TravelDetail>().max("id")
        val nextDetailId = (maxDetailId?.toInt() ?: 0) + 1
        val travelDetail = realm.createObject<TravelDetail>(nextDetailId)

        travelDetail.manageId = manageId
        travelDetail.day = day
        travelDetail.order = 0
        travelDetail.destination = include.findViewById<TextView>(R.id.sc2departurePlace).text.toString()
        travelDetail.startTime = include.findViewById<TextView>(R.id.sc2departureTime).text.toString()
        travelDetail.moveTime = include.findViewById<TextView>(R.id.sc2departureMoveTime).text.toString()
        if (day == 1){
            val travel = realm.where<Travel>()
                .equalTo("manageId", manageId)
                .findFirst()
            if (travel is Travel){
                travelDetail.latitude = travel.departureLatitude
                travelDetail.longitude = travel.departureLongitude
            }
        }
    }

    //TravelDetailデータベースに値を格納
    private fun createTravelDetail(order: Int, day:Int, include: View, destinationId: Int
                                   , startId: Int, requireId: Int, moveId: Int){
        val maxDetailId = realm.where<TravelDetail>().max("id")
        val nextDetailId = (maxDetailId?.toInt() ?: 0) + 1
        val travelDetail = realm.createObject<TravelDetail>(nextDetailId)
        travelDetail.manageId = manageId
        travelDetail.day = day
        travelDetail.order = order
        //最終目的地でないならばそのViewの目的地、最終目的地なら到着地を格納。
        travelDetail.destination = include.findViewById<TextView>(destinationId).text.toString()
        travelDetail.startTime = include.findViewById<TextView>(startId).text.toString()
        travelDetail.requiredTime = include.findViewById<TextView>(requireId).text.toString()
        if(travelDetail.requiredTime.isEmpty()) travelDetail.requiredTime = resources.getString(R.string.requireTimeText) + "0 min "
        travelDetail.moveTime = include.findViewById<TextView>(moveId).text.toString()
        if(travelDetail.moveTime.isEmpty()) travelDetail.moveTime = resources.getString(R.string.moveTimeText) + "0 min "
    }

    //目的地が空でなければ値をViewにセットする。
    //何日目かを元に出発地と到着地の設定も行う。
    private fun checkDestination(include: View, preInclude: View, travelPart: TravelPart) {
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
            include.findViewById<TextView>(R.id.sc2ArrivalPlace).text =
                intent.getStringExtra("ARRIVAL_PLACE")
        }
        if(travelPart.day > 1) {
            include.findViewById<TextView>(R.id.sc2departurePlace).text =
                preInclude.findViewById<TextView>(R.id.sc2ArrivalPlace).text
        }
    }

    //レイアウトのvisibilityをVisibleに、目的地をテキストに表示
    private fun setProperty(destinationName: String, layoutId: Int, dViewId: Int, include: View, lastDestinationFlag: Boolean, lastDayFlag: Boolean) {
        include.findViewById<TextView>(dViewId).text = destinationName
        if(lastDestinationFlag && !lastDayFlag){
            //最終目的地かつ最終日ではないとき、その日の到着地を最終目的地に入れる。
            include.findViewById<TextView>(R.id.sc2ArrivalPlace).text = destinationName
        }else{
            //上記以外の場合、Visibleにする。
            include.findViewById<View>(layoutId).visibility = View.VISIBLE
        }
    }

    //includeの表示非表示
    private fun visibleInclude(travelDays: Long) {
        if(travelDays > 1L) includeDay2.visibility = View.VISIBLE
        if(travelDays > 2L) includeDay3.visibility = View.VISIBLE
        if(travelDays > 3L) includeDay4.visibility = View.VISIBLE
        if(travelDays > 4L) includeDay5.visibility = View.VISIBLE
        if(travelDays > 5L) includeDay6.visibility = View.VISIBLE
        if(travelDays > 6L) includeDay7.visibility = View.VISIBLE
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
    private fun refresh(include: View) {
        val total00 = timeFirstRefresh(include.findViewById(R.id.sc2departureTime))
        val total11 = timeRefresh(total00, null, include.findViewById(R.id.sc2departureMoveTime)
            , include.findViewById(R.id.sc2startTime1))
        val total12 = timeRefresh(total11, include.findViewById(R.id.sc2requireTime1)
            , include.findViewById(R.id.sc2moveTime1)
            , include.findViewById(R.id.sc2startTime2))
        val total13 = timeRefresh(total12, include.findViewById(R.id.sc2requireTime2)
            , include.findViewById(R.id.sc2moveTime2)
            , include.findViewById(R.id.sc2startTime3))
        val total14 = timeRefresh(total13, include.findViewById(R.id.sc2requireTime3)
            , include.findViewById(R.id.sc2moveTime3)
            , include.findViewById(R.id.sc2startTime4))
        val total15 = timeRefresh(total14, include.findViewById(R.id.sc2requireTime4)
            , include.findViewById(R.id.sc2moveTime4)
            , include.findViewById(R.id.sc2startTime5))
        val total16 = timeRefresh(total15, include.findViewById(R.id.sc2requireTime5)
            , include.findViewById(R.id.sc2moveTime5)
            , include.findViewById(R.id.sc2startTime6))
        timeRefresh(total16, include.findViewById(R.id.sc2requireTime6)
            , include.findViewById(R.id.sc2moveTime6)
            , include.findViewById(R.id.sc2ArrivalTime))
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
        if (requireTime is TextView && requireTime.text.isNotEmpty()) total += extractData(requireTime, resources.getString(R.string.requireTimeText).length)
        if (moveTime.text.isNotEmpty()) total += extractData(moveTime, resources.getString(R.string.moveTimeText).length)
        val timeFormat = "%1$02d:%2$02d"
        startTime.text = timeFormat.format(total / 60, total % 60)
        return total
    }

    //移動時間と所要時間データを抽出して加算したものを返却（1時間以上未満で場合分け）
    private fun extractData(textView: TextView, deleteNum: Int): Long {
        val extractData = StringBuilder(textView.text).delete(textView.text.length-5, textView.text.length-1).delete(0, deleteNum-1)
        val rArr = extractData.split("h")
        return if(rArr.size == 2) {
            Log.i("【SimpleCreate2Activity】"
                , "[extractData] rArr.size == 2 rArr[0]=${rArr[0]} rArr[1]=${rArr[1]}")
            rArr[0].trim().toLong() * 60 + rArr[1].trim().toLong()
        }else {
            Log.i("【SimpleCreate2Activity】"
                , "[extractData] rArr.size == 1 rArr[0]=${rArr[0]}")
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
        updateTravel?.departureTime =  includeDay1.findViewById<TextView>(R.id.sc2departureTime).text.toString()
        updateTravel?.arrivalTime = when(travelDays){
            1L -> includeDay1.findViewById<TextView>(R.id.sc2ArrivalTime).text.toString()
            2L -> includeDay2.findViewById<TextView>(R.id.sc2ArrivalTime).text.toString()
            3L -> includeDay3.findViewById<TextView>(R.id.sc2ArrivalTime).text.toString()
            4L -> includeDay4.findViewById<TextView>(R.id.sc2ArrivalTime).text.toString()
            5L -> includeDay5.findViewById<TextView>(R.id.sc2ArrivalTime).text.toString()
            6L -> includeDay6.findViewById<TextView>(R.id.sc2ArrivalTime).text.toString()
            7L -> includeDay7.findViewById<TextView>(R.id.sc2ArrivalTime).text.toString()
            else -> includeDay1.findViewById<TextView>(R.id.sc2ArrivalTime).text.toString()
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
        includeDay1.findViewById<TextView>(R.id.sc2DayText).text = resources.getString(R.string.day1)
        includeDay2.findViewById<TextView>(R.id.sc2DayText).text = resources.getString(R.string.day2)
        includeDay3.findViewById<TextView>(R.id.sc2DayText).text = resources.getString(R.string.day3)
        includeDay4.findViewById<TextView>(R.id.sc2DayText).text = resources.getString(R.string.day4)
        includeDay5.findViewById<TextView>(R.id.sc2DayText).text = resources.getString(R.string.day5)
        includeDay6.findViewById<TextView>(R.id.sc2DayText).text = resources.getString(R.string.day6)
        includeDay7.findViewById<TextView>(R.id.sc2DayText).text = resources.getString(R.string.day7)
    }

    //時刻ダイアログ選択後に呼ばれるメソッド
    override fun onSelected(hourOfDay: Int, minute: Int) {
        when(temporalyTag) {
            //1日目
            departureTime10Tag -> setDepartureTime(includeDay1.findViewById(R.id.sc2departureTime)
                , hourOfDay, minute, 1)
            departureMoveTime10Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay1.findViewById(R.id.sc2departureMoveTime), resources.getString(R.string.moveTimeText), 1)
            requireTime11Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay1.findViewById(R.id.sc2requireTime1), resources.getString(R.string.requireTimeText), 1)
            moveTime11Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay1.findViewById(R.id.sc2moveTime1), resources.getString(R.string.moveTimeText), 1)
            requireTime12Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay1.findViewById(R.id.sc2requireTime2), resources.getString(R.string.requireTimeText), 1)
            moveTime12Tag -> setRequireOrMoveTime(hourOfDay, minute, includeDay1.findViewById(R.id.sc2moveTime2)
                , resources.getString(R.string.moveTimeText), 1)
            requireTime13Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay1.findViewById(R.id.sc2requireTime3), resources.getString(R.string.requireTimeText), 1)
            moveTime13Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay1.findViewById(R.id.sc2moveTime3), resources.getString(R.string.moveTimeText), 1)
            requireTime14Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay1.findViewById(R.id.sc2requireTime4), resources.getString(R.string.requireTimeText), 1)
            moveTime14Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay1.findViewById(R.id.sc2moveTime4), resources.getString(R.string.moveTimeText), 1)
            requireTime15Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay1.findViewById(R.id.sc2requireTime5), resources.getString(R.string.requireTimeText), 1)
            moveTime15Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay1.findViewById(R.id.sc2moveTime5), resources.getString(R.string.moveTimeText), 1)
            requireTime16Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay1.findViewById(R.id.sc2requireTime6), resources.getString(R.string.requireTimeText), 1)
            moveTime16Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay1.findViewById(R.id.sc2moveTime6), resources.getString(R.string.moveTimeText), 1)
            //2日目
            departureTime20Tag -> setDepartureTime(includeDay2.findViewById(R.id.sc2departureTime)
                , hourOfDay, minute, 2)
            departureMoveTime20Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay2.findViewById(R.id.sc2departureMoveTime), resources.getString(R.string.moveTimeText), 2)
            requireTime21Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay2.findViewById(R.id.sc2requireTime1), resources.getString(R.string.requireTimeText), 2)
            moveTime21Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay2.findViewById(R.id.sc2moveTime1), resources.getString(R.string.moveTimeText), 2)
            requireTime22Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay2.findViewById(R.id.sc2requireTime2), resources.getString(R.string.requireTimeText), 2)
            moveTime22Tag -> setRequireOrMoveTime(hourOfDay, minute, includeDay2.findViewById(R.id.sc2moveTime2)
                , resources.getString(R.string.moveTimeText), 2)
            requireTime23Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay2.findViewById(R.id.sc2requireTime3), resources.getString(R.string.requireTimeText), 2)
            moveTime23Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay2.findViewById(R.id.sc2moveTime3), resources.getString(R.string.moveTimeText), 2)
            requireTime24Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay2.findViewById(R.id.sc2requireTime4), resources.getString(R.string.requireTimeText), 2)
            moveTime24Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay2.findViewById(R.id.sc2moveTime4), resources.getString(R.string.moveTimeText), 2)
            requireTime25Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay2.findViewById(R.id.sc2requireTime5), resources.getString(R.string.requireTimeText), 2)
            moveTime25Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay2.findViewById(R.id.sc2moveTime5), resources.getString(R.string.moveTimeText), 2)
            requireTime26Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay2.findViewById(R.id.sc2requireTime6), resources.getString(R.string.requireTimeText), 2)
            moveTime26Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay2.findViewById(R.id.sc2moveTime6), resources.getString(R.string.moveTimeText), 2)
            //3日目
            departureTime30Tag -> setDepartureTime(includeDay3.findViewById(R.id.sc2departureTime)
                , hourOfDay, minute, 3)
            departureMoveTime30Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay3.findViewById(R.id.sc2departureMoveTime), resources.getString(R.string.moveTimeText), 3)
            requireTime31Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay3.findViewById(R.id.sc2requireTime1), resources.getString(R.string.requireTimeText), 3)
            moveTime31Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay3.findViewById(R.id.sc2moveTime1), resources.getString(R.string.moveTimeText), 3)
            requireTime32Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay3.findViewById(R.id.sc2requireTime2), resources.getString(R.string.requireTimeText), 3)
            moveTime32Tag -> setRequireOrMoveTime(hourOfDay, minute, includeDay3.findViewById(R.id.sc2moveTime2)
                , resources.getString(R.string.moveTimeText), 3)
            requireTime33Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay3.findViewById(R.id.sc2requireTime3), resources.getString(R.string.requireTimeText), 3)
            moveTime33Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay3.findViewById(R.id.sc2moveTime3), resources.getString(R.string.moveTimeText), 3)
            requireTime34Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay3.findViewById(R.id.sc2requireTime4), resources.getString(R.string.requireTimeText), 3)
            moveTime34Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay3.findViewById(R.id.sc2moveTime4), resources.getString(R.string.moveTimeText), 3)
            requireTime35Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay3.findViewById(R.id.sc2requireTime5), resources.getString(R.string.requireTimeText), 3)
            moveTime35Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay3.findViewById(R.id.sc2moveTime5), resources.getString(R.string.moveTimeText), 3)
            requireTime36Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay3.findViewById(R.id.sc2requireTime6), resources.getString(R.string.requireTimeText), 3)
            moveTime36Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay3.findViewById(R.id.sc2moveTime6), resources.getString(R.string.moveTimeText), 3)
            //4日目
            departureTime40Tag -> setDepartureTime(includeDay4.findViewById(R.id.sc2departureTime)
                , hourOfDay, minute, 4)
            departureMoveTime40Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay4.findViewById(R.id.sc2departureMoveTime), resources.getString(R.string.moveTimeText), 4)
            requireTime41Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay4.findViewById(R.id.sc2requireTime1), resources.getString(R.string.requireTimeText), 4)
            moveTime41Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay4.findViewById(R.id.sc2moveTime1), resources.getString(R.string.moveTimeText), 4)
            requireTime42Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay4.findViewById(R.id.sc2requireTime2), resources.getString(R.string.requireTimeText), 4)
            moveTime42Tag -> setRequireOrMoveTime(hourOfDay, minute, includeDay4.findViewById(R.id.sc2moveTime2)
                , resources.getString(R.string.moveTimeText), 4)
            requireTime43Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay4.findViewById(R.id.sc2requireTime3), resources.getString(R.string.requireTimeText), 4)
            moveTime43Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay4.findViewById(R.id.sc2moveTime3), resources.getString(R.string.moveTimeText), 4)
            requireTime44Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay4.findViewById(R.id.sc2requireTime4), resources.getString(R.string.requireTimeText), 4)
            moveTime44Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay4.findViewById(R.id.sc2moveTime4), resources.getString(R.string.moveTimeText), 4)
            requireTime45Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay4.findViewById(R.id.sc2requireTime5), resources.getString(R.string.requireTimeText), 4)
            moveTime45Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay4.findViewById(R.id.sc2moveTime5), resources.getString(R.string.moveTimeText), 4)
            requireTime46Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay4.findViewById(R.id.sc2requireTime6), resources.getString(R.string.requireTimeText), 4)
            moveTime46Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay4.findViewById(R.id.sc2moveTime6), resources.getString(R.string.moveTimeText), 4)
            //5日目
            departureTime50Tag -> setDepartureTime(includeDay5.findViewById(R.id.sc2departureTime)
                , hourOfDay, minute, 5)
            departureMoveTime50Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay5.findViewById(R.id.sc2departureMoveTime), resources.getString(R.string.moveTimeText), 5)
            requireTime51Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay5.findViewById(R.id.sc2requireTime1), resources.getString(R.string.requireTimeText), 5)
            moveTime51Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay5.findViewById(R.id.sc2moveTime1), resources.getString(R.string.moveTimeText), 5)
            requireTime52Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay5.findViewById(R.id.sc2requireTime2), resources.getString(R.string.requireTimeText), 5)
            moveTime52Tag -> setRequireOrMoveTime(hourOfDay, minute, includeDay5.findViewById(R.id.sc2moveTime2)
                , resources.getString(R.string.moveTimeText), 5)
            requireTime53Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay5.findViewById(R.id.sc2requireTime3), resources.getString(R.string.requireTimeText), 5)
            moveTime53Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay5.findViewById(R.id.sc2moveTime3), resources.getString(R.string.moveTimeText), 5)
            requireTime54Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay5.findViewById(R.id.sc2requireTime4), resources.getString(R.string.requireTimeText), 5)
            moveTime54Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay5.findViewById(R.id.sc2moveTime4), resources.getString(R.string.moveTimeText), 5)
            requireTime55Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay5.findViewById(R.id.sc2requireTime5), resources.getString(R.string.requireTimeText), 5)
            moveTime55Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay5.findViewById(R.id.sc2moveTime5), resources.getString(R.string.moveTimeText), 5)
            requireTime56Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay5.findViewById(R.id.sc2requireTime6), resources.getString(R.string.requireTimeText), 5)
            moveTime56Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay5.findViewById(R.id.sc2moveTime6), resources.getString(R.string.moveTimeText), 5)
            //6日目
            departureTime60Tag -> setDepartureTime(includeDay6.findViewById(R.id.sc2departureTime)
                , hourOfDay, minute, 6)
            departureMoveTime60Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay6.findViewById(R.id.sc2departureMoveTime), resources.getString(R.string.moveTimeText), 6)
            requireTime61Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay6.findViewById(R.id.sc2requireTime1), resources.getString(R.string.requireTimeText), 6)
            moveTime61Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay6.findViewById(R.id.sc2moveTime1), resources.getString(R.string.moveTimeText), 6)
            requireTime62Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay6.findViewById(R.id.sc2requireTime2), resources.getString(R.string.requireTimeText), 6)
            moveTime62Tag -> setRequireOrMoveTime(hourOfDay, minute, includeDay6.findViewById(R.id.sc2moveTime2)
                , resources.getString(R.string.moveTimeText), 6)
            requireTime63Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay6.findViewById(R.id.sc2requireTime3), resources.getString(R.string.requireTimeText), 6)
            moveTime63Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay6.findViewById(R.id.sc2moveTime3), resources.getString(R.string.moveTimeText), 6)
            requireTime64Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay6.findViewById(R.id.sc2requireTime4), resources.getString(R.string.requireTimeText), 6)
            moveTime64Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay6.findViewById(R.id.sc2moveTime4), resources.getString(R.string.moveTimeText), 6)
            requireTime65Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay6.findViewById(R.id.sc2requireTime5), resources.getString(R.string.requireTimeText), 6)
            moveTime65Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay6.findViewById(R.id.sc2moveTime5), resources.getString(R.string.moveTimeText), 6)
            requireTime66Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay6.findViewById(R.id.sc2requireTime6), resources.getString(R.string.requireTimeText), 6)
            moveTime66Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay6.findViewById(R.id.sc2moveTime6), resources.getString(R.string.moveTimeText), 6)
            //7日目
            departureTime70Tag -> setDepartureTime(includeDay7.findViewById(R.id.sc2departureTime)
                , hourOfDay, minute, 7)
            departureMoveTime70Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay7.findViewById(R.id.sc2departureMoveTime), resources.getString(R.string.moveTimeText), 7)
            requireTime71Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay7.findViewById(R.id.sc2requireTime1), resources.getString(R.string.requireTimeText), 7)
            moveTime71Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay7.findViewById(R.id.sc2moveTime1), resources.getString(R.string.moveTimeText), 7)
            requireTime72Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay7.findViewById(R.id.sc2requireTime2), resources.getString(R.string.requireTimeText), 7)
            moveTime72Tag -> setRequireOrMoveTime(hourOfDay, minute, includeDay7.findViewById(R.id.sc2moveTime2)
                , resources.getString(R.string.moveTimeText), 7)
            requireTime73Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay7.findViewById(R.id.sc2requireTime3), resources.getString(R.string.requireTimeText), 7)
            moveTime73Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay7.findViewById(R.id.sc2moveTime3), resources.getString(R.string.moveTimeText), 7)
            requireTime74Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay7.findViewById(R.id.sc2requireTime4), resources.getString(R.string.requireTimeText), 7)
            moveTime74Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay7.findViewById(R.id.sc2moveTime4), resources.getString(R.string.moveTimeText), 7)
            requireTime75Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay7.findViewById(R.id.sc2requireTime5), resources.getString(R.string.requireTimeText), 7)
            moveTime75Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay7.findViewById(R.id.sc2moveTime5), resources.getString(R.string.moveTimeText), 7)
            requireTime76Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay7.findViewById(R.id.sc2requireTime6), resources.getString(R.string.requireTimeText), 7)
            moveTime76Tag -> setRequireOrMoveTime(hourOfDay, minute
                , includeDay7.findViewById(R.id.sc2moveTime6), resources.getString(R.string.moveTimeText), 7)
        }
        when(temporalyDayFlag){
            1 -> if(includeDay1.findViewById<TextView>(R.id.sc2departureTime).text != "") refresh(includeDay1)
            2 -> if(includeDay2.findViewById<TextView>(R.id.sc2departureTime).text != "") refresh(includeDay2)
            3 -> if(includeDay3.findViewById<TextView>(R.id.sc2departureTime).text != "") refresh(includeDay3)
            4 -> if(includeDay4.findViewById<TextView>(R.id.sc2departureTime).text != "") refresh(includeDay4)
            5 -> if(includeDay5.findViewById<TextView>(R.id.sc2departureTime).text != "") refresh(includeDay5)
            6 -> if(includeDay6.findViewById<TextView>(R.id.sc2departureTime).text != "") refresh(includeDay6)
            7 -> if(includeDay7.findViewById<TextView>(R.id.sc2departureTime).text != "") refresh(includeDay7)
        }
    }

    private fun setViewLink(travelDays: Long) {
        //(1日目)タップ処理
        includeDay1.findViewById<TextView>(R.id.sc2departureTime)
            .setOnClickListener { callDepartureTimePicker(departureTime10Tag) }
        includeDay1.findViewById<TextView>(R.id.sc2departureMoveTime)
            .setOnClickListener { callRequireTimePicker(departureMoveTime10Tag) }
        includeDay1.findViewById<TextView>(R.id.sc2requireTime1)
            .setOnClickListener { callRequireTimePicker(requireTime11Tag) }
        includeDay1.findViewById<TextView>(R.id.sc2moveTime1)
            .setOnClickListener { callRequireTimePicker(moveTime11Tag) }
        includeDay1.findViewById<TextView>(R.id.sc2requireTime2)
            .setOnClickListener { callRequireTimePicker(requireTime12Tag) }
        includeDay1.findViewById<TextView>(R.id.sc2moveTime2)
            .setOnClickListener { callRequireTimePicker(moveTime12Tag) }
        includeDay1.findViewById<TextView>(R.id.sc2requireTime3)
            .setOnClickListener { callRequireTimePicker(requireTime13Tag) }
        includeDay1.findViewById<TextView>(R.id.sc2moveTime3)
            .setOnClickListener { callRequireTimePicker(moveTime13Tag) }
        includeDay1.findViewById<TextView>(R.id.sc2requireTime4)
            .setOnClickListener { callRequireTimePicker(requireTime14Tag) }
        includeDay1.findViewById<TextView>(R.id.sc2moveTime4)
            .setOnClickListener { callRequireTimePicker(moveTime14Tag) }
        includeDay1.findViewById<TextView>(R.id.sc2requireTime5)
            .setOnClickListener { callRequireTimePicker(requireTime15Tag) }
        includeDay1.findViewById<TextView>(R.id.sc2moveTime5)
            .setOnClickListener { callRequireTimePicker(moveTime15Tag) }
        includeDay1.findViewById<TextView>(R.id.sc2requireTime6)
            .setOnClickListener { callRequireTimePicker(requireTime16Tag) }
        includeDay1.findViewById<TextView>(R.id.sc2moveTime6)
            .setOnClickListener { callRequireTimePicker(moveTime16Tag) }
        //(2日目)タップ処理
        if(travelDays > 1){
            includeDay2.findViewById<TextView>(R.id.sc2departureTime)
                .setOnClickListener { callDepartureTimePicker(departureTime20Tag) }
            includeDay2.findViewById<TextView>(R.id.sc2departureMoveTime)
                .setOnClickListener { callRequireTimePicker(departureMoveTime20Tag) }
            includeDay2.findViewById<TextView>(R.id.sc2requireTime1)
                .setOnClickListener { callRequireTimePicker(requireTime21Tag) }
            includeDay2.findViewById<TextView>(R.id.sc2moveTime1)
                .setOnClickListener { callRequireTimePicker(moveTime21Tag) }
            includeDay2.findViewById<TextView>(R.id.sc2requireTime2)
                .setOnClickListener { callRequireTimePicker(requireTime22Tag) }
            includeDay2.findViewById<TextView>(R.id.sc2moveTime2)
                .setOnClickListener { callRequireTimePicker(moveTime22Tag) }
            includeDay2.findViewById<TextView>(R.id.sc2requireTime3)
                .setOnClickListener { callRequireTimePicker(requireTime23Tag) }
            includeDay2.findViewById<TextView>(R.id.sc2moveTime3)
                .setOnClickListener { callRequireTimePicker(moveTime23Tag) }
            includeDay2.findViewById<TextView>(R.id.sc2requireTime4)
                .setOnClickListener { callRequireTimePicker(requireTime24Tag) }
            includeDay2.findViewById<TextView>(R.id.sc2moveTime4)
                .setOnClickListener { callRequireTimePicker(moveTime24Tag) }
            includeDay2.findViewById<TextView>(R.id.sc2requireTime5)
                .setOnClickListener { callRequireTimePicker(requireTime25Tag) }
            includeDay2.findViewById<TextView>(R.id.sc2moveTime5)
                .setOnClickListener { callRequireTimePicker(moveTime25Tag) }
            includeDay2.findViewById<TextView>(R.id.sc2requireTime6)
                .setOnClickListener { callRequireTimePicker(requireTime26Tag) }
            includeDay2.findViewById<TextView>(R.id.sc2moveTime6)
                .setOnClickListener { callRequireTimePicker(moveTime26Tag) }
        }
        //(3日目)タップ処理
        if(travelDays > 2){
            includeDay3.findViewById<TextView>(R.id.sc2departureTime)
                .setOnClickListener { callDepartureTimePicker(departureTime30Tag) }
            includeDay3.findViewById<TextView>(R.id.sc2departureMoveTime)
                .setOnClickListener { callRequireTimePicker(departureMoveTime30Tag) }
            includeDay3.findViewById<TextView>(R.id.sc2requireTime1)
                .setOnClickListener { callRequireTimePicker(requireTime31Tag) }
            includeDay3.findViewById<TextView>(R.id.sc2moveTime1)
                .setOnClickListener { callRequireTimePicker(moveTime31Tag) }
            includeDay3.findViewById<TextView>(R.id.sc2requireTime2)
                .setOnClickListener { callRequireTimePicker(requireTime32Tag) }
            includeDay3.findViewById<TextView>(R.id.sc2moveTime2)
                .setOnClickListener { callRequireTimePicker(moveTime32Tag) }
            includeDay3.findViewById<TextView>(R.id.sc2requireTime3)
                .setOnClickListener { callRequireTimePicker(requireTime33Tag) }
            includeDay3.findViewById<TextView>(R.id.sc2moveTime3)
                .setOnClickListener { callRequireTimePicker(moveTime33Tag) }
            includeDay3.findViewById<TextView>(R.id.sc2requireTime4)
                .setOnClickListener { callRequireTimePicker(requireTime34Tag) }
            includeDay3.findViewById<TextView>(R.id.sc2moveTime4)
                .setOnClickListener { callRequireTimePicker(moveTime34Tag) }
            includeDay3.findViewById<TextView>(R.id.sc2requireTime5)
                .setOnClickListener { callRequireTimePicker(requireTime35Tag) }
            includeDay3.findViewById<TextView>(R.id.sc2moveTime5)
                .setOnClickListener { callRequireTimePicker(moveTime35Tag) }
            includeDay3.findViewById<TextView>(R.id.sc2requireTime6)
                .setOnClickListener { callRequireTimePicker(requireTime36Tag) }
            includeDay3.findViewById<TextView>(R.id.sc2moveTime6)
                .setOnClickListener { callRequireTimePicker(moveTime36Tag) }
        }
        //(4日目)タップ処理
        if(travelDays > 3){
            includeDay4.findViewById<TextView>(R.id.sc2departureTime)
                .setOnClickListener { callDepartureTimePicker(departureTime40Tag) }
            includeDay4.findViewById<TextView>(R.id.sc2departureMoveTime)
                .setOnClickListener { callRequireTimePicker(departureMoveTime40Tag) }
            includeDay4.findViewById<TextView>(R.id.sc2requireTime1)
                .setOnClickListener { callRequireTimePicker(requireTime41Tag) }
            includeDay4.findViewById<TextView>(R.id.sc2moveTime1)
                .setOnClickListener { callRequireTimePicker(moveTime41Tag) }
            includeDay4.findViewById<TextView>(R.id.sc2requireTime2)
                .setOnClickListener { callRequireTimePicker(requireTime42Tag) }
            includeDay4.findViewById<TextView>(R.id.sc2moveTime2)
                .setOnClickListener { callRequireTimePicker(moveTime42Tag) }
            includeDay4.findViewById<TextView>(R.id.sc2requireTime3)
                .setOnClickListener { callRequireTimePicker(requireTime43Tag) }
            includeDay4.findViewById<TextView>(R.id.sc2moveTime3)
                .setOnClickListener { callRequireTimePicker(moveTime43Tag) }
            includeDay4.findViewById<TextView>(R.id.sc2requireTime4)
                .setOnClickListener { callRequireTimePicker(requireTime44Tag) }
            includeDay4.findViewById<TextView>(R.id.sc2moveTime4)
                .setOnClickListener { callRequireTimePicker(moveTime44Tag) }
            includeDay4.findViewById<TextView>(R.id.sc2requireTime5)
                .setOnClickListener { callRequireTimePicker(requireTime45Tag) }
            includeDay4.findViewById<TextView>(R.id.sc2moveTime5)
                .setOnClickListener { callRequireTimePicker(moveTime45Tag) }
            includeDay4.findViewById<TextView>(R.id.sc2requireTime6)
                .setOnClickListener { callRequireTimePicker(requireTime46Tag) }
            includeDay4.findViewById<TextView>(R.id.sc2moveTime6)
                .setOnClickListener { callRequireTimePicker(moveTime46Tag) }
        }
        //(5日目)タップ処理
        if(travelDays > 4){
            includeDay5.findViewById<TextView>(R.id.sc2departureTime)
                .setOnClickListener { callDepartureTimePicker(departureTime50Tag) }
            includeDay5.findViewById<TextView>(R.id.sc2departureMoveTime)
                .setOnClickListener { callRequireTimePicker(departureMoveTime50Tag) }
            includeDay5.findViewById<TextView>(R.id.sc2requireTime1)
                .setOnClickListener { callRequireTimePicker(requireTime51Tag) }
            includeDay5.findViewById<TextView>(R.id.sc2moveTime1)
                .setOnClickListener { callRequireTimePicker(moveTime51Tag) }
            includeDay5.findViewById<TextView>(R.id.sc2requireTime2)
                .setOnClickListener { callRequireTimePicker(requireTime52Tag) }
            includeDay5.findViewById<TextView>(R.id.sc2moveTime2)
                .setOnClickListener { callRequireTimePicker(moveTime52Tag) }
            includeDay5.findViewById<TextView>(R.id.sc2requireTime3)
                .setOnClickListener { callRequireTimePicker(requireTime53Tag) }
            includeDay5.findViewById<TextView>(R.id.sc2moveTime3)
                .setOnClickListener { callRequireTimePicker(moveTime53Tag) }
            includeDay5.findViewById<TextView>(R.id.sc2requireTime4)
                .setOnClickListener { callRequireTimePicker(requireTime54Tag) }
            includeDay5.findViewById<TextView>(R.id.sc2moveTime4)
                .setOnClickListener { callRequireTimePicker(moveTime54Tag) }
            includeDay5.findViewById<TextView>(R.id.sc2requireTime5)
                .setOnClickListener { callRequireTimePicker(requireTime55Tag) }
            includeDay5.findViewById<TextView>(R.id.sc2moveTime5)
                .setOnClickListener { callRequireTimePicker(moveTime55Tag) }
            includeDay5.findViewById<TextView>(R.id.sc2requireTime6)
                .setOnClickListener { callRequireTimePicker(requireTime56Tag) }
            includeDay5.findViewById<TextView>(R.id.sc2moveTime6)
                .setOnClickListener { callRequireTimePicker(moveTime56Tag) }
        }
        //(6日目)タップ処理
        if(travelDays > 5){
            includeDay6.findViewById<TextView>(R.id.sc2departureTime)
                .setOnClickListener { callDepartureTimePicker(departureTime60Tag) }
            includeDay6.findViewById<TextView>(R.id.sc2departureMoveTime)
                .setOnClickListener { callRequireTimePicker(departureMoveTime60Tag) }
            includeDay6.findViewById<TextView>(R.id.sc2requireTime1)
                .setOnClickListener { callRequireTimePicker(requireTime61Tag) }
            includeDay6.findViewById<TextView>(R.id.sc2moveTime1)
                .setOnClickListener { callRequireTimePicker(moveTime61Tag) }
            includeDay6.findViewById<TextView>(R.id.sc2requireTime2)
                .setOnClickListener { callRequireTimePicker(requireTime62Tag) }
            includeDay6.findViewById<TextView>(R.id.sc2moveTime2)
                .setOnClickListener { callRequireTimePicker(moveTime62Tag) }
            includeDay6.findViewById<TextView>(R.id.sc2requireTime3)
                .setOnClickListener { callRequireTimePicker(requireTime63Tag) }
            includeDay6.findViewById<TextView>(R.id.sc2moveTime3)
                .setOnClickListener { callRequireTimePicker(moveTime63Tag) }
            includeDay6.findViewById<TextView>(R.id.sc2requireTime4)
                .setOnClickListener { callRequireTimePicker(requireTime64Tag) }
            includeDay6.findViewById<TextView>(R.id.sc2moveTime4)
                .setOnClickListener { callRequireTimePicker(moveTime64Tag) }
            includeDay6.findViewById<TextView>(R.id.sc2requireTime5)
                .setOnClickListener { callRequireTimePicker(requireTime65Tag) }
            includeDay6.findViewById<TextView>(R.id.sc2moveTime5)
                .setOnClickListener { callRequireTimePicker(moveTime65Tag) }
            includeDay6.findViewById<TextView>(R.id.sc2requireTime6)
                .setOnClickListener { callRequireTimePicker(requireTime66Tag) }
            includeDay6.findViewById<TextView>(R.id.sc2moveTime6)
                .setOnClickListener { callRequireTimePicker(moveTime66Tag) }
        }
        //(7日目)タップ処理
        if(travelDays > 6) {
            includeDay7.findViewById<TextView>(R.id.sc2departureTime)
                .setOnClickListener { callDepartureTimePicker(departureTime70Tag) }
            includeDay7.findViewById<TextView>(R.id.sc2departureMoveTime)
                .setOnClickListener { callRequireTimePicker(departureMoveTime70Tag) }
            includeDay7.findViewById<TextView>(R.id.sc2requireTime1)
                .setOnClickListener { callRequireTimePicker(requireTime71Tag) }
            includeDay7.findViewById<TextView>(R.id.sc2moveTime1)
                .setOnClickListener { callRequireTimePicker(moveTime71Tag) }
            includeDay7.findViewById<TextView>(R.id.sc2requireTime2)
                .setOnClickListener { callRequireTimePicker(requireTime72Tag) }
            includeDay7.findViewById<TextView>(R.id.sc2moveTime2)
                .setOnClickListener { callRequireTimePicker(moveTime72Tag) }
            includeDay7.findViewById<TextView>(R.id.sc2requireTime3)
                .setOnClickListener { callRequireTimePicker(requireTime73Tag) }
            includeDay7.findViewById<TextView>(R.id.sc2moveTime3)
                .setOnClickListener { callRequireTimePicker(moveTime73Tag) }
            includeDay7.findViewById<TextView>(R.id.sc2requireTime4)
                .setOnClickListener { callRequireTimePicker(requireTime74Tag) }
            includeDay7.findViewById<TextView>(R.id.sc2moveTime4)
                .setOnClickListener { callRequireTimePicker(moveTime74Tag) }
            includeDay7.findViewById<TextView>(R.id.sc2requireTime5)
                .setOnClickListener { callRequireTimePicker(requireTime75Tag) }
            includeDay7.findViewById<TextView>(R.id.sc2moveTime5)
                .setOnClickListener { callRequireTimePicker(moveTime75Tag) }
            includeDay7.findViewById<TextView>(R.id.sc2requireTime6)
                .setOnClickListener { callRequireTimePicker(requireTime76Tag) }
            includeDay7.findViewById<TextView>(R.id.sc2moveTime6)
                .setOnClickListener { callRequireTimePicker(moveTime76Tag) }
        }
    }
}