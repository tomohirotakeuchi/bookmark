package tomohiro.takeuchi.android.smarttravel.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Travel: RealmObject() {

    @PrimaryKey
    var manageId: Int = 0                   //管理ID
    var title: String = ""                  //タイトル
    var travelDays: Int = 0				    //旅行日数
    var departureDay: String = ""           //出発日
    var departurePlace: String = ""		    //出発地
    var departureTime: String = ""          //出発時刻
    var departureLatitude: Double = 0.0     //出発緯度
    var departureLongitude: Double = 0.0    //出発経度
    var arrivalDay: String = ""             //到着日
    var arrivalPlace: String = ""		    //到着地
    var arrivalTime: String = ""            //到着時刻
    var arrivalLatitude: Double = 0.0       //到着緯度
    var arrivalLongitude: Double = 0.0      //到着経度
    var totalCost: Int = 0				    //合計費用
    var deleteFlag: Int = 1                 //デリートフラグ1なら削除
}