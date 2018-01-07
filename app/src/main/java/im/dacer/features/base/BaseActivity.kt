package im.dacer.features.base

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.annotation.LayoutRes
import android.support.v4.util.LongSparseArray
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.Window
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import im.dacer.App
import im.dacer.injection.component.ActivityComponent
import im.dacer.injection.component.ConfigPersistentComponent
import im.dacer.injection.component.DaggerConfigPersistentComponent
import im.dacer.injection.module.ActivityModule
import im.dacer.util.helper.FabricHelper
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicLong


/**
 * Abstract activity that every other Activity in this application must implement. It provides the
 * following functionality:
 * - Handles creation of Dagger components and makes sure that instances of
 * ConfigPersistentComponent are kept across configuration changes.
 * - Set up and handles a GoogleApiClient instance that can be used to access the Google sign in
 * api.
 * - Handles signing out when an authentication error event is received.
 */
abstract class BaseActivity : AppCompatActivity(), MvpView {

    private var activityComponent: ActivityComponent? = null
    private var activityId = 0L
    private val mHideHandler by lazy { HideHandler(window) }
    private val progressDialog: MaterialDialog by lazy {
        //todo i18n
        MaterialDialog.Builder(this)
                .title("Loading...")
                .progress(true, 0)
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()
        setContentView(layoutId())
        ButterKnife.bind(this)
        // Create the ActivityComponent and reuses cached ConfigPersistentComponent if this is
        // being called after a configuration change.
        activityId = savedInstanceState?.getLong(KEY_ACTIVITY_ID) ?: NEXT_ID.getAndIncrement()
        val configPersistentComponent: ConfigPersistentComponent
        if (componentsArray.get(activityId) == null) {
            Timber.i("Creating new ConfigPersistentComponent id=%d", activityId)
            configPersistentComponent = DaggerConfigPersistentComponent.builder()
                    .appComponent(App[this].component)
                    .build()
            componentsArray.put(activityId, configPersistentComponent)
        } else {
            Timber.i("Reusing ConfigPersistentComponent id=%d", activityId)
            configPersistentComponent = componentsArray.get(activityId)
        }
        activityComponent = configPersistentComponent.activityComponent(ActivityModule(this))
        activityComponent?.inject(this)
    }

    @LayoutRes abstract fun layoutId(): Int

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(KEY_ACTIVITY_ID, activityId)
    }

    override fun onDestroy() {
        if (!isChangingConfigurations) {
            Timber.i("Clearing ConfigPersistentComponent id=%d", activityId)
            componentsArray.remove(activityId)
        }
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            delayedHide(300)
        } else {
            mHideHandler.removeMessages(0)
        }
    }

    override fun toastError(t: Throwable) {
        FabricHelper.log(t, this)
    }

    protected fun hideSystemUIWithDelay() {
        delayedHide(300)
    }
    protected fun hideSystemUI() {
        hideSystemUi(window)
    }

    private fun delayedHide(delayMillis: Long) {
        hideSystemUI()
        mHideHandler.removeMessages(0)
        mHideHandler.sendEmptyMessageDelayed(0, delayMillis)
    }

    fun showProgressDialog(show: Boolean) {
        if (progressDialog.isShowing) progressDialog.dismiss()
        if (show) progressDialog.show()
    }

    fun activityComponent() = activityComponent as ActivityComponent


    internal class HideHandler(window: Window) : Handler() {
        private var mWindowRef: WeakReference<Window> = WeakReference(window)
        override fun handleMessage(msg: Message) {
            hideSystemUi(mWindowRef.get())
        }
    }

    companion object {
        private val KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID"
        private val NEXT_ID = AtomicLong(0)
        private val componentsArray = LongSparseArray<ConfigPersistentComponent>()
        const val SYSTEM_IMMERSIVE_UI_VISIBILITY = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        fun hideSystemUi(window: Window?) {
            window?.decorView?.systemUiVisibility = SYSTEM_IMMERSIVE_UI_VISIBILITY
        }
    }
}