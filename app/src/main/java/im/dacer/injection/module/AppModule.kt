package im.dacer.injection.module

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.tasks.Tasks
import com.google.api.services.tasks.TasksScopes
import dagger.Module
import dagger.Provides
import im.dacer.data.dao.SimpleTaskDao
import im.dacer.data.local.AppDatabase
import im.dacer.injection.ApplicationContext
import javax.inject.Singleton


@Module
class AppModule(private val application: Application) {


    @Provides
    internal fun provideApplication(): Application = application

    @Provides
    @ApplicationContext
    internal fun provideContext(): Context = application

    @Provides
    @Singleton
    internal fun provideCredential(): GoogleAccountCredential =
            GoogleAccountCredential.usingOAuth2(application, SCOPES.toList())
                    .setBackOff(ExponentialBackOff())


    @Provides
    @Singleton
    internal fun provideTaskService(credential: GoogleAccountCredential): Tasks {
        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()
        return Tasks.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Kari")
                .build()
    }

    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "kari")
                    .allowMainThreadQueries().build()

    @Provides
    @Singleton
    fun providesTaskDao(database: AppDatabase): SimpleTaskDao = database.taskDao()

    companion object {
        val SCOPES = arrayOf(TasksScopes.TASKS)
    }
}