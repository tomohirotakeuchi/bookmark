package tafm.tt10tt10.mytesttravel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import tafm.tt10tt10.mytesttravel.adapter.Bookmark1Adapter
import tafm.tt10tt10.mytesttravel.adapter.MainAdapter
import tafm.tt10tt10.mytesttravel.model.Travel

class MainActivity : AppCompatActivity() {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        realm = Realm.getDefaultInstance()
        var travels = realm.where<Travel>().findAll()
        travels = travels.sort("manageId", Sort.DESCENDING)
        mainListView.adapter = MainAdapter(travels)

        //ListViewをタップ
        mainListView.setOnItemClickListener { parent, _, position, _ ->
            val tappedTravel = parent.getItemAtPosition(position) as Travel
            startActivity<Bookmark1Activity>("manageId" to tappedTravel.manageId)
        }

        mainCreate.setOnClickListener {
            startActivity<SimpleCreate1Activity>()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
