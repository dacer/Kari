package im.dacer.util.extension

import android.app.Dialog
import android.view.WindowManager
import im.dacer.features.base.BaseActivity.Companion.SYSTEM_IMMERSIVE_UI_VISIBILITY

/**
 * Created by Dacer on 03/01/2018.
 */

/**
 * http://stackoverflow.com/a/23207365/2669995
 */
fun Dialog.immersiveShow() {
    if (this.window == null) return
    this.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    this.show()
    this.window.decorView.systemUiVisibility = SYSTEM_IMMERSIVE_UI_VISIBILITY
    this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
}