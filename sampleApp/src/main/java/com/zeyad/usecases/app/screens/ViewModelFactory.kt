package com.zeyad.usecases.app.screens

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.zeyad.usecases.api.IDataService
import com.zeyad.usecases.app.screens.user.detail.UserDetailVM
import com.zeyad.usecases.app.screens.user.list.UserListVM

/**
 * @author ZIaDo on 12/13/17.
 */
class ViewModelFactory(private val dataService: IDataService) : ViewModelProvider.NewInstanceFactory() {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserListVM::class.java)) {
            return UserListVM(dataService) as T
        } else if (modelClass.isAssignableFrom(UserDetailVM::class.java)) {
            return UserDetailVM(dataService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.simpleName)
    }
}
