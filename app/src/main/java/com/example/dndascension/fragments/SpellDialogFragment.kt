package com.example.dndascension.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dndascension.models.Spell
import com.example.dndascension.utils.spellLevelSchool
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_spell_dialog_item.*

class SpellDialogFragment(private val spell: Spell) : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.example.dndascension.R.layout.fragment_spell_dialog_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        spell_name.text = spell.spell_name
        spell_description.text = spell.spell_desc
        spell_level_school_ritual.text = "${spellLevelSchool(spell.spell_level, spell.spell_school)}" + if (spell.ritual) " (ritual)" else ""
        spell_higher_level.text = spell.higher_level
        spell_range.text = spell.spell_range
        spell_components.text = spell.components
        spell_duration.text = spell.duration
        spell_casting_time.text = spell.casting_time
    }
}
