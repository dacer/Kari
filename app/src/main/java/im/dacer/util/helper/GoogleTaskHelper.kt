package im.dacer.util.helper

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.tbruyelle.rxpermissions2.RxPermissions
import im.dacer.R
import im.dacer.data.DataManager
import im.dacer.data.local.SettingUtility
import im.dacer.util.exception.GoogleServiceException
import im.dacer.util.exception.NoNetworkException
import im.dacer.util.extension.immersiveShow
import im.dacer.util.extension.isGooglePlayServicesAvailable
import im.dacer.util.extension.isNetworkConnected
import im.dacer.util.extension.toast
import im.dacer.util.rx.scheduler.SchedulerUtils
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Dacer on 02/01/2018.
 */
class GoogleTaskHelper @Inject
constructor(private val activity: FragmentActivity?,
            private val mCredential: GoogleAccountCredential,
            private val rxPermissions: RxPermissions,
            private val settingsUtil: SettingUtility,
            private val dataManager: DataManager) {

    var updateTaskListener: UpdateTaskListener? = null

    interface UpdateTaskListener {
        fun updateTask()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_GOOGLE_PLAY_SERVICES -> if (resultCode != Activity.RESULT_OK) {
                toast(R.string.google_play_error_miss_service)
            } else {
                showSelectListDialogObs(true).subscribe()
            }
            REQUEST_ACCOUNT_PICKER -> if (resultCode == Activity.RESULT_OK && data != null &&
                    data.extras != null) {
                val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                if (accountName != null) {
                    settingsUtil.setGoogleName(accountName)
                    mCredential.selectedAccountName = accountName
                    showSelectListDialogObs(true).subscribe()
                }
            }
            REQUEST_AUTHORIZATION -> if (resultCode == Activity.RESULT_OK) {
                showSelectListDialogObs(true).subscribe()
            }
        }
    }

    /**
     * @return true, if exception can be dealt with
     */
    fun dealWithException(exception: Throwable): Boolean {
        when (exception) {
            is GooglePlayServicesAvailabilityIOException ->
                showGooglePlayServicesAvailabilityErrorDialog(exception.connectionStatusCode)

            is UserRecoverableAuthIOException ->
                activity?.startActivityForResult(exception.intent, REQUEST_AUTHORIZATION)

            else -> return false
        }
        return true
    }

    /**
     * If available, will auto let user to choose Google Account and choose the Google task list for sync
     *
     * Throw NoNetworkException, GoogleServiceException
     */
    fun checkGoogleServiceObs(): Observable<Unit> {
        return if (!activity!!.isNetworkConnected()) {
            Observable.fromCallable { throw NoNetworkException(activity) }
        }else if (!activity.isGooglePlayServicesAvailable()) {
            acquireGooglePlayServicesObs()
        } else if (mCredential.selectedAccountName.isNullOrEmpty()) {
            chooseAccountObs()
        } else if (settingsUtil.getListId().isEmpty()) {
            showSelectListDialogObs()
        } else {
            Observable.just(Unit)
        }
    }

    //todo need a better UI
    private fun showSelectListDialogObs(fireUpdateListener: Boolean = false): Observable<Unit> {
        return dataManager.getTaskLists()
                .compose(SchedulerUtils.ioToMain())
                .map { lists ->
                    MaterialDialog.Builder(activity!!).items(lists.map { it.title }).itemsCallback { _, _, position, _ ->
                        val list = lists[position]
                        settingsUtil.setShowListId(list.id)
                        if (fireUpdateListener) updateTaskListener?.updateTask()
                    }.show()
                    return@map Unit
                }
    }

    private fun chooseAccountObs(): Observable<Unit> {
        return rxPermissions.request(Manifest.permission.GET_ACCOUNTS)
                .flatMap { granted ->
                    if (!granted) throw GoogleServiceException(activity!!.getString(R.string.google_play_error_no_permission))

                    val accountName = settingsUtil.getGoogleName()
                    if (accountName.isNotEmpty()) {
                        mCredential.selectedAccountName = accountName
                        if (settingsUtil.getListId().isEmpty()) return@flatMap showSelectListDialogObs()
                        return@flatMap Observable.just(Unit)
                    } else {
                        activity?.startActivityForResult(
                                mCredential.newChooseAccountIntent(),
                                REQUEST_ACCOUNT_PICKER)
                        throw GoogleServiceException("")
                    }
                }
    }

    private fun acquireGooglePlayServicesObs(): Observable<Unit> {
        return Observable.fromCallable {
            val apiAvailability = GoogleApiAvailability.getInstance()
            val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(activity)
            if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
                showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
            } else {
                toast(R.string.google_play_error_not_available)
            }
        }
    }


    private fun showGooglePlayServicesAvailabilityErrorDialog(
            connectionStatusCode: Int) {
        GoogleApiAvailability.getInstance()
                .getErrorDialog(activity, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES)
                .immersiveShow()
    }

    private fun toast(resId: Int) {
        activity?.toast(resId)
    }

    companion object {
        private val REQUEST_GOOGLE_PLAY_SERVICES = 1000
        private val REQUEST_ACCOUNT_PICKER = 1001
        private val REQUEST_AUTHORIZATION = 1002
        private val REQUEST_PERMISSION_GET_ACCOUNTS = 1003
    }

}