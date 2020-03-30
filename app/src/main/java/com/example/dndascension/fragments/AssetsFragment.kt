package com.example.dndascension.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dndascension.adapters.AssetsAdapter
import com.example.dndascension.interfaces.Asset
import com.example.dndascension.models.Spell
import com.example.dndascension.utils.AssetType
import kotlinx.android.synthetic.main.fragment_assets.*

class AssetsFragment(private val assetType: AssetType) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(com.example.dndascension.R.layout.fragment_assets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateList()
    }

    fun updateList() {
        var assets = mutableListOf<Asset>()
        assets.add(Spell(name = "Guiding Bolt", desc = "Level 1"))
        assets.add(Spell(name = "Spirit Guardians", desc = "Level 3"))
        assets.add(Spell(name = "Fireball", desc = "Level 1"))
        assets.add(Spell(name = assetType.toString(), desc = "Level 1"))

        val adapter = AssetsAdapter(activity?.applicationContext!!, assets)
        assets_list_view.adapter = adapter
    }
}
