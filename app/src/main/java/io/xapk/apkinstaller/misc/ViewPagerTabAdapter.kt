package io.xapk.apkinstaller.misc

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerTabAdapter : FragmentPagerAdapter {
    private var fragments = arrayOf<Fragment>()
    private var titles = arrayOf<String?>()

    constructor(fm: FragmentManager, fragments: Array<Fragment>) : super(fm) {
        this.fragments = fragments
    }

    constructor(fm: FragmentManager, fragments: Array<Fragment>, titles: Array<String?>) : super(fm) {
        this.fragments = fragments
        this.titles = titles
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position] ?: super.getPageTitle(position)
    }
}
