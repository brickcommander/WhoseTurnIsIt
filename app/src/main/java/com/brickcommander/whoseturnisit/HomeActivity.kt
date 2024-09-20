package com.brickcommander.whoseturnisit

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.brickcommander.whoseturnisit.databinding.ActivityHomeBinding
import com.brickcommander.whoseturnisit.logic.Calculate
import android.util.Log
import androidx.core.text.HtmlCompat

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var calculate: Calculate

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
            // Fetch data on a background thread
            Thread {
                val res = calculate.updateToLatestDB()

                // Update the UI on the main thread
                runOnUiThread {
                    if (res != null) {
                        val formattedText = res.joinToString("<br>") { person ->
                            "<b>Name:</b> ${person.getName()}, <b>Score:</b> ${person.getScore()}, <b>Last Working Day:</b> ${person.getLastWorkingDay()}"
                        }
                        binding.textView.text = HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    } else {
                        binding.textView.text = "Failed to fetch data"
                    }
                }
            }.start()
        }
    }
}
