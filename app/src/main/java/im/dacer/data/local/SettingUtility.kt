package im.dacer.data.local

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Dacer on 01/01/2018.
 */
@Singleton
class SettingUtility @Inject constructor(private val mHelper: PreferencesHelper) {

    fun getGoogleName(): String {
        return mHelper[PREF_GOOGLE_ACCOUNT_NAME, ""]
    }

    fun setGoogleName(type: String) {
        mHelper[PREF_GOOGLE_ACCOUNT_NAME] = type
    }

    fun getListId(): String {
        return mHelper[PREF_SHOW_LIST_ID, ""]
    }

    fun setShowListId(type: String) {
        mHelper[PREF_SHOW_LIST_ID] = type
    }

    companion object {
        val PREF_GOOGLE_ACCOUNT_NAME = "google_account_name"
        val PREF_SHOW_LIST_ID = "task_list_id"
    }
}