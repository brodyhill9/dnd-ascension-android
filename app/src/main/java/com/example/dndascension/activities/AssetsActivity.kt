package com.example.dndascension.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dndascension.R
import com.example.dndascension.adapters.AssetsAdapter
import com.example.dndascension.models.Asset
import com.example.dndascension.models.Spell

class AssetsActivity : AppCompatActivity() {
    private val TAG = AssetsActivity::class.java.simpleName
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assets)

        val actionBar = supportActionBar
        actionBar?.title = ""
        actionBar?.setDisplayHomeAsUpEnabled(true)

        updateList();
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_assets, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_assets_armor) {

        } else if (id == R.id.action_sign_out) {

        }
        Toast.makeText(this@AssetsActivity, "You have clicked " + item.title, Toast.LENGTH_SHORT).show()
        return super.onOptionsItemSelected(item)
    }

    fun updateList() {
        var assets = mutableListOf<Asset>()
        assets.add(Spell(spell_name = "Guiding Bolt", spell_desc = "Level 1"))
        assets.add(Spell(spell_name = "Spirit Guardians", spell_desc = "Level 3"))
        assets.add(Spell(spell_name = "Fireball", spell_desc = "Level 1"))

        listView = findViewById(R.id.assets_list_view)

        val adapter = AssetsAdapter(this, assets)
        listView.adapter = adapter
    }
}
