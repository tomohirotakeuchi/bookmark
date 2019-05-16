package tafm.tt10tt10.mytesttravel.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class TravelDetail: RealmObject(){

    @PrimaryKey
    var id: Int = 0				    //ID
    var manageId: Int = 0		    //管理ID
    var day: Int = 0			    //日目
    var order: Int = 0              //目的地順序
    var destination: String = ""	//目的地
    var startTime: String = ""		//開始時刻
    var requiredTime: String = ""	//所要時間
    var moveTime: String = ""		//移動時間
    var cost: Int = 0   			//費用
    var memo: String = ""			//メモ
    var imageUrl1: String = ""		//画像添付URL1
    var imageUrl2: String = ""		//画像添付URL2
    var imageUrl3: String = ""		//画像添付URL3
    var latitude: String = ""		//緯度
    var longitude: String = ""		//経度
    var deleteFlag: Int = 0         //デリートフラグ1なら削除
}