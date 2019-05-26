package tafm.tt10tt10.mytesttravel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.Toast
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_bookmark1.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.yesButton
import tafm.tt10tt10.mytesttravel.adapter.Bookmark1Adapter
import tafm.tt10tt10.mytesttravel.fragment.MenuFragment
import tafm.tt10tt10.mytesttravel.model.Travel
import tafm.tt10tt10.mytesttravel.model.TravelPart

class Bookmark1Activity : AppCompatActivity(), MenuFragment.OnClickListener {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark1)

        realm = Realm.getDefaultInstance()
        val manageId = intent.getIntExtra("manageId", -1)
        val travelParts = realm.where<TravelPart>()
            .equalTo("manageId", manageId)
            .sort("day", Sort.ASCENDING)
            .findAll()
        val travelDays = realm.where<Travel>().equalTo("manageId", manageId).findFirst()?.travelDays

        val adapter = Bookmark1Adapter(this, travelParts, true)
        bm1RecyclerView.adapter = adapter
        bm1RecyclerView.layoutManager = GridLayoutManager(this, 2)

        val menuFragment = MenuFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.bm1MenuFragment, menuFragment)
        transaction.commit()

        //RecyclerViewをタップ
        adapter.setOnBm1ItemClickListener(object: Bookmark1Adapter.OnBm1ItemClickListener{
            //作成したInterfaceを呼びだす。
            override fun onBm1ItemClick(view: View, manageId: Int?, day: Int?) {
                startActivity<Bookmark2Activity>("manageId" to manageId, "day" to day,"travelDays" to travelDays)
            }
        })

        adapter.setOnBm1EditClickListener(object: Bookmark1Adapter.OnBm1EditDeleteClickListener{
            override fun onBm1EditClick(view: View, manageId: Int?, day: Int?, bm1EditDeleteFlag: Int) {
                if(bm1EditDeleteFlag == 1){
                    startActivity<Edit2TravelActivity>("manageId" to manageId, "day" to day, "travelDays" to travelDays)
                }else{
                    alert ("削除しますか？"){
                        yesButton {
                            delete(manageId ?: 0, day ?: 0)
                        }
                        noButton {  }
                    }.show()
                }
            }
        })

        //戻るをタップ
        bm1BackToMain.setOnClickListener { finish() }
    }

    //削除タップ時の処理。
    private fun delete(manageId: Int, day: Int){
        realm.executeTransaction {
            //削除処理とデータ整形
        }
        Toast.makeText(applicationContext, "削除しました！", Toast.LENGTH_LONG).show()
    }

    //Viewが破壊されたときにRealmを閉じる。
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    //フラグメントのクリックリスナー。
    override fun onClick() {
        startActivity<MainActivity>()
    }
}