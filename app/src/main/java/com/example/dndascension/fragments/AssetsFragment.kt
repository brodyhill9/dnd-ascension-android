package com.example.dndascension.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.example.dndascension.adapters.AssetsAdapter
import com.example.dndascension.interfaces.Asset
import com.example.dndascension.models.Spell
import com.example.dndascension.utils.ApiClient
import com.example.dndascension.utils.AssetType
import kotlinx.android.synthetic.main.fragment_assets.*
import org.jetbrains.anko.toast
import kotlin.concurrent.thread

class AssetsFragment(private val assetType: AssetType) : Fragment() {
    private val TAG = this::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(com.example.dndascension.R.layout.fragment_assets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateList()
    }

    fun updateList() {
        try {
            when(assetType) {
                AssetType.Spells -> {
                    ApiClient(activity?.applicationContext!!).GetSpells { spells, message ->
                        if (spells != null) {
                            Log.e(TAG, "Retrieved spells")
                            thread {
                                runOnUiThread {
                                    val adapter = AssetsAdapter(activity?.applicationContext!!, spells)
                                    assets_list_view.adapter = adapter
                                }
                            }
                        } else {
                            activity?.toast(message)
                        }
                    }
                }
                else -> {
                    var assets = mutableListOf<Asset>()
                    assets.add(Spell(name = assetType.toString()))
                    val adapter = AssetsAdapter(activity?.applicationContext!!, assets)
                    assets_list_view.adapter = adapter
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
}
