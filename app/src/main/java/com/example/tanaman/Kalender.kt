package com.example.tanaman

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.example.tanaman.databinding.FragmentKalenderBinding
import java.util.*

class Kalender : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var taskAdapter: WateringTaskAdapter
    private var events: MutableMap<String, MutableList<WateringTask>> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_kalender, container, false)

        // Initialize the calendar view
        calendarView = view.findViewById(R.id.calendarView)
        taskRecyclerView = view.findViewById(R.id.taskRecyclerView)

        // Set up RecyclerView
        taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        taskAdapter = WateringTaskAdapter(emptyList()) { task ->
            // Mark task as done
            task.isDone = true
            taskAdapter.notifyDataSetChanged()
            Toast.makeText(requireContext(), "${task.plantName} marked as done!", Toast.LENGTH_SHORT).show()
        }
        taskRecyclerView.adapter = taskAdapter

        // Initialize calendar events
        setupEvents()

        // Handle calendar date click
        calendarView.setOnCalendarDayClickListener(object : OnCalendarDayClickListener {
            override fun onClick(calendarDay: CalendarDay) {
                val day = String.format("%02d", calendarDay.calendar.get(Calendar.DAY_OF_MONTH))
                val month = String.format("%02d", calendarDay.calendar.get(Calendar.MONTH) + 1)
                val year = calendarDay.calendar.get(Calendar.YEAR)

                val key = "$day-$month-$year"
                if (events.containsKey(key)) {
                    // Update RecyclerView with tasks for the selected date
                    val tasksForDate = events[key] ?: emptyList()
                    taskAdapter.updateData(tasksForDate)
                } else {
                    // Show "Nothing to do" if no tasks exist
                    taskAdapter.updateData(emptyList())
                    Toast.makeText(context, "Nothing to do", Toast.LENGTH_SHORT).show()
                }
            }
        })

        return view
    }

    private fun setupEvents() {
        // Add sample events
        val dec6Tasks = mutableListOf(
            WateringTask("Snake Plant", "200 ml", R.drawable.plant1, R.drawable.baseline_check_circle_24),
            WateringTask("Monstera", "150 ml", R.drawable.plant2, R.drawable.baseline_water_drop_24)
        )
        events["06-12-2024"] = dec6Tasks

        val dec7Tasks = mutableListOf(
            WateringTask("Pothos", "100 ml", R.drawable.plant3, R.drawable.baseline_water_drop_24)
        )
        events["07-12-2024"] = dec7Tasks

        // Highlight these dates on the calendar
        val calendar = Calendar.getInstance()
        val markedDates = arrayListOf<CalendarDay>()

        calendar.set(2024, Calendar.DECEMBER, 6)
        markedDates.add(CalendarDay(calendar))

        calendar.set(2024, Calendar.DECEMBER, 7)
        markedDates.add(CalendarDay(calendar))

        calendarView.setCalendarDays(markedDates)
    }
}
