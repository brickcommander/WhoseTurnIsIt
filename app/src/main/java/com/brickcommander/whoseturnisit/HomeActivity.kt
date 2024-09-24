package com.brickcommander.whoseturnisit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.brickcommander.whoseturnisit.databinding.ActivityHomeBinding
import com.brickcommander.whoseturnisit.logic.Calculate
import com.brickcommander.whoseturnisit.ui.activity.StatusActivity
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var calculate: Calculate

    companion object {
        const val TAG = "HomeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.i(TAG, "onCreate()")

        // Inflate layout using ViewBinding
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Calculate
        calculate = Calculate()

        // Fetch data from Calculate and update UI
        fetchDataAndUpdateUI()

        // Set onClick listener for the refresh button
        binding.btnRefresh.setOnClickListener {
            Log.i(TAG, "Refresh Button Clicked")
            fetchDataAndUpdateUI() // Refresh UI after refresh
        }

        // Navigate to StatusActivity when the status button is clicked
        binding.btnStatus.setOnClickListener {
            Log.i(TAG, "Status Button Clicked")
            val intent = Intent(this, StatusActivity::class.java)
            startActivity(intent)
        }

        // Show options popup when the 'Declare Not Eating' button is clicked
        binding.btnDeclarenoteating.setOnClickListener {
            Log.i(TAG, "Declare Not Eating Button Clicked")
            showOptionsPopup(this) // 'this' refers to the context
        }

        binding.btnDeclarefoodnotcooking.setOnClickListener {
            Log.i(TAG, "Declare Food Not Cooking Button Clicked")
            showPopupMessage(this)
        }

        binding.btnUnabletowash.setOnClickListener {
            Log.i(TAG, "Unable to Wash Button Clicked")
            showPopupMessage(this)
        }
    }

    private fun showPopupMessage(context: Context) {
        lifecycleScope.launch {
            var message: String = "Feature Coming Soon!"
//            if (calculate.updateFoodStatus()) message = "Updated!"

            val builder = AlertDialog.Builder(context)
            builder.setMessage(message)
            builder.setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    // Function to fetch data from Calculate and update the UI
    private fun fetchDataAndUpdateUI() {
        lifecycleScope.launch {
            calculate.refresh()
            val name = calculate.getWhoseTurnIsIt()
            binding.textView.text = name
        }
    }

    // Function to create and show the popup
    private fun showOptionsPopup(context: Context) {

        // Define the options
        val options = arrayOf("Pawan", "Satyam", "Anmol", "Yashwant")
        Log.i(TAG, "showOptionsPopup()")

        // Create the AlertDialog
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Naam?")

        // Set the options and handle item click
        builder.setItems(options) { _, which ->
            lifecycleScope.launch {
                calculate.updateEatersList(options[which])
                Log.i(TAG, "${options[which]} selected as not eating")
            }
        }

        // Create and show the dialog
        val dialog = builder.create()
        dialog.show()
    }

}
