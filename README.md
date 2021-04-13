# RxCleanNote
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8c2c68c84fbb4fa994f9f832292bddff)](https://app.codacy.com/manual/gudrmsglgl/RxCleanNote?utm_source=github.com&utm_medium=referral&utm_content=gudrmsglgl/RxCleanNote&utm_campaign=Badge_Grade_Dashboard)
[![CodeFactor](https://www.codefactor.io/repository/github/gudrmsglgl/rxcleannote/badge)](https://www.codefactor.io/repository/github/gudrmsglgl/rxcleannote)

RxCleanNote 프로젝트는 메모앱 입니다.

해당 프로젝트의 목적은 현재 안드로이드 서비스에서 사용하고 있는 모던한 기술을 다수 사용 하면서 협업 및 유지보수를 용이하게 함에 있습니다.
<br>코드를 작성함에 있어 SOLID 원칙과 마틴 파울러의 '리팩터링' 을 의식하여 코드와 데이터의 설계의 명확함을 최대한 살리고자 노력 하였습니다.
<br>또한 MVVM + CleanArchitecture 를 적용하여 관심사를 분리하여 수평적 확장과 테스트를 용이하게 설계 하였습니다.

## Tech-stack
- Jetpack
  - Navigation - 앱의 Navigation
  - LiveData - 앱의 라이프사이클에 맞는 데이터 갱신 관리
  - LifeCycle - 앱의 생명주기에 맞는 행동 관리
  - ViewModel - UI에 관련된 다수의 데이터를 생명주기에 맞게 관리
  - Room - 앱 내의 캐시 데이터 관리
  - Data Binding - 관찰 가능한 데이터를 UI 선언적으로 바인딩

- UI
  - Single Activity Architecture - 하나의 Activity에 다수의 프래그먼트 사용
  - MotionLayout - Transition과 Animation 관리
  - RxBinding - UI 의 이벤트를 조합 및 변경 처리
  
