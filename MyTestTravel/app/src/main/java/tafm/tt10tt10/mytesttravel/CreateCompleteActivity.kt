package tafm.tt10tt10.mytesttravel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_create_complete.*
import org.jetbrains.anko.startActivity

class CreateCompleteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_complete)

        cmpBookmarkBtn.setOnClickListener {
            it.notPressTwice()
            startActivity<MainActivity>("fromId" to 1)
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
}
