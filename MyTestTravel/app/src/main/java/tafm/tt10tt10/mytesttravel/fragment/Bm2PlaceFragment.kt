package tafm.tt10tt10.mytesttravel.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.bm2_place_fragment.*
import tafm.tt10tt10.mytesttravel.R
import tafm.tt10tt10.mytesttravel.model.TravelDetail
import java.lang.StringBuilder
import java.text.NumberFormat
import java.util.*
import kotlin.math.pow

class Bm2PlaceFragment : Fragment() {

    private lateinit var realm: Realm
    private lateinit var editListener: Bm2PlaceEditOnClickListener
    private lateinit var urlListener: Bm2PlaceUrlLink

    private var manageId = 1
    private var day = 1
    private var order = 0

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context !is Bm2PlaceEditOnClickListener){
            throw RuntimeException("リスナーを実装せよ")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bm2_place_fragment, container, false)
        val argument = arguments
        argument?.apply{
            Log.i("【Bm2PlaceFragment】", "[onCreateView] $argument")
            manageId = this["manageId"] as Int
            day = this["day"] as Int
            order = this["order"] as Int
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("【Bm2PlaceFragment】", "[onViewCreated]")
        realm = Realm.getDefaultInstance()
        val travelDetail = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day)
            .equalTo("order", order)
            .findFirst()
        travelDetail?.let {
            setTextViewValues(it, order == 0, order == 9)
            //クリックリスナー実装
            view.findViewById<TextView>(R.id.bm2_place_url1).setOnClickListener { view ->
                view.notPressTwice()
                urlListener = context as Bm2PlaceUrlLink
                urlListener.onPlaceUrlClick(it.urlLink1)
            }

            view.findViewById<TextView>(R.id.bm2_place_url2).setOnClickListener { view ->
                view.notPressTwice()
                urlListener = context as Bm2PlaceUrlLink
                urlListener.onPlaceUrlClick(it.urlLink2)
            }

            view.findViewById<TextView>(R.id.bm2_place_url3).setOnClickListener { view ->
                view.notPressTwice()
                urlListener = context as Bm2PlaceUrlLink
                urlListener.onPlaceUrlClick(it.urlLink3)
            }
        }

        view.findViewById<ImageView>(R.id.bm2_place_edit).setOnClickListener {
            it.notPressTwice()
            editListener = context as Bm2PlaceEditOnClickListener
            editListener.onPlaceEditClick()
        }
    }

    //TextViewに値をセットする。
    private fun setTextViewValues(travelDetail: TravelDetail, isOrder0: Boolean, isOrder9: Boolean) {
        bm2_place_destination.text = travelDetail.destination
        bm2_place_startTime.text = travelDetail.startTime
        bm2_place_requireTime.text = if(isOrder0 || isOrder9){
            "-------"
        }else{
            travelDetail.requiredTime.split(": ")[1]
        }
        bm2_place_departureTime.text = when {
            isOrder9 -> "-------"
            isOrder0 -> travelDetail.startTime
            else -> returnDepartureTime(travelDetail.startTime, travelDetail.requiredTime.split(": ")[1])
        }
        bm2_place_moveTime.text = if (isOrder9){
            "-------"
        }else{
            travelDetail.moveTime.split(": ")[1]
        }
        setMemoText(travelDetail.memo1, bm2_place_memo1)
        setMemoText(travelDetail.memo2, bm2_place_memo2)
        setMemoText(travelDetail.memo3, bm2_place_memo3)
        setUrlText(travelDetail.urlLink1, bm2_place_url1)
        setUrlText(travelDetail.urlLink2, bm2_place_url2)
        setUrlText(travelDetail.urlLink3, bm2_place_url3)
        bm2_place_totalCost.text = getCostText(travelDetail.totalCost)
        bm2_place_tableCost1.text = getCostText(travelDetail.costItem1)
        bm2_place_tableCost2.text = getCostText(travelDetail.costItem2)
        bm2_place_tableCost3.text = getCostText(travelDetail.costItem3)
        bm2_place_tableCost4.text = getCostText(travelDetail.costItem4)
        bm2_place_tableCost5.text = getCostText(travelDetail.costItem5)
    }

    //出発時刻を計算。
    private fun returnDepartureTime(startTime: String, requireTime: String): String {
        val sArr = startTime.split(":")
        val rArr = requireTime.split("h")
        val totalMinutes = when(rArr.size){
            1 -> {
                val builder = StringBuilder(rArr[0])
                builder.delete(rArr[0].length-5, rArr[0].length-1)
                sArr[0].trim().toInt()*60 + sArr[1].trim().toInt() + builder.toString().trim().toInt()
            }
            else -> {
                val builder = StringBuilder(rArr[1])
                builder.delete(rArr[1].length-5, rArr[1].length-1)
                sArr[0].trim().toInt()*60 + sArr[1].trim().toInt() +
                        rArr[0].trim().toInt()*60 + builder.toString().trim().toInt()
            }
        }
        val timeFormat = "%1$02d:%2$02d"
        return timeFormat.format(totalMinutes / 60, totalMinutes % 60)
    }

    //OnePointMemoに値をセットする。
    private fun setMemoText(memo: String, textView: TextView?) {
        when (memo != "" && memo.isNotEmpty()) {
            true -> {
                textView?.visibility = View.VISIBLE
                textView?.text = memo
            }
            false -> textView?.visibility = View.GONE
        }
    }

    //URLLinkに値をセットする。
    private fun setUrlText(url: String, textView: TextView?) {
        when (url != "" && url.isNotEmpty()) {
            true -> {
                textView?.visibility = View.VISIBLE
                val content = SpannableString(url)
                content.setSpan(UnderlineSpan(), 0, content.length, 0)
                textView?.text = content
            }
            false -> textView?.visibility = View.GONE
        }
    }

    //ロケールからお金の単位をつける。
    private fun getCostText(cost: Int): String {
        val nf = NumberFormat.getCurrencyInstance()
        val currency = Currency.getInstance(Locale.getDefault())
        val value = cost / 10.0.pow(currency.defaultFractionDigits.toDouble())
        return nf.format(value)
    }

    //Bm2PlaceEditのinterface。
    interface Bm2PlaceEditOnClickListener {
        fun onPlaceEditClick()
    }

    //Bm2PlaceUrlLinkのinterface
    interface Bm2PlaceUrlLink {
        fun onPlaceUrlClick(url: String)
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

    //フラグメント削除時にRealmを閉じる。
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}