package tafm.tt10tt10.mytesttravel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_bookmark1.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.yesButton
import tafm.tt10tt10.mytesttravel.adapter.Bookmark1Adapter
import tafm.tt10tt10.mytesttravel.fragment.MenuFragment
import tafm.tt10tt10.mytesttravel.model.Travel
import tafm.tt10tt10.mytesttravel.model.TravelDetail
import tafm.tt10tt10.mytesttravel.model.TravelPart
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Bookmark1Activity : AppCompatActivity(), MenuFragment.OnClickListener {

    private lateinit var realm: Realm
    private var preArrivalDestination = "defaultDestination"
    private var preArrivalStartTime = "defaultStartTime"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark1)

        realm = Realm.getDefaultInstance()
        val manageId = intent.getIntExtra("manageId", -1)
        val travelParts = realm.where<TravelPart>()
            .equalTo("manageId", manageId)
            .sort("day", Sort.ASCENDING)
            .findAll()
        val travelDays = realm.where<Travel>().equalTo("manageId", manageId).findFirst()?.travelDays

        val adapter = Bookmark1Adapter(this, travelParts, true)
        bm1RecyclerView.adapter = adapter
        bm1RecyclerView.layoutManager = GridLayoutManager(this, 2)

        val menuFragment = MenuFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.bm1MenuFragment, menuFragment)
        transaction.commit()

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
                    startActivity<Edit2TravelActivity>("manageId" to manageId, "day" to day, "travelDays" to travelDays)
                }else{
                    val currentTravelDays = realm.where<Travel>()
                        .equalTo("manageId", manageId)
                        .findFirst()?.travelDays
                    if(currentTravelDays is Int && currentTravelDays > 1){
                        alert ("削除しますか？"){
                            yesButton {
                                if (manageId is Int && day is Int && travelDays is Int){
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
            deleteArrivalTravelDetail(manageId)
            deleteTravelPart(manageId, day)
            deleteTravelDetail(manageId, day)
            arrangeTravelPart(manageId, day)
            arrangeTravelDetail(manageId, day)
            arrangeArrivalTravelDetail(manageId, day, travelDays)
            arrangeTravel(manageId)
        }
        Toast.makeText(applicationContext, "削除しました！", Toast.LENGTH_LONG).show()
    }

    //Travel更新。travelDaysと到着日付を変更する。
    private fun arrangeTravel(manageId: Int) {
        val updateTravel = realm.where<Travel>()
            .equalTo("manageId", manageId)
            .findFirst()
        val minusTravelDays = updateTravel?.travelDays?.minus(1)
        if(minusTravelDays is Int){
            Log.i("【Bookmark1Activity】", "[arrangeTravel] minusTravelDays $minusTravelDays")
            updateTravel.travelDays = minusTravelDays
            updateTravel.arrivalDay = minusArrivalDay(updateTravel.arrivalDay)
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
        if(day == travelDays){
            createNewTravelDetail(manageId, travelDays, preArrivalStartTime, true)
        }else {
            createNewTravelDetail(manageId, travelDays, preArrivalStartTime, false)
        }
    }

    //到着地のTravelDetail作成。最終日trueなら前日の最後のdestination、startTimeをもってくる。falseならそのまま。
    private fun createNewTravelDetail(manageId: Int, travelDays: Int, preArrivalStartTime: String, isLastDay: Boolean) {
        val maxDetailId = realm.where<TravelDetail>().max("id")
        val nextDetailId = (maxDetailId?.toInt() ?: 0) + 1
        val newTravelDetail = realm.createObject<TravelDetail>(nextDetailId)
        newTravelDetail.manageId = manageId
        newTravelDetail.startTime = when(isLastDay){
            true -> realm.where<TravelDetail>()
                .equalTo("manageId", manageId)
                .equalTo("day", travelDays - 1)
                .sort("order",Sort.DESCENDING)
                .findFirst()?.startTime.toString()
            false -> preArrivalStartTime
        }
        newTravelDetail.day = travelDays - 1
        newTravelDetail.destination =  preArrivalDestination
        newTravelDetail.order = 9
        newTravelDetail.deleteFlag = 0
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

    //TravelDetailを削除する。
    private fun deleteTravelDetail(manageId: Int, day: Int) {
        realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day).findAll()
            ?.deleteAllFromRealm()
    }

    //TravelDetailの最終目的地(order=9)のデータをいったん削除する。到着地と到着時間は一度退避する。
    private fun deleteArrivalTravelDetail(manageId: Int) {
        val lastOrder = 9
        val arriveTravelDetail = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("order", lastOrder)
            .findFirst()
        preArrivalDestination = arriveTravelDetail?.destination.toString()
        preArrivalStartTime = arriveTravelDetail?.startTime.toString()
        Log.i("【Bookmark1Activity】", "[deleteArrivalTravelDetail] $preArrivalDestination $preArrivalStartTime")
        arriveTravelDetail?.deleteFromRealm()
    }

    //Viewが破壊されたときにRealmを閉じる。
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    //フラグメントのクリックリスナー。
    override fun onClick() {
        startActivity<MainActivity>()
    }
}