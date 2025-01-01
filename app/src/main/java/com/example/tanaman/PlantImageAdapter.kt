package com.example.tanaman

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class PlantImageAdapter(
    private val plants: List<Pair<String, Bitmap>>, // Pasangan nama dan gambar
    private val onEditClick: (String) -> Unit // Callback untuk tombol edit
) : RecyclerView.Adapter<PlantImageAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.plant_image)
        val nameTextView: TextView = itemView.findViewById(R.id.plant_name)
        val editButton: Button = itemView.findViewById(R.id.edit_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plant_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (name, image) = plants[position]
        Log.d("PlantImageAdapter", "Name: $name") // Log nama tanaman
        holder.imageView.setImageBitmap(image)
        holder.nameTextView.text = name

        holder.editButton.setOnClickListener {
            onEditClick(name) // Panggil callback untuk edit
        }
    }


    override fun getItemCount() = plants.size
}
