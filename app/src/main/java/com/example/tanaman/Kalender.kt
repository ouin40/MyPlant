package com.example.tanaman

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tanaman.databinding.FragmentKalenderBinding
import java.util.*

class Kalender : Fragment() {
    private var _binding: FragmentKalenderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKalenderBinding.inflate(inflater, container, false)

        // Set listener for date change
        binding.calendarView.setOnDateChangeListener { _, year, month, day ->
            val dateString = String.format("%02d/%02d/%d", month + 1, day, year)
            binding.calendarText.text = dateString

            val mDate = Calendar.getInstance()
            mDate.set(year, month, day)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
