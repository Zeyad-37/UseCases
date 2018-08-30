package com.zeyad.usecases.app.screens.user.detail

import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.viewmodel.BaseViewModel
import com.zeyad.rxredux.core.viewmodel.StateReducer
import com.zeyad.usecases.api.IDataService
import com.zeyad.usecases.app.utils.Constants.URLS.REPOSITORIES
import com.zeyad.usecases.requests.GetRequest
import io.reactivex.Flowable
import io.reactivex.functions.Function

/**
 * @author zeyad on 1/10/17.
 */
class UserDetailVM(private val dataUseCase: IDataService) : BaseViewModel<UserDetailState>() {

    override fun stateReducer(): StateReducer<UserDetailState> {
        return object : StateReducer<UserDetailState> {
            override fun reduce(newResult: Any, event: BaseEvent<*>, currentStateBundle: UserDetailState?): UserDetailState {
                return UserDetailState.builder()
                        .setRepos(newResult as List<Repository>).setUser(currentStateBundle?.user!!)
                        .setIsTwoPane(currentStateBundle.isTwoPane).build()
            }
        }
    }

    override fun mapEventsToActions(): Function<BaseEvent<*>, Flowable<*>> {
        return Function { event -> getRepositories((event as GetReposEvent).getPayLoad()) }
    }

    private fun getRepositories(userLogin: String): Flowable<List<Repository>> {
        return dataUseCase.getList(GetRequest
                .Builder(Repository::class.java, true)
                .url(String.format(REPOSITORIES, userLogin)).build())
//        return dataUseCase
//                .queryDisk(object : RealmQueryProvider<Repository> {
//                    override fun create(realm: Realm): RealmQuery<Repository> {
//                        return realm.where(Repository::class.java).equalTo("owner.login", userLogin)
//                    }
//                })
//                .flatMap { list ->
//                    if (!list.isEmpty())
//                        Flowable.just<List<Repository>>(list)
//                    else
//                        dataUseCase.getList(GetRequest
//                                .Builder(Repository::class.java, true)
//                                .url(String.format(REPOSITORIES, userLogin)).build())
//                }
    }
}
