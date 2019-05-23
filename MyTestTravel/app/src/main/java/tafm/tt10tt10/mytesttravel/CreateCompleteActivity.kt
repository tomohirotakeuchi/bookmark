package tafm.tt10tt10.mytesttravel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_create_complete.*
import org.jetbrains.anko.startActivity

class CreateCompleteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_complete)

        cmpBookmarkBtn.setOnClickListener {
            startActivity<MainActivity>("fromId" to 1)
        }
    }
}
