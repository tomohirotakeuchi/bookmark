package tafm.tt10tt10.mytesttravel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit2_travel.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
import tafm.tt10tt10.mytesttravel.model.Travel
import tafm.tt10tt10.mytesttravel.model.TravelDetail
import tafm.tt10tt10.mytesttravel.model.TravelPart
import java.lang.StringBuilder

class EditBookmark1Activity : AppCompatActivity() {

    private lateinit var realm: Realm
    private lateinit var view: View
    private var diffCostForTravel = 0
    private val requireTimeText = "所要: ";   private val moveTimeText = "移動: "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit2_travel)

        view = findViewById(R.id.includeEdit2)
        realm = Realm.getDefaultInstance()
        val manageId = intent.getIntExtra("manageId", -1)
        val day = intent.getIntExtra("day", -1)
        val travelDays = intent.getIntExtra("travelDays", -1)
        val travelPart = realm.where<TravelPart>()
            .equalTo("manageId", manageId)
            .equalTo("day", day)
            .findFirst()

        setGuideText(travelPart)
        setVisibility()
        setData(travelPart)

        //戻るボタン押下
        edit2BackToBm1Btn.setOnClickListener {
            it.notPressTwice()
            finish()
        }

        //更新ボタン押下
        edit2UpdateBtn.setOnClickListener {
            it.notPressTwice()
            if (checkDestinationExist()){
                checkSuccess(manageId, day, travelDays, travelPart)
            }
        }
    }

    //何日目に行くかのテキスト
    private fun setGuideText(travelPart: TravelPart?) {
        travelPart?.let {
            val guideBuilder = StringBuilder("Where to Go on Day ")
            view.findViewById<TextView>(R.id.sc1whereToGoText).text = guideBuilder.append(it.day).toString()
        }
    }

    //目的地入力チェック
    private fun checkDestinationExist(): Boolean {
        val isExist1 = view.findViewById<TextView>(R.id.sc1destination1).text.isNotEmpty()
        val isExist2 = view.findViewById<TextView>(R.id.sc1destination2).text.isNotEmpty()
        val isExist3 = view.findViewById<TextView>(R.id.sc1destination3).text.isNotEmpty()
        val isExist4 = view.findViewById<TextView>(R.id.sc1destination4).text.isNotEmpty()
        val isExist5 = view.findViewById<TextView>(R.id.sc1destination5).text.isNotEmpty()
        val isExist6 = view.findViewById<TextView>(R.id.sc1destination6).text.isNotEmpty()
        if (!isExist1) {
            alert("目的地1を設定してください") { yesButton { } }.show()
            return false
        }
        if (!isExist2 && isExist3) {
            alert("目的地2を設定してください") { yesButton { } }.show()
            return false
        }
        if (!isExist3 && isExist4) {
            alert("目的地3を設定してください") { yesButton { } }.show()
            return false
        }
        if (!isExist4 && isExist5) {
            alert("目的地4を設定してください") { yesButton { } }.show()
            return false
        }
        if (!isExist5 && isExist6) {
            alert("目的地5を設定してください") { yesButton { } }.show()
            return false
        }
        return true
    }

    //バリデーションチェック成功後、データベースに値をセットする。
    private fun checkSuccess(manageId: Int, day: Int, travelDays: Int, travelPart: TravelPart?) {
        realm.executeTransaction {
            setTravelPart(travelPart)
            setTravelDetail(manageId, day, travelDays)
            when(travelDays) {
                day -> setLastStartDestination(manageId, day)
                else -> setNextStartDestination(manageId, day)
            }
            setCostTravel(manageId)
        }
        finish()
    }

    //削除した分の費用を総費用（Travel）から削除する。
    private fun setCostTravel(manageId: Int) {
        val travel = realm.where<Travel>()
            .equalTo("manageId", manageId)
            .findFirst() as Travel
        travel.totalCost -= diffCostForTravel
    }

    //TravelDetailの更新メソッド。
    private fun setTravelDetail(manageId: Int, day: Int, travelDays: Int) {

        var existLastStartTime = "00:00"

        for(order in 1..6){
            val existTravelDetail = realm.where<TravelDetail>()
                .equalTo("manageId", manageId)
                .equalTo("day", day)
                .equalTo("order", order)
                .findFirst()

            //すでにTravelDetailが存在する場合
            if(existTravelDetail is TravelDetail){
                Log.i("【EditBookmark1Activity】","[setTravelDetail] existTravelDetailが存在 $existTravelDetail")
                //目的地入力があり、内容に変更があれば更新。ここで最後のstartTimeを上書き。
                if(checkTextExist(order)) {
                    existLastStartTime = existTravelDetail.startTime
                    if (getDestinationByOrder(order) != existTravelDetail.destination){
                        Log.i("【EditBookmark1Activity】","[setTravelDetail] 目的地変更 order$order")
                        existTravelDetail.destination = getDestinationByOrder(order)
                        existTravelDetail.latitude = 0.0
                        existTravelDetail.longitude = 0.0
                    }
                }else {
                //目的地入力がない場合、totalCostを一次退避し存在するものを削除。
                    Log.i("【EditBookmark1Activity】","[setTravelDetail] 目的地入力がないため削除 order$order")
                    diffCostForTravel += existTravelDetail.totalCost
                    existTravelDetail.deleteFromRealm()
                }
                //TravelDetailが存在しない場合。
            }else {
                //目的地入力があれば新しく作成。
                Log.i("【EditBookmark1Activity】","[setTravelDetail] existTravelDetailが存在しない order$order")
                if(checkTextExist(order)) {
                    Log.i("【EditBookmark1Activity】","[setTravelDetail] 目的地入力あり order$order")
                    //最終日なら到着地の時間を設定。
                    if (day == travelDays){
                        Log.i("【EditBookmark1Activity】"
                            ,"[setTravelDetail] day$day == travelDays$travelDays 到着時間を入力 order$order")
                        val arrivalTime = realm.where<Travel>()
                            .equalTo("manageId", manageId)
                            .findFirst()?.arrivalTime
                            createNewTravelDetail(manageId, day, order, arrivalTime)
                    }else{
                        //最終日でないなら、その日の最後の目的地のstartTime + requireTime + moveTimeを設定。
                        Log.i("【EditBookmark1Activity】"
                            ,"[setTravelDetail] day$day != travelDays$travelDays 最後のstartTime入力 order$order")
                        createNewTravelDetail(manageId, day, order, existLastStartTime)
                    }
                }
                //目的地入力がなければスルー
            }
        }
    }

    //最終日でなければ、次の日の出発地を前日の最後の目的地に変更する。
    //その日の最終目的地のデータの所要、移動を0にする。
    private fun setNextStartDestination(manageId: Int, day: Int) {
        val startOrder = 0
        val nextTravelDetail = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day + 1)
            .equalTo("order", startOrder)
            .findFirst()
        val preTravelDetail = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day)
            .sort("order", Sort.DESCENDING)
            .findFirst()
        if (nextTravelDetail is TravelDetail && preTravelDetail is TravelDetail){
            preTravelDetail.requiredTime = requireTimeText + "0 min "
            preTravelDetail.moveTime = moveTimeText + "0 min "
            nextTravelDetail.destination = preTravelDetail.destination
            nextTravelDetail.latitude = 0.0
            nextTravelDetail.longitude = 0.0
            Log.i("【EditBookmark1Activity】","[setNextStartDestination] 次の日の出発地を変更 $nextTravelDetail")
        }
    }

    //最終日ならば、到着地とTravelの到着時間を更新。
    private fun setLastStartDestination(manageId: Int, day: Int) {
        val arrivalOrder = 9
        val preTravelDetail = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day)
            .between("order", 1, 6)
            .sort("order", Sort.DESCENDING)
            .findFirst()
        val endTravelDetail = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day)
            .equalTo("order", arrivalOrder)
            .findFirst()
        val travel = realm.where<Travel>()
            .equalTo("manageId", manageId)
            .findFirst()
        preTravelDetail?.let {
            val arrivalTime = getArrivalTime(it.startTime
                , it.requiredTime, it.moveTime)
            endTravelDetail?.startTime = arrivalTime
            travel?.arrivalTime = arrivalTime
        }
    }

    //ArrivalTimeを前の目的地の時間から決定する。
    private fun getArrivalTime(startTime: String, requiredTime: String, moveTime: String): String {
        val startTimeArr = startTime.split(":")
        val startTimeMinute = startTimeArr[0].toLong() * 60 + startTimeArr[1].toLong()
        var requiredTimeMinute = 0L
        var moveTimeMinute = 0L
        if (requiredTime.isNotEmpty()) requiredTimeMinute = extractData(requiredTime, requireTimeText.length)
        if (moveTime.isNotEmpty()) moveTimeMinute = extractData(moveTime, moveTimeText.length)
        val total = startTimeMinute + requiredTimeMinute + moveTimeMinute
        val timeFormat = "%1$02d:%2$02d"
        return timeFormat.format(total / 60, total % 60)
    }

    //所要時間、移動時間を分解
    private fun extractData(timeString: String, deleteNum: Int): Long {
        val extractData = StringBuilder(timeString)
            .delete(timeString.length - 5, timeString.length - 1)
            .delete(0, deleteNum-1)
        val rArr = extractData.split("h")
        return if(rArr.size == 2) {
            rArr[0].trim().toLong() * 60 + rArr[1].trim().toLong()
        }else {
            rArr[0].trim().toLong()
        }
    }

    //TravelDetailが存在せず、目的地入力があれば、あたらしくTravelDetailを作成する。
    private fun createNewTravelDetail(manageId: Int, day: Int, order: Int, lastStartTime: String?) {
        val maxDetailId = realm.where<TravelDetail>().max("id")
        val nextDetailId = (maxDetailId?.toInt() ?: 0) + 1
        val newTravelDetail = realm.createObject<TravelDetail>(nextDetailId)
        newTravelDetail.manageId = manageId
        newTravelDetail.day = day
        newTravelDetail.order = order
        newTravelDetail.destination = getDestinationByOrder(order)
        newTravelDetail.startTime = lastStartTime.toString()
        newTravelDetail.requiredTime = requireTimeText + "0 min "
        newTravelDetail.moveTime = moveTimeText + "0 min "
        newTravelDetail.deleteFlag = 0
    }

    //表示順から該当する目的地を返却。
    private fun getDestinationByOrder(order: Int): String {
        return when(order){
            1 -> view.findViewById<TextView>(R.id.sc1destination1).text.toString()
            2 -> view.findViewById<TextView>(R.id.sc1destination2).text.toString()
            3 -> view.findViewById<TextView>(R.id.sc1destination3).text.toString()
            4 -> view.findViewById<TextView>(R.id.sc1destination4).text.toString()
            5 -> view.findViewById<TextView>(R.id.sc1destination5).text.toString()
            6 -> view.findViewById<TextView>(R.id.sc1destination6).text.toString()
            else -> "Please Edit"
        }
    }

    //入力した目的地がEmptyでないかをBooleanで返却する。
    private fun checkTextExist(order: Int): Boolean {
        return when(order){
            1 -> view.findViewById<TextView>(R.id.sc1destination1).text.isNotEmpty()
            2 -> view.findViewById<TextView>(R.id.sc1destination2).text.isNotEmpty()
            3 -> view.findViewById<TextView>(R.id.sc1destination3).text.isNotEmpty()
            4 -> view.findViewById<TextView>(R.id.sc1destination4).text.isNotEmpty()
            5 -> view.findViewById<TextView>(R.id.sc1destination5).text.isNotEmpty()
            6 -> view.findViewById<TextView>(R.id.sc1destination6).text.isNotEmpty()
            else -> false
        }
    }

    //TravelPartを編集したものをデータベースにセット。
    private fun setTravelPart(travelPart: TravelPart?) {
        travelPart?.destination1 = view.findViewById<TextView>(R.id.sc1destination1).text.toString()
        travelPart?.destination2 = view.findViewById<TextView>(R.id.sc1destination2).text.toString()
        travelPart?.destination3 = view.findViewById<TextView>(R.id.sc1destination3).text.toString()
        travelPart?.destination4 = view.findViewById<TextView>(R.id.sc1destination4).text.toString()
        travelPart?.destination5 = view.findViewById<TextView>(R.id.sc1destination5).text.toString()
        travelPart?.destination6 = view.findViewById<TextView>(R.id.sc1destination6).text.toString()
    }

    //EditViewにデータベースの値をセットする。
    private fun setData(travelPart: TravelPart?) {
        view.findViewById<TextView>(R.id.sc1destination1).text = travelPart?.destination1
        view.findViewById<TextView>(R.id.sc1destination2).text = travelPart?.destination2
        view.findViewById<TextView>(R.id.sc1destination3).text = travelPart?.destination3
        view.findViewById<TextView>(R.id.sc1destination4).text = travelPart?.destination4
        view.findViewById<TextView>(R.id.sc1destination5).text = travelPart?.destination5
        view.findViewById<TextView>(R.id.sc1destination6).text = travelPart?.destination6
    }

    //visibilityを設定する。すべて表示。ボタンは非表示。
    private fun setVisibility() {
        view.findViewById<TextView>(R.id.sc1destination3).visibility = View.VISIBLE
        view.findViewById<TextView>(R.id.sc1destination4).visibility = View.VISIBLE
        view.findViewById<TextView>(R.id.sc1destination5).visibility = View.VISIBLE
        view.findViewById<TextView>(R.id.sc1destination6).visibility = View.VISIBLE
        view.findViewById<TextView>(R.id.sc1addDestination).visibility = View.GONE
        view.findViewById<TextView>(R.id.sc1removeDestination).visibility = View.GONE
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
}
