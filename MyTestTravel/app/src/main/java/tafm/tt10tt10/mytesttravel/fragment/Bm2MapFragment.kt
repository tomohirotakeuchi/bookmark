package tafm.tt10tt10.mytesttravel.fragment

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.realm.Realm
import tafm.tt10tt10.mytesttravel.R
import java.util.*

class Bm2MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
//    private lateinit var realm: Realm
    private var manageId = 1
    private var day = 1
    private var order = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bm2_map_fragment, container, false)
        val argument = arguments
        if (argument != null){
            manageId = argument["manageId"] as Int
            day = argument["day"] as Int
            order = argument["order"] as Int
            Log.i("【Bm2MapFragment】", "[onCreateView] !!arguments!! manageId=$manageId day=$day order=$order")
        }
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
        val lstAddr: List<Address>? = try {
            //いざGeocoderから取得。
            geoCoder.getFromLocationName(searchKey,1 )
        }catch (e: Exception){
            //失敗したらlstAddrをnullで返却。
            Log.i("【Bm2MapFragment】","[onMapReady] Geocode失敗")
            null
        }

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

//    //フラグメント削除時にRealmを閉じる。
//    override fun onDestroy() {
//        super.onDestroy()
//        realm.close()
//    }
}