package tafm.tt10tt10.mytesttravel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_bookmark2.*
import tafm.tt10tt10.mytesttravel.adapter.Bm2PagerAdapter
import tafm.tt10tt10.mytesttravel.model.TravelDetail
import tafm.tt10tt10.mytesttravel.model.TravelPart


class Bookmark2Activity : AppCompatActivity() {

    private lateinit var realm: Realm
    private val destinationArray: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark2)

        realm = Realm.getDefaultInstance()
        val manageId = intent.getIntExtra("manageId", 1)
        val day = intent.getIntExtra("day", 1)

        createSpinnerContents(manageId, day, getDestinationPlace(manageId, day))
        setPagerAdapter()

//        bm2Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//
//            }
//        }

        //戻るボタン押下。
        bm2BackToBm1Btn.setOnClickListener {
            finish()
        }
    }

    //その日の出発地を返却。travelDetailのorder = 0をとってくる。
    private fun getDestinationPlace(manageId: Int, day: Int): String {
            val order = 0
            val travelDetail = realm.where<TravelDetail>()
                .equalTo("manageId", manageId)
                .equalTo("day", day)
                .equalTo("order", order)
                .findFirst()
        return travelDetail?.destination.toString()
    }

    //Spinnerの中身をセットする。
    private fun createSpinnerContents(manageId: Int, day: Int, destinationPlace: String) {
        val travelPart = realm.where<TravelPart>()
            .equalTo("manageId", manageId)
            .equalTo("day", day)
            .findFirst()
        val dayText = "Day$day : "

        destinationArray.add(dayText + destinationPlace)
        if (travelPart?.destination1 is String && travelPart.destination1.isNotEmpty()) destinationArray.add(dayText + travelPart.destination1)
        if (travelPart?.destination2 is String && travelPart.destination2.isNotEmpty()) destinationArray.add(dayText + travelPart.destination2)
        if (travelPart?.destination3 is String && travelPart.destination3.isNotEmpty()) destinationArray.add(dayText + travelPart.destination3)
        if (travelPart?.destination4 is String && travelPart.destination4.isNotEmpty()) destinationArray.add(dayText + travelPart.destination4)
        if (travelPart?.destination5 is String && travelPart.destination5.isNotEmpty()) destinationArray.add(dayText + travelPart.destination5)
        if (travelPart?.destination6 is String && travelPart.destination6.isNotEmpty()) destinationArray.add(dayText + travelPart.destination6)

        val arrayAdapter = ArrayAdapter<String>(this, R.layout.bm2_spinner, destinationArray)
        bm2Spinner.adapter = arrayAdapter
    }

    //PagerAdapterの処理。
    private fun setPagerAdapter() {
        //ViewPagerに先ほど作成したAdapterのインスタンスを渡してあげる
        bm2ViewPager.adapter = Bm2PagerAdapter(supportFragmentManager)
        //TabLayoutにViewPagerのインスタンスを渡すと自動的に実装してくれる
        bm2TabLayout.setupWithViewPager(bm2ViewPager)

        //Tabへの処理はsetupWithViewPagerをした後だったら可能
        //ここではタブ名とアイコンを設定している
        for (i: Int in 0..bm2TabLayout.tabCount) {
            bm2TabLayout.getTabAt(i)?.also {
                when(i){
                    0 -> {
                        it.text = "place"
                        it.setIcon(R.drawable.ic_flag_black_24dp)
                    }
                    1 -> {
                        it.setIcon(R.drawable.ic_schedule_black_24dp)
                        it.text = "time"
                    }
                    2 -> {
                        it.setIcon(R.drawable.ic_image_black_24dp)
                        it.text = "image"
                    }
                    3 -> {
                        it.setIcon(R.drawable.ic_map_black_24dp)
                        it.text = "map"
                    }
                }
            }
        }
    }
}
