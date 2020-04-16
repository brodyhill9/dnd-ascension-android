package com.example.dndascension.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dndascension.R
import com.example.dndascension.fragments.AssetsFragment
import com.example.dndascension.utils.AssetType
import kotlinx.android.synthetic.main.activity_choose_asset.*

class ChooseAssetActivity : AppCompatActivity() {

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_asset)
        setSupportActionBar(choose_asset_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val assetType = intent.getSerializableExtra("assetType") as AssetType
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = AssetsFragment(assetType, true)
        fragmentTransaction.add(R.id.assets_fragment_container, fragment)
        fragmentTransaction.commit()

        choose_asset_toolbar.title = "Add " + when(assetType) {
            AssetType.Armor -> "Armor"
            AssetType.Spells -> "Spell"
            AssetType.Weapons -> "Weapon"
            else -> ""
        }
    }
}
