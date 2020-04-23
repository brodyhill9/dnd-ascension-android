package com.example.dndascension.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dndascension.R
import com.example.dndascension.fragments.CampaignsFragment
import kotlinx.android.synthetic.main.activity_campaigns.*

class CampaignsActivity : AppCompatActivity() {

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaigns)
        setSupportActionBar(camps_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = CampaignsFragment()
        fragmentTransaction.add(R.id.camps_fragment_container, fragment)
        fragmentTransaction.commit()
    }
}
