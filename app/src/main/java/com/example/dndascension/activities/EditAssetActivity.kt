package com.example.dndascension.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.dndascension.R
import com.example.dndascension.interfaces.Asset
import com.example.dndascension.interfaces.serializeToMap
import com.example.dndascension.models.Feat
import com.example.dndascension.utils.ApiClient
import kotlinx.android.synthetic.main.activity_edit_asset.*
import kotlinx.android.synthetic.main.content_feat_edit.*
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
        title = if (asset.isNew()) "New " else "Edit "
        when (asset) {
            is Feat -> {
                featEditor()
            }
            else -> {
                btn_save.setOnClickListener {
                    toast(asset.serializeToMap().toString())
                }
                btn_delete.setOnClickListener {
                    toast("Delete ${asset.name()}")
                }
            }

        }

        edit_asset_toolbar_title.text = title
        setSupportActionBar(edit_asset_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        if (asset.isNew()) {
            btn_delete.visibility = View.GONE
        } else {
            btn_delete.setOnClickListener {
                val assetId = asset.id()!!

                ApiClient(applicationContext!!).deleteFeat(assetId) { error, message ->
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

    private fun featEditor() {
        title += "Feat"
        val content: View = layoutInflater.inflate(R.layout.content_feat_edit, null)
        edit_asset_container.addView(content)
        btn_save.setOnClickListener {
            var feat = asset as Feat
            if (TextUtils.isEmpty(edit_feat_name.text)) {
                alert("Feat name is required", getString(R.string.title_form_error)) { yesButton { "Ok" } }.show()
            } else {
                feat.set_value = edit_feat_name.text.toString()
                feat.value_desc = edit_feat_desc.text.toString()

                ApiClient(applicationContext!!).saveFeat(feat) { newFeat, message ->
                    evaluateSave(newFeat, message)
                }
            }
        }
    }
}
