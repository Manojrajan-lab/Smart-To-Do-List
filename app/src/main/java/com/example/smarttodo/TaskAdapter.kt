package com.example.smarttodo

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val taskList: MutableList<Task>,
    private val onItemLongClick: (Task, Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val checkBox: CheckBox = view.findViewById(R.id.taskCheckBox)
        val timestamp: TextView = view.findViewById(R.id.taskTimestamp)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)

        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        val task = taskList[position]

        holder.checkBox.text = task.title
        holder.checkBox.isChecked = task.isCompleted

        val formatter = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        holder.timestamp.text = formatter.format(Date(task.timestamp))

        // Apply strike-through if completed
        if (task.isCompleted) {
            holder.checkBox.paintFlags =
                holder.checkBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.checkBox.paintFlags =
                holder.checkBox.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->

            task.isCompleted = isChecked

            if (isChecked) {
                holder.checkBox.paintFlags =
                    holder.checkBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                holder.checkBox.paintFlags =
                    holder.checkBox.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClick(task, holder.bindingAdapterPosition)
            true
        }
    }

    override fun getItemCount(): Int = taskList.size
}
