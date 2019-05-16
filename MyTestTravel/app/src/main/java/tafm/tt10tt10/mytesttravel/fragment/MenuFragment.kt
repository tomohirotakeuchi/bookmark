package tafm.tt10tt10.mytesttravel.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import tafm.tt10tt10.mytesttravel.R
import java.lang.RuntimeException

class MenuFragment: Fragment(){

    private var listener: OnClickListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context !is OnClickListener){
            throw RuntimeException("リスナーを実装せよ")
        }
    }

    // Fragmentで表示するViewを作成するメソッド
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.menu_fragment, container, false)
    }

    //Viewが生成された後に呼ばれる。クリックリスナー
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.menuFragmentSearch).setOnClickListener {
            listener = context as? OnClickListener
            listener?.onClick()
        }
    }

    //interface。
    interface OnClickListener {
        fun onClick()
    }
}