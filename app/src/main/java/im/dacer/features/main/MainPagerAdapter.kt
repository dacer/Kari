package im.dacer.features.main

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import im.dacer.features.main.task.TaskFragment
import kotlin.reflect.full.createInstance

/**
 * Created by Dacer on 02/01/2018.
 */
class MainPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return fragments[position].createInstance()
    }

    override fun getCount() = fragments.size

    companion object {
        val fragments = arrayOf(TaskFragment::class)
    }
}