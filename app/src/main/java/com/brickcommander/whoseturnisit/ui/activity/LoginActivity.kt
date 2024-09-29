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

class LoginActivity : AppCompatActivity() {
    private lateinit var pinInput: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.i("LoginActivity", "onCreate()")

        pinInput = findViewById(R.id.pin_input)
        btnLogin = findViewById(R.id.btn_pin_input)

        btnLogin.setOnClickListener {
            var pin = pinInput.text.toString()
            if (pin.length == 4 && CONSTANTS.pinArray.contains(pin)) {
                Toast.makeText(this, "Correct PIN", Toast.LENGTH_SHORT).show()
                SharedData.username = CONSTANTS.pinToNameMap[pin].toString()
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                pinInput.setText("")
                Toast.makeText(this, "Invalid PIN", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
