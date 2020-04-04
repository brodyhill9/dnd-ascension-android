package com.example.dndascension.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dndascension.activities.EditAssetActivity
import com.example.dndascension.interfaces.Asset
import com.example.dndascension.models.Feat
import com.example.dndascension.models.Spell
import com.example.dndascension.utils.spellLevelSchool
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.content_feat.*
import kotlinx.android.synthetic.main.content_spell.*
import kotlinx.android.synthetic.main.fragment_dialog_asset.*



class AssetDialogFragment(private var asset: Asset) : BottomSheetDialogFragment() {
    companion object {
        const val START_EDIT_ASSET_ACTIVITY_REQUEST_CODE = 0
    }
    private val TAG = this::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val layout = when (asset) {
            is Spell -> com.example.dndascension.R.layout.fragment_dialog_asset
            else -> com.example.dndascension.R.layout.fragment_dialog_asset
        }

        return inflater.inflate(layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btn_edit.setOnClickListener {
            val intent = Intent(activity?.applicationContext, EditAssetActivity::class.java)
            intent.putExtra("asset", asset)
            startActivityForResult(intent, START_EDIT_ASSET_ACTIVITY_REQUEST_CODE)
        }

        when (asset) {
            is Feat -> {
                val feat = asset
                val content: View = layoutInflater.inflate(com.example.dndascension.R.layout.content_feat, null)
                asset_container.addView(content)

                asset_name.text = feat.name()
                asset_tag.visibility = View.GONE

                feat_desc.text = feat.desc()
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
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == START_EDIT_ASSET_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, data)
            }
            dismiss()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
