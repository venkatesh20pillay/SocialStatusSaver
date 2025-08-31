package com.statuses.statussavers.ui.main

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.statuses.statussavers.ImagesFragment
import com.statuses.statussavers.R
import com.statuses.statussavers.SavedFragment
import com.statuses.statussavers.VideosFragment

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val mContext: Context, fm: FragmentManager?) :
    FragmentPagerAdapter(
        fm!!
    ) {
    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = ImagesFragment()
            1 -> fragment = VideosFragment()
            2 -> fragment = SavedFragment()
        }
        return fragment!!
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mContext.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 3
    }

    companion object {
        @StringRes
        private val TAB_TITLES =
            intArrayOf(R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3)
    }
}