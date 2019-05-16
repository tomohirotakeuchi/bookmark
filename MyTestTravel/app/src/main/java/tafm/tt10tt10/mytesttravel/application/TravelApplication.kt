package tafm.tt10tt10.mytesttravel.application

import android.app.Application
import io.realm.Realm

//データベースの設定処理をアプリケーション起動時に行う（manifestにも追加）
class TravelApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}