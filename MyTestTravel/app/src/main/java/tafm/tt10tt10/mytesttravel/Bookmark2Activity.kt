package tafm.tt10tt10.mytesttravel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.startActivity
import tafm.tt10tt10.mytesttravel.fragment.MenuFragment

class Bookmark2Activity : AppCompatActivity(), MenuFragment.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark2)

//        val manageId = intent.getIntExtra("manageId", 0).toString()
//        val day = intent.getIntExtra("day", 0).toString()

        val menuFragment = MenuFragment()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.bm2MenuFragment, menuFragment)
        transaction.commit()
    }

    //フラグメントのクリックリスナー
    override fun onClick() {
        startActivity<MainActivity>()
    }
}