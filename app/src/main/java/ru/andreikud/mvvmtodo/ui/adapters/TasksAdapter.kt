package ru.andreikud.mvvmtodo.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andreikud.mvvmtodo.databinding.ItemTaskBinding
import ru.andreikud.mvvmtodo.data.model.Task

class TasksAdapter(
    private val onItemClickListener: OnItemClickListener
) : ListAdapter<Task, TasksAdapter.TaskViewHolder>(TaskDiffCallback()) {

    interface OnItemClickListener {
        fun onItemClick(item: Task)
        fun onCheckBoxClick(item: Task, isChecked: Boolean)
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            with(binding) {
                root.setOnClickListener {
                    getTaskOrNull()?.let { item ->
                        onItemClickListener.onItemClick(item)
                    }
                }

                cbCompleted.setOnClickListener {
                    getTaskOrNull()?.let { item ->
                        onItemClickListener.onCheckBoxClick(item, cbCompleted.isChecked)
                    }
                }
            }
        }

        fun bind(task: Task) {
            with(binding) {
                cbCompleted.isChecked = task.isCompleted
                tvItem.text = task.name
                tvItem.paint.isStrikeThruText = task.isCompleted
                ivImportance.isVisible = task.isImportant
            }
        }

        private fun getTaskOrNull(): Task? {
            val position = adapterPosition
            return if (position != RecyclerView.NO_POSITION)
                getItem(position)
            else
                null
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}