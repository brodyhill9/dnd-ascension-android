package com.example.dndascension.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.dndascension.activities.CharacterActivity
import com.example.dndascension.activities.EditAssetActivity
import com.example.dndascension.interfaces.Asset
import com.example.dndascension.models.*
import com.example.dndascension.utils.AssetDialogFragmentType
import com.example.dndascension.utils.setListViewHeightBasedOnChildren
import com.example.dndascension.utils.spellLevelSchool
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.content_armor.*
import kotlinx.android.synthetic.main.content_background.*
import kotlinx.android.synthetic.main.content_class.*
import kotlinx.android.synthetic.main.content_feat.*
import kotlinx.android.synthetic.main.content_race.*
import kotlinx.android.synthetic.main.content_spell.*
import kotlinx.android.synthetic.main.content_weapon.*
import kotlinx.android.synthetic.main.fragment_dialog_asset.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton



class AssetDialogFragment(private var asset: Asset, private val assetDialogFragmentType: AssetDialogFragmentType = AssetDialogFragmentType.CRUD) : BottomSheetDialogFragment() {
    companion object {
        const val START_EDIT_ASSET_ACTIVITY_REQUEST_CODE = 0
        const val START_SUB_ASSET_DIALOG_FRAGMENT_REQUEST_CODE = 1
    }
    private val TAG = this::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.example.dndascension.R.layout.fragment_dialog_asset, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        when (assetDialogFragmentType) {
            AssetDialogFragmentType.CRUD -> {
                btn_edit.visibility = View.VISIBLE
                btn_edit.setOnClickListener {
                    val intent = Intent(activity?.applicationContext, EditAssetActivity::class.java)
                    intent.putExtra("asset", asset)
                    startActivityForResult(intent, START_EDIT_ASSET_ACTIVITY_REQUEST_CODE)
                }
            }
            AssetDialogFragmentType.RemoveAsset -> {
                btn_remove_asset.visibility = View.VISIBLE
                btn_remove_asset.setOnClickListener {
                    (activity as CharacterActivity).removeCharAsset(asset)
                    dismiss()
                }
            }
            AssetDialogFragmentType.AddAsset -> {
                btn_add_asset.visibility = View.VISIBLE
                btn_add_asset.setOnClickListener {
                    val intent = Intent().apply {
                        putExtra("asset", asset)
                    }
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    Log.i(TAG, "Adding ${asset.name()}")
                    dismiss()
                }
            }
        }

        when (asset) {
            is Armor -> {
                val armor = asset as Armor
                val content: View = layoutInflater.inflate(com.example.dndascension.R.layout.content_armor, null)
                asset_container.addView(content)

                asset_name.text = armor.armor_name
                asset_tag.text = armor.armorType()

                armor_class.text = armor.armor_class
                armor_cost.text = armor.cost
                armor_strength.text = if (armor.strength ?: 0 > 0) armor.strength.toString() else "\u2014"
                armor_stealth.text = if (armor.stealth_dis) "Disadvantage" else "\u2014"
                armor_weight.text = if (armor.weight ?: 0 > 0) armor.weight.toString() + " lb." else "\u2014"
                armor_desc.text = armor.armor_desc
            }
            is Background -> {
                val background = asset
                val content: View = layoutInflater.inflate(com.example.dndascension.R.layout.content_background, null)
                asset_container.addView(content)

                asset_name.text = background.name()
                asset_tag.visibility = View.GONE

                background_desc.text = background.desc()
            }
            is DndClass -> {
                val cls = asset as DndClass
                val content: View = layoutInflater.inflate(com.example.dndascension.R.layout.content_class, null)
                asset_container.addView(content)

                asset_name.text = cls.name()
                asset_tag.visibility = View.GONE

                class_hit_die.text = cls.hit_die
                class_str.isChecked = cls.str
                class_dex.isChecked = cls.dex
                class_con.isChecked = cls.con
                class_intel.isChecked = cls.intel
                class_wis.isChecked = cls.wis
                class_cha.isChecked = cls.cha

                if (cls.class_traits.count() > 0) {
                    val adapter = ArrayAdapter(activity?.applicationContext!!, com.example.dndascension.R.layout.item_text_link, cls.class_traits)
                    class_traits.adapter = adapter
                    class_traits.setOnItemClickListener {parent, _, position, _ ->
                        val trait = parent.getItemAtPosition(position) as Trait
                        activity?.alert(trait.trait_desc, trait.trait_name) {
                            yesButton { "Ok" }
                        }!!.show()
                    }
                    cls.class_traits.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.trait_name })
                    cls.class_traits.sortBy { it.char_level }
                    adapter.notifyDataSetChanged()
                    setListViewHeightBasedOnChildren(class_traits)
                } else {
                    class_traits_label.visibility = View.GONE
                    class_traits.visibility = View.GONE
                }

