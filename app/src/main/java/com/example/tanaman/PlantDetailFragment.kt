package com.example.tanaman

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlantDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlantDetailFragment : Fragment() {

    private lateinit var plantNameTextView: TextView
    private lateinit var plantDescriptionTextView: TextView
    private lateinit var plantImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plant_detail, container, false)

        plantNameTextView = view.findViewById(R.id.plant_name_detail)
        plantDescriptionTextView = view.findViewById(R.id.plant_description_detail)
        plantImageView = view.findViewById(R.id.plant_image_detail)

        // Get data passed from the adapter
        val plantName = arguments?.getString("plant_name")
        val plantDescription = arguments?.getString("plant_description")
        val plantImageUrl = arguments?.getString("plant_image_url")

        // Set the data to the views
        plantNameTextView.text = plantName
        plantDescriptionTextView.text = plantDescription

        // Load image if available
        if (!plantImageUrl.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(plantImageUrl)
                .into(plantImageView)
        }

        return view
    }
}