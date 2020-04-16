package com.example.dndascension.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dndascension.R
import com.example.dndascension.fragments.CharactersFragment
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

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = CharactersFragment(CharacterFragmentType.MyCharacters)
        fragmentTransaction.add(R.id.chars_fragment_container, fragment)
        fragmentTransaction.commit()
    }
}
