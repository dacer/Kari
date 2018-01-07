package im.dacer.data

import com.google.api.services.tasks.Tasks
import com.google.api.services.tasks.model.Task
import com.google.api.services.tasks.model.TaskList
import im.dacer.data.model.SimpleTask
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataManager @Inject
constructor(private val taskService: Tasks) {

    fun getTasks(taskListId: String): Observable<List<Task>> {
        return Observable.fromCallable({
            taskService.tasks().list(taskListId)
                    .setMaxResults(999)
                    .execute().items
        })
    }

    fun getTaskLists(): Observable<List<TaskList>> {
        return Observable.fromCallable({
            taskService.tasklists().list()
                    .setMaxResults(999)
                    .execute().items
        })
    }

    fun mergeInsertUpdate(listId: String, simpleTasks: List<SimpleTask>): Observable<List<Task>> {
        return Observable.merge(insertTasks(listId, simpleTasks), updateTasks(listId, simpleTasks))
    }

    fun insertTasks(taskListId: String, simpleTasks: List<SimpleTask>): Observable<List<Task>>  {
        return Observable.fromIterable(simpleTasks)
                .filter { it.dirty && it.googleId.isEmpty() }
                .map { taskService.tasks().insert(taskListId, it.toTask()).execute() }
                .toList()
                .toObservable()
    }

    fun updateTasks(taskListId: String, simpleTasks: List<SimpleTask>): Observable<List<Task>>  {
        return Observable.fromIterable(simpleTasks)
                .filter { it.dirty && it.googleId.isNotEmpty() }
                .map { taskService.tasks().update(taskListId, it.googleId, it.toTask()).execute() }
                .toList()
                .toObservable()
    }
}