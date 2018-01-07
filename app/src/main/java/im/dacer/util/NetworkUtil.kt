package im.dacer.util

import retrofit2.HttpException


object NetworkUtil {

    /**
     * Returns true if the Throwable is an instance of RetrofitError with an
     * http status code equals to the given one.
     */
    fun isHttpStatusCode(throwable: Throwable, statusCode: Int): Boolean {
        return throwable is HttpException && throwable.code() == statusCode
    }



}