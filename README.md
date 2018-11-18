[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/Zeyad-37/GenericUseCase/blob/master/LICENSE)
[![](https://www.jitpack.io/v/zeyad-37/usecases.svg)](https://www.jitpack.io/#zeyad-37/usecases)
# UseCases

Is a library that is a generic implementation of the Repository pattern applied in the Data layer
 in Uncle Bob's clean architecture.
Now in Kotlin

# Motivation

As developers, we always need to deliver high quality software on time,
 which is not an easy task.
In most tasks, we need to make either a IO operation, whether from the server,
 db or file, which is a lot of boiler plate. And getting it functioning and efficient every time
 is a bit challenging due to the many things that you need to take care of. 
 Like separation of concerns, error handling and writing robust code that 
 would not crash on you.
 I have noticed that this code repeats almost with every user story, and 
 i was basically re-writing the same code, but for different models. So i 
 thought what if i could pass the class with the request and not repeat this
 code over and over. Hence, please welcome the UseCases lib.

# Requirements

UseCases Library can be included in any Android application that supports Android 4.2 (Gingerbread) and later. 

# Installation

Easiest way to start
```
// Create Class Dao to expose your models to the lib configuration
// Standard Room init
@Dao
interface UserDao : BaseDao<User>

@Dao
interface RepoDao : BaseDao<Repository>

@Database(entities = [User::class, Repository::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun repoDao(): RepoDao
}

// Fastest start
DataServiceFactory(DataServiceConfig.Builder(context).build()).instance!!// all extra features are disabled
                
// Advanced init
DataServiceFactory(DataServiceConfig.Builder(context)
            .baseUrl(API_BASE_URL)
            .okHttpBuilder(getOkHttpBuilder())
            .withRoom(object : DataBaseManagerUtil {
                                      override fun getDataBaseManager(dataClass: Class<*>): DataBaseManager? {
                                          return RoomManager(db, object : DaoResolver {
                                              override fun <E> getDao(dataClass: Class<E>): BaseDao<E> {
                                                  return when (dataClass) {
                                                      User::class.java -> db.userDao() as BaseDao<E>
                                                      Repository::class.java -> db.repoDao() as BaseDao<E>
                                                      else -> throw IllegalArgumentException("")
                                                  }
                                              }
                                          })
                                      }
                                  })
            .withCache(3, TimeUnit.MINUTES, 8192) // adds a cache layer with maximum size to allocate in bytes
            .okHttpBuilder(provideOkHttpClientBuilder()) 
            .okhttpCache(provideCache()) // you can also provide a cache for okHttp
            .postExecutionThread(AndroidScheduler.mainThread()) // your implementation of the post execution thread
            .build())
            .instance!!

```
# Code Example

Get Object From Server:
```
dataService.<Order>getObject(GetRequest
        .GetRequestBuilder(Order::class.java, true) // true to save result to db, false otherwise.
        .url(URL) // if you provided a base url in the DataServiceConfig.Builder
        .idColumnName(Order.ID)
        .id(orderId)
        .build())
        .subscribe()
```
Get Object From DB:
```
mDataService.<Order>getObject(GetRequest
        .GetRequestBuilder(Order::class.java, true)
        .idColumnName(Order.ID)
        .id(mItemId)
        .build())
        .subscribe()
```
Get List From Server:
```
mDataService.<Order>getList(GetRequest
        .GetRequestBuilder(Order::class.java, false)
        .fullUrl(FULL_URL) // for server access
        .build())
        .subscribe()
```
Get List From DB:
```
mDataService.<Order>getList(GetRequest
        .GetRequestBuilder(Order::class.java, false)
        .build())
        .subscribe()
```
Post/Put Object:
```
mDataService.<MyResponse>postObject(PostRequest // putObject
        .PostRequestBuilder(Payload::class.java, true) // Type of expected server response
        .idColumnName(Order.ID) // for persistance
        .url(URL) // remove for DB access
        .payLoad(order) // or HashMap / JSONObject
        .responseType(MyResponse::class.java)
        .build())
        .subscribe()
```
Post/Put List:
```
mDataService.<MyResponse>postList(PostRequest // putList
        .PostRequestBuilder(Payload::class.java, true) // Type of expected server response
        .payLoad(orders)
        .idColumnName(Order.ID) // for persistance
        .url(URL) // remove for DB access
        .responseType(MyResponse::class.java)
        .build())
        .subscribe()
```
Delete Collection
```
mDataService().<MyResponse>deleteCollectionByIds(PostRequest // putList
        .PostRequestBuilder(Payload::class.java, true)
        .payLoad(ids)
        .idColumnName(Order.ID) // for persistance
        .url(URL) // remove for DB access
        .responseType(MyResponse::class.java)
        .build())
        .subscribe()
```
Delete Item:
```
mDataService().<MyResponse>deleteCollectionByIds(PostRequest // putList
        .PostRequestBuilder(Payload::class.java, true)
        .payLoad(id)
        .idColumnName(Order.ID) // for persistance
        .url(URL) // remove for DB access
        .responseType(MyResponse::class.java)
        .build())
        .subscribe()
```
Delete All from DB:
```
mDataService.deleteAll(PostRequest
        .PostRequestBuilder(Order::class.java, true)
        .idColumnName(Order.ID)
        .build())
        .subscribe()
```
Upload File
```
mDataService.<MyResponse>uploadFile(FileIORequest
        .FileIORequestBuilder(FULL_URL, File("")) // always full url
        .queuable(true, false) // onWifi, whileCharging
        .responseType(MyResponse::class.java)
        .build())
        .subscribe()
```
Download File
```
mDataService.downloadFile(FileIORequest
        .FileIORequestBuilder(FULL_URL, File(""))
        .queuable(true, false) // onWifi, whileCharging
        .requestType(Order::class.java)
        .build())
        .subscribe()
```
# License

Licensed under the Apache License, Version 2.0 (the "License")
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
