package com.zeyad.usecases.app.presentation.screens.user_list;

import com.zeyad.usecases.app.components.mvvm.BaseViewModel;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.data.requests.PostRequest;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.zeyad.usecases.app.utils.Constants.URLS.USER;
import static com.zeyad.usecases.app.utils.Constants.URLS.USERS;

/**
 * @author zeyad on 11/1/16.
 */
class UserListVM extends BaseViewModel {

    private final IDataUseCase dataUseCase;

    UserListVM(IDataUseCase dataUseCase) {
        this.dataUseCase = dataUseCase;
    }

    Observable getUsers() {
        return dataUseCase.getListOffLineFirst(new GetRequest
                .GetRequestBuilder(UserRealm.class, true)
                .url(String.format(USERS, 0))
                .build());
    }

    Observable incrementPage(long lastId) {
        return dataUseCase.getList(new GetRequest
                .GetRequestBuilder(UserRealm.class, true)
                .url(String.format(USERS, lastId))
                .build());
    }

    Observable search(String query) {
        return dataUseCase.queryDisk(realm -> realm.where(UserRealm.class)
                .beginsWith(UserRealm.LOGIN, query), UserRealm.class)
                .zipWith(dataUseCase.getObject(new GetRequest
                                .GetRequestBuilder(UserRealm.class, true)
                                .url(String.format(USER, query))
                                .build())
                                .onErrorReturn(o -> null)
                                .map(o -> o != null ? Collections.singletonList(o) : Collections.emptyList()),
                        (list, singletonList) -> {
                            list.addAll((Collection) singletonList);
                            return new ArrayList<>(new HashSet<>(list));
                        })
                .observeOn(AndroidSchedulers.mainThread());
    }

    Observable deleteCollection(List<Long> selectedItemsIds) {
        return dataUseCase.deleteCollection(new PostRequest
                .PostRequestBuilder(UserRealm.class, true)
                .payLoad(selectedItemsIds)
                .build())
                .map(o -> selectedItemsIds);
    }
}
