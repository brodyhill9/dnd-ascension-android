package com.example.dndascension.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.example.dndascension.R
import com.example.dndascension.activities.CharacterActivity
import com.example.dndascension.activities.EditCharacterActivity
import com.example.dndascension.adapters.CharactersAdapter
import com.example.dndascension.models.Character
import com.example.dndascension.utils.ApiClient
import com.example.dndascension.utils.CharacterFragmentType
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_characters.*
import kotlinx.android.synthetic.main.progress_bar.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import kotlin.concurrent.thread

class CharactersFragment(private val characterFragmentType: CharacterFragmentType = CharacterFragmentType.AllCharacters) : Fragment() {
    companion object {
        const val START_CHARACTER_ACTIVITY_REQUEST_CODE = 0
        const val START_EDIT_CHARACTER_ACTIVITY_REQUEST_CODE = 1
    }
    private val TAG = this::class.java.simpleName
    private lateinit var characters: MutableList<Character>
    private lateinit var adapter: CharactersAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_characters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateList()

        if (characterFragmentType == CharacterFragmentType.MyCharacters) {
            chars_btn_add.setOnClickListener { view ->
                val intent = Intent(activity?.applicationContext, EditCharacterActivity::class.java)
                intent.putExtra("character", Character())
                startActivityForResult(intent, START_EDIT_CHARACTER_ACTIVITY_REQUEST_CODE)
            }
        } else {
            chars_btn_add.hide()
        }
    }

    private fun updateList() {
        try {
            thread { runOnUiThread { progress_bar.isVisible = true } }
            when(characterFragmentType) {
                CharacterFragmentType.AllCharacters -> {
                    ApiClient(activity?.applicationContext!!).getCharacters { characters, message ->
                        if (characters == null) {
                            activity?.toast(message)
                        } else {
                            Log.i(TAG, "Retrieved all characters")
                            this.characters = characters as MutableList<Character>
                            createListView(message)
                        }
                    }
                }
                CharacterFragmentType.MyCharacters -> {
                    ApiClient(activity?.applicationContext!!).getCharacters { characters, message ->
                        if (characters == null) {
                            activity?.toast(message)
                        } else {
                            Log.i(TAG, "Retrieved my characters")
                            this.characters = characters as MutableList<Character>
                            createListView(message)
                        }
                    }
                }
                CharacterFragmentType.CampaignCharacters -> {

                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    private fun createListView(message: String) {
        thread { runOnUiThread { progress_bar.isVisible = false } }
        if (characters != null) {
            characters.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.char_name })
            adapter = CharactersAdapter(activity?.applicationContext!!, characters)
            chars_list_view.adapter = adapter

            chars_list_view.setOnItemClickListener {parent, view, position, id ->
                val character = parent.getItemAtPosition(position) as Character
                val intent = Intent(activity?.applicationContext, CharacterActivity::class.java)
                intent.putExtra("character", character)
                startActivityForResult(intent, START_CHARACTER_ACTIVITY_REQUEST_CODE)
            }
        } else {
            activity?.longToast(message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == START_CHARACTER_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val charId = data?.getIntExtra("charId", -1)
                if (charId != null && charId > -1) {
                    val index = characters.indexOfFirst {it.char_id == charId}
                    val c = characters[index]
                    characters.removeAt(index)
                    Snackbar.make(view!!, "${c.char_name} deleted successfully", Snackbar.LENGTH_LONG).show()
                } else {
                    val c = data?.getSerializableExtra("character") as Character
                    val index = characters.indexOfFirst {it.char_id == c.char_id}
                    if (index > -1) {
                        characters[index] = c
                    } else {
                        characters.add(c)
                        val intent = Intent(activity?.applicationContext, CharacterActivity::class.java)
                        intent.putExtra("character", c)
                        startActivityForResult(intent, START_CHARACTER_ACTIVITY_REQUEST_CODE)
                    }
                }
                characters.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.char_name })
                adapter.notifyDataSetChanged()
            }
        } else if (requestCode == START_EDIT_CHARACTER_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val c = data?.getSerializableExtra("character") as Character
                characters.add(c)
                Snackbar.make(view!!, "${c.char_name} created successfully", Snackbar.LENGTH_LONG).show()

                characters.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.char_name })
                adapter.notifyDataSetChanged()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
