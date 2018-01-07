package im.dacer.injection.component

import android.app.Application
import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.tasks.Tasks
import dagger.Component
import im.dacer.data.DataManager
import im.dacer.data.dao.SimpleTaskDao
import im.dacer.data.local.AppDatabase
import im.dacer.data.local.PreferencesHelper
import im.dacer.data.local.SettingUtility
import im.dacer.injection.ApplicationContext
import im.dacer.injection.module.AppModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    @ApplicationContext
    fun context(): Context

    fun application(): Application

    fun dataManager(): DataManager

    fun preferenceHelper(): PreferencesHelper

    fun settingUtility(): SettingUtility

    fun credential(): GoogleAccountCredential

    fun tasks(): Tasks

    fun appDatabase(): AppDatabase

    fun taskDao(): SimpleTaskDao
}
