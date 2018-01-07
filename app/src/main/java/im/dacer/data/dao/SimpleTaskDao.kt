package im.dacer.data.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import im.dacer.data.model.SimpleTask
import io.reactivex.Flowable

/**
 * Created by Dacer on 03/01/2018.
 *
 * refer https://medium.com/google-developers/room-rxjava-acb0cd4f3757
 */

@Dao
interface SimpleTaskDao {

    /**
     * Auto emit when data updated
     */
    @get:Query("SELECT * FROM task WHERE deleted = 0")
    val all: Flowable<List<SimpleTask>>
    
    @get:Query("SELECT * FROM task WHERE dirty = 1")
    val dirtyList: List<SimpleTask>

    @Insert(onConflict = REPLACE)
    fun insertAll(tasks: List<SimpleTask>)

    @Insert(onConflict = REPLACE)
    fun insert(task: SimpleTask)

    @Update(onConflict = REPLACE)
    fun update(task: SimpleTask)

    @Update(onConflict = REPLACE)
    fun update(task: List<SimpleTask>)
}