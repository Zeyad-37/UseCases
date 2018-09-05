[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/Zeyad-37/GenericUseCase/blob/master/LICENSE)
[![](https://www.jitpack.io/v/zeyad-37/usecases.svg)](https://www.jitpack.io/#zeyad-37/usecases)
# UseCases

Is a library that is a generic implementation of the Data layer in Uncle Bob's clean architecture.
Now in Kotlin

# Motivation

As developers, we always need to deliver high quality software on time,
 which is not an easy task.
In most tasks, we need to make either a IO operation, whether from the server,
 db or file, which is a lot of boiler plate. And getting it functioning and effiecient every time
 is a bit challenging due to the many things that you need to take care of. 
 Like separation of concerns, error handling and writing robust code that 
 would not crash on you.
 I have noticed that this code repeats almost with every user story, and 
 i was basically re-writing the same code, but for different models. So i 
 thought what if i could pass the class with the request and not repeat this
 code over and over. Hence, please welcome the UseCases lib.

# Requirements

UseCases Library can be included in any Android application that supports Android 2.3 (Gingerbread) and later. 

# Installation

Easiest way to start
```
// Create Class LibraryModule to expose your realm models to the lib configuration

@RealmModule(library = true, allClasses = true)
internal class LibraryModule
```
```
// This should be in the application class
Realm.init(this)
Realm.setDefaultConfiguration(RealmConfiguration.Builder()
        .name("app.realm")
        .modules(Realm.getDefaultModule(), LibraryModule())
        .rxFactory(RealmObservableFactory())
        .deleteRealmIfMigrationNeeded()
        .build())

// Fastest start
DataServiceFactory(DataServiceConfig.Builder(context).build()).instance!!// all extra features are disabled
                
// Advanced init
DataServiceFactory(DataServiceConfig.Builder(context)
            .baseUrl(API_BASE_URL)
            .okHttpBuilder(getOkHttpBuilder())
            .withRealm() // if you want a DB
            .withRealm(HandlerThread("BackgroundHandlerThread")) // If you want to supply your own Handler thread
            .withCache(3, TimeUnit.MINUTES) // adds a cache layer above the server & DB if exists
            .cacheSize(8192)  // maximum size to allocate in bytes
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
