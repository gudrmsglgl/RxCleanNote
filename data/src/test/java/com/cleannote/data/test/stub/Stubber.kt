package com.cleannote.data.test.stub

@Suppress("UNCHECKED_CAST")
open class Stubber<T> {
    operator fun invoke(func: T.()-> Unit){
        func(this as T)
    }
}