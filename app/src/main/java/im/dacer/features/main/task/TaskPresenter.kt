package im.dacer.features.main.task

import com.google.api.services.tasks.model.Task
import im.dacer.data.DataManager
import im.dacer.data.dao.SimpleTaskDao
import im.dacer.data.local.SettingUtility
import im.dacer.data.model.SimpleTask
import im.dacer.features.base.BasePresenter
import im.dacer.injection.ConfigPersistent
import im.dacer.util.extension.toSimpleTask
import im.dacer.util.helper.GoogleTaskHelper
import im.dacer.util.rx.scheduler.SchedulerUtils
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@ConfigPersistent
class TaskPresenter @Inject
constructor(private val dataManager: DataManager,
            private val settingsUtil: SettingUtility,
            private val taskDao: SimpleTaskDao) : BasePresenter<TaskMvpView>() {

    private var updateTaskDis: Disposable? = null

    // -- Network Operations --
    /**
     * Upload / Update all dirty data and download the latest data
     */
    internal fun syncTasks(googleTaskHelper: GoogleTaskHelper) {
        checkViewAttached()
        mvpView?.showProgress(true)
        updateTaskDis?.removeFromComposite()
        updateTaskDis = googleTaskHelper.checkGoogleServiceObs()
                .observeOn(Schedulers.io())
                .map { taskDao.dirtyList }
                .flatMap { dataManager.mergeInsertUpdate(listId, it) }
                .doOnNext { updateTasksFromServer(it) }
                .flatMap { dataManager.getTasks(listId) }
                .doOnNext { insertTasksFromServer(it) }
                .compose(SchedulerUtils.ioToMain())
                .subscribe({ tasks ->
                    mvpView?.showTask(tasks)
                    mvpView?.showProgress(false)
                    updateTaskDis?.removeFromComposite()
                }, {
                    if (!googleTaskHelper.dealWithException(it)) mvpView?.toastError(it)
                    mvpView?.showProgress(false)
                })
                .addToComposite()
    }

    private fun insertTasksFromServer(tasks: List<Task>) {
        val simpleTasks = tasks.map { it.toSimpleTask() }
        taskDao.insertAll(simpleTasks)
    }

    private fun updateTasksFromServer(tasks: List<Task>) {
        val simpleTasks = tasks.map { it.toSimpleTask() }
        taskDao.update(simpleTasks)
    }

    // -- Database Operations --

    internal fun listenTaskFromDb() {
        taskDao.all
                .map { it.map { it.toTask() } }
                .compose(SchedulerUtils.ioToMain())
                .subscribe { mvpView?.showTask(it) }
    }

    internal fun insertTaskLocal(title: String) {
        taskDao.insert(SimpleTask.getTaskWithDirty(title))
    }

    internal fun updateTaskLocal(task: SimpleTask) {
        taskDao.update(task.markDirty())
    }

    val listId: String get() = settingsUtil.getListId()
}