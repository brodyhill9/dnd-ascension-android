package com.example.dndascension.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dndascension.R
import com.example.dndascension.fragments.CharactersFragment
import com.example.dndascension.models.Campaign
import com.example.dndascension.utils.CharacterFragmentType
import kotlinx.android.synthetic.main.activity_characters.*

class CharactersActivity : AppCompatActivity() {

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_characters)
        setSupportActionBar(chars_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var fragmentType = CharacterFragmentType.MyCharacters
        if (intent.hasExtra("fragmentType")) {
            fragmentType = intent.getSerializableExtra("fragmentType") as CharacterFragmentType
        }

        var camp: Campaign? = null
        if (intent.hasExtra("campaign")) {
            camp = intent.getSerializableExtra("campaign") as Campaign
            supportActionBar?.title = camp.camp_name
        }

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = CharactersFragment(fragmentType, camp)
        fragmentTransaction.add(R.id.chars_fragment_container, fragment)
        fragmentTransaction.commit()
    }
}
