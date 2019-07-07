package tafm.tt10tt10.mytesttravel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
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
        // AdMob広告をロードする
        val adRequest = AdRequest.Builder().build()
        cmpAdView.loadAd(adRequest)
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
