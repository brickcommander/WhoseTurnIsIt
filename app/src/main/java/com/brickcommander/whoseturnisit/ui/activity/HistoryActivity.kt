package com.brickcommander.whoseturnisit.ui.activity

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brickcommander.whoseturnisit.R
import com.brickcommander.whoseturnisit.logic.Calculate
import com.brickcommander.whoseturnisit.model.Work
import com.brickcommander.whoseturnisit.ui.ItemAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private val items = mutableListOf<Work>()
    private lateinit var progressBar: ProgressBar
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worklist)

        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        textView = findViewById(R.id.textView4)

        // Initialize adapter
        adapter = ItemAdapter(items)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val calculate = Calculate()

        lifecycleScope.launch(Dispatchers.IO) { // Use coroutines for background work
            progressBar.isVisible = true // Update UI on main thread
            val historyList = calculate.getHistory()
            withContext(Dispatchers.Main) { // Switch to main thread for UI updates
                progressBar.isVisible = false
                if(historyList.isNotEmpty()) {
                    historyList.forEach { work ->
                        adapter.addItem(work)
                        recyclerView.scrollToPosition(items.size - 1)
                    }
                } else {
                    textView.isVisible = true
                    textView.text = "No History"
                }
            }
        }
    }

}
