package im.dacer.injection.component

import dagger.Subcomponent
import im.dacer.features.main.pomo.PomoFragment
import im.dacer.features.main.task.TaskFragment
import im.dacer.injection.PerFragment
import im.dacer.injection.module.FragmentModule

/**
 * This component inject dependencies to all Fragments across the application
 */
@PerFragment
@Subcomponent(modules = [FragmentModule::class])
interface FragmentComponent {
    fun inject(fragment: TaskFragment)
    fun inject(fragment: PomoFragment)
}