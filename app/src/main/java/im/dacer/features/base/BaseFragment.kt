package im.dacer.features.base

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v4.util.LongSparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import im.dacer.App
import im.dacer.injection.component.ConfigPersistentComponent
import im.dacer.injection.component.DaggerConfigPersistentComponent
import im.dacer.injection.component.FragmentComponent
import im.dacer.injection.module.FragmentModule
import im.dacer.util.extension.immersiveShow
import im.dacer.util.helper.FabricHelper
import timber.log.Timber
import java.util.concurrent.atomic.AtomicLong

/**
 * Abstract Fragment that every other Fragment in this application must implement. It handles
 * creation of Dagger components and makes sure that instances of ConfigPersistentComponent are kept
 * across configuration changes.
 */
abstract class BaseFragment : Fragment(), MvpView {

    private var fragmentComponent: FragmentComponent? = null
    private var fragmentId = 0L

    private val progressDialog: MaterialDialog by lazy {
        //todo i18n
        MaterialDialog.Builder(activity!!)
                .title("Loading...")
                .progress(true, 0)
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create the FragmentComponent and reuses cached ConfigPersistentComponent if this is
        // being called after a configuration change.
        fragmentId = savedInstanceState?.getLong(KEY_FRAGMENT_ID) ?: NEXT_ID.getAndIncrement()
        val configPersistentComponent: ConfigPersistentComponent
        if (componentsArray.get(fragmentId) == null) {
            Timber.i("Creating new ConfigPersistentComponent id=%d", fragmentId)
            configPersistentComponent = DaggerConfigPersistentComponent.builder()
                    .appComponent(App[activity as Context].component)
                    .build()
            componentsArray.put(fragmentId, configPersistentComponent)
        } else {
            Timber.i("Reusing ConfigPersistentComponent id=%d", fragmentId)
            configPersistentComponent = componentsArray.get(fragmentId)
        }
        fragmentComponent = configPersistentComponent.fragmentComponent(FragmentModule(this))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(layoutId(), container, false)
        ButterKnife.bind(this, view)
        return view
    }

    @LayoutRes abstract fun layoutId(): Int

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(KEY_FRAGMENT_ID, fragmentId)
    }

    override fun onDestroy() {
        if (!activity!!.isChangingConfigurations) {
            Timber.i("Clearing ConfigPersistentComponent id=%d", fragmentId)
            componentsArray.remove(fragmentId)
        }
        super.onDestroy()
    }

    override fun toastError(t: Throwable) {
        FabricHelper.log(t, activity)
    }

    fun showProgressDialog(show: Boolean) {
        if (progressDialog.isShowing) progressDialog.dismiss()
        if (show) progressDialog.immersiveShow()
    }

    fun fragmentComponent() = fragmentComponent as FragmentComponent


    companion object {
        private val KEY_FRAGMENT_ID = "KEY_FRAGMENT_ID"
        private val componentsArray = LongSparseArray<ConfigPersistentComponent>()
        private val NEXT_ID = AtomicLong(0)
    }

}