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
    var totalCost: Int = 0   		//費用 totalCost
    var costItem1: Int = 0   		//費用1
    var costItem2: Int = 0   		//費用2
    var costItem3: Int = 0   		//費用3
    var costItem4: Int = 0   		//費用4
    var costItem5: Int = 0   		//費用5
    var memo1: String = ""			//メモ1
    var memo2: String = ""			//メモ2
    var memo3: String = ""			//メモ3
    var urlLink1: String = ""		//URLリンク1
    var urlLink2: String = ""		//URLリンク2
    var urlLink3: String = ""		//URLリンク3
    var imageUrl1: String = ""		//画像添付URL1
    var imageUrl2: String = ""		//画像添付URL2
    var imageUrl3: String = ""		//画像添付URL3
    var latitude: Double = 0.0		//緯度
    var longitude: Double = 0.0		//経度
    var deleteFlag: Int = 0         //デリートフラグ1なら削除
}