package com.zeyad.usecases.integration

import android.app.Application
import io.appflate.restmock.RESTMockServerStarter
import io.appflate.restmock.android.AndroidLocalFileParser
import io.appflate.restmock.android.AndroidLogger
import org.robolectric.DefaultTestLifecycle
import org.robolectric.annotation.Config
import org.robolectric.manifest.AndroidManifest
import java.lang.reflect.Method

/**
 * @author by ZIaDo on 6/17/17.
 */
class AndroidTestLifecycle : DefaultTestLifecycle() {

    override fun createApplication(method: Method?, appManifest: AndroidManifest?, config: Config): Application {
        val app = super.createApplication(method, appManifest, config) as TestApplication
        RESTMockServerStarter.startSync(AndroidLocalFileParser(app), AndroidLogger())
        return app
    }
}
