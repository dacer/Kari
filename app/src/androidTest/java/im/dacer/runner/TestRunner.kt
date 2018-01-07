package im.dacer.runner

import android.app.Application
import android.content.Context
import im.dacer.App
import io.appflate.restmock.android.RESTMockTestRunner

/**
 * Created by ravindra on 4/2/17.
 */
class TestRunner : RESTMockTestRunner() {

    @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, App::class.java.name, context)
    }

}
