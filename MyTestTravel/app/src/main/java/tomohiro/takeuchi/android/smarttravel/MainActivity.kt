package tomohiro.takeuchi.android.smarttravel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.yesButton
import tomohiro.takeuchi.android.smarttravel.adapter.MainAdapter
import tomohiro.takeuchi.android.smarttravel.model.Travel
import tomohiro.takeuchi.android.smarttravel.model.TravelDetail
import tomohiro.takeuchi.android.smarttravel.model.TravelPart

class MainActivity : AppCompatActivity() {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        realm = Realm.getDefaultInstance()

        deleteFirst()
        val deleteFlag = 0
        var travels = realm.where<Travel>()
            .equalTo("deleteFlag", deleteFlag)
            .findAll()
        travels = travels.sort("manageId", Sort.DESCENDING)
        val adapter = MainAdapter(travels)
        mainListView.adapter = adapter

        //ListViewをタップ
        mainListView.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            mainListView.notPressTwice()
            val tappedTravel = parent.getItemAtPosition(position) as Travel
            startActivity<Bookmark1Activity>("manageId" to tappedTravel.manageId)
        }

        adapter.setOnMainEditDeleteClickListener(object: MainAdapter.OnMainEditDeleteClickListener{
            override fun onMainEditDeleteClick(view: View, manageId: Int?, mainEditDeleteFlag: Int) {
                if(mainEditDeleteFlag == 1){
                    startActivity<EditMainActivity>("manageId" to manageId)
                }else{
                    alert (resources.getString(R.string.deleteConform)){
                        yesButton {
                            delete(manageId ?: 0)
                        }
                        noButton {  }
                    }.show()
                }
            }
        })

        mainCreate.setOnClickListener {
            it.notPressTwice()
            startActivity<SimpleCreate1Activity>()
        }

        // AdMob広告の初期化. アプリIDはあとで変更する。
        MobileAds.initialize(this, resources.getString(R.string.adAppId))
        // AdMob広告をロードする
        val adRequest = AdRequest.Builder().build()
        mainAdView.loadAd(adRequest)
    }

    //deleteFlagが0なら削除する。
    private fun deleteFirst() {
        realm.executeTransaction {
           val deleteFlag = 1
           realm.where<Travel>().equalTo("deleteFlag", deleteFlag).findFirst()?.deleteFromRealm()
           realm.where<TravelPart>().equalTo("deleteFlag", deleteFlag).findAll()?.deleteAllFromRealm()
           realm.where<TravelDetail>().equalTo("deleteFlag", deleteFlag).findAll()?.deleteAllFromRealm()
        }
    }

    //削除タップ時にmanageIdをもつものをすべて削除。
    private fun delete(manageId: Int){
        realm.executeTransaction {
            realm.where<Travel>().equalTo("manageId", manageId).findFirst()?.deleteFromRealm()
            realm.where<TravelPart>().equalTo("manageId", manageId).findAll()?.deleteAllFromRealm()
            realm.where<TravelDetail>().equalTo("manageId", manageId).findAll()?.deleteAllFromRealm()
        }
        Toast.makeText(applicationContext, resources.getString(R.string.deleteSuccess), Toast.LENGTH_LONG).show()
    }

    //スイッチフラグメントに値を引き渡す。
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1){
            val switchFragment = supportFragmentManager.findFragmentById(R.id.mainLocationSwitch)
            switchFragment?.onActivityResult(requestCode, resultCode, data)
        }
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

    //アクティビティ消滅時にrealmを閉じる。
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
