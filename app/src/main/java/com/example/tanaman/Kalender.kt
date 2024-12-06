package com.example.tanaman

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
//import android.widget.CalendarView
import androidx.fragment.app.Fragment
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener

import com.example.tanaman.databinding.FragmentKalenderBinding
import java.util.*
import kotlin.collections.ArrayList

class Kalender : Fragment() {
//    private var _binding: FragmentKalenderBinding? = null
//    private val binding get() = _binding!!

    private lateinit var calendarView: CalendarView
    private var events: MutableMap<String, String> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_kalender, container, false)

        calendarView = view.findViewById(R.id.calendar)
        val calendars: ArrayList<CalendarDay> = ArrayList()
        val calendar = Calendar.getInstance()

        //mulai nya dari 0. jadi jan = 0, dec = 11
        //calendar.set(2024, 11, 6)
        calendar.set(2024, Calendar.DECEMBER, 6)
        val calendarDay = CalendarDay(calendar)
        calendarDay.labelColor = R.color.primary
        calendarDay.imageResource = R.drawable.plant
        calendars.add(calendarDay)

        events["06-12-2024"] = "Watering"

        calendarView.setCalendarDays(calendars)
        calendarView.setOnCalendarDayClickListener(object : OnCalendarDayClickListener {
            override fun onClick(calendarDay: CalendarDay) {
                val day = String.format("%02d", calendarDay.calendar.get(Calendar.DAY_OF_MONTH))
                val month = String.format("%02d", calendarDay.calendar.get(Calendar.MONTH) + 1)
                val year = calendarDay.calendar.get(Calendar.YEAR)

                val key = "$day-$month-$year"
                if (events.containsKey(key)) {
                    Toast.makeText(context, events[key], Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Nothing to do", Toast.LENGTH_SHORT).show()
                }
            }
        })

        calendarView.setOnPreviousPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {
                val month =
                    String.format("%02d", calendarView.currentPageDate.get(Calendar.MONTH) + 1)
                val year = calendarView.currentPageDate.get(Calendar.YEAR)
                Toast.makeText(context, "$month/$year", Toast.LENGTH_SHORT).show()
            }
        })

        calendarView.setOnForwardPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {
                val month =
                    String.format("%02d", calendarView.currentPageDate.get(Calendar.MONTH) + 1)
                val year = calendarView.currentPageDate.get(Calendar.YEAR)
                Toast.makeText(context, "$month/$year", Toast.LENGTH_SHORT).show()
            }
        })


        return view
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentKalenderBinding.inflate(inflater, container, false)
//
//        // Set listener for date change
//        binding.calendarView.setOnDateChangeListener { _, year, month, day ->
//            val dateString = String.format("%02d/%02d/%d", month + 1, day, year)
//            binding.calendarText.text = dateString
//
//            val mDate = Calendar.getInstance()
//            mDate.set(year, month, day)
//        }
//
//        return binding.root
//    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}
