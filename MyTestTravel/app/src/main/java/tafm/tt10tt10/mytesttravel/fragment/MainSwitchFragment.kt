package tafm.tt10tt10.mytesttravel.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import tafm.tt10tt10.mytesttravel.R
import tafm.tt10tt10.mytesttravel.model.Authority
import java.util.concurrent.TimeUnit

class MainSwitchFragment: Fragment() {

    private lateinit var realm: Realm

    //Viewがクリエイトするときに呼ばれる。フラグメントでは最初の処理をここに入れる。
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.main_switch, container, false)
        val switch = view.findViewById<Switch>(R.id.mainLocationSwitch)

        realm = Realm.getDefaultInstance()

        //保存されている位置情報のフラグに応じて、スイッチのON/OFF初期表示を変える。
        var gpsAuthority = realm.where<Authority>().findFirst()
        if (gpsAuthority == null){
            realm.executeTransaction {
                val maxId = 1L
                gpsAuthority = realm.createObject(maxId)
            }
        }

        switch.isChecked = gpsAuthority?.flag ?: false
        Log.i("【SwitchFragment】", "[onCreateView] switch.isChecked = ${gpsAuthority?.flag ?: false}")

        //ユーザーがスイッチを切り替えた時の処理。
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                Log.i("【SwitchFragment】", "[onCreateView] スイッチon")
                checkLocationAvailable()
            }else {
                Log.i("【SwitchFragment】", "[onCreateView] スイッチoff")
                stopLocationRequest()
            }
        }
        return view
    }

    //位置情報が取得可能かチェックする
    private fun checkLocationAvailable(){
        //Activityがnullなら何もしない。
        val act = activity ?: return

        val checkRequest = LocationSettingsRequest.Builder().addLocationRequest(
            getLocationRequest()
        ).build()
        val checkTest = LocationServices.getSettingsClient(act)
            .checkLocationSettings(checkRequest)

        checkTest.addOnCompleteListener { response ->
            try{
                response.getResult(ApiException ::class.java)
                //位置情報は取得可能。パーミッション確認に進む
                Log.i("【SwitchFragment】", "[checkTest.addOnCompleteListener] !!位置情報取得可能!!")
                checkLocationPermission()
            }catch (exception: ApiException){
                if(exception.statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED){
                    //解決可能
                    Log.i("【SwitchFragment】", "[checkTest.addOnCompleteListener] 解決可能")
                    val resolvable = exception as ResolvableApiException
                    resolvable.startResolutionForResult(activity, 1)
                }else {
                    //解決不可能
                    Log.i("【SwitchFragment】", "[checkTest.addOnCompleteListener] 解決不可")
                    showErrorMessage()
                }
            }
        }
    }

    //位置情報取得のパーミッションを得ているかチェックする。
    private fun checkLocationPermission(){
        //contextがnullなら何もしない
        val ctx = context ?: return

        //パーミッションを確認
        if(ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED){
            //パーミッションが有効ならフラグをtrueにする。
            realm.executeTransaction{
                val gpsAuthority = realm.where<Authority>().findFirst()
                gpsAuthority?.flag = true
                Log.i("【SwitchFragment】", "[checkLocationPermission] !!パーミッション有効!!")
            }
        }else {
            //パーミッションを要求
            Log.i("【SwitchFragment】", "[checkLocationPermission] パーミッション非有効")
            requestPermissions(arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION), 1
            )
        }
    }

    //エラーを表示する
    private fun showErrorMessage(){
        Toast.makeText(context, resources.getString(R.string.cannotGetPosition), Toast.LENGTH_SHORT).show()
    }

    //位置情報のリクエスト設定を返すメソッド。今回はテスト用。リクエストはActivityから。
    private fun getLocationRequest() =
        LocationRequest()
            .setInterval(TimeUnit.MINUTES.toMillis(10))
            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)

    //startResolutionForResultが呼ばれた後にActivityから呼びだす。
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            //ユーザーが設定を変更してくれた
            checkLocationPermission()
        } else{
            //変更してくれなかった
            showErrorMessage()
        }
    }

    //パーミッションの設定結果を受け取ったときに呼ばれる。
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //パーミッションが付与されたら位置情報のリクエストを開始
            Log.i("【SwitchFragment】", "[onRequestPermissionsResult] パーミッション有効化")
            checkLocationPermission()
        } else {
            //パーミッションが付与されなかった
            showErrorMessage()
        }
    }

    //スイッチをオフにしたらフラグをオフにする。
    private fun stopLocationRequest(){
        realm.executeTransaction{
            val gpsAuthority = realm.where<Authority>().findFirst()
            gpsAuthority?.flag = false
            Log.i("【SwitchFragment】", "[stopLocationRequest] flagをfalseに")
        }
    }
}