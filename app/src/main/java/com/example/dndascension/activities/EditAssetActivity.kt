package com.example.dndascension.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.dndascension.R
import com.example.dndascension.interfaces.Asset
import com.example.dndascension.interfaces.serializeToMap
import com.example.dndascension.models.Armor
import com.example.dndascension.models.Background
import com.example.dndascension.models.Feat
import com.example.dndascension.models.Spell
import com.example.dndascension.utils.*
import kotlinx.android.synthetic.main.activity_edit_asset.*
import kotlinx.android.synthetic.main.content_armor_edit.*
import kotlinx.android.synthetic.main.content_background_edit.*
import kotlinx.android.synthetic.main.content_feat_edit.*
import kotlinx.android.synthetic.main.content_spell_edit.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

class EditAssetActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName
    lateinit var asset: Asset
    lateinit var title: String

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_asset)

        asset = intent.getSerializableExtra("asset") as Asset
        setSupportActionBar(edit_asset_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        title = if (asset.isNew()) "New " else "Edit "
        if (asset.isNew()) {
            btn_delete.visibility = View.GONE
        }
        displayEditor()
        edit_asset_toolbar_title.text = title
    }

    private fun displayEditor() {
        when (asset) {
            is Armor -> armorEditor()
            is Background -> backgroundEditor()
            is Feat -> featEditor()
            is Spell -> spellEditor()

            else -> {
                btn_save.setOnClickListener {
                    toast(asset.serializeToMap().toString())
                }
                btn_delete.setOnClickListener {
                    toast("Delete ${asset.name()}")
                }
            }
        }
    }

    private fun evaluateSave(newAsset: Asset?, message: String) {
        if (newAsset == null) {
            alert(message, getString(R.string.title_api_error)) { yesButton { "Ok" } }.show()
        } else {
            val intent = Intent().apply {
                putExtra("asset", newAsset)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
    private fun evaluateDelete(assetId: Int, error: Boolean, message: String) {
        if (error) {
            alert(message, getString(R.string.title_api_error)) { yesButton { "Ok" } }.show()
        } else {
            val intent = Intent().apply {
                putExtra("assetId", assetId)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun armorEditor() {
        var armor = asset as Armor
        title += "Armor"
        val content: View = layoutInflater.inflate(R.layout.content_armor_edit, null)
        edit_asset_container.addView(content)

        edit_armor_name.setText(armor.armor_name)
        edit_armor_desc.setText(armor.armor_desc)
        edit_armor_type.adapter =
            ArrayAdapter(this, R.layout.spinner_dropdown_item, armorTypeList())
        edit_armor_type.setSelection(armor.armor_type.ordinal)
        edit_armor_stealth.isChecked = armor.stealth_dis
        edit_armor_cost.setText(armor.cost)
        edit_armor_class.setText(armor.armor_class)
        if (armor.strength != null) edit_armor_strength.setText(armor.strength.toString())
        if (armor.weight != null) edit_armor_weight.setText(armor.weight.toString())

        btn_save.setOnClickListener {
            if (edit_armor_name.text.isNullOrBlank()) {
                alert(
                    "Armor name is required",
                    getString(R.string.title_form_error)
                ) { yesButton { "Ok" } }.show()
            } else {
                armor.armor_name = edit_armor_name.text.toString()
                armor.armor_desc = edit_armor_desc.text.toString()
                armor.armor_type = ArmorType.values()[edit_armor_type.selectedItemPosition]
                armor.stealth_dis = edit_armor_stealth.isChecked
                armor.cost = edit_armor_cost.text.toString()
                armor.armor_class = edit_armor_class.text.toString()
                armor.strength = edit_armor_strength.text.toString().toIntOrNull()
                armor.weight = edit_armor_weight.text.toString().toIntOrNull()

                ApiClient(applicationContext!!).saveArmor(armor) { newFeat, message ->
                    evaluateSave(newFeat, message)
                }
            }
        }
        btn_delete.setOnClickListener {
            val assetId = asset.id()!!
            ApiClient(applicationContext!!).deleteArmor(assetId) { error, message ->
                evaluateDelete(assetId, error, message)
            }
        }
    }
    private fun backgroundEditor() {
        var background = asset as Background
        title += "Background"
        val content: View = layoutInflater.inflate(R.layout.content_background_edit, null)
        edit_asset_container.addView(content)

        edit_background_name.setText(background.set_value)
        edit_background_desc.setText(background.value_desc)

        btn_save.setOnClickListener {
            if (edit_background_name.text.isNullOrBlank()) {
                alert(
                    "Background name is required",
                    getString(R.string.title_form_error)
                ) { yesButton { "Ok" } }.show()
            } else {
                background.set_value = edit_background_name.text.toString()
                background.value_desc = edit_background_desc.text.toString()

                ApiClient(applicationContext!!).saveBackground(background) { newBackground, message ->
                    evaluateSave(newBackground, message)
                }
            }
        }
        btn_delete.setOnClickListener {
            val assetId = asset.id()!!
            ApiClient(applicationContext!!).deleteBackground(assetId) { error, message ->
                evaluateDelete(assetId, error, message)
            }
        }
    }
    private fun featEditor() {
        var feat = asset as Feat
        title += "Feat"
        val content: View = layoutInflater.inflate(R.layout.content_feat_edit, null)
        edit_asset_container.addView(content)

        edit_feat_name.setText(feat.set_value)
        edit_feat_desc.setText(feat.value_desc)

        btn_save.setOnClickListener {
            if (edit_feat_name.text.isNullOrBlank()) {
                alert(
                    "Feat name is required",
                    getString(R.string.title_form_error)
                ) { yesButton { "Ok" } }.show()
            } else {
                feat.set_value = edit_feat_name.text.toString()
                feat.value_desc = edit_feat_desc.text.toString()

                ApiClient(applicationContext!!).saveFeat(feat) { newFeat, message ->
                    evaluateSave(newFeat, message)
                }
            }
        }
        btn_delete.setOnClickListener {
            val assetId = asset.id()!!
            ApiClient(applicationContext!!).deleteFeat(assetId) { error, message ->
                evaluateDelete(assetId, error, message)
            }
        }
    }
    private fun spellEditor() {
        var spell = asset as Spell
        title += "Spell"
        val content: View = layoutInflater.inflate(R.layout.content_spell_edit, null)
        edit_asset_container.addView(content)

        edit_spell_name.setText(spell.spell_name)
        edit_spell_level.adapter =
            ArrayAdapter(this, R.layout.spinner_dropdown_item, spellLevelOrdinalList())
        edit_spell_level.setSelection(spell.spell_level)
        edit_spell_school.adapter =
            ArrayAdapter(this, R.layout.spinner_dropdown_item, SpellSchool.values())
        edit_spell_school.setSelection(spell.spell_school.ordinal)
        edit_spell_ritual.isChecked = spell.ritual
        edit_spell_casting_time.setText(spell.casting_time)
        edit_spell_range.setText(spell.spell_range)
        edit_spell_components.setText(spell.components)
        edit_spell_duration.setText(spell.duration)
        edit_spell_desc.setText(spell.spell_desc)
        edit_spell_higher_level.setText(spell.higher_level)

        btn_save.setOnClickListener {
            if (edit_spell_name.text.isNullOrBlank()) {
                alert(
                    "Spell name is required",
                    getString(R.string.title_form_error)
                ) { yesButton { "Ok" } }.show()
            } else {
                spell.spell_name = edit_spell_name.text.toString()
                spell.spell_level = edit_spell_level.selectedItemPosition
                spell.spell_school = edit_spell_school.selectedItem as SpellSchool
                spell.ritual = edit_spell_ritual.isChecked
                spell.casting_time = edit_spell_casting_time.text.toString()
                spell.spell_range = edit_spell_range.text.toString()
                spell.components = edit_spell_components.text.toString()
                spell.duration = edit_spell_duration.text.toString()
                spell.spell_desc = edit_spell_desc.text.toString()
                spell.higher_level = edit_spell_higher_level.text.toString()

                ApiClient(applicationContext!!).saveSpell(spell) { newFeat, message ->
                    evaluateSave(newFeat, message)
                }
            }
        }
        btn_delete.setOnClickListener {
            val assetId = asset.id()!!
            ApiClient(applicationContext!!).deleteSpell(assetId) { error, message ->
                evaluateDelete(assetId, error, message)
            }
        }
    }
}
