package tafm.tt10tt10.mytesttravel

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.format.DateFormat
import android.util.Log
import android.widget.TextView
import com.google.android.gms.location.*
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit1_travel.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
import tafm.tt10tt10.mytesttravel.fragment.DateDiffAlertDialogs
import tafm.tt10tt10.mytesttravel.fragment.DatePickerFragment
import tafm.tt10tt10.mytesttravel.model.GpsAuthority
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
    private lateinit var fusedLocationClient: FusedLocationProviderClient

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

            //既存の最終目的地データの削除。
            deleteArrivalTravelDetail(manageId)
            //新しい最終目的地データの作成。
            createNewArrivalTravelDetail(manageId, temporalTravelDays.toInt() + 1)
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
            val gpsAuthority = realm.where<GpsAuthority>().findFirst()
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
        newTravelDetailForDeparture.moveTime = moveTimeText + "0 min "
        newTravelDetailForDeparture.deleteFlag = 0

        //destination1用TravelDetail作成
        val maxDetailIdForDestination = realm.where<TravelDetail>().max("id")
        val nextDetailIdForDestination = (maxDetailIdForDestination?.toInt() ?: 0) + 1
        val newTravelDetailForDestination = realm.createObject<TravelDetail>(nextDetailIdForDestination)
        newTravelDetailForDestination.manageId = manageId
        newTravelDetailForDestination.day = day
        newTravelDetailForDestination.order = 1
        newTravelDetailForDestination.destination = "Please Edit"
        newTravelDetailForDestination.startTime = realm.where<Travel>()
            .equalTo("manageId", manageId)
            .findFirst()?.arrivalTime.toString()
        newTravelDetailForDestination.requiredTime = requireTimeText + "0 min "
        newTravelDetailForDestination.moveTime = moveTimeText + "0 min "
        newTravelDetailForDestination.deleteFlag = 0
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
                edit1DepartureText.text = builder.append("・").append(edit1DepartureDay.text).append("出発地点は？")
            }
            arrivalDateTag -> {
                edit1ArrivalDay.text = DateFormat.format("yyyy/MM/dd", calendar)
                edit1ArrivalDay.background = doneColorChange()
                edit1ArrivalText.text = builder.append("・").append(edit1ArrivalDay.text).append("到着地点は？")
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
                    edit1ArrivalText.text ="・到着地点は？"
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
        val travel = realm.where<Travel>()
            .equalTo("manageId", manageId).findFirst()
        if (travel is Travel){
            edit1TravelTitle.setText(travel.title)
            edit1DepartureDay.text = travel.departureDay
            edit1ArrivalDay.text = travel.arrivalDay
            val daysBuilder = StringBuilder()
                .append(travel.travelDays.minus(1)).append("泊").append(travel.travelDays).append("日")
            edit1TravelDays.text = daysBuilder.toString()
            edit1DeparturePlace.setText(travel.departurePlace)
            edit1ArrivalPlace.setText(travel.arrivalPlace)
            temporalTravelDays = travel.travelDays.minus(1).toLong()
        }
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
