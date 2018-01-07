package im.dacer.features.main.task

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import butterknife.OnClick
import com.google.api.services.tasks.model.Task
import im.dacer.R
import im.dacer.features.base.BaseFragment
import im.dacer.util.helper.GoogleTaskHelper
import kotlinx.android.synthetic.main.fragment_task.*
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject

/**
 * Created by Dacer on 02/01/2018.
 */
class TaskFragment: BaseFragment() , TaskMvpView, TaskAdapter.ClickListener, GoogleTaskHelper.UpdateTaskListener {
    @Inject lateinit var taskAdapter: TaskAdapter
    @Inject lateinit var taskPresenter: TaskPresenter
    @Inject lateinit var googleTaskHelper: GoogleTaskHelper

    override fun layoutId() = R.layout.fragment_task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent().inject(this)
        taskPresenter.attachView(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeToRefresh?.apply {
            setProgressBackgroundColorSchemeResource(R.color.primary)
            setColorSchemeResources(R.color.white)
            setOnRefreshListener { updateTask() }
        }

        taskRecyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
        }

        taskAdapter.clickListener = this
        googleTaskHelper.updateTaskListener = this
        startListenTaskFromDb()
    }

    @OnClick(R.id.bindGoogleBtn)
    fun onClickBindGoogle() {
        updateTask()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        googleTaskHelper.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        taskPresenter.detachView()
    }


    override fun showTask(tasks: List<Task>) {
        taskAdapter.apply {
            setTask(tasks)
            notifyDataSetChanged()
        }
    }

    override fun showProgress(show: Boolean) {
        showProgressDialog(show)
        swipeToRefresh?.isRefreshing = show
    }

    override fun showError(error: Throwable) {
        toast(error.localizedMessage)
    }

    override fun onTaskClick(task: Task) {
        //todo edit task
    }

    override fun updateTask() = taskPresenter.syncTasks(googleTaskHelper)

    private fun startListenTaskFromDb() = taskPresenter.listenTaskFromDb()
}