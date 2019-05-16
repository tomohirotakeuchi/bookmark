package tafm.tt10tt10.mytesttravel.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class TravelPart :RealmObject(){
    @PrimaryKey
    var id: Int = 0			    	//ID
    var manageId: Int = 0   		//管理ID
    var day: Int = 0    			//日目
    var destination1: String = ""	//目的地1
    var destination2: String = ""	//目的地2
    var destination3: String = ""	//目的地3
    var destination4: String = ""	//目的地4
    var destination5: String = ""	//目的地5
    var destination6: String = ""	//目的地6
    var deleteFlag: Int = 1         //デリートフラグ1なら削除
}