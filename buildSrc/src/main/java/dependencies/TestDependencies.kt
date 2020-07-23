package dependencies

object TestDependencies {
    val jupiter_api = "org.junit.jupiter:junit-jupiter-api:${Versions.junit_jupiter_version}"
    val jupiter_params = "org.junit.jupiter:junit-jupiter-params:${Versions.junit_jupiter_version}"
    val jupiter_engine = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit_jupiter_version}"
    val mockk = "io.mockk:mockk:${Versions.mockk_version}"
    val junit4 = "junit:junit:${Versions.junit_4_version}"
    val hamcrest_library = "org.hamcrest:hamcrest-library:${Versions.hamcrest_version}"
    val mockito_inline = "org.mockito:mockito-inline:${Versions.mockito_version}"
    val mockito_kotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockito_kotlin_version}"
    val mockitoTest = "com.nhaarman:mockito-kotlin:1.5.0"
    val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
    val roomTesting = "androidx.room:room-testing:${Versions.room}"
    val archTesting = "android.arch.core:core-testing:${Versions.room}"
    val testCore = "androidx.test:core:1.2.0"
}