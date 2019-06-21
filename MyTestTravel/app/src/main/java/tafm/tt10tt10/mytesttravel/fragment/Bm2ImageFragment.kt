package tafm.tt10tt10.mytesttravel.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import tafm.tt10tt10.mytesttravel.R

class Bm2ImageFragment : Fragment() {

//    private lateinit var realm: Realm
    private var listener: Bm2ImageEditOnClickListener? = null
    private var manageId = 1
    private var day = 1
    private var order = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bm2_image_fragment, container, false)
        val argument = arguments
        if (argument != null){
            manageId = argument["manageId"] as Int
            day = argument["day"] as Int
            order = argument["order"] as Int
            Log.i("【Bm2ImageFragment】", "[onCreateView] !!arguments!! manageId=$manageId day=$day order=$order")
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //クリックリスナー実装
        view.findViewById<ImageView>(R.id.bm2_image_edit).setOnClickListener {
            listener = context as? Bm2ImageEditOnClickListener
            listener?.onImageEditClick()
        }
    }

    //Bm2ImageEditのinterface。
    interface Bm2ImageEditOnClickListener{
        fun onImageEditClick()
    }

//    //フラグメント削除時にRealmを閉じる。
//    override fun onDestroy() {
//        super.onDestroy()
//        realm.close()
//    }
}