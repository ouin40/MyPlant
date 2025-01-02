package com.example.tanaman

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class PlantImageAdapter(
    private val plants: List<Triple<String, String, Bitmap>>, // Triple (plantId, name, image)
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
        val (plantId, name, image) = plants[position]
        Log.d("PlantImageAdapter", "Plant ID: $plantId, Name: $name")
        holder.imageView.setImageBitmap(image)
        holder.nameTextView.text = name

        holder.editButton.setOnClickListener {
            val fragment = Plant_Add()
            val bundle = Bundle()
            bundle.putBoolean("isEditMode", true)
            bundle.putString("plantId", plantId)
            fragment.arguments = bundle

            val transaction = (holder.itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    override fun getItemCount() = plants.size
}
