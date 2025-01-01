package com.example.tanaman

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(private val categories: ArrayList<Category>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryTitle: TextView = itemView.findViewById(R.id.category_title)
        val plantRecyclerView: RecyclerView = itemView.findViewById(R.id.plants_recycler_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryTitle.text = category.name

        // Pastikan data plants memiliki pasangan nama dan gambar
        val plantsWithNames = category.plants

        holder.plantRecyclerView.layoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.plantRecyclerView.adapter = PlantImageAdapter(plantsWithNames) { plantName ->
            // Callback tombol edit
            Toast.makeText(holder.itemView.context, "Edit $plantName clicked", Toast.LENGTH_SHORT).show()
        }
    }


    override fun getItemCount(): Int {
        return categories.size
    }
}