                if (cls.subclasses.count() > 0) {
                    val adapter = ArrayAdapter(activity?.applicationContext!!, com.example.dndascension.R.layout.item_text_link, cls.subclasses)
                    class_subclasses.adapter = adapter
                    class_subclasses.setOnItemClickListener {parent, _, position, _ ->
                        val subclass = parent.getItemAtPosition(position) as DndClass
                        val fragment = AssetDialogFragment(subclass)
                        fragment.setTargetFragment(this, START_SUB_ASSET_DIALOG_FRAGMENT_REQUEST_CODE)
                        fragment.show(activity?.supportFragmentManager!!, fragment.tag)
                    }
                    cls.subclasses.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name() })
                    cls.subclasses.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.secSort() })
                    adapter.notifyDataSetChanged()
                    setListViewHeightBasedOnChildren(class_subclasses)
                } else {
                    class_subclasses_label.visibility = View.GONE
                    class_subclasses.visibility = View.GONE
                }

                if (!cls.isParent()) {
                    class_parent_only_container.visibility = View.GONE
                    btn_edit.visibility = View.GONE
                }
            }
            is Feat -> {
                val feat = asset
                val content: View = layoutInflater.inflate(com.example.dndascension.R.layout.content_feat, null)
                asset_container.addView(content)

                asset_name.text = feat.name()
                asset_tag.visibility = View.GONE

                feat_desc.text = feat.desc()
            }
            is Race -> {
                val race = asset as Race
                val content: View = layoutInflater.inflate(com.example.dndascension.R.layout.content_race, null)
                asset_container.addView(content)

                asset_name.text = race.name()
                asset_tag.visibility = View.GONE

                race_speed.text = race.speed()
                race_str.text = race.str()
                race_dex.text = race.dex()
                race_con.text = race.con()
                race_intel.text = race.intel()
                race_wis.text = race.wis()
                race_cha.text = race.cha()

                if (race.race_traits.count() > 0) {
                    val adapter = ArrayAdapter(activity?.applicationContext!!, com.example.dndascension.R.layout.item_text_link, race.race_traits)
                    race_traits.adapter = adapter
                    race_traits.setOnItemClickListener {parent, _, position, _ ->
                        val trait = parent.getItemAtPosition(position) as Trait
                        activity?.alert(trait.trait_desc, trait.trait_name) {
                            yesButton { "Ok" }
                        }!!.show()
                    }
                    setListViewHeightBasedOnChildren(race_traits)
                } else {
                    race_traits_label.visibility = View.GONE
                    race_traits.visibility = View.GONE
                }

                if (race.subraces.count() > 0) {
                    val adapter = ArrayAdapter(activity?.applicationContext!!, com.example.dndascension.R.layout.item_text_link, race.subraces)
                    race_subraces.adapter = adapter
                    race_subraces.setOnItemClickListener {parent, _, position, _ ->
                        val subrace = parent.getItemAtPosition(position) as Race
                        val fragment = AssetDialogFragment(subrace)
                        fragment.setTargetFragment(this, START_SUB_ASSET_DIALOG_FRAGMENT_REQUEST_CODE)
                        fragment.show(activity?.supportFragmentManager!!, fragment.tag)
                    }
                    setListViewHeightBasedOnChildren(race_subraces)
                } else {
                    race_subraces_label.visibility = View.GONE
                    race_subraces.visibility = View.GONE
                }

                if (!race.isParent()) {
                    btn_edit.visibility = View.GONE
                }
            }
            is Spell -> {
                val spell = asset as Spell
                val content: View = layoutInflater.inflate(com.example.dndascension.R.layout.content_spell, null)
                asset_container.addView(content)

                asset_name.text = spell.spell_name
                asset_tag.text = "${spellLevelSchool(spell.spell_level, spell.spell_school)}" + if (spell.ritual) " (ritual)" else ""

                spell_desc.text = spell.spell_desc
                spell_higher_level.text = spell.higher_level
                spell_range.text = spell.spell_range
                spell_components.text = spell.components
                spell_duration.text = spell.duration
                spell_casting_time.text = spell.casting_time
            }
            is Weapon -> {
                val weapon = asset as Weapon
                val content: View = layoutInflater.inflate(com.example.dndascension.R.layout.content_weapon, null)
                asset_container.addView(content)

                asset_name.text = weapon.weapon_name
                asset_tag.text = weapon.weaponType()

                weapon_damage.text = weapon.damage
                weapon_props.text = weapon.weapon_props
                weapon_cost.text = weapon.cost
                weapon_weight.text = if (weapon.weight ?: 0 > 0) weapon.weight.toString() + " lb." else "\u2014"
                weapon_desc.text = weapon.weapon_desc
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == START_EDIT_ASSET_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, data)
            }
            dismiss()
        } else if(requestCode == START_SUB_ASSET_DIALOG_FRAGMENT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                when (asset) {
                    is DndClass -> {
                        val cls = asset as DndClass
                        val subclass = data?.getSerializableExtra("asset") as DndClass
                        cls.subclasses.forEachIndexed { index, sub ->
                            sub.takeIf { it.class_id == subclass.class_id}?.let {
                                cls.subclasses[index] = subclass
                            }
                        }
                        val intent = Intent().apply {
                            putExtra("asset", cls)
                        }
                        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    }
                    is Race -> {
                        val race = asset as Race
                        val subrace = data?.getSerializableExtra("asset") as Race
                        race.subraces.forEachIndexed { index, sub ->
                            sub.takeIf { it.race_id == subrace.race_id}?.let {
                                race.subraces[index] = subrace
                            }
                        }
                        val intent = Intent().apply {
                            putExtra("asset", race)
                        }
                        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
