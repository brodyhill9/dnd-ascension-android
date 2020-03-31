package com.example.dndascension.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.dndascension.fragments.AssetsFragment
import com.example.dndascension.utils.AssetType

class AssetsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return AssetsFragment(AssetType.values()[position])
    }

    override fun getCount(): Int {
        return AssetType.values().count()
    }

    override fun getPageTitle(position: Int): CharSequence {
        return AssetType.values()[position].toString()
    }
}
