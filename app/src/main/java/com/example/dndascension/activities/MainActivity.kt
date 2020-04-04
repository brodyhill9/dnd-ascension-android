package com.example.dndascension.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.dndascension.R.layout.activity_main)
        setSupportActionBar(main_toolbar)

        val listItems = listOf("Characters", "Campaigns", "Assets")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        home_list_view.adapter = adapter

        home_list_view.setOnItemClickListener {parent, view, position, id ->
            if (position == 2) {
                val intent = Intent(applicationContext, AssetsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(com.example.dndascension.R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == com.example.dndascension.R.id.action_settings) {
            Log.i(TAG, "Settings Action")
        } else if (id == com.example.dndascension.R.id.action_sign_out) {
            Log.i(TAG, "Sign Out Action")
            AWSMobileClient.getInstance().signOut()
            val intent = Intent(applicationContext, AuthenticationActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}
