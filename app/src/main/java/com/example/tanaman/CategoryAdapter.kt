package com.example.tanaman

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(private val categories: ArrayList<Category>, private val context: Context) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryTitle: TextView = itemView.findViewById(R.id.category_title)
        val plantRecyclerView: RecyclerView = itemView.findViewById(R.id.plants_recycler_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context) // Use context here
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryTitle.text = category.name

        // Set up RecyclerView for plant images in the category
        holder.plantRecyclerView.layoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)

        // Pass a list of Plant objects instead of Bitmaps
        holder.plantRecyclerView.adapter = PlantImageAdapter(category.plants, holder.itemView.context)
    }


    override fun getItemCount(): Int {
        return categories.size
    }
}
