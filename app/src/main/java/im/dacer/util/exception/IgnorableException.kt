package im.dacer.util.exception

/**
 * Created by Dacer on 02/01/2018.
 * Should be ignored by crash collection system
 */
abstract class IgnorableException(msg: String): Throwable(msg)