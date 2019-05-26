package tafm.tt10tt10.mytesttravel

import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.TextView
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit1_travel.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
import tafm.tt10tt10.mytesttravel.fragment.DateDiffAlertDialogs
import tafm.tt10tt10.mytesttravel.fragment.DatePickerFragment
import tafm.tt10tt10.mytesttravel.model.Travel
import tafm.tt10tt10.mytesttravel.model.TravelDetail
import tafm.tt10tt10.mytesttravel.model.TravelPart
import java.lang.StringBuilder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Edit1TravelActivity : AppCompatActivity(), DatePickerFragment.OnDateSelectedListener {

    private lateinit var temporalyTag: String
    private lateinit var realm: Realm
    private var temporalTravelDays: Long = 0L
    private val departureDateTag = "departureDateTag"
    private val arrivalDateTag = "arrivalDateTag"
    private val dateDiffTag = "dateDiffTag"

    private val requireTimeText = "所要: ";   private val moveTimeText = "移動: "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit1_travel)

        val manageId = intent.getIntExtra("manageId", 1)
        realm = Realm.getDefaultInstance()

        //データベースからデータをとってきてビューに表示する。
        setData(manageId)

        //出発日をタップ
        edit1DepartureDay.setOnClickListener {
            val dialog = DatePickerFragment()
            dialog.show(supportFragmentManager, departureDateTag)
            temporalyTag = dialog.tag.toString()
        }

        //到着日をタップ
        edit1ArrivalDay.setOnClickListener {
            val dialog = DatePickerFragment()
            dialog.show(supportFragmentManager, arrivalDateTag)
            temporalyTag = dialog.tag.toString()
        }
        //更新ボタンタップ
        edit1ApplyButton.setOnClickListener {
            if (checkInput()) checkSuccess(manageId)
        }
        //戻るボタンタップ
        edit1BackToMain.setOnClickListener {
            finish()
        }
    }

    //Inputの内容をチェックする。
    private fun checkInput(): Boolean {
        var check = true
        if (edit1TravelTitle.text.isEmpty()) {
            check = false
            alert("タイトルを設定してください") { yesButton { } }.show()
        }
        if (check && (edit1DepartureDay.text.isEmpty() || edit1ArrivalDay.text.isEmpty())) {
            check = false
            alert("出発・到着日時を設定してください") { yesButton { } }.show()
        }
        if (check && (edit1DeparturePlace.text.isEmpty() || edit1ArrivalPlace.text.isEmpty())) {
            check = false
            alert("出発・到着地を設定してください") { yesButton { } }.show()
        }
        return check
    }

    //入力チェックが完了後、データベースに保存。
    private fun checkSuccess(manageId: Int) {
        realm.executeTransaction {
            val travel = realm.where<Travel>()
                .equalTo("manageId", manageId)
                .findFirstAsync()
            setTravelModel(travel)
            setTravelPartAndDetailModel(manageId, travel.travelDays)
        }
        finish()
    }

    //TravelModelに値を格納
    private fun setTravelModel(travel: Travel) {
        travel.title = edit1TravelTitle.text.toString()
        travel.travelDays = temporalTravelDays.toInt() + 1
        travel.departureDay = edit1DepartureDay.text.toString()
        travel.departurePlace = edit1DeparturePlace.text.toString()
        travel.arrivalDay = edit1ArrivalDay.text.toString()
        travel.arrivalPlace = edit1ArrivalPlace.text.toString()
    }

    //travelDays変更によるTravelPartとTravelDetailの追加削除
    private fun setTravelPartAndDetailModel(manageId: Int, days: Int) {
        //最終目的地のデータの削除。
        deleteArrivalTravelDetail(manageId)

        //日付が増えた場合
        if(days > 1){
            for (day in 2..days){
                val existPartForAdd = realm.where<TravelPart>()
                    .equalTo("manageId", manageId)
                    .equalTo("day", day)
                    .findFirst()

                if (existPartForAdd !is TravelPart) {
                    Log.i("【Edit1TravelActivity】", "[setTravelPartAndDetailModel] existPartForAddが存在しない day:$day")
                    createNewTravelPart(manageId, day)
                    createNewTravelDetail(manageId, day)
                }
            }
        }
        //日付が減った場合
        if(7 > days){
            for(day in (days + 1)..7){
                val existPartForDelete = realm.where<TravelPart>()
                    .equalTo("manageId", manageId)
                    .equalTo("day", day)
                    .findFirst()

                if(existPartForDelete is TravelPart){
                    Log.i("【Edit1TravelActivity】", "[setTravelPartAndDetailModel] existPartForDeleteが存在 day:$day")
                    existPartForDelete.deleteFromRealm()
                    realm.where<TravelDetail>()
                        .equalTo("manageId", manageId)
                        .equalTo("day", day)
                        .findAll().deleteAllFromRealm()
                }
            }
        }
        //最終目的地データの作成。
        createNewArrivalTravelDetail(manageId, days)
    }

    //TravelDetailの最終目的地(order=9)のデータをいったん削除する。
    private fun deleteArrivalTravelDetail(manageId: Int) {
        val lastOrder = 9
        realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("order", lastOrder)
            .findFirst()?.deleteFromRealm()
    }

    //日付が増えた時に新しくTravelPartを作成する。
    private fun createNewTravelPart(manageId: Int, day: Int) {
        val maxPartId = realm.where<TravelPart>().max("id")
        val nextPartId = (maxPartId?.toInt() ?: 0) + 1
        val updatePart = realm.createObject<TravelPart>(nextPartId)
        updatePart.manageId = manageId
        updatePart.day = day
        updatePart.destination1 = "Please Edit"
        updatePart.deleteFlag = 0
    }

    //日付が増えた時に新しくTravelDetail(departurePlace、destination1)を作成する。
    private fun createNewTravelDetail(manageId: Int, day: Int) {
        //出発地用TravelDetail作成　requireTimeは出発地なのでなし。
        val maxDetailIdForDeparture = realm.where<TravelDetail>().max("id")
        val nextDetailIdForDeparture = (maxDetailIdForDeparture?.toInt() ?: 0) + 1
        val updateTravelDetailForDeparture = realm.createObject<TravelDetail>(nextDetailIdForDeparture)
        updateTravelDetailForDeparture.manageId = manageId
        updateTravelDetailForDeparture.day = day
        updateTravelDetailForDeparture.order = 0
        updateTravelDetailForDeparture.destination = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day - 1)
            .sort("order", Sort.DESCENDING)
            .findFirst()?.destination.toString()
        updateTravelDetailForDeparture.startTime = realm.where<Travel>()
            .equalTo("manageId", manageId)
            .findFirst()?.arrivalTime.toString()
        updateTravelDetailForDeparture.moveTime = moveTimeText + "0 min "
        updateTravelDetailForDeparture.deleteFlag = 0

        //destination1用TravelDetail作成
        val maxDetailIdForDestination = realm.where<TravelDetail>().max("id")
        val nextDetailIdForDestination = (maxDetailIdForDestination?.toInt() ?: 0) + 1
        val updateTravelDetailForDestination = realm.createObject<TravelDetail>(nextDetailIdForDestination)
        updateTravelDetailForDestination.manageId = manageId
        updateTravelDetailForDestination.day = day
        updateTravelDetailForDestination.order = 1
        updateTravelDetailForDestination.destination = "Please Edit"
        updateTravelDetailForDestination.startTime = realm.where<Travel>()
            .equalTo("manageId", manageId)
            .findFirst()?.arrivalTime.toString()
        updateTravelDetailForDestination.requiredTime = requireTimeText + "0 min "
        updateTravelDetailForDestination.moveTime = moveTimeText + "0 min "
        updateTravelDetailForDestination.deleteFlag = 0
    }

    //TravelDetailの最終目的地(order=9)のデータをあたらしく作成する。 moveTime, requireTimeは到着地なのでなし。
    private fun createNewArrivalTravelDetail(manageId: Int, days: Int){
        val maxDetailIdForArrival = realm.where<TravelDetail>().max("id")
        val nextDetailIdForArrival = (maxDetailIdForArrival?.toInt() ?: 0) + 1
        val updateTravelDetailForArrival = realm.createObject<TravelDetail>(nextDetailIdForArrival)
        updateTravelDetailForArrival.manageId = manageId
        updateTravelDetailForArrival.day = days
        updateTravelDetailForArrival.order = 9
        updateTravelDetailForArrival.destination = findViewById<TextView>(R.id.edit1ArrivalPlace).text.toString()
        updateTravelDetailForArrival.startTime = realm.where<Travel>()
            .equalTo("manageId", manageId)
            .findFirst()?.arrivalTime.toString()
        updateTravelDetailForArrival.deleteFlag = 0
    }

    //日付を選択した後に呼ばれるメソッド。
    override fun onSelected(year: Int, month: Int, date: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, date)
        val builder = StringBuilder()

        when(temporalyTag){
            departureDateTag -> {
                edit1DepartureDay.text = DateFormat.format("yyyy/MM/dd", calendar)
                edit1DepartureDay.background = doneColorChange()
                edit1DepartureText.text = builder.append("・").append(edit1DepartureDay.text).append("出発地点と時刻は？")
            }
            arrivalDateTag -> {
                edit1ArrivalDay.text = DateFormat.format("yyyy/MM/dd", calendar)
                edit1ArrivalDay.background = doneColorChange()
                edit1ArrivalText.text = builder.append("・").append(edit1ArrivalDay.text).append("到着地点と時刻は？")
            }
        }

        if (edit1DepartureDay.text != "" && edit1ArrivalDay.text != ""){
            when (val dateDiff = dateDiff(edit1DepartureDay.text.toString(), edit1ArrivalDay.text.toString())){
                in 0..6 -> setTravelDay(dateDiff)
                else -> {
                    val dialog = DateDiffAlertDialogs()
                    dialog.show(supportFragmentManager, dateDiffTag)
                    edit1ArrivalDay.text = null
                    edit1ArrivalDay.background = falseColorChange()
                    edit1ArrivalText.text ="・到着地点と時刻は？"
                    edit1TravelDays.text = "日付を選択してください。"
                }
            }
        }
    }

    //入力成功時に色を変える
    @Suppress("DEPRECATION")
    private fun doneColorChange(): Drawable? {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) getDrawable(R.color.timeBackGround)
        else resources.getDrawable(R.color.timeBackGround)
    }

    //入力失敗時に色を変える。
    @Suppress("DEPRECATION")
    private fun falseColorChange(): Drawable? {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) getDrawable(R.color.colorAccent)
        else resources.getDrawable(R.color.colorAccent)
    }

    //出発日と到着日から何泊何日かを計算する。
    private fun dateDiff(departureDay: String, arrivalDay: String):Int {
        val sdFormat = try{
            SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN)
        } catch (e: IllegalArgumentException){
            null
        }
        temporalTravelDays =  try{
            val dateFrom:Date? = sdFormat?.parse(departureDay)
            val dateTo:Date? = sdFormat?.parse(arrivalDay)
            val dateTimeFrom = dateFrom?.time
            val dateTimeTo = dateTo?.time
            if (dateTimeFrom != null && dateTimeTo != null){
                (dateTimeTo - dateTimeFrom)  / (1000 * 60 * 60 * 24)
            }else {
                10
            }
        }catch (e: ParseException){
            10
        }
        return temporalTravelDays.toInt()
    }

    //何泊何日の出力メソッド
    private fun setTravelDay(dateDiff: Int) {
        val dateDiffMap: MutableMap<Int, String> = mutableMapOf()
        for (i in 0..6) dateDiffMap[i] = (i.toString() + "泊" + (i + 1) + "日")
        edit1TravelDays.text = dateDiffMap[dateDiff]
    }

    //データベースからデータをとってきてビューに表示する。
    private fun setData(manageId: Int) {
        val travel = realm.where<Travel>().equalTo("manageId", manageId).findFirst()
        edit1TravelTitle.setText(travel?.title)
        edit1DepartureDay.text = travel?.departureDay
        edit1ArrivalDay.text = travel?.arrivalDay
        val daysBuilder = StringBuilder()
            .append(travel?.travelDays?.minus(1)).append("泊").append(travel?.travelDays).append("日")
        edit1TravelDays.text = daysBuilder.toString()
        edit1DeparturePlace.setText(travel?.departurePlace)
        edit1ArrivalPlace.setText(travel?.arrivalPlace)
    }

    //アクティビティ消滅時にrealmを閉じる。
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}