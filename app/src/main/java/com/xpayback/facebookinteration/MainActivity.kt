package com.xpayback.facebookinteration

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.Profile
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginResult
import com.xpayback.facebookinteration.databinding.ActivityMainBinding
import java.util.Arrays

class MainActivity : AppCompatActivity() {

    private lateinit var callbackManager: CallbackManager
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val accessToken: AccessToken? = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if (isLoggedIn) {
            accessToken.let {
                getUserDetails(it)

            }

        } else
            Toast.makeText(this, "not logged in", Toast.LENGTH_SHORT).show()

        callbackManager = CallbackManager.Factory.create()
        binding.loginButton.setPermissions(PUBLIC_PROFILE, EMAIL)

        binding.loginButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {


                override fun onCancel() {
                    binding.textView.text = "cancelld"
                }

                override fun onError(error: FacebookException) {
                    runOnUiThread {
                        binding.textView.text = error.message
                    }
                }

                override fun onSuccess(result: LoginResult) {
                    getUserDetails(result.accessToken)
                }
            });


    }

    companion object {
        const val EMAIL: String = "email"
        const val PUBLIC_PROFILE: String = "public_profile"
    }

    private fun getUserDetails(accessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(accessToken) { jsonObject, _ ->
            try {
                val email = jsonObject?.getString("email")
                val name = jsonObject?.getString("name")
                val id = jsonObject?.getString("id")

                runOnUiThread {
                    binding.textView.text = "$name $email $id"
                }



            }catch (e: Exception)
            {
                runOnUiThread {
                    binding.textView.text = e.message
                }
            }
        }
        val parameters = Bundle()
        parameters.putString("fields","id,name,email")
        request.parameters= parameters
        request.executeAsync()
    }
}
