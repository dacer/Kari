package im.dacer.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.api.services.tasks.model.Task

/**
 * Created by Dacer on 03/01/2018.
 */
@Entity(tableName = "task")
data class SimpleTask(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int,
        @ColumnInfo(name = "google_id") var googleId: String,
        @ColumnInfo(name = "title") var title: String,
        @ColumnInfo(name = "deleted") var deleted: Boolean = false,
        @ColumnInfo(name = "dirty") var dirty: Boolean = true) {

    fun toTask(): Task = Task().setId(googleId).setTitle(title).setDeleted(deleted)

    fun markDirty(): SimpleTask {
        this.dirty = true
        return this
    }

    companion object {
        fun getFromTask(task: Task) : SimpleTask = SimpleTask(0, task.id, task.title, task.deleted ?: false, false)
        fun getTaskWithDirty(title: String) : SimpleTask = SimpleTask(0, "", title)
    }

}