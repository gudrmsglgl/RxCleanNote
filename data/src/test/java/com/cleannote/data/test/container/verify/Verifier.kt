package com.cleannote.data.test.container.verify

@Suppress("UNCHECKED_CAST")
open class Verifier<T> {
    operator fun invoke(func: T.() -> Unit){
        func(this as T)
    }
}