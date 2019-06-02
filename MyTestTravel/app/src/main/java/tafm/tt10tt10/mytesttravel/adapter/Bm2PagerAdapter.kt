package tafm.tt10tt10.mytesttravel.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import tafm.tt10tt10.mytesttravel.fragment.Bm2ImageFragment
import tafm.tt10tt10.mytesttravel.fragment.Bm2MapFragment
import tafm.tt10tt10.mytesttravel.fragment.Bm2PlaceFragment
import tafm.tt10tt10.mytesttravel.fragment.Bm2ScheduleFragment

//FragmentPagerAdapterを継承したクラスを作成する。
class Bm2PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    //どのタブにどのFragmentを実装するか記述する。
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return Bm2PlaceFragment()
            }
            1 -> {
                return Bm2ScheduleFragment()
            }
            2 -> {
                return Bm2ImageFragment()
            }
            3 -> {
                return Bm2MapFragment()
            }
            else -> {
                return Bm2PlaceFragment()
            }
        }
    }

    //タブを4つに固定する。
    override fun getCount(): Int {
        return 4
    }
}