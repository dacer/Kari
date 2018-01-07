package im.dacer.common

import com.google.api.services.tasks.model.Task
import java.util.*

/**
 * Factory class that makes instances of data models with random field values.
 * The aim of this class is to help setting up test fixtures.
 */
object TestDataFactory {

    private val random = Random()

    fun randomUuid(): String {
        return UUID.randomUUID().toString()
    }

    fun makeTaskList(count: Int): List<Task> {
        return (0 until count).map { makeTask(it) }
    }

    fun makeTask(num: Int): Task {
        return makeTask("$num","randomTask $num", false)
    }

    fun makeTask(id: String, title: String, deleted: Boolean): Task {
        return Task().setId(id).setTitle(title).setDeleted(deleted)
    }
}