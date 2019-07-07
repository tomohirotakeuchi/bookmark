package tafm.tt10tt10.mytesttravel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_bookmark2.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.startActivity
import tafm.tt10tt10.mytesttravel.adapter.Bm2PagerAdapter
import tafm.tt10tt10.mytesttravel.fragment.Bm2ImageFragment
import tafm.tt10tt10.mytesttravel.fragment.Bm2PlaceFragment
import tafm.tt10tt10.mytesttravel.fragment.Bm2ScheduleFragment
import tafm.tt10tt10.mytesttravel.model.TravelDetail
import tafm.tt10tt10.mytesttravel.model.TravelPart


class Bookmark2Activity : AppCompatActivity()
    , Bm2PlaceFragment.Bm2PlaceEditOnClickListener
    , Bm2PlaceFragment.Bm2PlaceUrlLink
    , Bm2ScheduleFragment.Bm2ScheduleEditOnClickListener
    , Bm2ImageFragment.Bm2ImageEditOnClickListener{

    private lateinit var realm: Realm
    private lateinit var bundle: Bundle
    private lateinit var pagerAdapter: Bm2PagerAdapter
    private var manageId: Int = 1
    private var day: Int = 1
    private var order: Int = 0
    private var travelDays: Int = 1
    private var arrivalPlaceNum: Int = -1

    private val destinationArray: MutableList<String> = mutableListOf()

    override fun onRestart() {
        super.onRestart()
        Log.i("【Bookmark2Activity】", "[onRestart()]")
        pagerAdapter.notifyDataSetChanged()
        setTabAndIcon()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark2)

        realm = Realm.getDefaultInstance()
        manageId = intent.getIntExtra("manageId", 1)
        day = intent.getIntExtra("day", 1)
        travelDays = intent.getIntExtra("travelDays", 1)

        bundle = Bundle()
        bundle.putInt("manageId", manageId)
        bundle.putInt("day", day)
        bundle.putInt("order", order)
        bundle.putInt("travelDays", travelDays)

        createSpinnerContents(manageId, day, getDestinationPlace(manageId, day))
        setPagerAdapter(bundle)
        setTabAndIcon()

        bm2Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                order = position
                Log.i("【Bookmark2Activity】", "[bm2Spinner] position$position arrivalPlaceNum$arrivalPlaceNum")
                when(order){
                    arrivalPlaceNum -> {
                        bundle.putInt("order", 9)
                        order = 9
                    }
                    else -> bundle.putInt("order", order)
                }
                pagerAdapter.notifyDataSetChanged()
                setTabAndIcon()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }

        //戻るボタン押下。
        bm2BackToBm1Btn.setOnClickListener {
            it.notPressTwice()
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
        if (travelPart?.destination1 is String && travelPart.destination1.isNotEmpty()) {
            destinationArray.add(dayText + travelPart.destination1)
        }
        if (travelPart?.destination2 is String && travelPart.destination2.isNotEmpty()) {
            destinationArray.add(dayText + travelPart.destination2)
        }
        if (travelPart?.destination3 is String && travelPart.destination3.isNotEmpty()) {
            destinationArray.add(dayText + travelPart.destination3)
        }
        if (travelPart?.destination4 is String && travelPart.destination4.isNotEmpty()) {
            destinationArray.add(dayText + travelPart.destination4)
        }
        if (travelPart?.destination5 is String && travelPart.destination5.isNotEmpty()) {
            destinationArray.add(dayText + travelPart.destination5)
        }
        if (travelPart?.destination6 is String && travelPart.destination6.isNotEmpty()) {
            destinationArray.add(dayText + travelPart.destination6)
        }

        if (travelDays == day) {
            destinationArray.add(dayText + getArrivalPlace(manageId, day, 9))
            arrivalPlaceNum = destinationArray.size -1
        }

        Log.i("【Bookmark2Activity】", "[createSpinnerContents] arrivalPlaceNum$arrivalPlaceNum")
        val arrayAdapter = ArrayAdapter(this, R.layout.bm2_spinner, destinationArray)
        bm2Spinner.adapter = arrayAdapter
    }

    private fun getArrivalPlace(manageId: Int, day: Int, order: Int): String {
        val travelDetail = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day)
            .equalTo("order", order)
            .findFirst()
        return travelDetail?.destination ?: " "
    }

    //PagerAdapterの処理。
    private fun setPagerAdapter(bundle: Bundle) {
        //ViewPagerに先ほど作成したAdapterのインスタンスを渡してあげる
        pagerAdapter = Bm2PagerAdapter(supportFragmentManager, bundle)
        bm2ViewPager.adapter = pagerAdapter
        //TabLayoutにViewPagerのインスタンスを渡すと自動的に実装してくれる
        bm2TabLayout.setupWithViewPager(bm2ViewPager)
    }

    //タブ名とアイコンを設定する。
    private fun setTabAndIcon() {
        for (i: Int in 0..bm2TabLayout.tabCount) {
            bm2TabLayout.getTabAt(i)?.also {
                when (i) {
                    0 -> setTabAndIconValues(it
                        , resources.getString(R.string.bm2TabPlace), R.drawable.img_detail)
                    1 -> setTabAndIconValues(it
                        , resources.getString(R.string.bm2TabSchedule), R.drawable.img_schedule)
                    2 -> setTabAndIconValues(it
                        , resources.getString(R.string.bm2TabImage), R.drawable.img_image)
                    3 -> setTabAndIconValues(it
                        , resources.getString(R.string.bm2TabMap), R.drawable.img_map)
                }
            }
        }
    }

    //タブ名とアイコンの値を設定する。
    private fun setTabAndIconValues(it: TabLayout.Tab, tabName: String, tabImage: Int) {
        it.text = tabName
        it.setIcon(tabImage)
    }

    //PlaceFragmentでEditをクリック。
    override fun onPlaceEditClick() {
        startActivity<EditPlaceActivity>("manageId" to manageId, "day" to day
            , "order" to order)
    }

    override fun onPlaceUrlClick(url: String) {
        browse(url)
    }

    //ScheduleFragmentでEditをクリック。
    override fun onScheduleEditClick() {
        startActivity<EditScheduleActivity>("manageId" to manageId, "day" to day
            , "travelDays" to travelDays)
    }

    //ImageFragmentでEditをクリック。
    override fun onImageEditClick() {
        startActivity<EditImageActivity>("manageId" to manageId, "day" to day, "order" to order)
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
