package tafm.tt10tt10.mytesttravel

import android.location.Address
import android.location.Geocoder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_bookmark2.*
import java.util.*

class Bookmark2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark2)

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

class Bm2PlaceFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bm2_place_fragment, container, false)
    }
}

class Bm2ScheduleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bm2_schedule_fragment, container, false)
    }
}

class Bm2ImageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bm2_image_fragment, container, false)
    }
}

class Bm2MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bm2_maps_fragment, container, false)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return view
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    //GoogleMapがロードされると呼ばれる。
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //データベースアクセスでIDをキーに入力値をとってくる。
        val searchKey = "ユニコート初台A棟106 "
        //Geocoderのインスタンス作成。
        val geoCoder = Geocoder(context, Locale.getDefault())
        //取得結果を入れるリスト。
        val lstAddr: List<Address>?

        //いざGeocoderから取得。
        lstAddr = geoCoder.getFromLocationName(searchKey,1 )

        //無事取得できれば、1番目をGetして緯度経度を取得する。
        if(lstAddr != null && lstAddr.isNotEmpty()){
            val addr = lstAddr[0]
            val latitude = addr.latitude
            val longitude = addr.longitude
            // 緯度、経度設定
            val tokyo = LatLng(latitude, longitude)
            //インドアの情報を非表示。東京駅などは複雑でプログラムがクラッシュする。
            mMap.isIndoorEnabled = false
            //マーカーを追加。positionで位置を指定、titleでタップ時のメソッド。searchKeyを表示。
            mMap.addMarker(MarkerOptions().position(tokyo).title(searchKey))
            //地図上に表示する位置とズーム指定。
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tokyo,15f))
        }else {
            val tokyo = LatLng(-34.0, 151.0)
            //インドアの情報を非表示。東京駅などは複雑でプログラムがクラッシュする。
            mMap.isIndoorEnabled = false
            //マーカーを追加。positionで位置を指定、titleでタップ時のメソッド。searchKeyを表示。
            mMap.addMarker(MarkerOptions().position(tokyo).title("[$searchKey]の検索に失敗しました。"))
            //地図上に表示する位置とズーム指定。
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tokyo,1f))
        }
    }
}

//FragmentPagerAdapterを継承したクラスを作成する
class Bm2PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    //どのタブにどのFragmentを実装するか記述する
    //今回はテストなのですべて同じFragmentを実装している。
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return Bm2PlaceFragment()
            }
            1 -> {
                return Bm2ScheduleFragment()
            }
            2 -> {
                return Bm2ImageFragment()
            }
            3 -> {
                return Bm2MapFragment()
            }
            else -> {
                return Bm2PlaceFragment()
            }
        }
    }

    //今回はテストなので4つのタブ固定にしている
    //実際のアプリではタブの数などを管理することもあるだろう
    override fun getCount(): Int {
        return 4
    }
}