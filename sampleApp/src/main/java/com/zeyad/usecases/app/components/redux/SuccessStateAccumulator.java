package com.zeyad.usecases.app.components.redux;

/** @author by ZIaDo on 5/4/17. */
public interface SuccessStateAccumulator<S> {
    S accumulateSuccessStates(Object newResult, String event, S currentStateBundle);
}

