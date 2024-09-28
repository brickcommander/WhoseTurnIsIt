package com.brickcommander.whoseturnisit.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.brickcommander.whoseturnisit.R
import com.brickcommander.whoseturnisit.model.Person

class PersonItemAdapter(private val items: MutableList<Person>) :
    RecyclerView.Adapter<PersonItemAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.nameId)
        val day: TextView = itemView.findViewById(R.id.dayId)
        val score: TextView = itemView.findViewById(R.id.scoreId)
        val lastWorkingDate: TextView = itemView.findViewById(R.id.lastWorkingDateId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.work_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.getName()
        holder.day.text = "Day: " + item.getDay().name
        holder.score.text = "Score: " + item.getScore().toString()
        holder.lastWorkingDate.text = "Last Working Date: " + item.getLastWorkingDate().toString()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItem(item: Person) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

}
