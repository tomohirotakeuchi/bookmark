package tafm.tt10tt10.mytesttravel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.yesButton
import tafm.tt10tt10.mytesttravel.adapter.MainAdapter
import tafm.tt10tt10.mytesttravel.model.Travel
import tafm.tt10tt10.mytesttravel.model.TravelDetail
import tafm.tt10tt10.mytesttravel.model.TravelPart

class MainActivity : AppCompatActivity() {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        //モデル変更したら以下の2行で初期化する必要あり。
//        val config = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build()
//        Realm.setDefaultConfiguration(config)
//        ///////////////////////////////////////////////////////////////////////////////////

        realm = Realm.getDefaultInstance()

        deleteFirst()
        val deleteFlag = 0
        var travels = realm.where<Travel>().equalTo("deleteFlag", deleteFlag).findAll()
        travels = travels.sort("manageId", Sort.DESCENDING)
        val adapter = MainAdapter(travels)
        mainListView.adapter = adapter

        //ListViewをタップ
        mainListView.setOnItemClickListener { parent, _, position, _ ->
            val tappedTravel = parent.getItemAtPosition(position) as Travel
            startActivity<Bookmark1Activity>("manageId" to tappedTravel.manageId)
        }

        adapter.setOnMainEditDeleteClickListener(object: MainAdapter.OnMainEditDeleteClickListener{
            override fun onMainEditDeleteClick(view: View, manageId: Int?, mainEditDeleteFlag: Int) {
                if(mainEditDeleteFlag == 1){
                    startActivity<Edit1TravelActivity>("manageId" to manageId)
                }else{
                    alert ("削除しますか？"){
                        yesButton {
                            delete(manageId ?: 0)
                        }
                        noButton {  }
                    }.show()
                }
            }
        })

        mainCreate.setOnClickListener {
            startActivity<SimpleCreate1Activity>()
        }
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
        Toast.makeText(applicationContext, "削除しました！", Toast.LENGTH_LONG).show()
    }

    //アクティビティ消滅時にrealmを閉じる。
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
