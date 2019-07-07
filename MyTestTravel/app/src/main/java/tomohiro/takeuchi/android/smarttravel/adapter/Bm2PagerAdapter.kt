package tomohiro.takeuchi.android.smarttravel.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.util.Log
import tomohiro.takeuchi.android.smarttravel.fragment.Bm2ImageFragment
import tomohiro.takeuchi.android.smarttravel.fragment.Bm2MapFragment
import tomohiro.takeuchi.android.smarttravel.fragment.Bm2PlaceFragment
import tomohiro.takeuchi.android.smarttravel.fragment.Bm2ScheduleFragment

//FragmentPagerAdapterを継承したクラスを作成する。
class Bm2PagerAdapter(fm: FragmentManager, bundle: Bundle) : FragmentPagerAdapter(fm) {

    private val args = bundle

    //どのタブにどのFragmentを実装するか記述する。
    override fun getItem(position: Int): Fragment {
        val fragment: Fragment
        when (position) {
            0 -> fragment = Bm2PlaceFragment()
            1 -> fragment = Bm2ScheduleFragment()
            2 -> fragment = Bm2ImageFragment()
            3 -> fragment = Bm2MapFragment()
            else -> fragment = Bm2PlaceFragment()
        }

        Log.i("【Bm2PagerAdapter】", "$args")
        fragment.arguments = args
        return fragment
    }

    //タブを4つに固定する。
    override fun getCount(): Int {
        return 4
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
}