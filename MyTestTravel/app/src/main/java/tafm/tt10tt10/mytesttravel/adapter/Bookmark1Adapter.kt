package tafm.tt10tt10.mytesttravel.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import tafm.tt10tt10.mytesttravel.R
import tafm.tt10tt10.mytesttravel.model.TravelPart
import java.lang.StringBuilder

class Bookmark1Adapter(context: Context
                       , private val collection: OrderedRealmCollection<TravelPart>?
                       , autoUpdate: Boolean)
    : RealmRecyclerViewAdapter<TravelPart, Bookmark1Adapter.ViewHolderForBm1>(collection, autoUpdate){

    private val inflater = LayoutInflater.from(context)
    private lateinit var listener: OnItemClickListener

    //表示するアイテムの個数を設定する。
    override fun getItemCount(): Int {
        return collection?.size ?: 0
    }

    //リサイクラービューでアイテムを表示するときに呼ばれる。ここでviewHolderに値を設定する。
    override fun onBindViewHolder(viewHolder: ViewHolderForBm1, position: Int) {
        val travelPart = collection?.get(position)
        val dayBuilder = StringBuilder()
        viewHolder.day.text = dayBuilder.append("Day ").append(travelPart?.day.toString()).toString()
        viewHolder.destination.text = travelPart?.destination1.toString()
        viewHolder.layout.setOnClickListener {
            listener.onItemClick(it, travelPart?.manageId, travelPart?.day)
        }
    }

    //リサイクラービューで表示するアイテムを新規に生成するときに呼ばれる。ViewとviewHolderを生成し、viewHolderを返す。
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderForBm1 {
        setOnItemClickListener(listener)
        val view = inflater.inflate(R.layout.bookmark1_recycler_list_item, parent, false)
        return ViewHolderForBm1(view)
    }

    //listenerをセットする。
    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    //interface。Bookmark2Activityから呼びだす。
    interface OnItemClickListener {
        fun onItemClick(view: View, manageId: Int?, day: Int?)
    }

    //viewHolder
    class ViewHolderForBm1(view: View): RecyclerView.ViewHolder(view){
        val layout: View = view.findViewById(R.id.bm1ItemCellLayout)
        val day: TextView = view.findViewById(R.id.bm2DayText)
        val destination: TextView = view.findViewById(R.id.bm2Destination1)
    }
}