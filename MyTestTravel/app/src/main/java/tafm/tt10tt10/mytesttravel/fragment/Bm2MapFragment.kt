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
import io.realm.kotlin.where
import tafm.tt10tt10.mytesttravel.R
import tafm.tt10tt10.mytesttravel.model.TravelDetail
import java.util.*

class Bm2MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var realm: Realm
    private var manageId = 1
    private var day = 1
    private var order = 0

    //フラグメントの最初の処理
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bm2_map_fragment, container, false)
        val argument = arguments
        argument?.apply{
            manageId = this["manageId"] as Int
            day = this["day"] as Int
            order = this["order"] as Int
            Log.i("【Bm2MapFragment】", "[onCreateView] !!arguments!! manageId=$manageId day=$day order=$order")
        }
        realm = Realm.getDefaultInstance()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return view
    }

    //GoogleMapがロードされると呼ばれる。
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val travelDetail = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day)
            .equalTo("order", order)
            .findFirst()

        //経度緯度がデータベースに存在する場合
        if (travelDetail?.latitude != 0.0 && travelDetail?.longitude != 0.0){
            if (travelDetail?.destination == "現在地" || travelDetail?.destination == "Current Place"){
                when(travelDetail.order){
                    0 -> createMap("出発地", travelDetail.latitude, travelDetail.longitude, 15f)
                    else -> createMap("最終目的地", travelDetail.latitude, travelDetail.longitude, 15f)
                }
            }else {
                if (travelDetail is TravelDetail){
                    createMap(travelDetail.destination, travelDetail.latitude, travelDetail.longitude, 15f)
                }
            }
        }else{
            //経度緯度がデータベースに存在しない場合、GeoCordingを行う。
            val locationListAddress: List<Address>? = getLocationList(travelDetail.destination)
            if (locationListAddress != null && locationListAddress.isNotEmpty()){
                val address = locationListAddress[0]
                createMap(travelDetail.destination, address.latitude, address.longitude, 15f)
                //GooCordingが成功したらデータベースに登録。
                realm.executeTransaction {
                    travelDetail.latitude = address.latitude
                    travelDetail.longitude = address.longitude
                }
                Log.i("【Bm2MapFragment】","[onMapReady] GeoCoding成功!!$address")
            }else {
                createMap("${travelDetail.destination} の検索に失敗しました。"
                    , -34.0, 151.0, 1f)
                Log.i("【Bm2MapFragment】","[onMapReady] GeoCoding失敗")
            }
        }
    }

    //GeoCodingを行う。
    private fun getLocationList(searchKey: String): List<Address>? {
        val geoCoder = Geocoder(context, Locale.getDefault())
        return try {
            geoCoder.getFromLocationName(searchKey,1 )
        }catch (e: Exception){
            null
        }
    }

    //マップを生成する。
    private fun createMap(searchKey: String, latitude: Double, longitude: Double, zoomDegree: Float) {
        // 緯度、経度設定
        val latLng = LatLng(latitude, longitude)
        //インドアの情報を非表示。東京駅などは複雑でプログラムがクラッシュする。
        mMap.isIndoorEnabled = false
        //マーカーを追加。positionで位置を指定、titleでタップ時のメソッド。searchKeyを表示。
        mMap.addMarker(MarkerOptions().position(latLng).title(searchKey))
        //地図上に表示する位置とズーム指定。
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomDegree))
    }

    //フラグメント削除時にRealmを閉じる。
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}