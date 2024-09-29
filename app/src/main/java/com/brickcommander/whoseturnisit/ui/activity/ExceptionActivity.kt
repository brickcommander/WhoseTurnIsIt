package com.brickcommander.whoseturnisit.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.brickcommander.whoseturnisit.R

class ExceptionActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ExceptionActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exception)

        Log.i(TAG, "onCreate()")
    }

}
