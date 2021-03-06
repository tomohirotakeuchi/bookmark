package tomohiro.takeuchi.android.smarttravel.fragment

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
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
import tomohiro.takeuchi.android.smarttravel.R
import tomohiro.takeuchi.android.smarttravel.model.TravelDetail
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
                    0 -> createMap(resources.getString(R.string.departurePlaceText)
                        , travelDetail.latitude, travelDetail.longitude, 15f)
                    else -> createMap(resources.getString(R.string.arrivalPlaceText)
                        , travelDetail.latitude, travelDetail.longitude, 15f)
                }
            }else {
                travelDetail?.let {
                    createMap(it.destination, it.latitude, it.longitude, 15f)
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
                createMap(travelDetail.destination + resources.getString(R.string.mapIsNotFound)
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
        val latLng = LatLng(latitude, longitude)
        mMap.isIndoorEnabled = false
        mMap.addMarker(MarkerOptions().position(latLng).title(searchKey))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomDegree))
    }

    //フラグメント削除時にRealmを閉じる。
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}