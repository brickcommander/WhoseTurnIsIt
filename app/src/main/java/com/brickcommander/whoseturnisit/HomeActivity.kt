package com.brickcommander.whoseturnisit

import android.os.Bundle
import android.widget.TextView
import android.graphics.Typeface
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.brickcommander.whoseturnisit.databinding.ActivityHomeBinding
import com.brickcommander.whoseturnisit.logic.Calculate
import com.brickcommander.whoseturnisit.model.Person

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var calculate: Calculate
    private lateinit var persons: List<Person>

    companion object {
        const val TAG = "HomeActivity"
    }

    fun renderStatusGrid() {
        Log.i(TAG, "renderStatusGrid()")

        // Update the UI on the main thread
        runOnUiThread {
            if (persons != null) {
                // Clear any previous views
                binding.gridLayout.removeAllViews()

                // Add the header row
                val headerNames = listOf("Name", "Score", "Last Working Day")
                headerNames.forEach { header ->
                    val headerTextView = TextView(this).apply {
                        text = header
                        textSize = 18f
                        setPadding(10, 16, 16, 16)
                        setTypeface(null, Typeface.BOLD)  // Make the header bold
                    }
                    binding.gridLayout.addView(headerTextView)
                }

                // Add the data rows
                persons.forEach { person ->
                    // Add the name
                    val nameTextView = TextView(this).apply {
                        text = person.getName()
                        textSize = 16f
                        setPadding(10, 10, 10, 10)
                    }
                    binding.gridLayout.addView(nameTextView)

                    // Add the score
                    val scoreTextView = TextView(this).apply {
                        text = person.getScore().toString()
                        textSize = 16f
                        setPadding(14, 10, 10, 10)
                    }
                    binding.gridLayout.addView(scoreTextView)

                    // Add the last working day
                    val lastWorkingDayTextView = TextView(this).apply {
                        text = person.getLastWorkingDay().toString()
                        textSize = 16f
                        setPadding(10, 10, 10, 10)
                    }
                    binding.gridLayout.addView(lastWorkingDayTextView)
                }
            } else {
                binding.textView.text = "Failed to fetch data"
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart()")
        // Fetch data on a background thread
        Thread {
            persons = calculate.updateToLatestDB() ?: emptyList()
            renderStatusGrid()
        }.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inflate layout using ViewBinding
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize Calculate
        calculate = Calculate()

        // Set onClick listener for the button
        binding.btnRefresh.setOnClickListener {
            Log.i(TAG, "onCreate(): Refresh Button Clicked")
            Thread {
                persons = calculate.updateToLatestDB() ?: emptyList()
                renderStatusGrid()
            }.start()
        }
    }
}
