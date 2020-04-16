package com.example.dndascension.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.dndascension.fragments.CharactersFragment
import com.example.dndascension.models.Character
import com.example.dndascension.models.Trait
import com.example.dndascension.utils.setListViewHeightBasedOnChildren
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_character.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.contentView
import org.jetbrains.anko.yesButton


class CharacterActivity : AppCompatActivity() {
    companion object {
        const val START_EDIT_CHARACTER_ACTIVITY_REQUEST_CODE = 0
    }
    private lateinit var c: Character

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent().apply {
            putExtra("character", c)
        }
        setResult(Activity.RESULT_OK, intent)
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.dndascension.R.layout.activity_character)
        setSupportActionBar(char_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        c = intent.getSerializableExtra("character") as Character
        displayCharacter()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CharactersFragment.START_CHARACTER_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val charId = data?.getIntExtra("charId", -1)
                if (charId != null && charId > -1) {
                    setResult(Activity.RESULT_OK, data)
                    finish()
                } else {
                    val newChar = data?.getSerializableExtra("character") as Character
                    c = newChar
                    displayCharacter()
                    Snackbar.make(contentView!!, "${c.char_name} updated successfully", Snackbar.LENGTH_LONG).show()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun displayCharacter() {
        char_name.text = c.char_name
        char_tag.text = c.raceSubclassLevel()
        char_armor_class.text = c.armor_class.toString()
        char_speed.text = c.speed.toString()
        char_init.text = c.dexMod()
        char_prof_bonus.text = c.profBonus()

        char_as_str.text = c.strModScore()
        char_as_dex.text = c.dexModScore()
        char_as_con.text = c.conModScore()
        char_as_intel.text = c.intelModScore()
        char_as_wis.text = c.wisModScore()
        char_as_cha.text = c.chaModScore()

        char_st_str.text = c.strMod()
        char_st_dex.text = c.dexMod()
        char_st_con.text = c.conMod()
        char_st_intel.text = c.intelMod()
        char_st_wis.text = c.wisMod()
        char_st_cha.text = c.chaMod()

        char_acrobatics.text = c.dexMod()
        char_animal_handling.text = c.wisMod()
        char_arcana.text = c.intelMod()
        char_athletics.text = c.strMod()
        char_deception.text = c.chaMod()
        char_history.text = c.intelMod()
        char_insight.text = c.wisMod()
        char_intimidation.text = c.chaMod()
        char_investigation.text = c.intelMod()
        char_medicine.text = c.wisMod()
        char_nature.text = c.intelMod()
        char_perception.text = c.wisMod()
        char_performance.text = c.chaMod()
        char_persuasion.text = c.chaMod()
        char_religion.text = c.intelMod()
        char_sleight_of_hand.text = c.dexMod()
        char_stealth.text = c.dexMod()
        char_survival.text = c.wisMod()

        displayRaceTraits()
        displayClassTraits()

        btn_edit.setOnClickListener {
            val intent = Intent(applicationContext, EditCharacterActivity::class.java)
            intent.putExtra("character", c)
            startActivityForResult(intent, START_EDIT_CHARACTER_ACTIVITY_REQUEST_CODE)
        }
    }
    private fun displayRaceTraits() {
        val adapter = ArrayAdapter(applicationContext, com.example.dndascension.R.layout.item_text_link, c.race_traits)
        char_race_traits.adapter = adapter
        char_race_traits.setOnItemClickListener {parent, _, position, _ ->
            val trait = parent.getItemAtPosition(position) as Trait
            alert(trait.trait_desc, trait.trait_name) {
                yesButton { "Ok" }
            }!!.show()
        }
        c.race_traits.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.trait_name })
        c.race_traits.sortBy { it.char_level }
        adapter.notifyDataSetChanged()
        setListViewHeightBasedOnChildren(char_race_traits)
    }
    private fun displayClassTraits() {
        val adapter = ArrayAdapter(applicationContext, com.example.dndascension.R.layout.item_text_link, c.class_traits)
        char_class_traits.adapter = adapter
        char_class_traits.setOnItemClickListener {parent, _, position, _ ->
            val trait = parent.getItemAtPosition(position) as Trait
            alert(trait.trait_desc, trait.trait_name) {
                yesButton { "Ok" }
            }!!.show()
        }
        c.class_traits.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.trait_name })
        c.class_traits.sortBy { it.char_level }
        adapter.notifyDataSetChanged()
        setListViewHeightBasedOnChildren(char_class_traits)
    }
}
