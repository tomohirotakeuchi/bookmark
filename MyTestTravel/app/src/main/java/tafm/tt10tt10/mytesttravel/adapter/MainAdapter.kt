package tafm.tt10tt10.mytesttravel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.realm.RealmBaseAdapter
import io.realm.RealmResults
import tafm.tt10tt10.mytesttravel.R
import tafm.tt10tt10.mytesttravel.model.Travel

class MainAdapter(data: RealmResults<Travel>?) : RealmBaseAdapter<Travel>(data) {

    inner class ViewHolder(cell: View){
        val mainDepartureDayTime: TextView = cell.findViewById(R.id.mainDepartureDayTime)
        val mainArrivalDayTime: TextView = cell.findViewById(R.id.mainArrivalDayTime)
        val mainTitle: TextView = cell.findViewById(R.id.mainTitle)
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

        adapterData?.run{
            val travel = get(position)
            viewHolder.mainTitle.text = travel.title
            viewHolder.mainDepartureDayTime.text = travel.departureDay
            viewHolder.mainArrivalDayTime.text = travel.arrivalDay
        }
        println(view)
        return view
    }
}