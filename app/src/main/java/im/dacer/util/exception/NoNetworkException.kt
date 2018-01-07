package im.dacer.util.exception

import android.content.Context
import im.dacer.R

/**
 * Created by Dacer on 03/01/2018.
 */
class NoNetworkException(context: Context):
        IgnorableException(context.getString(R.string.network_error_no_network))