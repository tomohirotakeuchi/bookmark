package tafm.tt10tt10.mytesttravel

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.gms.location.*
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit1_travel.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
//import tafm.tt10tt10.mytesttravel.fragment.DateDiffAlertDialogs
import tafm.tt10tt10.mytesttravel.fragment.DatePickerFragment
import tafm.tt10tt10.mytesttravel.model.Authority
import tafm.tt10tt10.mytesttravel.model.Travel
import tafm.tt10tt10.mytesttravel.model.TravelDetail
import tafm.tt10tt10.mytesttravel.model.TravelPart
import java.lang.StringBuilder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EditMainActivity : AppCompatActivity(), DatePickerFragment.OnDateSelectedListener {

    private lateinit var temporalyTag: String
    private lateinit var realm: Realm
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var preTravelDays: Long = 0L
    private var temporalTravelDays: Long = 0L
    private val departureDateTag = "departureDateTag"
    private val arrivalDateTag = "arrivalDateTag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit1_travel)

        val manageId = intent.getIntExtra("manageId", 1)
        realm = Realm.getDefaultInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
            it.notPressTwice()
            if (checkInput()) {
                when(preTravelDays > temporalTravelDays){
                    true -> {
                        alert("Day ${temporalTravelDays + 2} " + resources.getString(R.string.willBeDelete)) {
                            yesButton { checkSuccess(manageId) }
                            noButton {  }
                        }.show()
                    }
                    false -> checkSuccess(manageId)
                }
            }
        }
        //戻るボタンタップ
        edit1BackToMain.setOnClickListener {
            it.notPressTwice()
            finish()
        }
    }

    //Inputの内容をチェックする。
    private fun checkInput(): Boolean {
        var check = true
        if (edit1TravelTitle.text.isEmpty()) {
            check = false
            alert(resources.getString(R.string.setTitleMsg)) { yesButton { } }.show()
        }
        if (check && (edit1DepartureDay.text.isEmpty() || edit1ArrivalDay.text.isEmpty())) {
            check = false
            alert(resources.getString(R.string.setDayMsg)) { yesButton { } }.show()
        }
        if (check && (edit1DeparturePlace.text.isEmpty() || edit1ArrivalPlace.text.isEmpty())) {
            check = false
            alert(resources.getString(R.string.setPlaceMsg)) { yesButton { } }.show()
        }
        return check
    }

    //入力チェックが完了後、データベースに保存。
    private fun checkSuccess(manageId: Int) {
        realm.executeTransaction {
            val travel = realm.where<Travel>()
                .equalTo("manageId", manageId)
                .findFirstAsync()

            //最終目的地データの更新。
            updateArrivalTravelDetail(manageId, temporalTravelDays.toInt() + 1)
            //1日目のロケーションをリセットする。
            travelDetailDepartureLocationReset(travel.manageId)
            setTravelModelAndLocation(travel)
            setTravelPartAndDetailModel(manageId, temporalTravelDays.toInt() + 1)
        }
        finish()
    }

    //TravelModelに値を格納
    private fun setTravelModelAndLocation(travel: Travel) {
        travel.title = edit1TravelTitle.text.toString()
        travel.travelDays = temporalTravelDays.toInt() + 1
        travel.departureDay = edit1DepartureDay.text.toString()
        travel.departurePlace = edit1DeparturePlace.text.toString()
        travel.departureLatitude = 0.0
        travel.departureLongitude = 0.0
        setLocation(travel, edit1DeparturePlace.text.toString(), 0)
        travel.arrivalDay = edit1ArrivalDay.text.toString()
        travel.arrivalPlace = edit1ArrivalPlace.text.toString()
        travel.arrivalLatitude = 0.0
        travel.arrivalLongitude = 0.0
        setLocation(travel, edit1ArrivalPlace.text.toString(), 1)
    }

    //1日目のロケーションをリセットする。
    private fun travelDetailDepartureLocationReset(manageId: Int) {
        val day = 1
        val order = 0
        val departureTravelDetail = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day)
            .equalTo("order", order)
            .findFirst()
        departureTravelDetail?.latitude = 0.0
        departureTravelDetail?.longitude = 0.0
    }

    //位置情報取得可かつ「現在地（Current Place）」なら位置情報取得
    private fun setLocation(travel: Travel, searchKey: String, identify: Int) {
        if (searchKey == "現在地" || searchKey == "Current Place") {
            val gpsAuthority = realm.where<Authority>().findFirst()
            if (gpsAuthority?.flag == true
                && (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)
            ) {
                fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful && task.result != null) {
                        val location = task.result
                        realm.executeTransaction {
                            if (identify == 0 && location is Location) {
                                travel.departureLatitude = location.latitude
                                travel.departureLongitude = location.longitude

                                val day = 1
                                val order = 0
                                val departureTravelDetail = realm.where<TravelDetail>()
                                    .equalTo("manageId", travel.manageId)
                                    .equalTo("day", day)
                                    .equalTo("order", order)
                                    .findFirst()
                                departureTravelDetail?.latitude = location.latitude
                                departureTravelDetail?.longitude = location.longitude
                                Log.i("【Edit1Travel1Activity】", "[setLocation] !!Success!!departure$location")

                            } else if (identify == 1 && location is Location) {
                                travel.arrivalLatitude = location.latitude
                                travel.arrivalLongitude = location.longitude

                                val order = 9
                                val arrivalTravelDetail = realm.where<TravelDetail>()
                                    .equalTo("manageId", travel.manageId)
                                    .equalTo("order", order)
                                    .findFirst()
                                arrivalTravelDetail?.latitude = location.latitude
                                arrivalTravelDetail?.longitude = location.longitude
                                Log.i("【Edit1Travel1Activity】", "[setLocation] !!Success!!arrival$location")
                            }
                        }
                    } else {
                        getUpdateLocation()
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUpdateLocation() {
        // とりあえず1回だけ取得
        val request = LocationRequest.create().apply {
            interval = 10000 // ms
            numUpdates = 1
        }
        fusedLocationClient.let { client ->
            client.requestLocationUpdates(request, locationCallback, null)
                .addOnCompleteListener { task ->
                    if (task.result == null) {
                        // 1回目で失敗した場合は必ずここを通ります。
                        // その後、コールバックがうまくいけばlocationCallbackのonLocationResultに入ります。
                    }
                }
        }
    }

    private val locationCallback: LocationCallback by lazy {
        (object: LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                super.onLocationResult(result)
                // 位置情報の更新が取得できた場合
                // result?.lastLocationのlatitudeとlongitudeで位置情報を取得
                Log.i("【Edit1Travel1Activity】", "[locationCallback] !!Success!!")
            }
        })
    }

    //travelDays変更によるTravelPartとTravelDetailの追加削除
    private fun setTravelPartAndDetailModel(manageId: Int, days: Int) {
        //日付が増えた場合
        if(days > 1){
            for (day in 2..days){
                val existPartForAdd = realm.where<TravelPart>()
                    .equalTo("manageId", manageId)
                    .equalTo("day", day)
                    .findFirst()

                if (existPartForAdd !is TravelPart) {
                    Log.i("【EditMainActivity】", "[setTravelPartAndDetailModel] existPartForAddが存在しない day:$day")
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

                existPartForDelete?.let{
                    Log.i("【EditMainActivity】", "[setTravelPartAndDetailModel] existPartForDeleteが存在 day:$day")
                    it.deleteFromRealm()
                }
                realm.where<TravelDetail>()
                    .equalTo("manageId", manageId)
                    .equalTo("day", day)
                    .findAll().deleteAllFromRealm()
            }
        }
    }

    //日付が増えた時に新しくTravelPartを作成する。
    private fun createNewTravelPart(manageId: Int, day: Int) {
        val maxPartId = realm.where<TravelPart>().max("id")
        val nextPartId = (maxPartId?.toInt() ?: 0) + 1
        val updatePart = realm.createObject<TravelPart>(nextPartId)
        updatePart.manageId = manageId
        updatePart.day = day
        updatePart.destination1 = resources.getString(R.string.pleaseEdit)
        updatePart.deleteFlag = 0
    }

    //日付が増えた時に新しくTravelDetail(departurePlace、destination1)を作成する。
    private fun createNewTravelDetail(manageId: Int, day: Int) {
        //出発地用TravelDetail作成　requireTimeは出発地なのでなし。
        val maxDetailIdForDeparture = realm.where<TravelDetail>().max("id")
        val nextDetailIdForDeparture = (maxDetailIdForDeparture?.toInt() ?: 0) + 1
        val newTravelDetailForDeparture = realm.createObject<TravelDetail>(nextDetailIdForDeparture)
        newTravelDetailForDeparture.manageId = manageId
        newTravelDetailForDeparture.day = day
        newTravelDetailForDeparture.order = 0
        newTravelDetailForDeparture.destination = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day - 1)
            .sort("order", Sort.DESCENDING)
            .findFirst()?.destination.toString()
        newTravelDetailForDeparture.startTime = realm.where<Travel>()
            .equalTo("manageId", manageId)
            .findFirst()?.arrivalTime.toString()
        newTravelDetailForDeparture.moveTime = resources.getString(R.string.moveTimeText) + "0 min "
        newTravelDetailForDeparture.deleteFlag = 0

        //destination1用TravelDetail作成
        val maxDetailIdForDestination = realm.where<TravelDetail>().max("id")
        val nextDetailIdForDestination = (maxDetailIdForDestination?.toInt() ?: 0) + 1
        val newTravelDetailForDestination = realm.createObject<TravelDetail>(nextDetailIdForDestination)
        newTravelDetailForDestination.manageId = manageId
        newTravelDetailForDestination.day = day
        newTravelDetailForDestination.order = 1
        newTravelDetailForDestination.destination = resources.getString(R.string.pleaseEdit)
        newTravelDetailForDestination.startTime = realm.where<Travel>()
            .equalTo("manageId", manageId)
            .findFirst()?.arrivalTime.toString()
        newTravelDetailForDestination.requiredTime = resources.getString(R.string.requireTimeText) + "0 min "
        newTravelDetailForDestination.moveTime = resources.getString(R.string.moveTimeText) + "0 min "
        newTravelDetailForDestination.deleteFlag = 0
    }

    //TravelDetailの最終目的地(order=9)のデータを更新する。
    private fun updateArrivalTravelDetail(manageId: Int, days: Int){
        val lastOrder = 9
        val updateArrivalTravelDetail = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("order", lastOrder)
            .findFirst() as TravelDetail
        updateArrivalTravelDetail.day = days
        updateArrivalTravelDetail.destination = findViewById<TextView>(R.id.edit1ArrivalPlace).text.toString()
        updateArrivalTravelDetail.startTime = realm.where<Travel>()
            .equalTo("manageId", manageId)
            .findFirst()?.arrivalTime.toString()
    }

    //日付を選択した後に呼ばれるメソッド。
    override fun onSelected(year: Int, month: Int, date: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, date)

        when(temporalyTag){
            departureDateTag -> {
                edit1DepartureDay.text = DateFormat.format("yyyy/MM/dd", calendar)
                edit1DepartureDay.background = doneColorChange()
            }
            arrivalDateTag -> {
                edit1ArrivalDay.text = DateFormat.format("yyyy/MM/dd", calendar)
                edit1ArrivalDay.background = doneColorChange()
            }
        }

        if (edit1DepartureDay.text != "" && edit1ArrivalDay.text != ""){
            when (val dateDiff = dateDiff(edit1DepartureDay.text.toString(), edit1ArrivalDay.text.toString())){
                in 0..6 -> setTravelDay(dateDiff)
                else -> {
                    alert(resources.getString(R.string.edit1TravelDays)) { yesButton {  } }.show()
                    edit1ArrivalDay.text = null
                    edit1ArrivalDay.background = falseColorChange()
                    edit1TravelDays.text = resources.getString(R.string.edit1TravelDays)
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
        val dayStayBuilder = StringBuilder()
        edit1TravelDays.text = when(Locale.getDefault()){
            Locale.JAPAN ->{
                dayStayBuilder.append(dateDiff)
                    .append("泊").append(dateDiff + 1)
                    .append("日").toString()
            }
            else -> {
                dayStayBuilder.append(dateDiff + 1)
                when(dateDiff + 1 > 1){
                    true -> dayStayBuilder.append(" days ").append(dateDiff)
                    false -> dayStayBuilder.append(" day ").append(dateDiff)
                }
                when(dateDiff > 1){
                    true -> dayStayBuilder.append(" nights")
                    false -> dayStayBuilder.append(" night")
                }
            }
        }
    }

    //データベースからデータをとってきてビューに表示する。
    private fun setData(manageId: Int) {
        val travel = realm.where<Travel>()
            .equalTo("manageId", manageId).findFirst()
       travel?.let{
           edit1TravelTitle.setText(it.title)
           edit1DepartureDay.text = it.departureDay
           edit1ArrivalDay.text = it.arrivalDay
           setTravelDay(it.travelDays.minus(1))
           edit1DeparturePlace.setText(it.departurePlace)
           edit1ArrivalPlace.setText(it.arrivalPlace)
           temporalTravelDays = it.travelDays.minus(1).toLong()
           preTravelDays = temporalTravelDays
        }
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

    //アクティビティ消滅時にrealmを閉じる。
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onStop() {
        super.onStop()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}