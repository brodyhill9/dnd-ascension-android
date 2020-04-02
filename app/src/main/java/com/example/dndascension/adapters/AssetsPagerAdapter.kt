package com.example.dndascension.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.dndascension.fragments.AssetsFragment
import com.example.dndascension.utils.AssetType

class AssetsPagerAdapter(fm: FragmentManager,
                         private val assetTypes: List<AssetType>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return AssetsFragment(assetTypes[position])
    }

    override fun getCount(): Int {
        return assetTypes.count()
    }

    override fun getPageTitle(position: Int): CharSequence {
        return assetTypes[position].toString()
    }
}
