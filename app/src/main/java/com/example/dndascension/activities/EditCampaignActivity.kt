package com.example.dndascension.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.example.dndascension.R
import com.example.dndascension.models.Campaign
import com.example.dndascension.utils.ApiClient
import com.example.dndascension.utils.setListViewHeightBasedOnChildren
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_edit_campaign.*
import kotlinx.android.synthetic.main.progress_overlay.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import kotlin.concurrent.thread

class EditCampaignActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName
    private lateinit var c: Campaign
    private lateinit var title: String
    private lateinit var users: MutableList<String>

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_campaign)

        c = intent.getSerializableExtra("campaign") as Campaign
        setSupportActionBar(edit_camp_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        if (c.isNew()) {
            title = "New Campaign"
            btn_delete.visibility = View.GONE
            users_list_label.visibility = View.GONE
            users_list.visibility = View.GONE
        } else {
            title = "Edit ${c.camp_name}"
        }
        edit_comp_toolbar_title.text = title
        campaignEditor()
    }

    private fun evaluateSave(newCamp: Campaign?, message: String = "") {
        thread { runOnUiThread { progress_overlay.isVisible = false } }
        if (newCamp == null) {
            alert(
                message,
                getString(com.example.dndascension.R.string.title_api_error)
            ) { yesButton { "Ok" } }.show()
        } else {
            val intent = Intent().apply {
                putExtra("campaign", newCamp)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun evaluateDelete(campId: Int, error: Boolean = false, message: String = "") {
        thread { runOnUiThread { progress_overlay.isVisible = false } }
        if (error) {
            alert(
                message,
                getString(com.example.dndascension.R.string.title_api_error)
            ) { yesButton { "Ok" } }.show()
        } else {
            val intent = Intent().apply {
                putExtra("campId", campId)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun campaignEditor() {
        if (!c.isNew()) {
            thread { ThreadUtils.runOnUiThread { progress_overlay.isVisible = true } }
            ApiClient(applicationContext).getUsersForInvite(c.camp_id!!) { u, message ->
                ThreadUtils.runOnUiThread { progress_overlay.isVisible = false }
                if (u == null) {
                    toast(message)
                } else {
                    users = u as MutableList<String>
                    Log.i(TAG, "Retrieved users for invite")
                    users.sorted()
                    val adapter = ArrayAdapter(applicationContext, R.layout.item_text_link, users)
                    users_list.adapter = adapter
                    users_list.setOnItemClickListener { parent, view, position, id ->
                        val user = parent.getItemAtPosition(position) as String
                        alert("Invite $user to ${c.camp_name}?", "Invite") {
                            yesButton {
                                thread { runOnUiThread { progress_overlay.isVisible = true } }
                                ApiClient(applicationContext!!).createInvite(
                                    c.camp_id!!,
                                    user
                                ) { error, message ->
                                    thread { runOnUiThread { progress_overlay.isVisible = false } }
                                    if (error) {
                                        alert(
                                            message,
                                            getString(R.string.title_api_error)
                                        ) { yesButton { "Ok" } }?.show()
                                    } else {
                                        Snackbar.make(view!!, "$user invited", Snackbar.LENGTH_LONG)
                                            .show()
                                        users.removeAt(users.indexOfFirst { it == user })
                                        adapter.notifyDataSetChanged()
                                    }
                                }
                            }
                            noButton {}
                        }?.show()
                    }
                    setListViewHeightBasedOnChildren(users_list)
                }
            }
        }
        edit_camp_name.setText(c.camp_name)

        btn_save.setOnClickListener {
            if (edit_camp_name.text.isNullOrBlank()) {
                alert(
                    "Campaign name is required",
                    getString(com.example.dndascension.R.string.title_form_error)
                ) { yesButton { "Ok" } }.show()
            } else {
                c.camp_name = edit_camp_name.text.toString()

                thread { runOnUiThread { progress_overlay.isVisible = true } }
                ApiClient(applicationContext!!).saveCampaign(c) { newCamp, message ->
                    evaluateSave(newCamp, message)
                }
            }
        }
        btn_delete.setOnClickListener {
            thread { runOnUiThread { progress_overlay.isVisible = true } }
            ApiClient(applicationContext!!).deleteCampaign(c.camp_id!!) { error, message ->
                evaluateDelete(c.camp_id!!, error, message)
            }
        }
    }
}
