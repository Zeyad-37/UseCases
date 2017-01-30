package com.zeyad.usecases.data.requests;

import android.support.annotation.NonNull;

import com.zeyad.usecases.Config;
import com.zeyad.usecases.data.repository.DataRepository;
import com.zeyad.usecases.domain.interactors.data.DataUseCase;

import io.realm.RealmQuery;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * @author zeyad on 7/29/16.
 */
public class GetRequest {

    private String url;
    private Subscriber subscriber;
    private Class dataClass, presentationClass;
    private boolean persist;
    private String idColumnName;
    private int itemId;
    private boolean shouldCache;
    private RealmQuery realmQuery;

    public GetRequest(@NonNull GetRequestBuilder getRequestBuilder) {
        url = getRequestBuilder.getUrl();
        dataClass = getRequestBuilder.getDataClass();
        presentationClass = getRequestBuilder.getPresentationClass();
        persist = getRequestBuilder.isPersist();
        subscriber = getRequestBuilder.getSubscriber();
        idColumnName = getRequestBuilder.getIdColumnName();
        itemId = getRequestBuilder.getItemId();
        shouldCache = getRequestBuilder.isShouldCache();
        realmQuery = getRequestBuilder.getRealmQuery();
    }

    public GetRequest(@NonNull Subscriber subscriber, String url, String idColumnName,
                      int itemId, @NonNull Class presentationClass, Class dataClass, boolean persist,
                      boolean shouldCache) {
        this.subscriber = subscriber;
        this.url = url;
        this.idColumnName = idColumnName;
        this.itemId = itemId;
        this.presentationClass = presentationClass;
        this.dataClass = dataClass;
        this.persist = persist;
        this.shouldCache = shouldCache;
    }

    public String getUrl() {
        return url != null ? url : "";
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public Class getDataClass() {
        return dataClass;
    }

    public Class getPresentationClass() {
        return presentationClass != null ? presentationClass : dataClass;
    }

    public boolean isPersist() {
        return persist;
    }

    public boolean isShouldCache() {
        return shouldCache;
    }

    public String getIdColumnName() {
        return idColumnName != null ? idColumnName : DataRepository.DEFAULT_ID_KEY;
    }

    public int getItemId() {
        return itemId;
    }

    public RealmQuery getRealmQuery() {
        return realmQuery;
    }

    public static class GetRequestBuilder {
        private boolean mShouldCache;
        private String mIdColumnName;
        private int mItemId;
        private String mUrl;
        private Subscriber mSubscriber;
        private Class mDataClass, mPresentationClass;
        private boolean mPersist;
        private RealmQuery mRealmQuery;

        public GetRequestBuilder(Class dataClass, boolean persist) {
            mDataClass = dataClass;
            mPersist = persist;
        }

        @NonNull
        public GetRequestBuilder realmQuery(RealmQuery realmQuery) {
            Observable.just(realmQuery)
                    .flatMap(realmQuery1 -> {
                        mRealmQuery = realmQuery1;
                        return Observable.just(mRealmQuery);
                    })
                    .subscribeOn(AndroidSchedulers.from(DataUseCase.getHandlerThread().getLooper()))
                    .observeOn(AndroidSchedulers.from(DataUseCase.getHandlerThread().getLooper()))
                    .subscribe(realmQuery1 -> mRealmQuery = realmQuery1);
//            mRealmQuery = realmQuery;
            return this;
        }

        @NonNull
        public GetRequestBuilder url(String url) {
            mUrl = Config.getBaseURL() + url;
            return this;
        }

        @NonNull
        public GetRequestBuilder fullUrl(String url) {
            mUrl = url;
            return this;
        }

        @NonNull
        public GetRequestBuilder presentationClass(Class presentationClass) {
            mPresentationClass = presentationClass;
            return this;
        }

        @NonNull
        public GetRequestBuilder subscriber(Subscriber subscriber) {
            mSubscriber = subscriber;
            return this;
        }

        @NonNull
        public GetRequestBuilder shouldCache(boolean shouldCache) {
            mShouldCache = shouldCache;
            return this;
        }

        @NonNull
        public GetRequestBuilder idColumnName(String idColumnName) {
            mIdColumnName = idColumnName;
            return this;
        }

        @NonNull
        public GetRequestBuilder id(int id) {
            mItemId = id;
            return this;
        }

        @NonNull
        public GetRequest build() {
            return new GetRequest(this);
        }

        public String getUrl() {
            return mUrl;
        }

        public Subscriber getSubscriber() {
            return mSubscriber;
        }

        public Class getDataClass() {
            return mDataClass;
        }

        public Class getPresentationClass() {
            return mPresentationClass;
        }

        public boolean isPersist() {
            return mPersist;
        }

        public boolean isShouldCache() {
            return mShouldCache;
        }

        public String getIdColumnName() {
            return mIdColumnName;
        }

        public int getItemId() {
            return mItemId;
        }

        public RealmQuery getRealmQuery() {
            return mRealmQuery;
        }
    }
}