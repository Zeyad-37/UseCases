package com.zeyad.usecases.network;

public interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}