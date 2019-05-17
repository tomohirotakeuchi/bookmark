package tafm.tt10tt10.mytesttravel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.yesButton
import tafm.tt10tt10.mytesttravel.adapter.Bookmark1Adapter
import tafm.tt10tt10.mytesttravel.adapter.MainAdapter
import tafm.tt10tt10.mytesttravel.model.Travel
import tafm.tt10tt10.mytesttravel.model.TravelDetail
import tafm.tt10tt10.mytesttravel.model.TravelPart

class MainActivity : AppCompatActivity() {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        realm = Realm.getDefaultInstance()
        var travels = realm.where<Travel>().findAll()
        travels = travels.sort("manageId", Sort.DESCENDING)
        val adapter = MainAdapter(travels)
        mainListView.adapter = adapter

        //ListViewをタップ
        mainListView.setOnItemClickListener { parent, _, position, _ ->
            val tappedTravel = parent.getItemAtPosition(position) as Travel
            startActivity<Bookmark1Activity>("manageId" to tappedTravel.manageId)
        }

        adapter.setOnEditDeleteClickListener(object: MainAdapter.OnEditDeleteClickListener{
            override fun onEditDeleteClick(view: View, manageId: Int?, editDeleteFlag: Int) {
                if(editDeleteFlag == 1){
                    startActivity<SimpleCreate1Activity>("EditFlag" to true)
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

    //削除タップ時にmanageIdをもつものをすべて削除。
    private fun delete(manageId: Int){
        realm.executeTransaction {
            realm.where<Travel>().equalTo("manageId", manageId).findFirst()?.deleteFromRealm()
            realm.where<TravelPart>().equalTo("manageId", manageId).findAll()?.deleteAllFromRealm()
            realm.where<TravelDetail>().equalTo("manageId", manageId).findAll()?.deleteAllFromRealm()
        }
        Toast.makeText(applicationContext, "削除しました！", Toast.LENGTH_LONG).show()
    }
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
