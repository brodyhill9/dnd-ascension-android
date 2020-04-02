package com.example.dndascension.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dndascension.R
import com.example.dndascension.adapters.AssetsAdapter
import com.example.dndascension.interfaces.Asset
import com.example.dndascension.models.Spell
import com.example.dndascension.utils.ApiClient
import com.example.dndascension.utils.AssetType
import kotlinx.android.synthetic.main.fragment_assets.*
import org.jetbrains.anko.longToast

class AssetsFragment(private val assetType: AssetType) : Fragment() {
    private val TAG = this::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_assets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateList()
    }

    private fun updateList() {
        try {
            when(assetType) {
                AssetType.Spells -> {
                    ApiClient(activity?.applicationContext!!).GetSpells { spells, message ->
                        if (spells != null) {
                            Log.e(TAG, "Retrieved spells")
                            val adapter = AssetsAdapter(activity?.applicationContext!!, spells)
                            assets_list_view.adapter = adapter

                            assets_list_view.setOnItemClickListener {parent, view, position, id ->
                                val asset = parent.getItemAtPosition(position) as Spell
                                val fragment = SpellDialogFragment(asset)
                                fragment.show(activity?.supportFragmentManager!!, fragment.tag)
                            }
                        } else {
                            activity?.longToast(message)
                        }
                    }
                }
                else -> {
                    val assets = mutableListOf<Asset>()
                    assets.add(Spell(spell_name = assetType.toString()))
                    val adapter = AssetsAdapter(activity?.applicationContext!!, assets)
                    assets_list_view.adapter = adapter
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
}
