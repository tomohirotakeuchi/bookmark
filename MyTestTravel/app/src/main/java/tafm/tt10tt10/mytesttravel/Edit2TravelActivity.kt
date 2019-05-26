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

class Edit2TravelActivity : AppCompatActivity() {

    private lateinit var realm: Realm
    private val requireTimeText = "所要: ";   private val moveTimeText = "移動: "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit2_travel)

        realm = Realm.getDefaultInstance()
        val manageId = intent.getIntExtra("manageId", -1)
        val day = intent.getIntExtra("day", -1)
        val travelDays = intent.getIntExtra("travelDays", -1)
        val travelPart = realm.where<TravelPart>()
            .equalTo("manageId", manageId)
            .equalTo("day", day)
            .findFirst()

        setVisibility()
        setData(travelPart)

        //戻るボタン押下
        edit2BackToBm1Btn.setOnClickListener {
            finish()
        }

        //更新ボタン押下
        edit2UpdateBtn.setOnClickListener {
            if(findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination1).text.isNotEmpty()) {
                checkSuccess(manageId, day, travelDays, travelPart)
            }else {
                alert("目的地1を設定してください") { yesButton { } }.show()
            }
        }
    }

    //バリデーションチェック成功後、データベースに値をセットする。
    private fun checkSuccess(manageId: Int, day: Int, travelDays: Int, travelPart: TravelPart?) {
        realm.executeTransaction {
            setTravelPart(travelPart)
            setTravelDetail(manageId, day, travelDays)
            if(travelDays > day) setNextStartDestination(manageId, day)
        }
        finish()
    }

    //TravelDetailの更新メソッド。
    private fun setTravelDetail(manageId: Int, day: Int, travelDays: Int) {

        var existLastStartTime = "00:00"
        val arrivalTime = realm.where<Travel>()
            .equalTo("manageId", manageId)
            .findFirst()?.arrivalTime

        for(order in 1..6){
            val existTravelDetail = realm.where<TravelDetail>()
                .equalTo("manageId", manageId)
                .equalTo("day", day)
                .equalTo("order", order)
                .findFirst()

            //すでにTravelDetailが存在する場合
            if(existTravelDetail is TravelDetail){
                Log.i("【Edit2TravelActivity】","[setTravelDetail] existTravelDetailが存在 $existTravelDetail")
                //目的地入力があれば更新。
                if(checkText(order)) {
                    Log.i("【Edit2TravelActivity】","[setTravelDetail] 目的地入力があるため更新 order$order")
                    existLastStartTime = existTravelDetail.startTime
                    existTravelDetail.destination = setDestinationByOrder(order)
                }else {
                //目的地入力がない場合、存在するものを削除。
                    Log.i("【Edit2TravelActivity】","[setTravelDetail] 目的地入力がないため削除 order$order")
                    existTravelDetail.deleteFromRealm()
                }
                //TravelDetailが存在しない場合。
            }else {
                //目的地入力があれば新しく作成。なければスルー。
                Log.i("【Edit2TravelActivity】","[setTravelDetail] existTravelDetailが存在しない order$order")
                if(checkText(order)) {
                    Log.i("【Edit2TravelActivity】","[setTravelDetail] 目的地入力あり order$order")
                    //最終日なら到着地の時間。
                    if (day == travelDays){
                        Log.i("【Edit2TravelActivity】","[setTravelDetail] day$day == travelDays$travelDays 到着時間を入力 order$order")
                        createNewTravelDetail(manageId, day, order, arrivalTime)
                    }else{
                        //最終日でないなら、その日の最後の目的地のstartTime
                        Log.i("【Edit2TravelActivity】","[setTravelDetail] day$day != travelDays$travelDays 最後のstartTimeを入力 order$order")
                        createNewTravelDetail(manageId, day, order, existLastStartTime)
                    }
                }
            }
        }
    }

    //最終日でなければ、次の日の出発地を変更する。
    private fun setNextStartDestination(manageId: Int, day: Int) {
        val startOrder = 0
        val nextTravelDetail = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day + 1)
            .equalTo("order", startOrder)
            .findFirst()
        nextTravelDetail?.destination = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day)
            .sort("order", Sort.DESCENDING)
            .findFirst()?.destination.toString()
        Log.i("【Edit2TravelActivity】","[setNextStartDestination] 次の日の出発地を変更 $nextTravelDetail")
    }

    //TravelDetailが存在せず、目的地入力があれば、あたらしくTravelDetailを作成する。
    private fun createNewTravelDetail(manageId: Int, day: Int, order: Int, lastStartTime: String?) {
        val maxDetailId = realm.where<TravelDetail>().max("id")
        val nextDetailId = (maxDetailId?.toInt() ?: 0) + 1
        val newTravelDetail = realm.createObject<TravelDetail>(nextDetailId)
        newTravelDetail.manageId = manageId
        newTravelDetail.day = day
        newTravelDetail.order = order
        newTravelDetail.destination = setDestinationByOrder(order)
        newTravelDetail.startTime = lastStartTime.toString()
        newTravelDetail.requiredTime = requireTimeText + "0 min "
        newTravelDetail.moveTime = moveTimeText + "0 min "
        newTravelDetail.deleteFlag = 0
    }

    //表示順から該当する目的地を返却。
    private fun setDestinationByOrder(order: Int): String {
        return when(order){
            1 -> findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination1).text.toString()
            2 -> findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination2).text.toString()
            3 -> findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination3).text.toString()
            4 -> findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination4).text.toString()
            5 -> findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination5).text.toString()
            6 -> findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination6).text.toString()
            else -> "Please Edit"
        }
    }

    //入力した目的地がEmptyでないかをBooleanで返却する。
    private fun checkText(order: Int): Boolean {
        return when(order){
            1 -> findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination1).text.isNotEmpty()
            2 -> findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination2).text.isNotEmpty()
            3 -> findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination3).text.isNotEmpty()
            4 -> findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination4).text.isNotEmpty()
            5 -> findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination5).text.isNotEmpty()
            6 -> findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination6).text.isNotEmpty()
            else -> false
        }
    }

    //TravelPartを編集したものをデータベースにセット。
    private fun setTravelPart(travelPart: TravelPart?) {
        travelPart?.destination1 = findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination1).text.toString()
        travelPart?.destination2 = findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination2).text.toString()
        travelPart?.destination3 = findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination3).text.toString()
        travelPart?.destination4 = findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination4).text.toString()
        travelPart?.destination5 = findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination5).text.toString()
        travelPart?.destination6 = findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination6).text.toString()
    }

    //EditViewにデータベースの値をセットする。
    private fun setData(travelPart: TravelPart?) {
        findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination1).text = travelPart?.destination1
        findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination2).text = travelPart?.destination2
        findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination3).text = travelPart?.destination3
        findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination4).text = travelPart?.destination4
        findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination5).text = travelPart?.destination5
        findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination6).text = travelPart?.destination6
    }

    //visibilityを設定する。すべて表示。ボタンは非表示。
    private fun setVisibility() {
        findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination3).visibility = View.VISIBLE
        findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination4).visibility = View.VISIBLE
        findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination5).visibility = View.VISIBLE
        findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1destination6).visibility = View.VISIBLE
        findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1addDestination).visibility = View.GONE
        findViewById<View>(R.id.includeEdit2).findViewById<TextView>(R.id.sc1removeDestination).visibility = View.GONE
    }

    //アクティビティ消滅時にrealmを閉じる。
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
