package im.dacer.util.helper

import android.content.Context
import com.crashlytics.android.Crashlytics
import im.dacer.util.exception.IgnorableException
import org.jetbrains.anko.toast
import timber.log.Timber

/**
 * Created by Dacer on 02/01/2018.
 */
class FabricHelper {

    companion object {
        fun log(exception: Throwable, context: Context? = null) {
            if (exception !is IgnorableException) {
                Crashlytics.logException(exception)
                Timber.e(exception)
            }
            if (!exception.message.isNullOrEmpty()) context?.toast(exception.message!!)
        }
    }
}