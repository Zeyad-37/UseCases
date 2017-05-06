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

// This should be in the application class

// For Data Access
Realm.init(this); // First initialize realm
Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
            .name("app.realm")
            .modules(Realm.getDefaultModule(), new LibraryModule())
            .rxFactory(new RealmObservableFactory())
            .deleteRealmIfMigrationNeeded()
            .build());

// Fastest start
DataUseCaseFactory.init(new DataUseCaseConfig.Builder(applicationContext).build()); // all extra features are disabled
                
// Advanced init
DataUseCaseFactory.init(new DataUseCaseConfig.Builder(applicationContext)
                .baseUrl(API_BASE_URL) 
                .withRealm() // if you want a DB
                .withCache() // adds a cache layer above the server & DB if exists
                .cacheSize(8192)  // maximum size to allocate in bytes
                .okHttpBuilder(provideOkHttpClientBuilder()) 
                .okhttpCache(provideCache()) // you can also provide a cache for okHttp
                .threadExecutor(new JobExecutor()) // your implementation of background thread.
                .postExecutionThread(new UIThread()) // your implementation of the post execution thread
                .build());
DataUseCaseFactory.getInstance();

// For File Access
FileUseCaseFactory.init(applicationContext); 
// or
FileUseCaseFactory.init(applicationContext, threadExecutor, postExecutionThread);
FileUseCaseFactory.getInstance();
```

# Code Example

Get Object From Server:
```
mDataUseCase.getObject(new GetRequest
        .GetRequestBuilder(OrderRealmModel.class, true) // true to save result to db, false otherwise.
        .presentationClass(OrderViewModel.class)
        .url(URL) // if you have provided the base url in the DataUseCaseConfig.Builder
        .idColumnName(OrderRealmModel.ID)
        .id(orderId)
        .build())
        .subscribe(new Subscriber<OrderViewModel>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(OrderViewModel orderViewModel) {
            }
        });
```
Get Object From DB:
```
mDataUseCase.getObject(new GetRequest
        .GetRequestBuilder(OrderRealmModel.class, true)
        .presentationClass(OrderViewModel.class)
        .idColumnName(OrderViewModel.ID)
        .id(mItemId)
        .build());
```
Get List From Server:
```
mDataUseCase.getList(new GetRequest
        .GetRequestBuilder(OrdersRealmModel.class, false)
        .presentationClass(OrderViewModel.class)
        .fullUrl(FULL_URL)
        .build())
        .subscribe(new Subscriber<List<OrderViewModel>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(List<OrderViewModel> orderViewModel){
            }
        });
```
Get List From DB:
```
mDataUseCase.getList(new GetRequest
        .GetRequestBuilder(OrdersRealmModel.class, false)
        .presentationClass(OrderViewModel.class)
        .build());
```
Post/Put Object to Server:
```
mDataUseCase.postObject(new PostRequest // putObject
        .PostRequestBuilder(OrdersRealmModel.class, true)
        .presentationClass(OrderViewModel.class)
        .idColumnName(OrdersRealmModel.ID)
        .url(URL)
        .payLoad(OrderViewModel.toJSONObject()) // or HashMap 
        .build());
```
Post/Put Object to DB:
```
mDataUseCase.postObject(new PostRequest // putObject
        .PostRequestBuilder(OrdersRealmModel.class, true)
        .idColumnName(OrderViewModel.ID)
        .presentationClass(OrderViewModel.class)
        .payLoad(OrderViewModel.toJSONObject()) // or HashMap 
        .build());
```
Post/Put List to Server:
```
mDataUseCase.postList(new PostRequest // putList
        .PostRequestBuilder(OrdersRealmModel.class, true)
        .presentationClass(OrdersViewModel.class)
        .payLoad(OrdersViewModel.toJSONArray())
        .idColumnName(OrdersRealmModel.ID)
        .url(URL)
        .build())
```
Post/Put List to DB:
```
mDataUseCase.postList(new PostRequest // putList
        .PostRequestBuilder(OrdersRealmModel.class, true)
        .presentationClass(OrdersViewModel.class)
        .payLoad(OrdersViewModel.toJSONArray())
        .idColumnName(OrdersRealmModel.ID)
        .build())
```
Delete All from DB:
```
mDataUseCase().deleteAll(new PostRequest
        .PostRequestBuilder(OrdersRealmModel.class, true)
        .idColumnName(OrdersRealmModel.ID)
        .build())
```
Delete Collection from Server
```
mDataUseCase().deleteCollection(new PostRequest // putList
        .PostRequestBuilder(OrdersRealmModel.class, true)
        .presentationClass(OrdersViewModel.class)
        .payLoad(OrdersViewModel.toJSONArrayOfId())
        .url(URL)
        .build())
```
Upload File
```
mFileUseCase.uploadFile(new FileIORequest
        .FileIORequestBuilder(FULL_URL, new File()) // always full url
        .onWifi(true)
        .whileCharging(false)
        .dataClass(OrdersRealmModel.class)
        .presentationClass(OrdersViewModel.class)
        .build())
```
Download File
```
mFileUseCase.downloadFile(new FileIORequest
        .FileIORequestBuilder(FULL_URL, new File())
        .onWifi(true)
        .whileCharging(false)
        .dataClass(OrdersRealmModel.class)
        .presentationClass(OrdersViewModel.class)
        .build())
```
Initialization Example:
```
 DataUseCaseFactory.init(new DataUseCaseConfig.Builder(applicationContext)
                .withRealm()
                .build());
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
