package tafm.tt10tt10.mytesttravel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_bookmark1.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.yesButton
import tafm.tt10tt10.mytesttravel.adapter.Bookmark1Adapter
import tafm.tt10tt10.mytesttravel.model.Travel
import tafm.tt10tt10.mytesttravel.model.TravelDetail
import tafm.tt10tt10.mytesttravel.model.TravelPart
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Bookmark1Activity : AppCompatActivity() {

    private lateinit var realm: Realm
    private lateinit var adapter: Bookmark1Adapter
    private var travelDays: Int = 1
    private var diffCostForTravel = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark1)

        realm = Realm.getDefaultInstance()
        val manageId = intent.getIntExtra("manageId", -1)
        val travelParts = realm.where<TravelPart>()
            .equalTo("manageId", manageId)
            .sort("day", Sort.ASCENDING)
            .findAll()
        val travel = realm.where<Travel>()
            .equalTo("manageId", manageId)
            .findFirst()
        if (travel is Travel) travelDays = travel.travelDays

        adapter = Bookmark1Adapter(this, travelParts, true)
        bm1RecyclerView.adapter = adapter
        bm1RecyclerView.layoutManager = GridLayoutManager(this, 2)

        //RecyclerViewをタップ
        adapter.setOnBm1ItemClickListener(object: Bookmark1Adapter.OnBm1ItemClickListener{
            //作成したInterfaceを呼びだす。
            override fun onBm1ItemClick(view: View, manageId: Int?, day: Int?) {
                startActivity<Bookmark2Activity>("manageId" to manageId, "day" to day,"travelDays" to travelDays)
            }
        })

        adapter.setOnBm1EditClickListener(object: Bookmark1Adapter.OnBm1EditDeleteClickListener{
            override fun onBm1EditClick(view: View, manageId: Int?, day: Int?, bm1EditDeleteFlag: Int) {
                if(bm1EditDeleteFlag == 1){
                    startActivity<EditBookmark1Activity>("manageId" to manageId, "day" to day, "travelDays" to travelDays)
                }else{
                    val currentTravelDays = realm.where<Travel>()
                        .equalTo("manageId", manageId)
                        .findFirst()?.travelDays
                    if(currentTravelDays is Int && currentTravelDays > 1){
                        alert ("削除しますか？"){
                            yesButton {
                                if (manageId is Int && day is Int){
                                    delete(manageId, day, travelDays)
                                }
                            }
                            noButton {  }
                        }.show()
                    }else {
                        alert ("旅行日数を0日にはできません。"){ yesButton { } }.show()
                    }
                }
            }
        })

        //戻るをタップ
        bm1BackToMain.setOnClickListener { finish() }
    }

    //削除タップ時の処理。
    private fun delete(manageId: Int, day: Int, travelDays: Int){
        realm.executeTransaction {
            deleteTravelPart(manageId, day)
            deleteTravelDetail(manageId, day)
            arrangeTravelPart(manageId, day)
            arrangeTravelDetail(manageId, day)
            arrangeArrivalTravelDetail(manageId, day, travelDays)
            arrangeTravel(manageId, travelDays)
        }
        Toast.makeText(applicationContext, "削除しました！", Toast.LENGTH_LONG).show()
    }

    //Travel更新。travelDays、到着日付、到着日時、コストを変更する。
    private fun arrangeTravel(manageId: Int, travelDays: Int) {
        val updateTravel = realm.where<Travel>()
            .equalTo("manageId", manageId)
            .findFirst()
        val endTravelDetail = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", travelDays - 1)
            .sort("order", Sort.DESCENDING)
            .findFirst()
        if(updateTravel is Travel && endTravelDetail is TravelDetail){
            this.travelDays = updateTravel.travelDays.minus(1)
            Log.i("【Bookmark1Activity】", "[arrangeTravel] minusTravelDays ${this.travelDays}")
            updateTravel.travelDays = this.travelDays
            updateTravel.arrivalDay = minusArrivalDay(updateTravel.arrivalDay)
            updateTravel.arrivalTime = endTravelDetail.startTime
            updateTravel.totalCost -= diffCostForTravel
        }
    }

    //削除時にTravelの日付を-1する。
    private fun minusArrivalDay(arrivalDay: String): String {
        return try{
            val sdFormat = SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN)
            val arrivalDayDate = sdFormat.parse(arrivalDay)

            val calendar = Calendar.getInstance()
            calendar.time = arrivalDayDate
            calendar.add(Calendar.DATE, -1)
            val minusArrivalDayDate = calendar.time

            sdFormat.format(minusArrivalDayDate).toString()
        }catch (e: ParseException){
            arrivalDay
        }catch (e: IllegalArgumentException){
            arrivalDay
        }
    }

    //到着地のTravelDetail更新。最終日かどうかで分類
    private fun arrangeArrivalTravelDetail(manageId: Int, day: Int, travelDays: Int) {
        when(day == travelDays){
            true -> updateArrivalTravelDetail(manageId, travelDays, true)
            false -> updateArrivalTravelDetail(manageId, travelDays, false)
        }
    }

    //到着地のTravelDetail更新。最終日trueなら到着（Travel）のstartTimeをもってくる。falseならそのまま。
    private fun updateArrivalTravelDetail(manageId: Int, travelDays: Int, isLastDay: Boolean) {
        val lastOrder = 9
        val newTravelDetail = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("order", lastOrder)
            .findFirst() as TravelDetail
        if (isLastDay){
            newTravelDetail.startTime =realm.where<TravelDetail>()
                .equalTo("manageId", manageId)
                .equalTo("day", travelDays - 1)
                .sort("order",Sort.DESCENDING)
                .findFirst()?.startTime.toString()
        }
        newTravelDetail.day = travelDays - 1
    }

    //削除したTravelPartの分ほかのデータをつめて整形
    private fun arrangeTravelPart(manageId: Int, day: Int) {
        for(arrangeDay in (day + 1)..7){
            val arrangeTravelPart = realm.where<TravelPart>()
                .equalTo("manageId", manageId)
                .equalTo("day", arrangeDay)
                .findFirst()
            if(arrangeTravelPart is TravelPart){
                Log.i("【Bookmark1Activity】", "[arrangeTravelPart] day$arrangeDay を-1")
                arrangeTravelPart.day = arrangeDay - 1
            }
        }
    }

    //削除したTravelDetailの分ほかのデータをつめて整形
    private fun arrangeTravelDetail(manageId: Int, day: Int) {
        for(arrangeDay in (day + 1)..7){
            val arrangeTravelDetails = realm.where<TravelDetail>()
                .equalTo("manageId", manageId)
                .equalTo("day", arrangeDay)
                .findAll()
            for (arrangeTravelDetail in arrangeTravelDetails){
                if(arrangeTravelDetail is TravelDetail){
                    Log.i("【Bookmark1Activity】", "[arrangeTravelPart] day$arrangeDay が存在するため整形")
                    //order=0かつ1日目を削除したとき、2日目の目的地を最初（Travel）の出発地に設定。
                    if(arrangeDay == 2 && arrangeTravelDetail.order == 0){
                        arrangeTravelDetail.destination = realm.where<Travel>()
                            .equalTo("manageId", manageId)
                            .findFirst()?.departurePlace.toString()
                        //order=0かつ2日目以降を削除したとき、削除した日の前日の最終目的地を出発地に設定。
                    }else if (arrangeDay == (day + 1) && arrangeTravelDetail.order == 0){
                        arrangeTravelDetail.destination = realm.where<TravelDetail>()
                            .equalTo("manageId", manageId)
                            .equalTo("day", day - 1)
                            .sort("order", Sort.DESCENDING)
                            .findFirst()?.destination.toString()
                    }
                    arrangeTravelDetail.day = arrangeDay - 1
                }
            }
        }
    }

    //TravelPartを削除する。
    private fun deleteTravelPart(manageId: Int, day: Int) {
        realm.where<TravelPart>()
            .equalTo("manageId", manageId)
            .equalTo("day", day).findFirst()
            ?.deleteFromRealm()
    }

    //totalCostを一次退避し、到着以外のTravelDetailを削除する。
    private fun deleteTravelDetail(manageId: Int, day: Int) {
        val targetTravelDetails = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day)
            .between("order", 0, 6)
            .findAll()
        for (travelDetail in targetTravelDetails){
            diffCostForTravel += travelDetail.totalCost
        }
        targetTravelDetails?.deleteAllFromRealm()
    }

    //Viewが破壊されたときにRealmを閉じる。
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}