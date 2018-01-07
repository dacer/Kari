package im.dacer

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.runner.AndroidJUnit4
import im.dacer.common.TestDataFactory
import im.dacer.data.dao.SimpleTaskDao
import im.dacer.data.local.AppDatabase
import im.dacer.util.extension.toSimpleTask
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Dacer on 03/01/2018.
 */
@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var mDatabase: AppDatabase
    private lateinit var mTaskDao: SimpleTaskDao

    @Before
    fun initDb() {
        mDatabase = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getContext(),
                AppDatabase::class.java)
                        .allowMainThreadQueries()
                        .build()
        mTaskDao = mDatabase.taskDao()
    }

    @After
    fun closeDb() {
        mDatabase.close()
    }


    @Test
    fun writeUserAndReadInList()  {
        val task = TestDataFactory.makeTask("1", "testTask", false)
        mTaskDao.insertAll(listOf(task.toSimpleTask()))
        mTaskDao.all.subscribe {
            ViewMatchers.assertThat(it[0].toTask(), Matchers.equalTo(task))
        }
    }

}