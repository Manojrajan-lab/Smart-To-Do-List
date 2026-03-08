package com.example.smarttodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var taskInput: EditText
    private lateinit var addTaskButton: Button
    private lateinit var recyclerView: RecyclerView

    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()

    private lateinit var database: TaskDatabase

    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskInput = findViewById(R.id.taskInput)
        addTaskButton = findViewById(R.id.addTaskButton)
        recyclerView = findViewById(R.id.taskRecyclerView)

        // Using TaskDatabase.getDatabase(this) for consistent initialization
        database = TaskDatabase.getDatabase(this)

        // Notification setup
        notificationHelper = NotificationHelper(this)
        notificationHelper.createChannel()

        taskAdapter = TaskAdapter(taskList) { task, position ->

            thread {

                database.taskDao().deleteTask(task)

                runOnUiThread {
                    if (position != RecyclerView.NO_POSITION && position < taskList.size) {
                        taskList.removeAt(position)
                        taskAdapter.notifyItemRemoved(position)
                    }
                }
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        loadTasks()

        addTaskButton.setOnClickListener {

            val text = taskInput.text.toString()

            if (text.isNotEmpty()) {

                val task = Task(title = text)

                thread {

                    database.taskDao().insertTask(task)

                    runOnUiThread {

                        taskList.add(task)
                        taskAdapter.notifyItemInserted(taskList.size - 1)
                        taskInput.text.clear()

                        // Show notification when task is added
                        notificationHelper.showNotification(text)

                    }
                }
            }
        }
    }

    private fun loadTasks() {

        thread {

            val tasks = database.taskDao().getAllTasks()

            runOnUiThread {

                taskList.clear()
                taskList.addAll(tasks)

                taskAdapter.notifyDataSetChanged()

            }
        }
    }
}
