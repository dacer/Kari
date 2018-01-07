package im.dacer.util.extension

import com.google.api.services.tasks.model.Task
import im.dacer.data.model.SimpleTask

/**
 * Created by Dacer on 03/01/2018.
 */

fun Task.toSimpleTask(): SimpleTask = SimpleTask.getFromTask(this)