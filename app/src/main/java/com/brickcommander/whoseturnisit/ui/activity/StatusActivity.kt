package com.brickcommander.whoseturnisit.ui.activity

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.brickcommander.whoseturnisit.HomeActivity.Companion.TAG
import com.brickcommander.whoseturnisit.R
import com.brickcommander.whoseturnisit.logic.Calculate

class StatusActivity : AppCompatActivity() {

    private lateinit var calculate: Calculate

    private fun renderStatusGrid(gridLayout: GridLayout, textView: TextView) {
        Log.i(TAG, "renderStatusGrid()")

        calculate = Calculate()

        val personList = calculate.getPersonStatus()
        Log.i(TAG, "renderStatusGrid : personList=$personList")

        // Update the UI on the main thread
        runOnUiThread {
            if (personList.isNotEmpty()) {
                // Clear any previous views
                gridLayout.removeAllViews()

                // Add the header row
                val headerNames = listOf("Name", "Score", "Date", "Day")
                headerNames.forEach { header ->
                    val headerTextView = TextView(this).apply {
                        text = header
                        textSize = 20f
                        setPadding(5, 16, 16, 16)
                        setTypeface(null, Typeface.BOLD)  // Make the header bold
                    }
                    gridLayout.addView(headerTextView)
                }

                // Add the data rows
                personList.forEach { person ->
                    // Add the name
                    val nameTextView = TextView(this).apply {
                        text = person.getName()
                        textSize = 18f
                        setPadding(5, 10, 10, 10)
                    }
                    gridLayout.addView(nameTextView)

                    // Add the score
                    val scoreTextView = TextView(this).apply {
                        text = person.getScore().toString()
                        textSize = 18f
                        setPadding(14, 10, 10, 10)
                    }
                    gridLayout.addView(scoreTextView)

                    // Add the last working day
                    val lastWorkingDateTextView = TextView(this).apply {
                        text = person.getLastWorkingDate().toString()
                        textSize = 18f
                        setPadding(20, 10, 10, 10)
                    }
                    gridLayout.addView(lastWorkingDateTextView)

                    // Add the last working day
                    val dayTextView = TextView(this).apply {
                        text = person.getDay().toString()
                        textSize = 18f
                        setPadding(20, 10, 10, 10)
                    }
                    gridLayout.addView(dayTextView)
                }
            } else {
                textView.text = "Failed to Fetch Data. \nPlease Refresh."
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        val gridLayout2 = findViewById<GridLayout>(R.id.gridLayout2)
        val textView2 = findViewById<TextView>(R.id.textView2)

        Thread {
            renderStatusGrid(gridLayout2, textView2)
        }.start()
    }

}
