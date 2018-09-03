package com.zeyad.usecases.integration;

/**
 * @author by ZIaDo on 7/7/17.
 */
public class Success {

    private boolean success;

    public Success(boolean b) {
        success = b;
    }

    @Override
    public int hashCode() {
        return (success ? 1 : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Success success1 = (Success) o;
        return success == success1.success;
    }
}
