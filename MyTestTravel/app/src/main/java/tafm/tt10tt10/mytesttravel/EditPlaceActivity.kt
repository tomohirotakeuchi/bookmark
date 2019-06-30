package tafm.tt10tt10.mytesttravel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit_place.*
import tafm.tt10tt10.mytesttravel.model.Travel
import tafm.tt10tt10.mytesttravel.model.TravelDetail
import java.text.NumberFormat
import java.util.*
import kotlin.math.pow

class EditPlaceActivity : AppCompatActivity() {

    private lateinit var realm: Realm
    private var manageId = 1
    private var day = 1
    private var order = 0
    private var preTotalCost = 0
    private var lastTotalCost = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_place)

        realm = Realm.getDefaultInstance()
        manageId = intent.getIntExtra("manageId", 1)
        day = intent.getIntExtra("day", 1)
        order = intent.getIntExtra("order", 0)

        val travelDetail = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day)
            .equalTo("order", order)
            .findFirst()

        travelDetail?.let{
            setPlaceEditText(it)
            setGuideText(it)
        }
        editCostClicked()

        placeEditBack.setOnClickListener {
            it.notPressTwice()
            finish()
        }

        placeEditUpdate.setOnClickListener {
            it.notPressTwice()
            travelDetail?.let {
                realm.executeTransaction {
                    updateTravelDetail(travelDetail)
                    updateTravel(manageId)
                    finish()
                }
            }
        }
    }

    //ガイドテキストを表示
    private fun setGuideText(travelDetail: TravelDetail) {
        val text = "- Day$day : " + travelDetail.destination + " -"
        placeEditGuide.text = text
    }

    //Travelの総費用を更新する。
    private fun updateTravel(manageId: Int) {
        val travel = realm.where<Travel>()
            .equalTo("manageId", manageId)
            .findFirst()
        val travelCost = travel?.totalCost ?: 0
        val dif = travelCost + lastTotalCost - preTotalCost
        travel?.totalCost = dif
    }

    //入力値をTravelDetailに登録する。
    private fun updateTravelDetail(travelDetail: TravelDetail) {
        travelDetail.memo1 = placeEditMemo1.text.toString()
        travelDetail.memo2 = placeEditMemo2.text.toString()
        travelDetail.memo3 = placeEditMemo3.text.toString()
        travelDetail.urlLink1 = placeEditURL1.text.toString()
        travelDetail.urlLink2 = placeEditURL2.text.toString()
        travelDetail.urlLink3 = placeEditURL3.text.toString()
        travelDetail.costItem1 = getPlaceEditCost(placeEditCost1)
        travelDetail.costItem2 = getPlaceEditCost(placeEditCost2)
        travelDetail.costItem3 = getPlaceEditCost(placeEditCost3)
        travelDetail.costItem4 = getPlaceEditCost(placeEditCost4)
        travelDetail.costItem5 = getPlaceEditCost(placeEditCost5)
        travelDetail.totalCost = lastTotalCost
    }

    //Costの値を取得。未入力なら0を入れる。
    private fun getPlaceEditCost(placeEditCost: EditText?): Int {
        return if (placeEditCost1.text.toString() != ""){
            placeEditCost?.text.toString().toInt()
        }else{
            0
        }
    }

    //Costを編集したときに、合計を計算する。
    private fun editCostClicked() {
        editCostClicked(placeEditCost1)
        editCostClicked(placeEditCost2)
        editCostClicked(placeEditCost3)
        editCostClicked(placeEditCost4)
        editCostClicked(placeEditCost5)
    }

    //costを入力したあとの処理
    private fun editCostClicked(editText: EditText?) {
        editText?.addTextChangedListener(object: CustomTextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                sumCost()
            }
        })
    }

    //totalコストを計算する。
    private fun sumCost() {
        var total = 0
        if (placeEditCost1.text.toString() != "") total += placeEditCost1.text.toString().toInt()
        if (placeEditCost2.text.toString() != "") total += placeEditCost2.text.toString().toInt()
        if (placeEditCost3.text.toString() != "") total += placeEditCost3.text.toString().toInt()
        if (placeEditCost4.text.toString() != "") total += placeEditCost4.text.toString().toInt()
        if (placeEditCost5.text.toString() != "") total += placeEditCost5.text.toString().toInt()
        lastTotalCost = total
        placeEditTotal.text = getCostText(total)
    }

    //テキストビューにデータベースの値をセットする。
    private fun setPlaceEditText(travelDetail: TravelDetail) {
        placeEditMemo1.setText(travelDetail.memo1)
        placeEditMemo2.setText(travelDetail.memo2)
        placeEditMemo3.setText(travelDetail.memo3)
        placeEditURL1.setText(travelDetail.urlLink1)
        placeEditURL2.setText(travelDetail.urlLink2)
        placeEditURL3.setText(travelDetail.urlLink3)
        placeEditCost1.setText(travelDetail.costItem1.toString())
        placeEditCost2.setText(travelDetail.costItem2.toString())
        placeEditCost3.setText(travelDetail.costItem3.toString())
        placeEditCost4.setText(travelDetail.costItem4.toString())
        placeEditCost5.setText(travelDetail.costItem5.toString())
        preTotalCost = travelDetail.totalCost
        lastTotalCost = preTotalCost
        placeEditTotal.text = getCostText(preTotalCost)
    }

    //ロケールからお金の単位をつける。
    private fun getCostText(cost: Int): String {
        val nf = NumberFormat.getCurrencyInstance()
        val currency = Currency.getInstance(Locale.getDefault())
        val value = cost / 10.0.pow(currency.defaultFractionDigits.toDouble())
        return nf.format(value)
    }

    //TextWatcherのインターフェース。
    interface CustomTextWatcher: TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
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

    //アクティビティ削除時にRealmを閉じる。
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
