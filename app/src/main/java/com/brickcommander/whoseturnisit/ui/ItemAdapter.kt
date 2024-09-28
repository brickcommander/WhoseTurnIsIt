package com.brickcommander.whoseturnisit.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.brickcommander.whoseturnisit.R
import com.brickcommander.whoseturnisit.model.Work
import java.time.LocalDate

class ItemAdapter(private val items: MutableList<Work>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val createdDate: TextView = itemView.findViewById(R.id.createdDateId)
        val day: TextView = itemView.findViewById(R.id.dayId)
        val eatersList: TextView = itemView.findViewById(R.id.eatersListId)
        val unableToWash: TextView = itemView.findViewById(R.id.unableToWashId)
        val foodCooked: TextView = itemView.findViewById(R.id.foodCookedId)
        val whoWashed: TextView = itemView.findViewById(R.id.whoWashedId)

//        val deleteItemButton: Button = itemView.findViewById(R.id.deleteItemButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.work_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.createdDate.text = "Created Date: " + item.getWhoWashed()
        holder.day.text = "Day: " + item.getDay().toString()
        holder.eatersList.text = "Eaters: " + item.getEatersList().toString()
        holder.unableToWash.text = "Unable To Wash: " + item.getUnableToWash().toString()
        holder.foodCooked.text = "Food Cooked: " + item.isFoodCooked().toString()
        holder.whoWashed.text = "Who Washed: " + item.getWhoWashed()

//        // Set the click listener for the delete button
//        holder.deleteItemButton.setOnClickListener {
//            removeItem(position)
//        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // Method to add an item
    fun addItem(item: Work) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    // Method to remove an item
    private fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

}
