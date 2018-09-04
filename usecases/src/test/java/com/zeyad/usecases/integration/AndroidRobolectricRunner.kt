package com.zeyad.usecases.integration

import org.junit.runners.model.InitializationError
import org.robolectric.RobolectricTestRunner
import org.robolectric.TestLifecycle

/**
 * @author by ZIaDo on 6/17/17.
 */
class AndroidRobolectricRunner @Throws(InitializationError::class)
constructor(klass: Class<*>) : RobolectricTestRunner(klass) {

    public override fun getTestLifecycleClass(): Class<out TestLifecycle<*>> {
        return AndroidTestLifecycle::class.java
    }
}
