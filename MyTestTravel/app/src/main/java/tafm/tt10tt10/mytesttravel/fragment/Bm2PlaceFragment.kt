package tafm.tt10tt10.mytesttravel.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
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
import java.text.NumberFormat
import java.util.*

class Bm2PlaceFragment : Fragment() {

    private lateinit var realm: Realm
    private var listener: Bm2PlaceEditOnClickListener? = null

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
        if (argument != null){
            Log.i("【Bm2PlaceFragment】", "[onCreateView] $argument")
            manageId = argument["manageId"] as Int
            day = argument["day"] as Int
            order = argument["order"] as Int
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
        if(travelDetail is TravelDetail) setTextViewValues(travelDetail, order == 0, order == 9)
        //クリックリスナー実装
        view.findViewById<ImageView>(R.id.bm2_place_edit).setOnClickListener {
            listener = context as? Bm2PlaceEditOnClickListener
            listener?.onPlaceEditClick()
        }
    }

    //TextViewに値をセットする。
    private fun setTextViewValues(travelDetail: TravelDetail, isOrder0: Boolean, isOrder9: Boolean) {
        bm2_place_destination.text = travelDetail.destination
        bm2_place_startTime.text = travelDetail.startTime
        bm2_place_requireTime.text = if(isOrder0 || isOrder9){
            "---------"
        }else{
            travelDetail.requiredTime.split(": ")[1]
        }
        bm2_place_moveTime.text = if (isOrder9){
            "---------"
        }else{
            travelDetail.moveTime.split(": ")[1]
        }
        setStringText(travelDetail.memo1, bm2_place_memo1)
        setStringText(travelDetail.memo2, bm2_place_memo2)
        setStringText(travelDetail.memo3, bm2_place_memo3)
        setStringText(travelDetail.urlLink1, bm2_place_url1)
        setStringText(travelDetail.urlLink2, bm2_place_url2)
        setStringText(travelDetail.urlLink3, bm2_place_url3)
        bm2_place_totalCost.text = getCostText(travelDetail.totalCost)
        bm2_place_tableCost1.text = getCostText(travelDetail.costItem1)
        bm2_place_tableCost2.text = getCostText(travelDetail.costItem2)
        bm2_place_tableCost3.text = getCostText(travelDetail.costItem3)
        bm2_place_tableCost4.text = getCostText(travelDetail.costItem4)
        bm2_place_tableCost5.text = getCostText(travelDetail.costItem5)
    }

    //OnePointMemoとURLLinkに値をセットする。
    private fun setStringText(text: String?, textView: TextView?) {
        if (text is String && text.isNotEmpty()) {
            textView?.visibility = View.VISIBLE
            textView?.text = text
        }else {
            textView?.visibility = View.GONE
        }
    }

    //ロケールからお金の単位をつける。
    private fun getCostText(cost: Int): String {
        val nf = NumberFormat.getCurrencyInstance()
        val currency = Currency.getInstance(Locale.getDefault())
        val value = cost / Math.pow(10.0, currency.defaultFractionDigits.toDouble() )
        return nf.format(value)
    }

    //Bm2PlaceEditのinterface。
    interface Bm2PlaceEditOnClickListener {
        fun onPlaceEditClick()
    }

    //フラグメント削除時にRealmを閉じる。
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}