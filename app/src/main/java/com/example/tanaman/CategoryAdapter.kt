package com.example.tanaman

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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

        // Mengonversi List<Bitmap> menjadi ArrayList<Bitmap>
        val plantList = ArrayList(category.plants)

        // Set up RecyclerView untuk gambar tanaman dalam kategori
        holder.plantRecyclerView.layoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.plantRecyclerView.adapter = PlantImageAdapter(plantList)
    }

    override fun getItemCount(): Int {
        return categories.size // Mengembalikan jumlah kategori
    }
}
