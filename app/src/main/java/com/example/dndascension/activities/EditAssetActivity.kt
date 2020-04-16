package com.example.dndascension.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.dndascension.R
import com.example.dndascension.interfaces.Asset
import com.example.dndascension.models.*
import com.example.dndascension.utils.*
import kotlinx.android.synthetic.main.activity_edit_asset.*
import kotlinx.android.synthetic.main.content_armor_edit.*
import kotlinx.android.synthetic.main.content_background_edit.*
import kotlinx.android.synthetic.main.content_class_edit.*
import kotlinx.android.synthetic.main.content_feat_edit.*
import kotlinx.android.synthetic.main.content_race_edit.*
import kotlinx.android.synthetic.main.content_spell_edit.*
import kotlinx.android.synthetic.main.content_trait_edit.*
import kotlinx.android.synthetic.main.content_weapon_edit.*
import kotlinx.android.synthetic.main.progress_overlay.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import kotlin.concurrent.thread

class EditAssetActivity : AppCompatActivity() {
    companion object {
        const val START_TRAIT_EDIT_ASSET_ACTIVITY_REQUEST_CODE = 0
    }
    private val TAG = this::class.java.simpleName
    private lateinit var asset: Asset
    private lateinit var title: String
    private lateinit var traitAdapter: ArrayAdapter<Trait>
    private lateinit var subAdapter: ArrayAdapter<Asset>

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
        if (asset.isNewAllowDelete()) {
            title = "Edit New "
        } else if (asset.isNew()) {
            title = "New "
            btn_delete.visibility = View.GONE
        } else {
            title = "Edit "
        }
        displayEditor()
        edit_asset_toolbar_title.text = title
    }

    private fun displayEditor() {
        when (asset) {
            is Armor -> armorEditor()
            is Background -> backgroundEditor()
            is DndClass -> classEditor()
            is Feat -> featEditor()
            is Race -> raceEditor()
            is Spell -> spellEditor()
            is Trait -> traitEditor()
            is Weapon -> weaponEditor()
            else -> {
                btn_save.setOnClickListener {
                    toast(asset.toJSON())
                }
                btn_delete.setOnClickListener {
                    toast("Delete ${asset.name()}")
                }
            }
        }
    }
    private fun evaluateSave(newAsset: Asset?, message: String = "") {
        thread { runOnUiThread { progress_overlay.isVisible = false } }
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
    private fun evaluateDelete(assetId: Int, error: Boolean = false, message: String = "") {
        thread { runOnUiThread { progress_overlay.isVisible = false } }
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
                thread { runOnUiThread { progress_overlay.isVisible = true } }
                armor.armor_name = edit_armor_name.text.toString()
                armor.armor_desc = edit_armor_desc.text.toString()
                armor.armor_type = ArmorType.values()[edit_armor_type.selectedItemPosition]
                armor.stealth_dis = edit_armor_stealth.isChecked
                armor.cost = edit_armor_cost.text.toString()
                armor.armor_class = edit_armor_class.text.toString()
                armor.strength = edit_armor_strength.text.toString().toIntOrNull()
                armor.weight = edit_armor_weight.text.toString().toIntOrNull()

                ApiClient(applicationContext!!).saveArmor(armor) { newArmor, message ->
                    evaluateSave(newArmor, message)
                }
            }
        }
        btn_delete.setOnClickListener {
            thread { runOnUiThread { progress_overlay.isVisible = true } }

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
                thread { runOnUiThread { progress_overlay.isVisible = true } }
                background.set_value = edit_background_name.text.toString()
                background.value_desc = edit_background_desc.text.toString()

                ApiClient(applicationContext!!).saveBackground(background) { newBackground, message ->
                    evaluateSave(newBackground, message)
                }
            }
        }
        btn_delete.setOnClickListener {
            thread { runOnUiThread { progress_overlay.isVisible = true } }
            val assetId = asset.id()!!
            ApiClient(applicationContext!!).deleteBackground(assetId) { error, message ->
                evaluateDelete(assetId, error, message)
            }
        }
    }
    private fun classEditor() {
        var cls = asset as DndClass
        title += "Class"
        val content: View = layoutInflater.inflate(R.layout.content_class_edit, null)
        edit_asset_container.addView(content)

        edit_class_name.setText(cls.class_name)
        edit_class_hit_die.setText(cls.hit_die)
        edit_class_str.isChecked = cls.str
        edit_class_dex.isChecked = cls.dex
        edit_class_con.isChecked = cls.con
        edit_class_intel.isChecked = cls.intel
        edit_class_wis.isChecked = cls.wis
        edit_class_cha.isChecked = cls.cha

        traitAdapter = ArrayAdapter(applicationContext, R.layout.item_text_link, cls.class_traits)
        edit_class_traits.adapter = traitAdapter
        edit_class_traits.setOnItemClickListener {parent, _, position, _ ->
            val trait = parent.getItemAtPosition(position) as Trait
            trait.index = position
            val intent = Intent(applicationContext, EditAssetActivity::class.java)
            intent.putExtra("asset", trait)
            startActivityForResult(intent, START_TRAIT_EDIT_ASSET_ACTIVITY_REQUEST_CODE)
        }
        //setListViewHeightBasedOnChildren(edit_class_traits)
        btn_add_class_trait.setOnClickListener {
            val intent = Intent(applicationContext, EditAssetActivity::class.java)
            intent.putExtra("asset", Trait())
            startActivityForResult(intent, START_TRAIT_EDIT_ASSET_ACTIVITY_REQUEST_CODE)
        }

        if (cls.isParent()) {
            subAdapter = ArrayAdapter(applicationContext, R.layout.item_text_link, cls.subclasses as MutableList<Asset>)
            edit_class_subclasses.adapter = subAdapter
            edit_class_subclasses.setOnItemClickListener {parent, _, position, _ ->
                val subclass = parent.getItemAtPosition(position) as DndClass
                subclass.index = position
                val intent = Intent(applicationContext, EditAssetActivity::class.java)
                intent.putExtra("asset", subclass)
                startActivityForResult(intent, START_TRAIT_EDIT_ASSET_ACTIVITY_REQUEST_CODE)
            }
            //setListViewHeightBasedOnChildren(edit_class_subclasses)
            btn_add_class_subclass.setOnClickListener {
                val intent = Intent(applicationContext, EditAssetActivity::class.java)
                intent.putExtra("asset", DndClass(parent_id = cls.class_id ?: 1))
                startActivityForResult(intent, START_TRAIT_EDIT_ASSET_ACTIVITY_REQUEST_CODE)
            }
        } else {
            edit_class_parent_only_container.visibility = View.GONE
            edit_class_subclasses_container.visibility = View.GONE
        }

        btn_save.setOnClickListener {
            if (edit_class_name.text.isNullOrBlank()) {
                alert(
                    "Class name is required",
                    getString(R.string.title_form_error)
                ) { yesButton { "Ok" } }.show()
            } else {
                cls.class_name = edit_class_name.text.toString()
                cls.hit_die = edit_class_hit_die.text.toString()
                cls.str = edit_class_str.isChecked
                cls.dex = edit_class_dex.isChecked
                cls.con = edit_class_con.isChecked
                cls.intel = edit_class_intel.isChecked
                cls.wis = edit_class_wis.isChecked
                cls.cha = edit_class_cha.isChecked

                if (cls.isParent()) {
                    thread { runOnUiThread { progress_overlay.isVisible = true } }
                    ApiClient(applicationContext!!).saveClass(cls) { newClass, message ->
                        evaluateSave(newClass, message)
                    }
                } else {
                    cls.delete = false
                    evaluateSave(cls)
                }
            }
        }
        btn_delete.setOnClickListener {
            if (cls.isParent()) {
                val assetId = asset.id()!!
                thread { runOnUiThread { progress_overlay.isVisible = true } }
                ApiClient(applicationContext!!).deleteClass(assetId) { error, message ->
                    evaluateDelete(assetId, error, message)
                }
            } else {
                cls.delete = true
                evaluateSave(cls)
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
                thread { runOnUiThread { progress_overlay.isVisible = true } }
                feat.set_value = edit_feat_name.text.toString()
                feat.value_desc = edit_feat_desc.text.toString()

                ApiClient(applicationContext!!).saveFeat(feat) { newFeat, message ->
                    evaluateSave(newFeat, message)
                }
            }
        }
        btn_delete.setOnClickListener {
            thread { runOnUiThread { progress_overlay.isVisible = true } }
            val assetId = asset.id()!!
            ApiClient(applicationContext!!).deleteFeat(assetId) { error, message ->
                evaluateDelete(assetId, error, message)
            }
        }
    }
    private fun raceEditor() {
        var race = asset as Race
        title += "Race"
        val content: View = layoutInflater.inflate(R.layout.content_race_edit, null)
        edit_asset_container.addView(content)

        edit_race_name.setText(race.race_name)
        edit_race_speed.setText(race.speed.toString())
        edit_race_str.setText(race.str.toString())
        edit_race_dex.setText(race.dex.toString())
        edit_race_con.setText(race.con.toString())
        edit_race_intel.setText(race.intel.toString())
        edit_race_wis.setText(race.wis.toString())
        edit_race_cha.setText(race.cha.toString())

        traitAdapter = ArrayAdapter(applicationContext, R.layout.item_text_link, race.race_traits)
        edit_race_traits.adapter = traitAdapter
        edit_race_traits.setOnItemClickListener {parent, _, position, _ ->
            val trait = parent.getItemAtPosition(position) as Trait
            trait.index = position
            val intent = Intent(applicationContext, EditAssetActivity::class.java)
            intent.putExtra("asset", trait)
            startActivityForResult(intent, START_TRAIT_EDIT_ASSET_ACTIVITY_REQUEST_CODE)
        }
        //setListViewHeightBasedOnChildren(edit_race_traits)
        btn_add_race_trait.setOnClickListener {
            val intent = Intent(applicationContext, EditAssetActivity::class.java)
            intent.putExtra("asset", Trait())
            startActivityForResult(intent, START_TRAIT_EDIT_ASSET_ACTIVITY_REQUEST_CODE)
        }

        if (race.isParent()) {
            subAdapter = ArrayAdapter(applicationContext, R.layout.item_text_link, race.subraces as MutableList<Asset>)
            edit_race_subraces.adapter = subAdapter
            edit_race_subraces.setOnItemClickListener {parent, _, position, _ ->
                val subrace = parent.getItemAtPosition(position) as Race
                subrace.index = position
                val intent = Intent(applicationContext, EditAssetActivity::class.java)
                intent.putExtra("asset", subrace)
                startActivityForResult(intent, START_TRAIT_EDIT_ASSET_ACTIVITY_REQUEST_CODE)
            }
            //setListViewHeightBasedOnChildren(edit_race_subraces)
            btn_add_race_subrace.setOnClickListener {
                val intent = Intent(applicationContext, EditAssetActivity::class.java)
                intent.putExtra("asset", Race(parent_id = race.race_id ?: 1))
                startActivityForResult(intent, START_TRAIT_EDIT_ASSET_ACTIVITY_REQUEST_CODE)
            }
        } else {
            edit_race_subraces_container.visibility = View.GONE
        }

        btn_save.setOnClickListener {
            if (edit_race_name.text.isNullOrBlank()) {
                alert(
                    "Race name is required",
                    getString(R.string.title_form_error)
                ) { yesButton { "Ok" } }.show()
            } else {
                race.race_name = edit_race_name.text.toString()
                race.speed = edit_race_speed.text.toString().toIntOrNull() ?: 0
                race.str = edit_race_str.text.toString().toIntOrNull() ?: 0
                race.dex = edit_race_dex.text.toString().toIntOrNull() ?: 0
                race.con = edit_race_con.text.toString().toIntOrNull() ?: 0
                race.intel = edit_race_intel.text.toString().toIntOrNull() ?: 0
                race.wis = edit_race_wis.text.toString().toIntOrNull() ?: 0
                race.cha = edit_race_cha.text.toString().toIntOrNull() ?: 0

                if (race.isParent()) {
                    thread { runOnUiThread { progress_overlay.isVisible = true } }
                    ApiClient(applicationContext!!).saveRace(race) { newRace, message ->
                        evaluateSave(newRace, message)
                    }
                } else {
                    race.delete = false
                    evaluateSave(race)
                }
            }
        }
        btn_delete.setOnClickListener {
            if (race.isParent()) {
                val assetId = asset.id()!!
                thread { runOnUiThread { progress_overlay.isVisible = true } }
                ApiClient(applicationContext!!).deleteRace(assetId) { error, message ->
                    evaluateDelete(assetId, error, message)
                }
            } else {
                race.delete = true
                evaluateSave(race)
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
                thread { runOnUiThread { progress_overlay.isVisible = true } }
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

                ApiClient(applicationContext!!).saveSpell(spell) { newSpell, message ->
                    evaluateSave(newSpell, message)
                }
            }
        }
        btn_delete.setOnClickListener {
            thread { runOnUiThread { progress_overlay.isVisible = true } }
            val assetId = asset.id()!!
            ApiClient(applicationContext!!).deleteSpell(assetId) { error, message ->
                evaluateDelete(assetId, error, message)
            }
        }
    }
    private fun traitEditor() {
        var trait = asset as Trait
        title += "Trait"
        val content: View = layoutInflater.inflate(R.layout.content_trait_edit, null)
        edit_asset_container.addView(content)

        edit_trait_name.setText(trait.trait_name)
        edit_trait_char_level.setText(trait.char_level.toString())
        edit_trait_desc.setText(trait.trait_desc)

        btn_save.setOnClickListener {
            if (edit_trait_name.text.isNullOrBlank()) {
                alert(
                    "Trait name is required",
                    getString(R.string.title_form_error)
                ) { yesButton { "Ok" } }.show()
            } else {
                trait.trait_name = edit_trait_name.text.toString()
                trait.char_level = edit_trait_char_level.text.toString().toIntOrNull() ?: 0
                trait.trait_desc = edit_trait_desc.text.toString()

                trait.delete = false
                evaluateSave(trait)
            }
        }
        btn_delete.setOnClickListener {
            trait.delete = true
            evaluateSave(trait)
        }
    }
    private fun weaponEditor() {
        var weapon = asset as Weapon
        title += "Weapon"
        val content: View = layoutInflater.inflate(R.layout.content_weapon_edit, null)
        edit_asset_container.addView(content)

        edit_weapon_name.setText(weapon.weapon_name)
        edit_weapon_desc.setText(weapon.weapon_desc)
        edit_weapon_type.adapter =
            ArrayAdapter(this, R.layout.spinner_dropdown_item, weaponTypeList())
        edit_weapon_type.setSelection(weapon.weapon_type.ordinal)
        edit_weapon_damage.setText(weapon.damage)
        edit_weapon_props.setText(weapon.weapon_props)
        edit_weapon_cost.setText(weapon.cost)
        if (weapon.weight != null) edit_weapon_weight.setText(weapon.weight.toString())

        btn_save.setOnClickListener {
            if (edit_weapon_name.text.isNullOrBlank()) {
                alert(
                    "Weapon name is required",
                    getString(R.string.title_form_error)
                ) { yesButton { "Ok" } }.show()
            } else {
                thread { runOnUiThread { progress_overlay.isVisible = true } }
                weapon.weapon_name = edit_weapon_name.text.toString()
                weapon.weapon_desc = edit_weapon_desc.text.toString()
                weapon.weapon_type = WeaponType.values()[edit_weapon_type.selectedItemPosition]
                weapon.damage = edit_weapon_damage.text.toString()
                weapon.weapon_props = edit_weapon_props.text.toString()
                weapon.cost = edit_weapon_cost.text.toString()
                weapon.weight = edit_weapon_weight.text.toString().toIntOrNull()

                ApiClient(applicationContext!!).saveWeapon(weapon) { newWeapon, message ->
                    evaluateSave(newWeapon, message)
                }
            }
        }
        btn_delete.setOnClickListener {
            thread { runOnUiThread { progress_overlay.isVisible = true } }
            val assetId = asset.id()!!
            ApiClient(applicationContext!!).deleteWeapon(assetId) { error, message ->
                evaluateDelete(assetId, error, message)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == START_TRAIT_EDIT_ASSET_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                when (asset) {
                    is DndClass -> {
                        val cls = asset as DndClass
                        val childAsset = data?.getSerializableExtra("asset") as Asset
                        when (childAsset) {
                            is Trait -> {
                                val index = childAsset.index ?: -1
                                if (index > -1) {
                                    if (childAsset.isNew() && childAsset.delete) {
                                        cls.class_traits.removeAt(index)
                                    } else {
                                        cls.class_traits[index] = childAsset
                                    }
                                } else {
                                    cls.class_traits.add(childAsset)
                                }

                                cls.class_traits.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name() })
                                cls.class_traits.sortBy { it.char_level }
                                traitAdapter.notifyDataSetChanged()
                                setListViewHeightBasedOnChildren(edit_class_traits)
                            }
                            is DndClass -> {
                                val index = childAsset.index ?: -1
                                if (index > -1) {
                                    if (childAsset.isNew() && childAsset.delete) {
                                        cls.subclasses.removeAt(index)
                                    } else {
                                        cls.subclasses[index] = childAsset
                                    }
                                } else {
                                    cls.subclasses.add(childAsset)
                                }

                                cls.subclasses.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name() })
                                cls.subclasses.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.secSort() })
                                subAdapter.notifyDataSetChanged()
                                setListViewHeightBasedOnChildren(edit_class_subclasses)
                            }
                        }
                    }
                    is Race -> {
                        val race = asset as Race
                        val childAsset = data?.getSerializableExtra("asset") as Asset
                        when (childAsset) {
                            is Trait -> {
                                val index = childAsset.index ?: -1
                                if (index > -1) {
                                    if (childAsset.isNew() && childAsset.delete) {
                                        race.race_traits.removeAt(index)
                                    } else {
                                        race.race_traits[index] = childAsset
                                    }
                                } else {
                                    race.race_traits.add(childAsset)
                                }

                                race.race_traits.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name() })
                                race.race_traits.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.secSort() })
                                traitAdapter.notifyDataSetChanged()
                                setListViewHeightBasedOnChildren(edit_race_traits)
                            }
                            is Race -> {
                                val index = childAsset.index ?: -1
                                if (index > -1) {
                                    if (childAsset.isNew() && childAsset.delete) {
                                        race.subraces.removeAt(index)
                                    } else {
                                        race.subraces[index] = childAsset
                                    }
                                } else {
                                    race.subraces.add(childAsset)
                                }

                                race.subraces.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name() })
                                race.subraces.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.secSort() })
                                subAdapter.notifyDataSetChanged()
                                setListViewHeightBasedOnChildren(edit_race_subraces)
                            }
                        }
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
