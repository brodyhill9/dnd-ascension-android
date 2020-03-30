package com.example.dndascension.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserState
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.client.results.SignInResult
import com.amazonaws.mobile.client.results.SignInState
import kotlinx.android.synthetic.main.activity_authentication.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
import kotlin.concurrent.thread

class AuthenticationActivity : AppCompatActivity() {
    private val TAG = AuthenticationActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.dndascension.R.layout.activity_authentication)

        AWSMobileClient.getInstance()
            .initialize(applicationContext, object : Callback<UserStateDetails> {
                override fun onResult(userStateDetails: UserStateDetails) {
                    Log.i(TAG, userStateDetails.userState.toString())
                    when (userStateDetails.userState) {
                        UserState.SIGNED_IN -> {
                            val i = Intent(this@AuthenticationActivity, MainActivity::class.java)
                            startActivity(i)
                        }
                        else -> {
                            AWSMobileClient.getInstance().signOut()
                            showSignIn()
                        }
                    }
                }

                override fun onError(e: Exception) {
                    Log.e(TAG, e.toString())
                }
            })
    }

    override fun onResume() {
        super.onResume()
        val activityIntent = intent
        if (activityIntent.data != null && "myapp" == activityIntent.data!!.scheme) {
            AWSMobileClient.getInstance().handleAuthResponse(activityIntent)
        }
    }

    private fun showSignIn() {
        try {
            btn_login.setOnClickListener {
                val email = et_email.text.toString()
                val password = et_password.text.toString()

                if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
                    alert("Missing email or password", "Sign in") {
                        yesButton { "Ok" }
                    }.show()
                    return@setOnClickListener
                }

                AWSMobileClient.getInstance().signIn(email, password, null, object : Callback<SignInResult> {
                    override fun onResult(signInResult: SignInResult) {
                        when (signInResult.signInState) {
                            SignInState.DONE -> {
                                val i = Intent(this@AuthenticationActivity, MainActivity::class.java)
                                startActivity(i)
                            }
                            else -> Toast.makeText(applicationContext, signInResult.signInState.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(e: Exception) {
                        thread {
                            runOnUiThread {
                                alert("Incorrect email or password", "Sign in") {
                                    yesButton { "Ok" }
                                }.show()
                            }
                        }
                        Log.e(TAG, e.toString())
                    }
                })
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
}
