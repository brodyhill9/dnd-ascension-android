package com.example.dndascension.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.dndascension.fragments.AssetsFragment
import com.example.dndascension.utils.AssetType

class AssetsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return AssetsFragment(AssetType.Armor)
            1 -> return AssetsFragment(AssetType.Backgrounds)
            2 -> return AssetsFragment(AssetType.Classes)
            3 -> return AssetsFragment(AssetType.Feats)
            4 -> return AssetsFragment(AssetType.Races)
            5 -> return AssetsFragment(AssetType.Spells)
            6 -> return AssetsFragment(AssetType.Weapons)
            else -> return AssetsFragment(AssetType.Armor)
        }
    }

    override fun getCount(): Int {
        return AssetType.values().count()
    }

    override fun getPageTitle(position: Int): CharSequence {
        return AssetType.values()[position].toString()
    }
}
