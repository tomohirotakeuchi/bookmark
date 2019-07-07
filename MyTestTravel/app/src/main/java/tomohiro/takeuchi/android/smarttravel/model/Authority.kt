package tomohiro.takeuchi.android.smarttravel.model


import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Authority : RealmObject() {
    @PrimaryKey
    var id : Long = 0L
    var flag : Boolean = false
}