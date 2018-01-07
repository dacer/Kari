package im.dacer.common.injection.module

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.tasks.Tasks
import dagger.Module
import dagger.Provides
import im.dacer.data.DataManager
import im.dacer.data.dao.SimpleTaskDao
import im.dacer.data.local.AppDatabase
import im.dacer.injection.ApplicationContext
import im.dacer.injection.module.AppModule
import org.mockito.Mockito.mock
import javax.inject.Singleton

/**
 * Provides application-level dependencies for an app running on a testing environment
 * This allows injecting mocks if necessary.
 */
@Module
class ApplicationTestModule(private val application: Application) {

    @Provides
    @Singleton
    internal fun provideApplication(): Application {
        return application
    }

    @Provides
    @ApplicationContext
    internal fun provideContext(): Context {
        return application
    }

    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "kari")
                    .allowMainThreadQueries().build()

    @Provides
    @Singleton
    fun providesTaskDao(database: AppDatabase): SimpleTaskDao = database.taskDao()

    @Provides
    @Singleton
    internal fun provideCredential(): GoogleAccountCredential =
            GoogleAccountCredential.usingOAuth2(application, AppModule.SCOPES.toList())
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

    /*************
     * MOCKS
     */

    @Provides
    @Singleton
    internal fun providesDataManager(): DataManager {
        return mock(DataManager::class.java)
    }


}
