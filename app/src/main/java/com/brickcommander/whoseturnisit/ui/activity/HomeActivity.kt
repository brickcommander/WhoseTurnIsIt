package com.brickcommander.whoseturnisit.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.brickcommander.whoseturnisit.data.CONSTANTS
import com.brickcommander.whoseturnisit.databinding.ActivityHomeBinding
import com.brickcommander.whoseturnisit.logic.Calculate
import com.brickcommander.whoseturnisit.model.Day
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch

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

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        calculate = Calculate()
        updateWhoseTurnIsIt()

        binding.btnRefresh.setOnClickListener {
            Log.i(TAG, "Refresh Button Clicked")
            updateWhoseTurnIsIt()
        }

        binding.btnStatus.setOnClickListener {
            Log.i(TAG, "Status Button Clicked")
            val intent = Intent(this, StatusActivity::class.java)
            startActivity(intent)
        }

        binding.btnPendingWork.setOnClickListener {
            Log.i(TAG, "Pending Work Button Clicked")
            val intent = Intent(this, PendingWorkActivity::class.java)
            startActivity(intent)
        }

        binding.btnDeclareNotEating.setOnClickListener {
            Log.i(TAG, "Declare Not Eating Button Clicked")
            Thread {
                val options = CONSTANTS.namesInArray
                val name = showOptionsPopup(this, options, "Naam?")
                val message = calculate.declareNotEating(name, getDay())
                showPopupMessage(this, message)
            }.start()
        }

        binding.btnDeclareFoodCooking.setOnClickListener {
            Log.i(TAG, "Declare Food Not Cooking Button Clicked")
            Thread {
                val options = getLast3Days()
                val day = showOptionsPopup(this, options, "Din?")
                val message = calculate.declareFoodCooking(Day.fromKey(day))
                showPopupMessage(this, message)
            }.start()
        }

        binding.btnUnableToWash.setOnClickListener {
            Log.i(TAG, "Unable to Wash Button Clicked")
            Thread {
                val options = CONSTANTS.namesInArray
                val name = showOptionsPopup(this, options, "Naam?")
                val message = calculate.declareCantWash(name, getDay())
                showPopupMessage(this, message)
            }.start()
        }

        binding.btnWashed.setOnClickListener {
            Log.i(TAG, "Washed Button Clicked")
            Thread {
                val options = calculate.getPendingWorkDays()
                val day = showOptionsPopup(this, options, "Din?")
                val name = showOptionsPopup(this, CONSTANTS.namesInArray, "Naam?")
                val message = calculate.declareWashed(name, Day.fromKey(day))
                showPopupMessage(this, message)
            }.start()
        }

        binding.btnHistory.setOnClickListener {
            Log.i(TAG, "History Button Clicked")
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

    }

    private fun updateWhoseTurnIsIt() {
        Log.i(TAG, "Update Whose Turn Is It")
        Thread {
            val name = calculate.whoseTurnIsIt(getDay())
            runOnUiThread { binding.textView.text = name }
        }.start()
    }

    private fun getDay(): Day {
        val date = LocalDateTime.now()
        return if (date.dayOfWeek == DayOfWeek.SUNDAY) Day.Sunday
        else if (date.dayOfWeek == DayOfWeek.MONDAY) Day.Monday
        else if (date.dayOfWeek == DayOfWeek.TUESDAY) Day.Tuesday
        else if (date.dayOfWeek == DayOfWeek.WEDNESDAY) Day.Wednesday
        else if (date.dayOfWeek == DayOfWeek.THURSDAY) Day.Thursday
        else if (date.dayOfWeek == DayOfWeek.FRIDAY) Day.Friday
        else if (date.dayOfWeek == DayOfWeek.SATURDAY && date.hour < 14) Day.SaturdayMorning
        else Day.SaturdayEvening
    }

    private fun showPopupMessage(context: Context, message: String) {
        runOnUiThread {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(message)
            builder.setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun getLast3Days(): Array<String> {
        val now = LocalDateTime.now()
        val hour1800 = LocalDateTime.now().withHour(18)

        if (now.dayOfWeek == DayOfWeek.SUNDAY) {
            return arrayOf(Day.SaturdayMorning.name, Day.SaturdayEvening.name)
        }

        if (now.dayOfWeek == DayOfWeek.SATURDAY) {
            val hour1000 = now.withHour(10)
            if (now.isBefore(hour1000)) {
                return arrayOf(Day.Friday.name)
            }
            if (now.isBefore(hour1800)) {
                return arrayOf(Day.Friday.name, Day.SaturdayMorning.name)
            }
            return arrayOf(Day.Friday.name, Day.SaturdayMorning.name, Day.SaturdayEvening.name)
        }

        if (now.isBefore(hour1800)) {
            val previousDayOfWeek = now.minusDays(1).dayOfWeek
            return arrayOf(Day.convertToDay(previousDayOfWeek).name)
        }

        val previousDayOfWeek = now.minusDays(1).dayOfWeek
        return arrayOf(
            Day.convertToDay(previousDayOfWeek).name,
            Day.convertToDay(now.dayOfWeek).name
        )
    }

    // Function to create and show the popup
    private fun showOptionsPopup(context: Context, options: Array<String>, title: String): String {
        var resName = "P1"
        val latch = CountDownLatch(1) // Initialize a CountDownLatch

        runOnUiThread {
            Log.i(TAG, "showOptionsPopup : options=$options : title=$title")

            // Create the AlertDialog
            val builder = AlertDialog.Builder(context)
            builder.setTitle("title")

            // Set the options and handle item click
            builder.setItems(options) { _, which ->
                resName = options[which]
                latch.countDown() // Release the latch once a selection is made
            }

            // Create and show the dialog
            val dialog = builder.create()
            dialog.show()
        }

        latch.await() // Wait for the dialog to complete
        return resName
    }

}
