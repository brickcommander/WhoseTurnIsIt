package com.brickcommander.whoseturnisit.ui.activity

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.brickcommander.whoseturnisit.HomeActivity.Companion.TAG
import com.brickcommander.whoseturnisit.R
import com.brickcommander.whoseturnisit.data.SharedData
import com.brickcommander.whoseturnisit.model.Person

class StatusActivity : AppCompatActivity() {

    fun renderStatusGrid(gridLayout: GridLayout, textView: TextView) {
        Log.i(TAG, "renderStatusGrid()")

        // Update the UI on the main thread
        runOnUiThread {
            if (SharedData.personsList.isNotEmpty()) {
                // Clear any previous views
                gridLayout.removeAllViews()

                // Add the header row
                val headerNames = listOf("Name", "Score", "Last Working Day")
                headerNames.forEach { header ->
                    val headerTextView = TextView(this).apply {
                        text = header
                        textSize = 20f
                        setPadding(10, 16, 16, 16)
                        setTypeface(null, Typeface.BOLD)  // Make the header bold
                    }
                    gridLayout.addView(headerTextView)
                }

                // Add the data rows
                SharedData.personsList.forEach { person ->
                    // Add the name
                    val nameTextView = TextView(this).apply {
                        text = person.getName()
                        textSize = 18f
                        setPadding(10, 10, 10, 10)
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
                    val lastWorkingDayTextView = TextView(this).apply {
                        text = person.getLastWorkingDay().toString()
                        textSize = 18f
                        setPadding(10, 10, 10, 10)
                    }
                    gridLayout.addView(lastWorkingDayTextView)
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
