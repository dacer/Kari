package im.dacer.injection.component

import dagger.Subcomponent
import im.dacer.features.base.BaseActivity
import im.dacer.features.main.MainActivity
import im.dacer.injection.PerActivity
import im.dacer.injection.module.ActivityModule

@PerActivity
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {
    fun inject(baseActivity: BaseActivity)

    fun inject(mainActivity: MainActivity)

}
