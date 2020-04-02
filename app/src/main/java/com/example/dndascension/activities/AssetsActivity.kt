package com.example.dndascension.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dndascension.adapters.AssetsPagerAdapter
import com.example.dndascension.utils.AssetType
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_assets.*

class AssetsActivity : AppCompatActivity() {
    private val TAG = AssetsActivity::class.java.simpleName

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.dndascension.R.layout.activity_assets)

        setSupportActionBar(assets_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        AssetType.values().forEach {
            assets_tab_layout.addTab(assets_tab_layout.newTab().setText(it.toString()))
        }

        val tabsAdapter = AssetsPagerAdapter(supportFragmentManager, AssetType.values().toList())
        assets_pager.adapter = tabsAdapter
        assets_pager.offscreenPageLimit = AssetType.values().count()
        assets_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(assets_tab_layout))
        assets_tab_layout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                assets_pager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        assets_pager.currentItem = AssetType.values().indexOf(AssetType.Spells)
    }
}
