package com.brickcommander.whoseturnisit.ui.activity

import android.os.Bundle
import android.util.Log
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
import com.brickcommander.whoseturnisit.ui.WorkItemAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PendingWorkActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WorkItemAdapter
    private lateinit var textView: TextView
    private lateinit var progressBar: ProgressBar
    private val items = mutableListOf<Work>()

    companion object {
        const val TAG = "PendingWorkActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worklist)

        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        textView = findViewById(R.id.textView4)

        adapter = WorkItemAdapter(items)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        lifecycleScope.launch(Dispatchers.IO) {
            progressBar.isVisible = true
            val calculate = Calculate()
            val historyList = calculate.getPendingWork()
            Log.i(TAG, "historyList=$historyList")

            withContext(Dispatchers.Main) {
                progressBar.isVisible = false

                if(historyList.isNotEmpty()) {
                    historyList.forEach { work ->
                        adapter.addItem(work)
                        recyclerView.scrollToPosition(items.size - 1)
                    }
                } else {
                    textView.isVisible = true
                    textView.text = "No Pending Work :)"
                }
            }
        }
    }

}
