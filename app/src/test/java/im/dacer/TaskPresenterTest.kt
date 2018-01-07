package im.dacer

import com.nhaarman.mockito_kotlin.*
import im.dacer.common.TestDataFactory
import im.dacer.data.DataManager
import im.dacer.data.dao.SimpleTaskDao
import im.dacer.data.local.SettingUtility
import im.dacer.data.model.SimpleTask
import im.dacer.features.main.task.TaskMvpView
import im.dacer.features.main.task.TaskPresenter
import im.dacer.util.RxSchedulersOverrideRule
import im.dacer.util.helper.GoogleTaskHelper
import io.reactivex.Observable
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.junit.MockitoJUnitRunner



@RunWith(MockitoJUnitRunner::class)
class TaskPresenterTest {

    val taskList = TestDataFactory.makeTaskList(2)
    val taskListId = "id"

    val mockTaskMvpView: TaskMvpView = mock()
    val mockDataManager: DataManager = mock {
        on { getTasks(taskListId) } doReturn Observable.just(taskList)
        on { mergeInsertUpdate(any(), any()) } doReturn Observable.just(emptyList())
    }
    val googleTaskHelper: GoogleTaskHelper = mock {
        on { checkGoogleServiceObs() } doReturn Observable.just(Unit)
    }
    val settingUtils: SettingUtility = mock {
        on { getListId() } doReturn taskListId
    }
    val taskDao: SimpleTaskDao = mock {
        on { dirtyList } doReturn emptyList<SimpleTask>()
    }

    private lateinit var taskPresenter: TaskPresenter

    @JvmField
    @Rule
    val overrideSchedulersRule = RxSchedulersOverrideRule()

    @Before
    fun setUp() {
        taskPresenter = TaskPresenter(mockDataManager, settingUtils, taskDao)
        taskPresenter.attachView(mockTaskMvpView)
    }

    @After
    fun tearDown() {
        taskPresenter.detachView()
    }

    @Test
    fun getTaskReturnsTaskNames() {
        taskPresenter.syncTasks(googleTaskHelper)

        verify(mockTaskMvpView, times(2)).showProgress(anyBoolean())
        verify(mockTaskMvpView).showTask(taskList)
        verify(mockTaskMvpView, never()).showError(RuntimeException())

    }

}