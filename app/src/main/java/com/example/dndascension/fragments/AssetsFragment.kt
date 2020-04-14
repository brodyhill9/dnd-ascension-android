package com.example.dndascension.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.example.dndascension.R
import com.example.dndascension.activities.EditAssetActivity
import com.example.dndascension.adapters.AssetsAdapter
import com.example.dndascension.interfaces.Asset
import com.example.dndascension.models.*
import com.example.dndascension.utils.ApiClient
import com.example.dndascension.utils.AssetType
import kotlinx.android.synthetic.main.fragment_assets.*
import kotlinx.android.synthetic.main.progress_bar.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import kotlin.concurrent.thread

class AssetsFragment(private val assetType: AssetType) : Fragment() {
    companion object {
        const val START_ASSET_DIALOG_FRAGMENT_REQUEST_CODE = 0
        const val START_EDIT_ASSET_ACTIVITY_REQUEST_CODE = 0
    }
    private val TAG = this::class.java.simpleName
    private lateinit var assets: MutableList<Asset>
    private lateinit var adapter: AssetsAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_assets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateList()

        assets_btn_add.setOnClickListener {
            val asset = when(assetType) {
                AssetType.Armor -> Armor()
                AssetType.Backgrounds -> Background()
                AssetType.Classes -> DndClass()
                AssetType.Feats -> Feat()
                AssetType.Races -> Race()
                AssetType.Spells -> Spell()
                AssetType.Weapons -> Weapon()
                else -> Feat()
            }

            val intent = Intent(activity?.applicationContext, EditAssetActivity::class.java)
            intent.putExtra("asset", asset)
            startActivityForResult(intent, START_EDIT_ASSET_ACTIVITY_REQUEST_CODE)
        }
    }

    private fun updateList() {
        try {
            thread { runOnUiThread { progress_bar.isVisible = true } }
            when(assetType) {
                AssetType.Armor -> {
                    ApiClient(activity?.applicationContext!!).getArmor { armor, message ->
                        if (armor == null) {
                            activity?.toast(message)
                        } else {
                            Log.i(TAG, "Retrieved armor")
                            assets = armor as MutableList<Asset>
                            createListView(message)
                        }
                    }
                }
                AssetType.Backgrounds -> {
                    ApiClient(activity?.applicationContext!!).getBackgrounds { backgrounds, message ->
                        if (backgrounds == null) {
                            activity?.toast(message)
                        } else {
                            Log.i(TAG, "Retrieved backgrounds")
                            assets = backgrounds as MutableList<Asset>
                            createListView(message)
                        }
                    }
                }
                AssetType.Classes -> {
                    ApiClient(activity?.applicationContext!!).getClasses() { classes, message ->
                        if (classes == null) {
                            activity?.toast(message)
                        } else {
                            Log.i(TAG, "Retrieved classes")
                            assets = classes as MutableList<Asset>
                            createListView(message)
                        }
                    }
                }
                AssetType.Feats -> {
                    ApiClient(activity?.applicationContext!!).getFeats { feats, message ->
                        if (feats == null) {
                            activity?.toast(message)
                        } else {
                            Log.i(TAG, "Retrieved feats")
                            assets = feats as MutableList<Asset>
                            createListView(message)
                        }
                    }
                }
                AssetType.Races -> {
                    ApiClient(activity?.applicationContext!!).getRaces { races, message ->
                        if (races == null) {
                            activity?.toast(message)
                        } else {
                            Log.i(TAG, "Retrieved races")
                            assets = races as MutableList<Asset>
                            createListView(message)
                        }
                    }
                }
                AssetType.Spells -> {
                    ApiClient(activity?.applicationContext!!).getSpells { spells, message ->
                        if (spells == null) {
                            activity?.toast(message)
                        } else {
                            Log.i(TAG, "Retrieved spells")
                            assets = spells as MutableList<Asset>
                            createListView(message)
                        }
                    }
                }
                AssetType.Weapons -> {
                    ApiClient(activity?.applicationContext!!).getWeapons { weapons, message ->
                        if (weapons == null) {
                            activity?.toast(message)
                        } else {
                            Log.i(TAG, "Retrieved weapons")
                            assets = weapons as MutableList<Asset>
                            createListView(message)
                        }
                    }
                }
                else -> {
                    thread { runOnUiThread { progress_bar.isVisible = false } }
                    assets = mutableListOf()
                    createListView("")
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    private fun createListView(message: String) {
        thread { runOnUiThread { progress_bar.isVisible = false } }
        if (assets != null) {
            assets.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name() })
            assets.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.secSort() })
            adapter = AssetsAdapter(activity?.applicationContext!!, assets)
            assets_list_view.adapter = adapter

            assets_list_view.setOnItemClickListener {parent, view, position, id ->
                val asset = parent.getItemAtPosition(position) as Asset
                val fragment = AssetDialogFragment(asset)
                fragment.setTargetFragment(this, START_ASSET_DIALOG_FRAGMENT_REQUEST_CODE)
                fragment.show(activity?.supportFragmentManager!!, fragment.tag)
            }
        } else {
            activity?.longToast(message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == START_ASSET_DIALOG_FRAGMENT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val assetId = data?.getIntExtra("assetId", -1)

                if (assetId != null && assetId > -1) {
                    val index = assets.indexOfFirst {it.id() == assetId}
                    assets.removeAt(index)
                } else {
                    val asset = data?.getSerializableExtra("asset") as Asset
                    val index = assets.indexOfFirst {it.id() == asset.id()}
                    if (index > -1) {
                        assets[index] = asset
                    } else {
                        assets.add(asset)
                    }
                }

                assets.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name() })
                assets.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.secSort() })
                adapter.notifyDataSetChanged()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
