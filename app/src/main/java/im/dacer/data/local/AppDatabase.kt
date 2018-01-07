package im.dacer.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import im.dacer.data.dao.SimpleTaskDao
import im.dacer.data.model.SimpleTask


/**
 * Created by Dacer on 03/01/2018.
 */
@Database(entities = [(SimpleTask::class)], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): SimpleTaskDao
}