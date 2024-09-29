package com.brickcommander.whoseturnisit.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.brickcommander.whoseturnisit.R
import com.brickcommander.whoseturnisit.data.CONSTANTS
import com.brickcommander.whoseturnisit.data.SharedData
import com.brickcommander.whoseturnisit.logic.SharedPreferencesHandler

class LoginActivity : AppCompatActivity() {
    private lateinit var pinInput: EditText
    private lateinit var btnLogin: Button

    companion object {
        const val TAG = "LoginActivity"
    }

    private fun startHomeActivity(username: String) {
        Toast.makeText(this, "Welcome $username", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.i("LoginActivity", "onCreate()")
        setTitle(R.string.login_activity_title)

        var username: String = SharedPreferencesHandler.getUsername(this)
        Log.i(TAG, "sharedPreferences : username=$username")

        if(CONSTANTS.namesInList.contains(username)) {
            startHomeActivity(username)
            finish()
        } else {
            pinInput = findViewById(R.id.pin_input)
            btnLogin = findViewById(R.id.btn_pin_input)

            btnLogin.setOnClickListener {
                val pin = pinInput.text.toString()

                if ((pin.length == 4 || pin.length == 5) && CONSTANTS.pinArray.contains(pin)) {
                    username = CONSTANTS.pinToNameMap[pin].toString()
                    SharedData.username = username
                    SharedPreferencesHandler.update(username, this)

                    startHomeActivity(username)
                    finish()
                } else {
                    pinInput.setText("")
                    Toast.makeText(this, "Invalid PIN", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
