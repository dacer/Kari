package im.dacer.util.extension

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.net.ConnectivityManager
import android.support.v4.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

/**
 * Created by Dacer on 01/01/2018.
 */

fun Activity.toast(resId :Int) {
    Toast.makeText(this, getString(resId), Toast.LENGTH_LONG).show()
}

fun Activity.isGooglePlayServicesAvailable() : Boolean {
    val apiAvailability = GoogleApiAvailability.getInstance()
    val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this)
    return connectionStatusCode == ConnectionResult.SUCCESS
}

fun Activity.isNetworkConnected(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
}

fun Fragment.isGooglePlayServicesAvailable() : Boolean {
    val apiAvailability = GoogleApiAvailability.getInstance()
    val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(activity)
    return connectionStatusCode == ConnectionResult.SUCCESS
}

fun Fragment.isNetworkConnected(): Boolean {
    val cm = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
}
/**
 * Extension method to provide hide keyboard for [Activity].
 */
fun Activity.hideSoftKeyboard() {
    if (currentFocus != null) {
        val inputMethodManager = getSystemService(Context
                .INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
}

/**
 * Extension method to provide hide keyboard for [Fragment].
 */
fun Fragment.hideSoftKeyboard() {
    activity?.hideSoftKeyboard()
}


/**
 * Return whether Keyboard is currently visible on screen or not.
 *
 * @return true if keyboard is visible.
 */
fun Activity.isKeyboardVisible(): Boolean {
    val r = Rect()

    //r will be populated with the coordinates of your view that area still visible.
    window.decorView.getWindowVisibleDisplayFrame(r)

    //get screen height and calculate the difference with the usable area from the r
    val height = getDisplaySize().y
    val diff = height - r.bottom

    // If the difference is not 0 we assume that the keyboard is currently visible.
    return diff != 0
}