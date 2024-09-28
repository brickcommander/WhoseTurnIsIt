package com.brickcommander.whoseturnisit.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brickcommander.whoseturnisit.R
import com.brickcommander.whoseturnisit.logic.Calculate
import com.brickcommander.whoseturnisit.model.Work
import com.brickcommander.whoseturnisit.ui.ItemAdapter

class PendingWorkActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private val items = mutableListOf<Work>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worklist)

        recyclerView = findViewById(R.id.recyclerView)

        // Initialize adapter
        adapter = ItemAdapter(items)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val calculate = Calculate()

        Thread {
            val historyList = calculate.getPendingWork()
            runOnUiThread {
                historyList.forEach { work ->
                    adapter.addItem(work)
                    recyclerView.scrollToPosition(items.size - 1)
                }
            }
        }.start()
    }
}