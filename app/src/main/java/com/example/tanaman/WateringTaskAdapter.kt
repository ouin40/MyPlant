package com.example.tanaman

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tanaman.R

class WateringTaskAdapter(
    private var wateringTasks: List<WateringTask>,
    private val onTaskDone: (WateringTask) -> Unit
) : RecyclerView.Adapter<WateringTaskAdapter.WateringTaskViewHolder>() {

    class WateringTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val plantImage: ImageView = itemView.findViewById(R.id.iv_plant_image)
        val plantName: TextView = itemView.findViewById(R.id.tv_plant_name)
        val waterQuantity: TextView = itemView.findViewById(R.id.tv_water_quantity)
        val actionIcon: ImageView = itemView.findViewById(R.id.iv_action_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WateringTaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_watering_task, parent, false)
        return WateringTaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: WateringTaskViewHolder, position: Int) {
        val task = wateringTasks[position]
        holder.plantImage.setImageResource(task.imageResource)
        holder.plantName.text = task.plantName
        holder.waterQuantity.text = task.waterQuantity

        // Update action icon
        if (task.isDone) {
            holder.actionIcon.setImageResource(R.drawable.baseline_check_circle_24)
        } else {
            holder.actionIcon.setImageResource(R.drawable.baseline_water_drop_24)
        }

        // Mark as done on icon click
        holder.actionIcon.setOnClickListener { onTaskDone(task) }
    }

    override fun getItemCount(): Int = wateringTasks.size

    fun updateData(newTasks: List<WateringTask>) {
        wateringTasks = newTasks
        notifyDataSetChanged()
    }
}
