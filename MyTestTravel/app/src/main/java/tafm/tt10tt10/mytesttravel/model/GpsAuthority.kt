package tafm.tt10tt10.mytesttravel.model


import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class GpsAuthority : RealmObject() {
    @PrimaryKey
    var id : Long = 0L
    var flag : Boolean = false
}