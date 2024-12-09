package com.example.tanaman

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PlantImageAdapter(private val plantList: List<Plant>, private val context: Context) :
    RecyclerView.Adapter<PlantImageAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.plant_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_plant_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val plant = plantList[position]

        // If you are using a URL or URI, load it using Glide:
        Glide.with(context)
            .load(plant.imageUrl) // `imageUrl` is a URL string in your `Plant` object
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return plantList.size
    }
}