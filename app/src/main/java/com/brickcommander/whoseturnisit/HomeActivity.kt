package com.brickcommander.whoseturnisit

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.brickcommander.whoseturnisit.databinding.ActivityHomeBinding
import com.brickcommander.whoseturnisit.logic.Calculate

class HomeActivity : AppCompatActivity() {

    // Declare binding variable
    private lateinit var binding: ActivityHomeBinding
    private lateinit var calculate: Calculate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inflate layout using ViewBinding
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Initialize Calculate
        calculate = Calculate()

        binding.btnRefresh.setOnClickListener {
            var res = calculate.updateToLatestDB()
            binding.textView.text = res.toString()
        }

    }

}