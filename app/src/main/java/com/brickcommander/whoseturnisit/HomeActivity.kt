package com.brickcommander.whoseturnisit

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.graphics.Typeface
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.brickcommander.whoseturnisit.data.SharedData
import com.brickcommander.whoseturnisit.databinding.ActivityHomeBinding
import com.brickcommander.whoseturnisit.logic.Calculate
import com.brickcommander.whoseturnisit.ui.activity.StatusActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var calculate: Calculate

    companion object {
        const val TAG = "HomeActivity"
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "App Started")

        // Fetch data on a background thread
        Thread {
            calculate = Calculate()
        }.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inflate layout using ViewBinding
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Set onClick listener for the button
        binding.btnRefresh.setOnClickListener {
            Log.i(TAG, "onCreate(): Refresh Button Clicked")
            Thread {
                calculate.updateToLatestDB()
            }.start()
        }

        binding.btnStatus.setOnClickListener {
            Log.i(TAG, "onCreate(): Status Button Clicked")
            val intent = Intent(this, StatusActivity::class.java)
            startActivity(intent)
        }
    }

//    fun showWhoseTurnIsIt() {
//        val whoseTurnIsItTextView = findViewById<TextView>(R.id.textView)
//        whoseTurnIsItTextView.text = "NA"
//        if(SharedData.workList.isNotEmpty()) {
//
//        }
//        whoseTurnIsItTextView.setTypeface(null, Typeface.BOLD)
//    }

}
