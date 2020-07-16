package dependencies

object AnnotationProcessing {
    val room_compiler = "androidx.room:room-compiler:${Versions.room}"
    val dagger_compiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    val lifecycle_compiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycle_version}"
    val dagger_android_processor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"
    val dagger_hilt_compiler = "com.google.dagger:hilt-android-compiler:${Versions.dagger_hilt}"
    val hilt_JetpackCompiler = "androidx.hilt:hilt-compiler:${Versions.hiltJetpack}"
}