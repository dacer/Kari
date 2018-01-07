package im.dacer.features.main.task

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.api.services.tasks.model.Task
import im.dacer.R
import javax.inject.Inject

class TaskAdapter @Inject
constructor() : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var taskList: List<Task>
    var clickListener: ClickListener? = null

    init {
        taskList = emptyList()
    }

    fun setTask(tasks: List<Task>) {
        taskList = tasks
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.bind(task)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    interface ClickListener {
        fun onTaskClick(task: Task)
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private lateinit var selectedTask: Task

        @BindView(R.id.task_name)
        @JvmField var taskName: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
            itemView.setOnClickListener {
                clickListener?.onTaskClick(selectedTask)
            }
        }

        fun bind(task: Task) {
            selectedTask = task
            taskName?.text = String.format("%s", task.title)
        }
    }

}