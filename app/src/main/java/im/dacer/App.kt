package im.dacer

import android.app.Application
import android.content.Context
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import com.singhajit.sherlock.core.Sherlock
import com.squareup.leakcanary.LeakCanary
import im.dacer.injection.component.AppComponent
import im.dacer.injection.component.DaggerAppComponent
import im.dacer.injection.module.AppModule
import io.fabric.sdk.android.Fabric
import timber.log.Timber


class App : Application() {

    private var appComponent: AppComponent? = null

    companion object {
        operator fun get(context: Context): App {
            return context.applicationContext as App
        }
    }

    override fun onCreate() {
        super.onCreate()

        Fabric.with(this, Crashlytics())
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
            LeakCanary.install(this)
            Sherlock.init(this)
//            Traceur.enableLogging()
        }
    }

    var component: AppComponent
        get() {
            if (appComponent == null) {
                appComponent = DaggerAppComponent.builder()
                        .appModule(AppModule(this))
                        .build()
            }
            return appComponent as AppComponent
        }
        set(appComponent) {
            this.appComponent = appComponent
        }

}