package tafm.tt10tt10.mytesttravel

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit1_travel.*
import org.jetbrains.anko.startActivity
import tafm.tt10tt10.mytesttravel.model.Travel
import java.lang.StringBuilder

class Edit1TravelActivity : AppCompatActivity() {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit1_travel)

        val manageId = intent.getIntExtra("manageId", 1)

        realm = Realm.getDefaultInstance()
        val travel = realm.where<Travel>().equalTo("manageId", manageId).findFirst()
        edit1TravelTitle.setText(travel?.title)
        edit1DepartureDay.text = travel?.departureDay
        edit1ArrivalDay.text = travel?.arrivalDay
        val daysBuilder = StringBuilder()
            .append(travel?.travelDays?.minus(1)).append("泊").append(travel?.travelDays).append("日")
        edit1TravelDays.text = daysBuilder.toString()
        edit1DeparturePlace.setText(travel?.departurePlace)
        edit1ArrivalPlace.setText(travel?.arrivalPlace)


        edit1ApplyButton.setOnClickListener {
            startActivity<MainActivity>()
        }
        edit1BackToMain.setOnClickListener {
            finish()
        }
    }
}
