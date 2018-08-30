package com.zeyad.usecases.app.screens.user.list

import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.viewmodel.BaseViewModel
import com.zeyad.rxredux.core.viewmodel.StateReducer
import com.zeyad.usecases.api.IDataService
import com.zeyad.usecases.app.screens.user.User
import com.zeyad.usecases.app.screens.user.list.events.DeleteUsersEvent
import com.zeyad.usecases.app.screens.user.list.events.GetPaginatedUsersEvent
import com.zeyad.usecases.app.screens.user.list.events.SearchUsersEvent
import com.zeyad.usecases.app.utils.Constants
import com.zeyad.usecases.requests.GetRequest
import com.zeyad.usecases.requests.PostRequest
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.Function
import java.util.*

/**
 * @author ZIaDo on 6/8/18.
 */
class UserListVM(private var dataUseCase: IDataService) : BaseViewModel<UserListState>() {

    override fun mapEventsToActions(): Function<BaseEvent<*>, Flowable<*>> {
        return Function { event ->
            when (event) {
                is GetPaginatedUsersEvent -> getUsers(event.getPayLoad())
                is DeleteUsersEvent -> deleteCollection(event.getPayLoad())
//                is SearchUsersEvent -> search(event.getPayLoad())
                else -> Flowable.empty<Any>()
            }
        }
    }

    override fun stateReducer(): StateReducer<UserListState> {
        return object : StateReducer<UserListState> {
            override fun reduce(newResult: Any, event: BaseEvent<*>, currentStateBundle: UserListState?): UserListState {
                var users: MutableList<User>
                users = if (currentStateBundle?.users == null)
                    ArrayList<User>()
                else
                    Observable.fromIterable(currentStateBundle.users)
                            .map<User> { it.getData() }
                            .toList().blockingGet()
                val searchList = ArrayList<User>()
                when (event) {
                    is GetPaginatedUsersEvent -> users.addAll(newResult as List<User>)
                    is SearchUsersEvent -> searchList.addAll(newResult as List<User>)
                    is DeleteUsersEvent -> users = Observable.fromIterable(users)
                            .filter { user -> !(newResult as List<*>).contains(user.login) }
                            .distinct().toList().blockingGet()
                    else -> {
                    }
                }
                return UserListState.builder()
                        .users(users)
                        .searchList(searchList)
                        .lastId(users[users.size - 1].id.toLong())
                        .build()
            }
        }
    }

    private fun getUsers(lastId: Long): Flowable<List<User>> {
        val getRequest = GetRequest(String.format(Constants.URLS.USERS, lastId), "",
                User::class.java, Int::class.java, true, "id")
        return if (lastId == 0L)
            dataUseCase.getListOffLineFirst(getRequest)
        else
            dataUseCase.getList(getRequest)
    }

//    private fun search(query: String): Flowable<List<User>> {
//        return dataUseCase
//                .queryDisk(object : RealmQueryProvider<User> {
//                    override fun create(realm: Realm): RealmQuery<User> {
//                        return realm.where(User::class.java).beginsWith(User.LOGIN, query)
//                    }
//                })
//                .map { it.toMutableList() }
//                .zipWith<List<User>>(dataUseCase.getObject<User>(GetRequest.Builder(User::class.java, false)
//                        .url(String.format(USER, query))
//                        .build())
//                        .onErrorReturnItem(User())
//                        .filter { user -> user.id != 0 },
//                        BiFunction<MutableList<User>, User, List<User>> { users, singleton ->
//                            users.add(singleton)
//                            users.toSet().toList()
//                        })
//    }

    private fun deleteCollection(selectedItemsIds: List<String>): Flowable<List<String>> {
        return dataUseCase.deleteCollectionByIds<Boolean>(PostRequest("", "", User::class.java,
                Boolean::class.java, true, false, false, false,
                true, User.LOGIN, String::class.java, `object` = selectedItemsIds))
                .map { selectedItemsIds }
    }
}