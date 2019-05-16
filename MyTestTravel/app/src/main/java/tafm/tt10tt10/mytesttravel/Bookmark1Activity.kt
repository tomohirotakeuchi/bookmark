package tafm.tt10tt10.mytesttravel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_bookmark1.*
import org.jetbrains.anko.startActivity
import tafm.tt10tt10.mytesttravel.adapter.Bookmark1Adapter
import tafm.tt10tt10.mytesttravel.fragment.MenuFragment
import tafm.tt10tt10.mytesttravel.model.TravelPart

class Bookmark1Activity : AppCompatActivity(), MenuFragment.OnClickListener {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark1)

        realm = Realm.getDefaultInstance()
        val manageId = intent.getIntExtra("manageId", -1)
        val travelParts = realm.where<TravelPart>().equalTo("manageId", manageId).findAll()

        val adapter = Bookmark1Adapter(this, travelParts, true)
        bm1RecyclerView.adapter = adapter
        bm1RecyclerView.layoutManager = GridLayoutManager(this, 2)

        val menuFragment = MenuFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.bm1MenuFragment, menuFragment)
        transaction.commit()

        //RecyclerViewをタップ
        adapter.setOnItemClickListener(object: Bookmark1Adapter.OnItemClickListener{
            //作成したInterfaceを呼びだす。
            override fun onItemClick(view: View, manageId: Int?, day: Int?) {
                startActivity<Bookmark2Activity>("manageId" to manageId, "day" to day)
            }
        })

        //戻るをタップ
        bm1BackToMain.setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onClick() {
        startActivity<MainActivity>()
    }
}