package john.example.jp.kotlinproject.activity


import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter


/**
 * Created by naoi on 2017/04/24.
 */

class UserInfoViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = Fragment1()
            1 -> fragment = Fragment2()
            else -> fragment = Fragment3()
        }
        return fragment
    }

    override fun getCount(): Int {
        return PAGE_NUM
    }

    companion object {
        private val PAGE_NUM = 3
    }
}
