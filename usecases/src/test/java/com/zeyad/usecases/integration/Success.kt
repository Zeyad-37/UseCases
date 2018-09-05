package com.zeyad.usecases.integration

/**
 * @author by ZIaDo on 7/7/17.
 */
class Success(private val success: Boolean) {

    override fun hashCode(): Int {
        return if (success) 1 else 0
    }

    override fun equals(o: Any?): Boolean {
        if (this === o)
            return true
        if (o == null || javaClass != o.javaClass)
            return false
        val success1 = o as Success?
        return success == success1!!.success
    }
}
