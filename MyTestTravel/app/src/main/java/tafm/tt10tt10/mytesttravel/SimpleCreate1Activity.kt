package tafm.tt10tt10.mytesttravel

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_simple_create1.*
import android.text.format.DateFormat
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.location.*
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.yesButton
import tafm.tt10tt10.mytesttravel.fragment.*
import tafm.tt10tt10.mytesttravel.model.Authority
import tafm.tt10tt10.mytesttravel.model.Travel
import tafm.tt10tt10.mytesttravel.model.TravelPart
import java.lang.StringBuilder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SimpleCreate1Activity : AppCompatActivity(),
    DatePickerFragment.OnDateSelectedListener {

    private lateinit var realm: Realm
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var temporalTravelDays: Long = 0L
    private lateinit var temporalyTag: String

    private val noAnyMoreAddTag = "noAnyMoreAddTag"
    private val noAnyMoreRemoveTag = "noAnyMoreRemoveTag"
    private val departureDateTag = "departureDateTag"
    private val arrivalDateTag = "arrivalDateTag"
    private val dateDiffTag = "dateDiffTag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_create1)

        setWhereToGoText()
        realm = Realm.getDefaultInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //departureDayをタップ
        sc1departureDay.setOnClickListener {
            val dialog = DatePickerFragment()
            dialog.show(supportFragmentManager, departureDateTag)
            temporalyTag = dialog.tag.toString()
        }

        //arrivalDayをタップ
        sc1ArrivalDay.setOnClickListener {
            val dialog = DatePickerFragment()
            dialog.show(supportFragmentManager, arrivalDateTag)
            temporalyTag = dialog.tag.toString()
        }

        setAddDeleteClickListener(R.id.includeSC1Day1)
        setAddDeleteClickListener(R.id.includeSC1Day2)
        setAddDeleteClickListener(R.id.includeSC1Day3)
        setAddDeleteClickListener(R.id.includeSC1Day4)
        setAddDeleteClickListener(R.id.includeSC1Day5)
        setAddDeleteClickListener(R.id.includeSC1Day6)
        setAddDeleteClickListener(R.id.includeSC1Day7)

        //「戻る」をタップ
        sc1backToMain.setOnClickListener { finish() }

        //「決定」をタップ
        sc1nextToSC2.setOnClickListener {
            if (checkInput()) checkSuccess()
        }
    }

    //Inputの内容をチェックする。
    private fun checkInput(): Boolean {
        var check = true
        if (sc1Title.text.isEmpty()) {
            check = false
            alert("タイトルを設定してください") { yesButton { } }.show()
        }
        if (check && (sc1departureDay.text.isEmpty() || sc1ArrivalDay.text.isEmpty())) {
            check = false
            alert("出発・到着日時を設定してください") { yesButton { } }.show()
        }
        if (check && (sc1departurePlace.text.isEmpty() || sc1ArrivalPlace.text.isEmpty())) {
            check = false
            alert("出発・到着地を設定してください") { yesButton { } }.show()
        }
        if (check) check = checkDestination(temporalTravelDays)
        return check
    }

    //目的地が空欄だったらエラーメッセージ
    private fun checkDestination(travelDays: Long): Boolean {
        var isSuccess = true
        for (day in 1..(travelDays + 1L)){
            when(day){
                1L -> isSuccess = checkDestinationExist(1, R.id.includeSC1Day1)
                2L -> isSuccess = checkDestinationExist(2, R.id.includeSC1Day2)
                3L -> isSuccess = checkDestinationExist(3, R.id.includeSC1Day3)
                4L -> isSuccess = checkDestinationExist(4, R.id.includeSC1Day4)
                5L -> isSuccess = checkDestinationExist(5, R.id.includeSC1Day5)
                6L -> isSuccess = checkDestinationExist(6, R.id.includeSC1Day6)
                7L -> isSuccess = checkDestinationExist(7, R.id.includeSC1Day7)
            }
            if (!isSuccess) return false
        }
        return true
    }

    //目的地入力チェック
    private fun checkDestinationExist(day: Int, include: Int): Boolean {
        val isExist1 = findViewById<View>(include).findViewById<TextView>(R.id.sc1destination1).text.isNotEmpty()
        val isExist2 = findViewById<View>(include).findViewById<TextView>(R.id.sc1destination2).text.isNotEmpty()
        val isExist3 = findViewById<View>(include).findViewById<TextView>(R.id.sc1destination3).text.isNotEmpty()
        val isExist4 = findViewById<View>(include).findViewById<TextView>(R.id.sc1destination4).text.isNotEmpty()
        val isExist5 = findViewById<View>(include).findViewById<TextView>(R.id.sc1destination5).text.isNotEmpty()
        val isExist6 = findViewById<View>(include).findViewById<TextView>(R.id.sc1destination6).text.isNotEmpty()
        if (!isExist1) {
            alert("Day $day :目的地1を設定してください") { yesButton { } }.show()
            return false
        }
        if (!isExist2 && isExist3) {
            alert("Day $day :目的地2を設定してください") { yesButton { } }.show()
            return false
        }
        if (!isExist3 && isExist4) {
            alert("Day $day :目的地3を設定してください") { yesButton { } }.show()
            return false
        }
        if (!isExist4 && isExist5) {
            alert("Day $day :目的地4を設定してください") { yesButton { } }.show()
            return false
        }
        if (!isExist5 && isExist6) {
            alert("Day $day :目的地5を設定してください") { yesButton { } }.show()
            return false
        }
        return true
    }

    //入力チェックが完了後、データベースに保存
    private fun checkSuccess() {
        realm.executeTransaction {

            val deleteFlag = 1
            realm.where<Travel>().equalTo("deleteFlag", deleteFlag).findAll().deleteAllFromRealm()
            realm.where<TravelPart>().equalTo("deleteFlag", deleteFlag).findAll().deleteAllFromRealm()

            val maxTravelId = realm.where<Travel>().max("manageId")
            val newManegeId = (maxTravelId?.toInt() ?: 0) + 1
            val travel = realm.createObject<Travel>(newManegeId)
            setTravelModel(travel)

            for (day in 1..(temporalTravelDays + 1)){
                val maxPartId = realm.where<TravelPart>().max("id")
                val nextPartId = (maxPartId?.toInt() ?: 0) + 1
                val part = realm.createObject<TravelPart>(nextPartId)
                when(day){
                    1L -> setTravelPartModel(part, newManegeId, 1, R.id.includeSC1Day1)
                    2L -> setTravelPartModel(part, newManegeId, 2, R.id.includeSC1Day2)
                    3L -> setTravelPartModel(part, newManegeId, 3, R.id.includeSC1Day3)
                    4L -> setTravelPartModel(part, newManegeId, 4, R.id.includeSC1Day4)
                    5L -> setTravelPartModel(part, newManegeId, 5, R.id.includeSC1Day5)
                    6L -> setTravelPartModel(part, newManegeId, 6, R.id.includeSC1Day6)
                    7L -> setTravelPartModel(part, newManegeId, 7, R.id.includeSC1Day7)
                }
            }

            startActivity<SimpleCreate2Activity>(
                "MANAGE_ID" to newManegeId
                , "TRAVEL_DAYS" to temporalTravelDays + 1
                , "DEPARTURE_PLACE" to sc1departurePlace.text.toString()
                , "ARRIVAL_PLACE" to sc1ArrivalPlace.text.toString())
        }
    }

    //日付ダイアログで選択された後に呼ばれるメソッド
    override fun onSelected(year: Int, month: Int, date: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, date)
        val builder = StringBuilder()

        when(temporalyTag){
            departureDateTag -> {
                sc1departureDay.text = DateFormat.format("yyyy/MM/dd", calendar)
                sc1departureDay.background = doneColorChange()
                sc1departureText.text = builder.append("・").append(sc1departureDay.text).append("出発地点は？")
            }
            arrivalDateTag -> {
                sc1ArrivalDay.text = DateFormat.format("yyyy/MM/dd", calendar)
                sc1ArrivalDay.background = doneColorChange()
                sc1arrivalText.text = builder.append("・").append(sc1ArrivalDay.text).append("到着地点は？")
            }
        }

        if (sc1departureDay.text != "" && sc1ArrivalDay.text != ""){
            when (val dateDiff = dateDiff(sc1departureDay.text.toString(), sc1ArrivalDay.text.toString())){
                in 0..6 -> setTravelDay(dateDiff)
                else -> {
                    val dialog = DateDiffAlertDialogs()
                    dialog.show(supportFragmentManager, dateDiffTag)
                    sc1ArrivalDay.text = null
                    sc1ArrivalDay.background = falseColorChange()
                    sc1arrivalText.text ="・到着地点は？"
                    sc1travelDays.text = "日付を選択してください。"
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

    //何泊何日の出力メソッド + 表示非表示
    private fun setTravelDay(dateDiff: Int) {
        val dateDiffMap:MutableMap<Int, String> = mutableMapOf()
        for (i in 0..6) dateDiffMap[i] = (i.toString() + "泊" + (i+1) + "日")
        sc1travelDays.text = dateDiffMap[dateDiff]

        if(dateDiff > 5)  {
            changeInclude(R.id.includeSC1Day7, View.VISIBLE)
        }else{
            changeInclude(R.id.includeSC1Day7, View.GONE)
        }
        if(dateDiff > 4) {
            changeInclude(R.id.includeSC1Day6, View.VISIBLE)
        }else{
            changeInclude(R.id.includeSC1Day6, View.GONE)
        }
        if(dateDiff > 3) {
            changeInclude(R.id.includeSC1Day5, View.VISIBLE)
        }else{
            changeInclude(R.id.includeSC1Day5, View.GONE)
        }
        if(dateDiff > 2) {
            changeInclude(R.id.includeSC1Day4, View.VISIBLE)
        }else{
            changeInclude(R.id.includeSC1Day4, View.GONE)
        }
        if(dateDiff > 1) {
            changeInclude(R.id.includeSC1Day3, View.VISIBLE)
        }else{
            changeInclude(R.id.includeSC1Day3, View.GONE)
        }
        if(dateDiff > 0) {
            changeInclude(R.id.includeSC1Day2, View.VISIBLE)
        }else{
            changeInclude(R.id.includeSC1Day2, View.GONE)
        }
    }

    private fun changeInclude(include: Int, visibility: Int) {
        findViewById<View>(include).visibility = visibility
    }

    //目的地追加メソッド
    private fun addDestination(second:EditText,third:EditText,forth:EditText,fifth:EditText,six:EditText){
        if (six.visibility == View.VISIBLE){
            val dialog = AddAlertDialogs()
            dialog.show(supportFragmentManager, noAnyMoreAddTag)
        }
        if (six.visibility != View.VISIBLE && fifth.visibility == View.VISIBLE) six.visibility = View.VISIBLE
        if (fifth.visibility != View.VISIBLE && forth.visibility == View.VISIBLE) fifth.visibility = View.VISIBLE
        if (forth.visibility != View.VISIBLE && third.visibility == View.VISIBLE) forth.visibility = View.VISIBLE
        if (third.visibility != View.VISIBLE && second.visibility == View.VISIBLE) third.visibility = View.VISIBLE
        if (second.visibility != View.VISIBLE) second.visibility = View.VISIBLE
    }

    //目的地削除メソッド
    private fun removeDestination(second:EditText,third:EditText,forth:EditText,fifth:EditText,six:EditText){
        if (second.visibility != View.VISIBLE){
            val dialog = RemoveAlertDialogs()
            dialog.show(supportFragmentManager, noAnyMoreRemoveTag)
        }
        if (second.visibility == View.VISIBLE && third.visibility != View.VISIBLE) setProperty(second)
        if (third.visibility == View.VISIBLE && forth.visibility != View.VISIBLE) setProperty(third)
        if (forth.visibility == View.VISIBLE && fifth.visibility != View.VISIBLE) setProperty(forth)
        if (fifth.visibility == View.VISIBLE && six.visibility != View.VISIBLE) setProperty(fifth)
        if (six.visibility == View.VISIBLE) setProperty(six)
    }

    //目的地削除時のvisibilityとtextの設定
    private fun setProperty(editText: EditText){
        editText.visibility = View.GONE
        editText.text = null
    }

    //TravelModelに値を格納
    private fun setTravelModel(travel: Travel) {
        travel.title = sc1Title.text.toString()
        travel.travelDays = temporalTravelDays.toInt() + 1
        travel.departureDay = sc1departureDay.text.toString()
        travel.departurePlace = sc1departurePlace.text.toString()
        setLocation(travel, sc1departurePlace.text.toString(), 0)
        travel.arrivalDay = sc1ArrivalDay.text.toString()
        travel.arrivalPlace = sc1ArrivalPlace.text.toString()
        setLocation(travel, sc1ArrivalPlace.text.toString(), 1)
    }

    //位置情報取得可かつ「現在地（Current Place）」なら位置情報取得
    private fun setLocation(travel: Travel, searchKey: String, identify: Int) {
        if (searchKey == "現在地" || searchKey == "Current Place"){
            val gpsAuthority = realm.where<Authority>().findFirst()
            if (gpsAuthority?.flag == true
                && (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)){
                fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful && task.result != null) {
                        val location = task.result
                        realm.executeTransaction {
                            if (identify == 0 && location is Location){
                                travel.departureLatitude = location.latitude
                                travel.departureLongitude = location.longitude
                                Log.i("【SimpleCreate1Activity】", "[setLocation] !!Success!!departure$location")
                            }else if (identify == 1 && location is Location){
                                travel.arrivalLatitude = location.latitude
                                travel.arrivalLongitude = location.longitude
                                Log.i("【SimpleCreate1Activity】", "[setLocation] !!Success!!arrival$location")
                            }
                        }
                    }else{
                        getUpdateLocation()
                    }
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
                Log.i("【SimpleCreate1Activity】", "[locationCallback] !!Success!!")
            }
        })
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

    //TravelPartModelに値を格納
    private fun setTravelPartModel(part: TravelPart, newManegeId: Int, day: Int, include: Int) {
        part.manageId = newManegeId
        part.day = day
        part.destination1 = findViewById<View>(include).findViewById<TextView>(R.id.sc1destination1).text.toString()
        part.destination2 = findViewById<View>(include).findViewById<TextView>(R.id.sc1destination2).text.toString()
        part.destination3 = findViewById<View>(include).findViewById<TextView>(R.id.sc1destination3).text.toString()
        part.destination4 = findViewById<View>(include).findViewById<TextView>(R.id.sc1destination4).text.toString()
        part.destination5 = findViewById<View>(include).findViewById<TextView>(R.id.sc1destination5).text.toString()
        part.destination6 = findViewById<View>(include).findViewById<TextView>(R.id.sc1destination6).text.toString()
    }

    //何日にどこに行くテキストをつくる。
    private fun setWhereToGoText() {
        findViewById<View>(R.id.includeSC1Day1).findViewById<TextView>(R.id.sc1whereToGoText).text = "・1日目どこに行く？"
        findViewById<View>(R.id.includeSC1Day2).findViewById<TextView>(R.id.sc1whereToGoText).text = "・2日目どこに行く？"
        findViewById<View>(R.id.includeSC1Day3).findViewById<TextView>(R.id.sc1whereToGoText).text = "・3日目どこに行く？"
        findViewById<View>(R.id.includeSC1Day4).findViewById<TextView>(R.id.sc1whereToGoText).text = "・4日目どこに行く？"
        findViewById<View>(R.id.includeSC1Day5).findViewById<TextView>(R.id.sc1whereToGoText).text = "・5日目どこに行く？"
        findViewById<View>(R.id.includeSC1Day6).findViewById<TextView>(R.id.sc1whereToGoText).text = "・6日目どこに行く？"
        findViewById<View>(R.id.includeSC1Day7).findViewById<TextView>(R.id.sc1whereToGoText).text = "・7日目どこに行く？"
    }

    //「追加」ボタンタップ「削除」ボタンタップ
    private fun setAddDeleteClickListener(include: Int) {
        findViewById<View>(include).findViewById<Button>(R.id.sc1addDestination).setOnClickListener {
            addDestination(findViewById<View>(include).findViewById(R.id.sc1destination2)
                , findViewById<View>(include).findViewById(R.id.sc1destination3)
                , findViewById<View>(include).findViewById(R.id.sc1destination4)
                , findViewById<View>(include).findViewById(R.id.sc1destination5)
                , findViewById<View>(include).findViewById(R.id.sc1destination6))
        }
        findViewById<View>(include).findViewById<Button>(R.id.sc1removeDestination).setOnClickListener {
            removeDestination(findViewById<View>(include).findViewById(R.id.sc1destination2)
                , findViewById<View>(include).findViewById(R.id.sc1destination3)
                , findViewById<View>(include).findViewById(R.id.sc1destination4)
                , findViewById<View>(include).findViewById(R.id.sc1destination5)
                , findViewById<View>(include).findViewById(R.id.sc1destination6))
        }
    }

    //アクティビティを離れるとき、fusedLocationClientを閉じる。
    override fun onStop() {
        super.onStop()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    //アクティビティが削除されたときにRealmを閉じる
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
