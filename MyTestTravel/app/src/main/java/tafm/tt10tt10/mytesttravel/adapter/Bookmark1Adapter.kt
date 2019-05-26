package tafm.tt10tt10.mytesttravel.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
    private lateinit var bm1ItemListener: OnBm1ItemClickListener
    private lateinit var bm1EditListener: OnBm1EditDeleteClickListener

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
            bm1ItemListener.onBm1ItemClick(it, travelPart?.manageId, travelPart?.day)
        }
        viewHolder.editBtn.setOnClickListener {
            bm1EditListener.onBm1EditClick(it, travelPart?.manageId, travelPart?.day, 1)
        }
        viewHolder.deleteBtn.setOnClickListener {
            bm1EditListener.onBm1EditClick(it, travelPart?.manageId, travelPart?.day, 2)
        }
    }

    //リサイクラービューで表示するアイテムを新規に生成するときに呼ばれる。ViewとviewHolderを生成し、viewHolderを返す。
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderForBm1 {
        setOnBm1ItemClickListener(bm1ItemListener)
        val view = inflater.inflate(R.layout.bookmark1_recycler_list_item, parent, false)
        return ViewHolderForBm1(view)
    }

    //ListViewのlistenerをセットする。
    fun setOnBm1ItemClickListener(bm1ItemListener: OnBm1ItemClickListener){
        this.bm1ItemListener = bm1ItemListener
    }

    //編集削除のlistenerをセットする。
    fun setOnBm1EditClickListener(bm1EditListener: OnBm1EditDeleteClickListener){
        this.bm1EditListener = bm1EditListener
    }

    //interface。ListViewクリック。Bookmark1Activityから呼びだす。
    interface OnBm1ItemClickListener {
        fun onBm1ItemClick(view: View, manageId: Int?, day: Int?)
    }

    //interface。編集・削除クリック。Bookmark1Activityから呼びだす。
    interface OnBm1EditDeleteClickListener {
        fun onBm1EditClick(view: View, manageId: Int?, day: Int?, bm1EditDeleteFlag: Int)
    }

    //viewHolder
    class ViewHolderForBm1(view: View): RecyclerView.ViewHolder(view){
        val layout: View = view.findViewById(R.id.bm1ItemCellLayout)
        val day: TextView = view.findViewById(R.id.bm1DayText)
        val destination: TextView = view.findViewById(R.id.bm1Destination1)
        val editBtn: ImageView = view.findViewById(R.id.bm1Edit)
        val deleteBtn: ImageView = view.findViewById(R.id.bm1Delete)
    }
}