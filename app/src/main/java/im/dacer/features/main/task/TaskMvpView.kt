package im.dacer.features.main.task

import com.google.api.services.tasks.model.Task
import im.dacer.features.base.MvpView

interface TaskMvpView : MvpView {

    fun showTask(tasks: List<Task>)

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)

}