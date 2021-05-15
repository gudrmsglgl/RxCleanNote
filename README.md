# RxCleanNote
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8c2c68c84fbb4fa994f9f832292bddff)](https://app.codacy.com/manual/gudrmsglgl/RxCleanNote?utm_source=github.com&utm_medium=referral&utm_content=gudrmsglgl/RxCleanNote&utm_campaign=Badge_Grade_Dashboard)
[![CodeFactor](https://www.codefactor.io/repository/github/gudrmsglgl/rxcleannote/badge)](https://www.codefactor.io/repository/github/gudrmsglgl/rxcleannote)

RxCleanNote 프로젝트는 메모앱 입니다.

해당 프로젝트의 목적은 현재 안드로이드 서비스에서 사용하고 있는 
<br>모던한 기술을 다수 사용 하면서 협업 및 유지보수를 용이하게 함에 있습니다.
<br>코드를 작성함에 있어 SOLID 원칙과 마틴 파울러의 '리팩터링' 을 의식하여
<br>코드와 데이터의 설계의 명확함을 최대한 살리고자 노력 하였습니다.
<br>또한 MVVM + CleanArchitecture 를 적용하여 관심사를 분리하여 수평적 확장과 테스트를 용이하게 설계 하였습니다.
<br>테스트 코드는 UI를 포함한 230여개를 작성 하였습니다.

![app_slash](https://user-images.githubusercontent.com/16537977/118366110-5cd6a180-b5da-11eb-9e31-5c219f0d6dd2.gif)
![app_edit](https://user-images.githubusercontent.com/16537977/118366401-0b7ae200-b5db-11eb-9a63-766a09d19ca4.gif)

## Tech-stack
- 100% Kotlin Project

- Jetpack
  - Navigation - 앱의 Navigation
  - LiveData - 앱의 라이프사이클에 맞는 데이터 갱신 관리
  - LifeCycle - 앱의 생명주기에 맞는 행동 관리
  - ViewModel - UI에 관련된 다수의 데이터를 생명주기에 맞게 관리
  - Room - 앱 내의 캐시 데이터 관리
  - Data Binding - 관찰 가능한 데이터를 UI 선언적으로 바인딩
  - View Binding - xml 뷰 바인딩

- UI
  - Single Activity Architecture - 하나의 Activity에 다수의 프래그먼트 사용
  - Material Design 
  - MotionLayout - Transition과 Animation 관리
  - RxBinding - UI 의 이벤트를 조합 및 변경 처리
  - Lottie - UI 애니메이션 아이콘  

- Test
  - Junit5 - Test 프레임워크   
  - Mockk - Mock 프레임워크
  - Espresso(Junit4) - UI Test 프레임워크
  - Robolectric - Android Unit Test 프레임워크
  - Hamcrest - JUnit에 사용되는 Matcher 라이브러리

- Third Party
  - Glide - Image Loading 
  - ReactiveX - 비동기 처리

  - Dagger2 - DI 라이브러리
  - Retrofit2 - Http 처리
  - Timber - Debug Logger
  - ImagePicker - 앨범 이미지 Picker
  - FirebaseCrashticsKTX - Crash 관리
  
- Static analysis tools
  - SonarQube
  - Ktlint

## Clean Architecture
![스크린샷 2021-04-21 오후 11 01 02](https://user-images.githubusercontent.com/16537977/115673977-2b2a3c00-a388-11eb-95ce-7434d835b405.png)

## Test Code
테스트 코드는 다음과 같이 작성 하였습니다. 

- BDD Style
  - Given - 테스트 시작 전 사전조건 
  - When - 사용자가 지정하는 동작
  - Then - 예상되는 변경 사항

- bind into Class
  - Readable - 테스트 코드 가독성 향상
  - Comprehensibility - 테스트 코드 이해 향상

### [ UI Layer ]
```kotlin
 screenNoteList {
    recyclerView {
        isDisplayed()
        hasSize(notes.size)
        firstItem<NoteItem> {
            itemTitle {
                hasText(notes[0].title)
            }            
        }
    }
    noDataTextView {
        isGone()
    }
 }
```

### [ Presentation Layer ]
```kotlin
with(searchFeatureTester) {
    search()
       .verifyUseCaseExecute()
       .verifyChangeState(State.LOADING)

    stubUseCaseOnSuccess(notes.transNotes())
       .verifyChangeState(State.SUCCESS)
       .expectData(notes)          
}
```

### [ Data Layer ]
```kotlin
stubContainer {
    scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 같거나 클 때") {
        rDataStoreStubber {
            searchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
        }
        cDataStoreStubber {
            pageIsCache(param = defaultQuery.page, stub = false)
            saveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
            currentPageNoteSize(param = defaultQuery.transQueryEntity(), stub = cacheNote.size)
        }
    }
}

whenDataRepositorySearchNotes(defaultQuery)
    .test()

dataStoreVerifyScope {
    times(1)
        .remoteSearchNotes(defaultQuery.transQueryEntity())
    never()
        .cacheSearchNotes(defaultQuery.transQueryEntity())
}

```

