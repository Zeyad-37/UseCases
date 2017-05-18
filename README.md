[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/Zeyad-37/GenericUseCase/blob/master/LICENSE)
[![](https://www.jitpack.io/v/zeyad-37/usecases.svg)](https://www.jitpack.io/#zeyad-37/usecases)
# UseCases

Is a library that is a generic implementation of the Domain and Data layers in a clean architecture.

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
class LibraryModule {
}
```
```
// This should be in the application class

Realm.init(this); // First initialize realm
Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
            .name("app.realm")
            .modules(Realm.getDefaultModule(), new LibraryModule())
            .rxFactory(new RealmObservableFactory())
            .deleteRealmIfMigrationNeeded()
            .build());

// Fastest start
DataServiceFactory.init(new DataUseCaseConfig.Builder(applicationContext).build()); // all extra features are disabled
                
// Advanced init
DataServiceFactory.init(new DataServiceConfig.Builder(applicationContext)
                .baseUrl(API_BASE_URL) 
                .withRealm() // if you want a DB
                .withCache(3, TimeUnit.MINUTES) // adds a cache layer above the server & DB if exists
                .cacheSize(8192)  // maximum size to allocate in bytes
                .okHttpBuilder(provideOkHttpClientBuilder()) 
                .okhttpCache(provideCache()) // you can also provide a cache for okHttp
                .postExecutionThread(AndroidScheduler.mainThread()) // your implementation of the post execution thread
                .build());
DataServiceFactory.getInstance();
```
# Code Example

Get Object From Server:
```
mDataService.<Order>getObject(new GetRequest
        .GetRequestBuilder(Order.class, true) // true to save result to db, false otherwise.
        .url(URL) // if you provided a base url in the DataServiceConfig.Builder
        .idColumnName(Order.ID)
        .id(orderId)
        .build())
        .subscribe(new Subscriber<Order>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Order order) {
            }
        });
```
Get Object From DB:
```
mDataService.<Order>getObject(new GetRequest
        .GetRequestBuilder(Order.class, true)
        .idColumnName(Order.ID)
        .id(mItemId)
        .build());
```
Get List From Server:
```
mDataService.<Order>getList(new GetRequest
        .GetRequestBuilder(Order.class, false)
        .fullUrl(FULL_URL) // for server access
        .build())
        .subscribe(new Subscriber<List<Order>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(List<Order> order){
            }
        });
```
Get List From DB:
```
mDataService.<Order>getList(new GetRequest
        .GetRequestBuilder(Order.class, false)
        .build());
```
Post/Put Object:
```
mDataService.postObject(new PostRequest // putObject
        .PostRequestBuilder(MyResult.class, true) // Type of expected server response
        .idColumnName(Order.ID) // for persistance
        .url(URL) // remove for DB access
        .payLoad(order) // or HashMap / JSONObject
        .build());
```
Post/Put List:
```
mDataService.postList(new PostRequest // putList
        .PostRequestBuilder(MyResponse.class, true) // Type of expected server response
        .payLoad(orders)
        .idColumnName(Order.ID) // for persistance
        .url(URL) // remove for DB access
        .build())
```
Delete Collection
```
mDataService().deleteCollectionByIds(new PostRequest // putList
        .PostRequestBuilder(Order.class, true)
        .payLoad(ids)
        .idColumnName(Order.ID) // for persistance
        .url(URL) // remove for DB access
        .build())
```
Delete Item:
```
mDataService().deleteCollectionByIds(new PostRequest // putList
        .PostRequestBuilder(Order.class, true)
        .payLoad(id)
        .idColumnName(Order.ID) // for persistance
        .url(URL) // remove for DB access
        .build())
```
Delete All from DB:
```
mDataService.deleteAll(new PostRequest
        .PostRequestBuilder(Order.class, true)
        .idColumnName(Order.ID)
        .build())
```
Upload File
```
mDataService.uploadFile(new FileIORequest
        .FileIORequestBuilder(FULL_URL, new File()) // always full url
        .onWifi(true)
        .whileCharging(false)
        .dataClass(Order.class)
        .build())
```
Download File
```
mDataService.downloadFile(new FileIORequest
        .FileIORequestBuilder(FULL_URL, new File())
        .onWifi(true)
        .whileCharging(false)
        .dataClass(Order.class)
        .build())
```
# Contributors

Just make pull request. You are in!

# License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
