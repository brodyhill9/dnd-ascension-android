package com.example.dndascension.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.dndascension.models.Background
import com.example.dndascension.models.Character
import com.example.dndascension.models.DndClass
import com.example.dndascension.models.Race
import com.example.dndascension.utils.ApiClient
import kotlinx.android.synthetic.main.activity_edit_character.*
import kotlinx.android.synthetic.main.progress_overlay.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import kotlin.concurrent.thread



class EditCharacterActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName
    private lateinit var c: Character
    private lateinit var title: String
    private lateinit var races: List<Race>
    private lateinit var classes: List<DndClass>
    private lateinit var backgrounds: List<Background>

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.dndascension.R.layout.activity_edit_character)

        c = intent.getSerializableExtra("character") as Character
        setSupportActionBar(edit_char_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        if (c.isNew()) {
            progress_overlay.isVisible = true
            title = "New Character"
            btn_delete.visibility = View.GONE
            ApiClient(applicationContext).getRaces { races, message ->
                if (races == null) {
                    progress_overlay.isVisible = false
                    toast(message)
                    btn_save.visibility = View.GONE
                    btn_delete.visibility = View.GONE
                } else {
                    Log.i(TAG, "Retrieved races")
                    this.races = races
                    characterEditor()
                }
            }
            ApiClient(applicationContext).getClasses { classes, message ->
                if (classes == null) {
                    progress_overlay.isVisible = false
                    toast(message)
                    btn_save.visibility = View.GONE
                    btn_delete.visibility = View.GONE
                } else {
                    Log.i(TAG, "Retrieved classes")
                    this.classes = classes
                    characterEditor()
                }
            }
            ApiClient(applicationContext).getBackgrounds { backgrounds, message ->
                if (backgrounds == null) {
                    progress_overlay.isVisible = false
                    toast(message)
                    btn_save.visibility = View.GONE
                    btn_delete.visibility = View.GONE
                } else {
                    Log.i(TAG, "Retrieved backgrounds")
                    this.backgrounds = backgrounds
                    characterEditor()
                }
            }
        } else {
            title = "Edit ${c.char_name}"
            races = listOf()
            classes = listOf()
            backgrounds = listOf()
            edit_char_new_only_container.visibility = View.GONE
        }
        edit_char_toolbar_title.text = title
        characterEditor()
    }

    private fun evaluateSave(newChar: Character?, message: String = "") {
        thread { runOnUiThread { progress_overlay.isVisible = false } }
        if (newChar == null) {
            alert(message, getString(com.example.dndascension.R.string.title_api_error)) { yesButton { "Ok" } }.show()
        } else {
            val intent = Intent().apply {
                putExtra("character", newChar)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
    private fun evaluateDelete(charId: Int, error: Boolean = false, message: String = "") {
        thread { runOnUiThread { progress_overlay.isVisible = false } }
        if (error) {
            alert(message, getString(com.example.dndascension.R.string.title_api_error)) { yesButton { "Ok" } }.show()
        } else {
            val intent = Intent().apply {
                putExtra("charId", charId)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun characterEditor() {
        if (!::races.isInitialized || !::classes.isInitialized || !::backgrounds.isInitialized) {
            return
        }
        if (c.isNew()) {
            progress_overlay.isVisible = false
            initRaces()
            initClasses()
            initBackgrounds()
        }

        edit_char_name.setText(c.char_name)
        edit_char_lvl.setText(c.lvl.toString())
        edit_char_armor_class.setText(c.armor_class.toString())
        edit_char_caster.isChecked = c.caster

        edit_char_str.setText(c.str.toString())
        edit_char_dex.setText(c.dex.toString())
        edit_char_con.setText(c.con.toString())
        edit_char_intel.setText(c.intel.toString())
        edit_char_wis.setText(c.wis.toString())
        edit_char_cha.setText(c.cha.toString())

        btn_save.setOnClickListener {
            if (edit_char_name.text.isNullOrBlank()) {
                alert(
                    "Character name is required",
                    getString(com.example.dndascension.R.string.title_form_error)
                ) { yesButton { "Ok" } }.show()
            } else {
                if (c.isNew()) {
                    val race = races[edit_char_race.selectedItemPosition]
                    c.race_id = race.race_id
                    if (race.hasSubraces()) { c.race_id = race.subraces[edit_char_subrace.selectedItemPosition].race_id }

                    val dndClass = classes[edit_char_class.selectedItemPosition]
                    c.class_id = dndClass.class_id
                    if (dndClass.hasSubclasses()) { c.class_id = dndClass.subclasses[edit_char_subclass.selectedItemPosition].class_id }

                    c.background_id = backgrounds[edit_char_background.selectedItemPosition].value_id
                }

                c.char_name = edit_char_name.text.toString()
                c.lvl = edit_char_lvl.text.toString().toIntOrNull() ?: 0
                c.armor_class = edit_char_armor_class.text.toString().toIntOrNull() ?: 0
                c.caster = edit_char_caster.isChecked

                c.str = edit_char_str.text.toString().toIntOrNull() ?: 0
                c.dex = edit_char_dex.text.toString().toIntOrNull() ?: 0
                c.con = edit_char_con.text.toString().toIntOrNull() ?: 0
                c.intel = edit_char_intel.text.toString().toIntOrNull() ?: 0
                c.wis = edit_char_wis.text.toString().toIntOrNull() ?: 0
                c.cha = edit_char_cha.text.toString().toIntOrNull() ?: 0

                thread { runOnUiThread { progress_overlay.isVisible = true } }
                ApiClient(applicationContext!!).saveCharacter(c) { newCharacter, message ->
                    evaluateSave(newCharacter, message)
                }
            }
        }
        btn_delete.setOnClickListener {
            thread { runOnUiThread { progress_overlay.isVisible = true } }
            ApiClient(applicationContext!!).deleteCharacter(c.char_id!!) { error, message ->
                evaluateDelete(c.char_id!!, error, message)
            }
        }
    }
    private fun initRaces() {
        edit_char_race.adapter = ArrayAdapter(this, com.example.dndascension.R.layout.spinner_dropdown_item, races)
        edit_char_race.setSelection(races.indexOfFirst { it.race_id == c.race_id ?: it.race_id })

        edit_char_race.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                val race = edit_char_race.selectedItem as Race
                if (race.hasSubraces()) {
                    edit_char_subrace.adapter = ArrayAdapter(applicationContext, com.example.dndascension.R.layout.spinner_dropdown_item, race.subraces)
                    edit_char_subrace.setSelection(race.subraces.indexOfFirst { it.race_id == c.subrace_id ?: it.race_id })
                    edit_char_subrace_container.visibility = View.VISIBLE
                } else {
                    edit_char_subrace.adapter = ArrayAdapter(applicationContext, com.example.dndascension.R.layout.spinner_dropdown_item, listOf<Race>())
                    edit_char_subrace_container.visibility = View.GONE
                }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }
    }
    private fun initClasses() {
        edit_char_class.adapter = ArrayAdapter(this, com.example.dndascension.R.layout.spinner_dropdown_item, classes)
        edit_char_class.setSelection(classes.indexOfFirst { it.class_id == c.class_id ?: it.class_id })

        edit_char_class.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                val dndClass = edit_char_class.selectedItem as DndClass
                if (dndClass.hasSubclasses()) {
                    edit_char_subclass.adapter = ArrayAdapter(applicationContext, com.example.dndascension.R.layout.spinner_dropdown_item, dndClass.subclasses)
                    edit_char_subclass.setSelection(dndClass.subclasses.indexOfFirst { it.class_id == c.subclass_id ?: it.class_id })
                    edit_char_subclass_container.visibility = View.VISIBLE
                } else {
                    edit_char_subclass.adapter = ArrayAdapter(applicationContext, com.example.dndascension.R.layout.spinner_dropdown_item, listOf<DndClass>())
                    edit_char_subclass_container.visibility = View.GONE
                }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }
    }
    private fun initBackgrounds() {
        edit_char_background.adapter = ArrayAdapter(this, com.example.dndascension.R.layout.spinner_dropdown_item, backgrounds)
        edit_char_background.setSelection(backgrounds.indexOfFirst { it.value_id == c.background_id ?: it.value_id })
    }
}
