package com.example.tanaman

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class HomeAdapter(
    private val sections: List<HomeSection>,
    private val onItemClicked: (Fragment) -> Unit
) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    inner class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.icon)
        val title: TextView = view.findViewById(R.id.title)
        val description: TextView = view.findViewById(R.id.description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_section, parent, false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val section = sections[position]
        holder.icon.setImageResource(section.iconResId)
        holder.title.text = section.title
        holder.description.text = section.description
        holder.itemView.setOnClickListener {
            onItemClicked(section.fragment)
        }
    }

    override fun getItemCount(): Int = sections.size
}
