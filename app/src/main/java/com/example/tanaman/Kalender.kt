package com.example.tanaman

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.example.tanaman.R
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import androidx.appcompat.app.AlertDialog
import com.google.common.reflect.TypeToken
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.logging.Log
import com.google.firebase.events.Event
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase



class Kalender : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var taskAdapter: WateringTaskAdapter
    private var events: MutableMap<String, MutableList<WateringTask>> = mutableMapOf()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_kalender, container, false)
        val addTaskButton = view.findViewById<Button>(R.id.btn_add_task)
        addTaskButton.setOnClickListener {
            showAddTaskDialog()
        }


        // Initialize the calendar view
        calendarView = view.findViewById(R.id.calendarView)
        taskRecyclerView = view.findViewById(R.id.taskRecyclerView)

        // Set up RecyclerView
        taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        taskAdapter = WateringTaskAdapter(emptyList()) { task ->
            task.isDone = true
            saveTaskToFirebase(task)
            taskAdapter.notifyDataSetChanged()
            Toast.makeText(
                requireContext(),
                "${task.plantName} marked as done!",
                Toast.LENGTH_SHORT
            ).show()
        }
        taskRecyclerView.adapter = taskAdapter

        // Fetch tasks from Firebase
        fetchEventsFromFirebase()

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
                    taskAdapter.updateData(emptyList())
                    Toast.makeText(context, "Nothing to do", Toast.LENGTH_SHORT).show()
                }
            }
        })

        return view
    }

    private fun fetchEventsFromFirebase() {
        val db = Firebase.firestore

        db.collection("events").get()
            .addOnSuccessListener { querySnapshot ->
                events.clear()
                for (document in querySnapshot.documents) {
                    val dateKey = document.id
                    // Safely map the "tasks" array from Firestore
                    val taskList = document.get("tasks") as? List<Map<String, Any>> ?: emptyList()

                    // Convert each task map to a WateringTask object
                    val tasks = taskList.mapNotNull { taskMap ->
                        try {
                            WateringTask(
                                plantName = taskMap["plantName"] as? String ?: "",
                                waterQuantity = taskMap["waterQuantity"] as? String ?: "",
                                imageResource = (taskMap["imageResource"] as? Long)?.toInt() ?: 0,
                                actionIconResource = (taskMap["actionIconResource"] as? Long)?.toInt() ?: 0,
                                isDone = taskMap["isDone"] as? Boolean ?: false
                            )
                        } catch (e: Exception) {
                            android.util.Log.e("Kalender", "Error converting task: ${e.message}")
                            null
                        }
                    }
                    events[dateKey] = tasks.toMutableList()
                }
                updateCalendar()
            }
            .addOnFailureListener { exception ->
                android.util.Log.e("Kalender", "Error fetching events: ${exception.message}")
            }
    }

    private fun saveTaskToFirebase(task: WateringTask) {
        val selectedDate = calendarView.selectedDate
        val day = String.format("%02d", selectedDate.get(Calendar.DAY_OF_MONTH))
        val month = String.format("%02d", selectedDate.get(Calendar.MONTH) + 1)
        val year = selectedDate.get(Calendar.YEAR)
        val key = "$day-$month-$year"

        val tasksForDate = events[key] ?: mutableListOf()
        if (!tasksForDate.contains(task)) {
            tasksForDate.add(task)
        }

        db.collection("events").document(key)
            .set(FirebaseEvent(tasksForDate))
            .addOnSuccessListener {
                Toast.makeText(context, "Task saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to save task", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAddTaskDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_task, null)
        val etPlantName = dialogView.findViewById<EditText>(R.id.et_plant_name)
        val etWaterQuantity = dialogView.findViewById<EditText>(R.id.et_water_quantity)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Watering Task")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .create()

        dialogView.findViewById<Button>(R.id.btn_save_task).setOnClickListener {
            val plantName = etPlantName.text.toString()
            val waterQuantity = etWaterQuantity.text.toString()

            if (plantName.isBlank() || waterQuantity.isBlank()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val selectedDate = calendarView.selectedDate
                val day = String.format("%02d", selectedDate.get(Calendar.DAY_OF_MONTH))
                val month = String.format("%02d", selectedDate.get(Calendar.MONTH) + 1)
                val year = selectedDate.get(Calendar.YEAR)
                val key = "$day-$month-$year"

                // Create a new task
                val newTask = WateringTask(
                    plantName = plantName,
                    waterQuantity = waterQuantity,
                    imageResource = R.drawable.plant, // Placeholder image
                    actionIconResource = R.drawable.baseline_water_drop_24
                )

                // Save locally
                if (!events.containsKey(key)) {
                    events[key] = mutableListOf()
                }
                events[key]?.add(newTask)

                // Save to Firebase
                saveTaskToFirebase(newTask)

                // Update UI
                if (calendarView.selectedDate.get(Calendar.DAY_OF_MONTH) == selectedDate.get(
                        Calendar.DAY_OF_MONTH
                    )
                ) {
                    taskAdapter.updateData(events[key] ?: emptyList())
                }

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun updateCalendar() {
        val markedDates = arrayListOf<CalendarDay>()
        val calendar = Calendar.getInstance()

        for (dateKey in events.keys) {
            val parts = dateKey.split("-")
            if (parts.size == 3) {
                val day = parts[0].toInt()
                val month = parts[1].toInt() - 1 // Months are zero-indexed
                val year = parts[2].toInt()
                calendar.set(year, month, day)
                markedDates.add(CalendarDay(calendar))
            }
        }
        calendarView.setCalendarDays(markedDates)
    }

    override fun onResume() {
        super.onResume()
        fetchEventsFromFirebase()
    }

}
