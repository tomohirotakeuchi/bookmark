package tomohiro.takeuchi.android.smarttravel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.realm.RealmBaseAdapter
import io.realm.RealmResults
import tomohiro.takeuchi.android.smarttravel.R
import tomohiro.takeuchi.android.smarttravel.model.Travel
import java.text.NumberFormat
import java.util.*
import kotlin.math.pow

class MainAdapter(private val data: RealmResults<Travel>?) : RealmBaseAdapter<Travel>(data) {

    private lateinit var mainListener: OnMainEditDeleteClickListener

    inner class ViewHolder(cell: View){
        val mainDepartureDayTime: TextView = cell.findViewById(R.id.mainDepartureDayTime)
        val mainArrivalDayTime: TextView = cell.findViewById(R.id.mainArrivalDayTime)
        val mainTitle: TextView = cell.findViewById(R.id.mainTitle)
        val mainEdit: ImageView = cell.findViewById(R.id.mainEdit)
        val mainDelete: ImageView = cell.findViewById(R.id.mainDelete)
        val mainTotalCost: TextView = cell.findViewById(R.id.mainTotalCost)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View
        val viewHolder : ViewHolder
        val inflater = LayoutInflater.from(parent?.context)

        when(convertView){
            null ->{
                view = inflater.inflate(R.layout.main_list_item, parent, false)
                viewHolder = ViewHolder(view)
                view.tag = viewHolder
            }
            else -> {
                view = convertView
                viewHolder = view.tag as ViewHolder
            }
        }

        setOnMainEditDeleteClickListener(mainListener)

        viewHolder.mainEdit.setOnClickListener {
            it.notPressTwice()
            val travel = data?.get(position)
            mainListener.onMainEditDeleteClick(view, travel?.manageId, 1)
        }
        viewHolder.mainDelete.setOnClickListener {
            it.notPressTwice()
            val travel = data?.get(position)
            mainListener.onMainEditDeleteClick(view, travel?.manageId, 2)
        }

        adapterData?.run{
            val travel = get(position)
            val titleBuilder = StringBuilder("- ")
            viewHolder.mainTitle.text = titleBuilder.append(travel.title).append(" -")
            val departureBuilder = StringBuilder()
            viewHolder.mainDepartureDayTime.text = departureBuilder.append(travel.departureDay).append("  ").append(travel.departureTime)
            val arrivalBuilder = StringBuilder()
            viewHolder.mainArrivalDayTime.text = arrivalBuilder.append(travel.arrivalDay).append("  ").append(travel.arrivalTime)
            viewHolder.mainTotalCost.text = getCostText(travel.totalCost)
        }
        return view
    }

    private fun getCostText(totalCost: Int): String {
        val nf = NumberFormat.getCurrencyInstance()
        val currency = Currency.getInstance(Locale.getDefault())
        val value = totalCost / 10.0.pow(currency.defaultFractionDigits.toDouble())
        return nf.format(value)
    }

    //listenerをセットする。
    fun setOnMainEditDeleteClickListener(mainListener: OnMainEditDeleteClickListener){
        this.mainListener = mainListener
    }

    //interface。Activityから呼びだす。
    interface OnMainEditDeleteClickListener {
        fun onMainEditDeleteClick(view: View, manageId: Int?, mainEditDeleteFlag: Int)
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
}



