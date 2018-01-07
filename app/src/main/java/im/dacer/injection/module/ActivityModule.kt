package im.dacer.injection.module

import android.app.Activity
import android.content.Context
import com.tbruyelle.rxpermissions2.RxPermissions

import dagger.Module
import dagger.Provides
import im.dacer.injection.ActivityContext

@Module
class ActivityModule(private val activity: Activity) {

    @Provides
    internal fun provideActivity(): Activity = activity

    @Provides
    @ActivityContext
    internal fun providesContext(): Context = activity

    @Provides
    internal fun providesRxPermissions(): RxPermissions = RxPermissions(activity)
}
