package tomohiro.takeuchi.android.smarttravel

import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit_schedule.*
import tomohiro.takeuchi.android.smarttravel.fragment.RequireTimePickerFragment
import tomohiro.takeuchi.android.smarttravel.fragment.TimePickerFragment
import tomohiro.takeuchi.android.smarttravel.model.Travel
import tomohiro.takeuchi.android.smarttravel.model.TravelDetail
import java.lang.StringBuilder

class EditScheduleActivity : AppCompatActivity()
    , TimePickerFragment.OnTimeSelectedListener
    , RequireTimePickerFragment.OnTimeSelectedListener{

    private lateinit var realm: Realm
    private lateinit var view: View
    private lateinit var temporalyTag: String

    private var manageId = 1
    private var day = 1
    private var travelDays = 1
    private var lastDestinationFlag = true

    private val departureTime10Tag = "departureTime10Tag";    private val departureMoveTime10Tag = "departureMoveTime10Tag"
    private val requireTime11Tag = "requireTime11Tag";   private val moveTime11Tag = "moveTime11Tag"
    private val requireTime12Tag = "requireTime12Tag";   private val moveTime12Tag = "moveTime12Tag"
    private val requireTime13Tag = "requireTime13Tag";   private val moveTime13Tag = "moveTime13Tag"
    private val requireTime14Tag = "requireTime14Tag";   private val moveTime14Tag = "moveTime14Tag"
    private val requireTime15Tag = "requireTime15Tag";   private val moveTime15Tag = "moveTime15Tag"
    private val requireTime16Tag = "requireTime16Tag";   private val moveTime16Tag = "moveTime16Tag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_schedule)

        realm = Realm.getDefaultInstance()
        view = findViewById(R.id.scheduleEditInclude)
        manageId = intent.getIntExtra("manageId", 1)
        day = intent.getIntExtra("day", 1)
        travelDays = intent.getIntExtra("travelDays", 1)

        setGuideText(day)

        val travelDetails = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day)
            .sort("order", Sort.DESCENDING)
            .findAllAsync()

        for (travelDetail in travelDetails) setTextViews(travelDetail)
        setViewLink()

        scheduleEditBack.setOnClickListener {
            it.notPressTwice()
            finish()
        }

        scheduleEditUpdate.setOnClickListener {
            it.notPressTwice()
            realm.executeTransaction {
                devideTravelDetail()
            }
            finish()
        }
    }

    //目的地別に分類
    private fun devideTravelDetail() {
        lastDestinationFlag = true
        updateArrivalTravelDetail()

        if(view.findViewById<TextView>(R.id.sc2destination6).text.isNotEmpty()){
            updateTravelDetail(6, R.id.sc2startTime6, R.id.sc2requireTime6 ,R.id.sc2moveTime6)
        }
        if(view.findViewById<TextView>(R.id.sc2destination5).text.isNotEmpty()){
            updateTravelDetail(5, R.id.sc2startTime5, R.id.sc2requireTime5 ,R.id.sc2moveTime5)
        }
        if(view.findViewById<TextView>(R.id.sc2destination4).text.isNotEmpty()){
            updateTravelDetail(4, R.id.sc2startTime4, R.id.sc2requireTime4 ,R.id.sc2moveTime4)
        }
        if(view.findViewById<TextView>(R.id.sc2destination3).text.isNotEmpty()){
            updateTravelDetail(3, R.id.sc2startTime3, R.id.sc2requireTime3 ,R.id.sc2moveTime3)
        }
        if(view.findViewById<TextView>(R.id.sc2destination2).text.isNotEmpty()){
            updateTravelDetail(2, R.id.sc2startTime2, R.id.sc2requireTime2 ,R.id.sc2moveTime2)
        }
        if(view.findViewById<TextView>(R.id.sc2destination1).text.isNotEmpty()){
            updateTravelDetail(1, R.id.sc2startTime1, R.id.sc2requireTime1 ,R.id.sc2moveTime1)
        }
        updateDepartureTravelDetail()
    }

    //それぞれの目的地のTravelDetail更新。その日の最終目的地かどうか判別。
    private fun updateTravelDetail(order: Int, startId: Int, requireId: Int, moveId: Int) {
        updateLastTravelDetail(order + 1)
        val travelDetail =  realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day)
            .equalTo("order", order)
            .findFirst() as TravelDetail
        travelDetail.startTime = view.findViewById<TextView>(startId).text.toString()
        travelDetail.requiredTime = view.findViewById<TextView>(requireId).text.toString()
        travelDetail.moveTime = view.findViewById<TextView>(moveId).text.toString()
    }

    //出発のTravelDetail更新
    private fun updateDepartureTravelDetail() {
        val order = 0
        val travelDetail = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day)
            .equalTo("order", order)
            .findFirst() as TravelDetail

        travelDetail.startTime = view.findViewById<TextView>(R.id.sc2departureTime).text.toString()
        travelDetail.moveTime = view.findViewById<TextView>(R.id.sc2departureMoveTime).text.toString()
        if (day == 1){
            val travel = realm.where<Travel>()
                .equalTo("manageId", manageId)
                .findFirst() as Travel
                travel.departureTime = travelDetail.startTime
        }
        updateLastTravelDetail(order + 1)
    }

    //最終日の場合、到着のTravelDetail更新。Travelの到着時間も更新。
    private fun updateArrivalTravelDetail(){
        if(day == travelDays) {
            lastDestinationFlag = false
            val order = 9
            val travelDetail = realm.where<TravelDetail>()
                .equalTo("manageId", manageId)
                .equalTo("day", day)
                .equalTo("order", order)
                .findFirst()
            val travel = realm.where<Travel>()
                .equalTo("manageId", manageId)
                .findFirst() as Travel
            travelDetail?.let{
                it.startTime = view.findViewById<TextView>(R.id.sc2ArrivalTime).text.toString()
                travel.arrivalTime = it.startTime
            }
        }
    }

    //最終日でない場合、最終目的地のTravelDetail更新。
    private fun updateLastTravelDetail(nextOrder: Int){
        if(lastDestinationFlag){
            lastDestinationFlag = false
            val lastTravelDetail =  realm.where<TravelDetail>()
                .equalTo("manageId", manageId)
                .equalTo("day", day)
                .equalTo("order", nextOrder)
                .findFirst()
            lastTravelDetail?.let{
                it.startTime = view.findViewById<TextView>(R.id.sc2ArrivalTime).text.toString()
            }
        }
    }

    //所要・移動時間をセットするメソッド
    private fun setRequireOrMoveTime(hourOfDay: Int, minute: Int, textView: TextView, reqOrMove: String) {
        val builder = StringBuilder(reqOrMove)
        if (hourOfDay != 0) builder.append(hourOfDay).append(" h ")
        textView.text = builder.append(minute).append(" min ").toString()
        textView.background = doneColorChange()
    }

    //時間自動入力メソッド
    private fun refresh() {
        val total00 = timeFirstRefresh(view.findViewById(R.id.sc2departureTime))
        val total11 = timeRefresh(total00, null, view.findViewById(R.id.sc2departureMoveTime)
            , view.findViewById(R.id.sc2startTime1))
        val total12 = timeRefresh(total11, view.findViewById(R.id.sc2requireTime1)
            , view.findViewById(R.id.sc2moveTime1)
            , view.findViewById(R.id.sc2startTime2))
        val total13 = timeRefresh(total12, view.findViewById(R.id.sc2requireTime2)
            , view.findViewById(R.id.sc2moveTime2)
            , view.findViewById(R.id.sc2startTime3))
        val total14 = timeRefresh(total13, view.findViewById(R.id.sc2requireTime3)
            , view.findViewById(R.id.sc2moveTime3)
            , view.findViewById(R.id.sc2startTime4))
        val total15 = timeRefresh(total14, view.findViewById(R.id.sc2requireTime4)
            , view.findViewById(R.id.sc2moveTime4)
            , view.findViewById(R.id.sc2startTime5))
        val total16 = timeRefresh(total15, view.findViewById(R.id.sc2requireTime5)
            , view.findViewById(R.id.sc2moveTime5)
            , view.findViewById(R.id.sc2startTime6))
        timeRefresh(total16, view.findViewById(R.id.sc2requireTime6)
            , view.findViewById(R.id.sc2moveTime6)
            , view.findViewById(R.id.sc2ArrivalTime))
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
        if (requireTime is TextView && requireTime.text.isNotEmpty()) total +=
            extractData(requireTime, resources.getString(R.string.requireTimeText).length)
        if (moveTime.text.isNotEmpty()) total +=
            extractData(moveTime, resources.getString(R.string.moveTimeText).length)
        val timeFormat = "%1$02d:%2$02d"
        startTime.text = timeFormat.format(total / 60, total % 60)
        return total
    }

    //移動時間と所要時間データを抽出して加算したものを返却（1時間以上未満で場合分け）
    private fun extractData(textView: TextView, deleteNum: Int): Long {
        val extractData =
            StringBuilder(textView.text).delete(textView.text.length-5, textView.text.length-1).delete(0, deleteNum -1)
        val rArr = extractData.split("h")
        return if(rArr.size == 2) {
            Log.i("【Edit1ScheduleActivity】"
                , "[extractData] rArr.size == 2 rArr[0]=${rArr[0]} rArr[1]=${rArr[1]}")
            rArr[0].trim().toLong() * 60 + rArr[1].trim().toLong()
        }else {
            Log.i("【Edit1ScheduleActivity】"
                , "[extractData] rArr.size == 1 rArr[0]=${rArr[0]}")
            rArr[0].trim().toLong()
        }
    }

    //時刻ダイアログ選択後に呼ばれるメソッド
    override fun onSelected(hourOfDay: Int, minute: Int) {
        when (temporalyTag) {
            //1日目
            departureTime10Tag -> setDepartureTime(
                view.findViewById(R.id.sc2departureTime), hourOfDay, minute
            )
            departureMoveTime10Tag -> setRequireOrMoveTime(
                hourOfDay, minute, view.findViewById(R.id.sc2departureMoveTime)
                , resources.getString(R.string.moveTimeText)
            )
            requireTime11Tag -> setRequireOrMoveTime(
                hourOfDay, minute, view.findViewById(R.id.sc2requireTime1)
                , resources.getString(R.string.requireTimeText)
            )
            moveTime11Tag -> setRequireOrMoveTime(
                hourOfDay, minute, view.findViewById(R.id.sc2moveTime1)
                , resources.getString(R.string.moveTimeText)
            )
            requireTime12Tag -> setRequireOrMoveTime(
                hourOfDay, minute, view.findViewById(R.id.sc2requireTime2)
                , resources.getString(R.string.requireTimeText)
            )
            moveTime12Tag -> setRequireOrMoveTime(
                hourOfDay, minute, view.findViewById(R.id.sc2moveTime2)
                , resources.getString(R.string.moveTimeText)
            )
            requireTime13Tag -> setRequireOrMoveTime(
                hourOfDay, minute, view.findViewById(R.id.sc2requireTime3)
                , resources.getString(R.string.requireTimeText)
            )
            moveTime13Tag -> setRequireOrMoveTime(
                hourOfDay, minute, view.findViewById(R.id.sc2moveTime3)
                , resources.getString(R.string.moveTimeText)
            )
            requireTime14Tag -> setRequireOrMoveTime(
                hourOfDay, minute, view.findViewById(R.id.sc2requireTime4)
                , resources.getString(R.string.requireTimeText)
            )
            moveTime14Tag -> setRequireOrMoveTime(
                hourOfDay, minute, view.findViewById(R.id.sc2moveTime4)
                , resources.getString(R.string.moveTimeText)
            )
            requireTime15Tag -> setRequireOrMoveTime(
                hourOfDay, minute, view.findViewById(R.id.sc2requireTime5)
                , resources.getString(R.string.requireTimeText)
            )
            moveTime15Tag -> setRequireOrMoveTime(
                hourOfDay, minute, view.findViewById(R.id.sc2moveTime5)
                , resources.getString(R.string.moveTimeText)
            )
            requireTime16Tag -> setRequireOrMoveTime(
                hourOfDay, minute, view.findViewById(R.id.sc2requireTime6)
                , resources.getString(R.string.requireTimeText)
            )
            moveTime16Tag -> setRequireOrMoveTime(
                hourOfDay, minute, view.findViewById(R.id.sc2moveTime6)
                , resources.getString(R.string.moveTimeText)
            )
        }
        refresh()
    }

    //タップ処理
    private fun setViewLink() {
        view.findViewById<TextView>(R.id.sc2departureTime)
            .setOnClickListener { callDepartureTimePicker(departureTime10Tag) }
        view.findViewById<TextView>(R.id.sc2departureMoveTime)
            .setOnClickListener { callRequireTimePicker(departureMoveTime10Tag) }
        view.findViewById<TextView>(R.id.sc2requireTime1)
            .setOnClickListener { callRequireTimePicker(requireTime11Tag) }
        view.findViewById<TextView>(R.id.sc2moveTime1)
            .setOnClickListener { callRequireTimePicker(moveTime11Tag) }
        view.findViewById<TextView>(R.id.sc2requireTime2)
            .setOnClickListener { callRequireTimePicker(requireTime12Tag) }
        view.findViewById<TextView>(R.id.sc2moveTime2)
            .setOnClickListener { callRequireTimePicker(moveTime12Tag) }
        view.findViewById<TextView>(R.id.sc2requireTime3)
            .setOnClickListener { callRequireTimePicker(requireTime13Tag) }
        view.findViewById<TextView>(R.id.sc2moveTime3)
            .setOnClickListener { callRequireTimePicker(moveTime13Tag) }
        view.findViewById<TextView>(R.id.sc2requireTime4)
            .setOnClickListener { callRequireTimePicker(requireTime14Tag) }
        view.findViewById<TextView>(R.id.sc2moveTime4)
            .setOnClickListener { callRequireTimePicker(moveTime14Tag) }
        view.findViewById<TextView>(R.id.sc2requireTime5)
            .setOnClickListener { callRequireTimePicker(requireTime15Tag) }
        view.findViewById<TextView>(R.id.sc2moveTime5)
            .setOnClickListener { callRequireTimePicker(moveTime15Tag) }
        view.findViewById<TextView>(R.id.sc2requireTime6)
            .setOnClickListener { callRequireTimePicker(requireTime16Tag) }
        view.findViewById<TextView>(R.id.sc2moveTime6)
            .setOnClickListener { callRequireTimePicker(moveTime16Tag) }
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
    private fun setDepartureTime(textView: TextView, hourOfDay: Int, minute: Int) {
        val timeFormat = "%1$02d:%2$02d"
        textView.text = timeFormat.format(hourOfDay, minute)
        if(textView.text != "") textView.background = doneColorChange()
    }

    //orderを元に場合分け。
    private fun setTextViews(travelDetail: TravelDetail) {
        when(travelDetail.order){
            0 -> setDepartureTextView(travelDetail, R.id.sc2departureTime, R.id.sc2departurePlace
                ,R.id.sc2departureMoveTime)
            1 -> setDestinationTextView(travelDetail, R.id.sc2destination1Layout
                , R.id.sc2startTime1, R.id.sc2destination1, R.id.sc2requireTime1, R.id.sc2moveTime1)
            2 -> setDestinationTextView(travelDetail, R.id.sc2destination2Layout
                , R.id.sc2startTime2, R.id.sc2destination2, R.id.sc2requireTime2, R.id.sc2moveTime2)
            3 -> setDestinationTextView(travelDetail, R.id.sc2destination3Layout
                , R.id.sc2startTime3, R.id.sc2destination3, R.id.sc2requireTime3, R.id.sc2moveTime3)
            4 -> setDestinationTextView(travelDetail, R.id.sc2destination4Layout
                , R.id.sc2startTime4, R.id.sc2destination4, R.id.sc2requireTime4, R.id.sc2moveTime4)
            5 -> setDestinationTextView(travelDetail, R.id.sc2destination5Layout
                , R.id.sc2startTime5, R.id.sc2destination5, R.id.sc2requireTime5, R.id.sc2moveTime5)
            6 -> setDestinationTextView(travelDetail, R.id.sc2destination6Layout
                , R.id.sc2startTime6, R.id.sc2destination6, R.id.sc2requireTime6, R.id.sc2moveTime6)
            9 -> setArrivalTextView(travelDetail, R.id.sc2ArrivalTime, R.id.sc2ArrivalPlace)
        }
    }

    //それぞれの目的地にデータベースの値を入れる。その日の最終目的地なら到着地に入れる。
    private fun setDestinationTextView(travelDetail: TravelDetail, layoutId: Int, startTimeId: Int
                                       , destinationId: Int, requireTimeId: Int, moveTimeId: Int) {
        when(lastDestinationFlag) {
            true -> setArrivalTextView(travelDetail, R.id.sc2ArrivalTime, R.id.sc2ArrivalPlace)
            else -> {
                view.findViewById<View>(layoutId).visibility = View.VISIBLE
                view.findViewById<TextView>(startTimeId).text = travelDetail.startTime
                view.findViewById<TextView>(destinationId).text = travelDetail.destination
                view.findViewById<TextView>(requireTimeId).text = travelDetail.requiredTime
                view.findViewById<TextView>(moveTimeId).text = travelDetail.moveTime
                view.findViewById<TextView>(moveTimeId).background = doneColorChange()
                view.findViewById<TextView>(requireTimeId).background = doneColorChange()
            }
        }
    }

    //出発地にデータベースの値を入れる。
    private fun setDepartureTextView(travelDetail: TravelDetail, departureTimeId: Int
                                     , departurePlaceId: Int, moveTimeId: Int) {
        view.findViewById<TextView>(departureTimeId).text = travelDetail.startTime
        view.findViewById<TextView>(departurePlaceId).text = travelDetail.destination
        view.findViewById<TextView>(moveTimeId).text = travelDetail.moveTime
        view.findViewById<TextView>(departureTimeId).background = doneColorChange()
        view.findViewById<TextView>(moveTimeId).background = doneColorChange()
    }

    //最終日以外の場合、到着地にTravelDetailデータベースの値を入れる。
    private fun setArrivalTextView(travelDetail: TravelDetail, arrivalTimeId: Int, arrivalPlaceId: Int){
        lastDestinationFlag = false
        view.findViewById<TextView>(arrivalTimeId).text = travelDetail.startTime
        view.findViewById<TextView>(arrivalPlaceId).text = travelDetail.destination
    }

    //ガイドテキストを表示。何日目かを非表示
    private fun setGuideText(day: Int) {
        val text = "- Day $day -"
        scheduleEditGuide.text = text
        view.findViewById<TextView>(R.id.sc2DayText).visibility = View.GONE
    }

    //入力成功時に色を変える
    @Suppress("DEPRECATION")
    private fun doneColorChange(): Drawable? {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) getDrawable(R.color.timeBackGround)
        else resources.getDrawable(R.color.timeBackGround)
    }

    /**
     * 二度押し防止施策として 0.5 秒間タップを禁止する
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
