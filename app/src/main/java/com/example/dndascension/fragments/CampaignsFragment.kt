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
import com.example.dndascension.activities.CharactersActivity
import com.example.dndascension.activities.EditCampaignActivity
import com.example.dndascension.adapters.CampaignsAdapter
import com.example.dndascension.models.Campaign
import com.example.dndascension.utils.ApiClient
import com.example.dndascension.utils.CharacterFragmentType
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_campaigns.*
import kotlinx.android.synthetic.main.progress_bar.*
import kotlinx.android.synthetic.main.progress_overlay.*
import org.jetbrains.anko.*
import kotlin.concurrent.thread


class CampaignsFragment() : Fragment() {
    companion object {
        const val START_CHARACTERS_ACTIVITY_REQUEST_CODE = 0
        const val START_EDIT_CAMPAIGN_ACTIVITY_REQUEST_CODE = 1
    }

    private val TAG = this::class.java.simpleName
    private lateinit var campaigns: MutableList<Campaign>
    private lateinit var adapter: CampaignsAdapter
    private lateinit var selectedCamp: Campaign

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            com.example.dndascension.R.layout.fragment_campaigns,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateList()

        camps_btn_add.setOnClickListener { view ->
            val intent = Intent(activity?.applicationContext, EditCampaignActivity::class.java)
            intent.putExtra("campaign", Campaign())
            startActivityForResult(intent, START_EDIT_CAMPAIGN_ACTIVITY_REQUEST_CODE)
        }
    }

    private fun updateList() {
        try {
            thread { runOnUiThread { progress_bar.isVisible = true } }
            ApiClient(activity?.applicationContext!!).getMyCampaigns { camps, message ->
                if (camps == null) {
                    activity?.toast(message)
                } else {
                    Log.i(TAG, "Retrieved my campaigns")
                    this.campaigns = camps as MutableList<Campaign>
                    createListView(message)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    private fun createListView(message: String) {
        thread { runOnUiThread { progress_bar.isVisible = false } }
        if (campaigns != null) {
            campaigns.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.camp_name })
            adapter = CampaignsAdapter(activity?.applicationContext!!, campaigns)
            camps_list_view.adapter = adapter

            camps_list_view.setOnItemClickListener { parent, view, position, id ->
                val camp = parent.getItemAtPosition(position) as Campaign
                val intent = Intent(activity?.applicationContext, CharactersActivity::class.java)
                intent.putExtra("fragmentType", CharacterFragmentType.CampaignCharacters)
                intent.putExtra("campaign", camp)
                startActivityForResult(intent, START_CHARACTERS_ACTIVITY_REQUEST_CODE)
            }

            camps_list_view.setOnItemLongClickListener { parent, view, position, id ->
                val camp = parent.getItemAtPosition(position) as Campaign
                if (camp.is_owner) {
                    val intent = Intent(activity?.applicationContext, EditCampaignActivity::class.java)
                    intent.putExtra("campaign", camp)
                    startActivityForResult(intent, START_EDIT_CAMPAIGN_ACTIVITY_REQUEST_CODE)
                } else {
                    activity?.alert("Are you sure you want to leave ${camp?.camp_name}?", "Leave ${camp.camp_name}") {
                        yesButton {
                            thread { runOnUiThread { progress_overlay.isVisible = true } }
                            ApiClient(activity?.applicationContext!!).leaveCamp(camp!!.camp_id!!) { error, message ->
                                thread { runOnUiThread { progress_overlay.isVisible = false } }
                                if (error) {
                                    activity?.alert(message, getString(R.string.title_api_error)) { yesButton { "Ok" } }?.show()
                                } else {
                                    campaigns.removeAt(campaigns.indexOfFirst { it.camp_id == camp.camp_id })
                                    Snackbar.make(view!!, "${camp.camp_name} left successfully", Snackbar.LENGTH_LONG).show()
                                    campaigns.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.camp_name })
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        }
                        noButton {}
                    }?.show()
                }
                true
            }
        } else {
            activity?.longToast(message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == START_EDIT_CAMPAIGN_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val campId = data?.getIntExtra("campId", -1)
                if (campId != null && campId > -1) {
                    val index = campaigns.indexOfFirst { it.camp_id == campId }
                    val c = campaigns[index]
                    campaigns.removeAt(index)
                    Snackbar.make(
                        view!!,
                        "${c.camp_name} deleted successfully",
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    val c = data?.getSerializableExtra("campaign") as Campaign
                    val index = campaigns.indexOfFirst { it.camp_id == c.camp_id }
                    if (index > -1) {
                        campaigns[index] = c
                    } else {
                        campaigns.add(c)
                        Snackbar.make(
                            view!!,
                            "${c.camp_name} saved successfully",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
                campaigns.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.camp_name })
                adapter.notifyDataSetChanged()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
