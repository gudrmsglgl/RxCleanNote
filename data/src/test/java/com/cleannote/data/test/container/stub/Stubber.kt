package com.cleannote.data.test.container.stub

@Suppress("UNCHECKED_CAST")
open class Stubber<T> {
    operator fun invoke(func: T.() -> Unit) {
        func(this as T)
    }

    fun scenario(param: String, func: T.() -> Unit) {
        func(this as T)
    }
}
