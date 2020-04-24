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
import com.example.dndascension.activities.CharactersActivity
import com.example.dndascension.activities.EditCharacterActivity
import com.example.dndascension.adapters.CharactersAdapter
import com.example.dndascension.models.Campaign
import com.example.dndascension.models.Character
import com.example.dndascension.utils.ApiClient
import com.example.dndascension.utils.CharacterFragmentType
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_characters.*
import kotlinx.android.synthetic.main.progress_bar.*
import kotlinx.android.synthetic.main.progress_overlay.*
import org.jetbrains.anko.*
import kotlin.concurrent.thread

class CharactersFragment(private val characterFragmentType: CharacterFragmentType = CharacterFragmentType.AllCharacters, private val campaign: Campaign? = null) : Fragment() {
    companion object {
        const val START_CHARACTER_ACTIVITY_REQUEST_CODE = 0
        const val START_EDIT_CHARACTER_ACTIVITY_REQUEST_CODE = 1
        const val START_CHOOSE_CHARACTER_ACTIVITY_REQUEST_CODE = 2
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

        when (characterFragmentType) {
            CharacterFragmentType.MyCharacters ->  {
                chars_btn_add.setOnClickListener { view ->
                    val intent = Intent(activity?.applicationContext, EditCharacterActivity::class.java)
                    intent.putExtra("character", Character())
                    startActivityForResult(intent, START_EDIT_CHARACTER_ACTIVITY_REQUEST_CODE)
                }
            }
            CharacterFragmentType.CampaignCharacters -> {
                chars_btn_add.setOnClickListener { view ->
                    val intent = Intent(activity?.applicationContext, CharactersActivity::class.java)
                    intent.putExtra("fragmentType", CharacterFragmentType.CharacterSelect)
                    startActivityForResult(intent, START_CHOOSE_CHARACTER_ACTIVITY_REQUEST_CODE)
                }

                chars_list_view.setOnItemLongClickListener { parent, view, position, id ->
                    val char = parent.getItemAtPosition(position) as Character
                    activity?.alert("Are you sure you want to remove ${char.char_name} from ${campaign?.camp_name}?", "Remove ${char.char_name}") {
                        yesButton {
                            thread { runOnUiThread { progress_overlay.isVisible = true } }
                            ApiClient(activity?.applicationContext!!).removeCampChar(campaign!!.camp_id!!, char.char_id!!) { error, message ->
                                thread { runOnUiThread { progress_overlay.isVisible = false } }
                                if (error) {
                                    activity?.alert(message, getString(R.string.title_api_error)) { yesButton { "Ok" } }?.show()
                                } else {
                                    characters.removeAt(characters.indexOfFirst { it.char_id == char.char_id })
                                    Snackbar.make(view!!, "${char.char_name} removed successfully", Snackbar.LENGTH_LONG).show()
                                    characters.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.char_name })
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        }
                        noButton {}
                    }?.show()
                    true
                }
            }
            else -> chars_btn_add.hide()
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
                CharacterFragmentType.MyCharacters, CharacterFragmentType.CharacterSelect -> {
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
                    ApiClient(activity?.applicationContext!!).getCampChars(campaign!!.camp_id!!) { characters, message ->
                        if (characters == null) {
                            activity?.toast(message)
                        } else {
                            Log.i(TAG, "Retrieved campaign characters")
                            this.characters = characters as MutableList<Character>
                            createListView(message)
                        }
                    }
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

            when (characterFragmentType) {
                CharacterFragmentType.CharacterSelect -> {
                    chars_list_view.setOnItemClickListener {parent, view, position, id ->
                        val character = parent.getItemAtPosition(position) as Character
                        val intent = Intent().apply {
                            putExtra("character", character)
                        }
                        activity?.setResult(Activity.RESULT_OK, intent)
                        activity?.finish()
                    }
                }
                else -> {
                    chars_list_view.setOnItemClickListener {parent, view, position, id ->
                        val character = parent.getItemAtPosition(position) as Character
                        val intent = Intent(activity?.applicationContext, CharacterActivity::class.java)
                        intent.putExtra("character", character)
                        startActivityForResult(intent, START_CHARACTER_ACTIVITY_REQUEST_CODE)
                    }
                }
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
        } else if (requestCode == START_CHOOSE_CHARACTER_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val c = data?.getSerializableExtra("character") as Character
                thread { runOnUiThread { progress_overlay.isVisible = true } }
                ApiClient(activity?.applicationContext!!).addCampChar(campaign!!.camp_id!!, c.char_id!!) { error, message ->
                    thread { runOnUiThread { progress_overlay.isVisible = false } }
                    if (error) {
                        activity?.alert(message, getString(R.string.title_api_error)) { yesButton { "Ok" } }?.show()
                    } else {
                        characters.add(c)
                        Snackbar.make(view!!, "${c.char_name} added successfully", Snackbar.LENGTH_LONG).show()
                        characters.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.char_name })
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
