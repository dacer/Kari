package im.dacer

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.google.api.services.tasks.model.Task
import im.dacer.common.TestComponentRule
import im.dacer.common.TestDataFactory
import im.dacer.features.main.MainActivity
import io.reactivex.Observable
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private val component = TestComponentRule(InstrumentationRegistry.getTargetContext())
    private val main = ActivityTestRule(MainActivity::class.java, false, false)

    // TestComponentRule needs to go first to make sure the Dagger ApplicationTestComponent is set
    // in the Application before any Activity is launched.
    @Rule @JvmField
    var chain: TestRule = RuleChain.outerRule(component).around(main)

    @Test
    fun checkTasksDisplay() {
        val taskList = TestDataFactory.makeTaskList(5)
        stubDataManagerGetTaskList(Observable.just(taskList))
        main.launchActivity(null)

        for (task in taskList) {
            onView(withText(task.title)).check(matches(isDisplayed()))
        }
    }

    private fun stubDataManagerGetTaskList(observable: Observable<List<Task>>) {
        `when`(component.mockDataManager.getTasks(ArgumentMatchers.anyString()))
                .thenReturn(observable)
    }


}