package com.zeyad.usecases.services;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.services.jobs.FileIO;
import com.zeyad.usecases.services.jobs.Post;
import com.zeyad.usecases.stores.CloudDataStore;
import com.zeyad.usecases.utils.Utils;

import io.reactivex.Completable;

class GenericJobServiceLogic {
    GenericJobServiceLogic() {
    }

    Completable startJob(@NonNull Bundle extras, CloudDataStore cloudDataStore, Utils utils, String log) {
        if (extras.containsKey(GenericJobService.PAYLOAD)) {
            int trailCount = extras.getInt(GenericJobService.TRIAL_COUNT);
            switch (extras.getString(GenericJobService.JOB_TYPE, "")) {
                case GenericJobService.POST:
                    Log.d(GenericJobServiceLogic.class.getSimpleName(),
                            String.format(log, GenericJobService.POST));
                    return new Post(null, extras.getParcelable(GenericJobService.PAYLOAD),
                            Config.getApiConnection(), trailCount, utils)
                            .execute();
                case GenericJobService.DOWNLOAD_FILE:
                    Log.d(GenericJobServiceLogic.class.getSimpleName(),
                            String.format(log, GenericJobService.DOWNLOAD_FILE));
                    return new FileIO(trailCount, extras.getParcelable(GenericJobService.PAYLOAD),
                            null, true, cloudDataStore, utils)
                            .execute();
                case GenericJobService.UPLOAD_FILE:
                    Log.d(GenericJobServiceLogic.class.getSimpleName(),
                            String.format(log, GenericJobService.UPLOAD_FILE));
                    return new FileIO(trailCount, extras.getParcelable(GenericJobService.PAYLOAD),
                            null, false, cloudDataStore, utils)
                            .execute();
                default:
                    return Completable.complete();
            }
        }
        return Completable.complete();
    }
}

