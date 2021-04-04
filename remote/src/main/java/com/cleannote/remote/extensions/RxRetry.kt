package com.cleannote.remote.extensions

import io.reactivex.Flowable
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer
import io.reactivex.rxkotlin.Flowables
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

internal typealias RETRY_PREDICATE = (Throwable) -> Boolean

internal const val MAX_RETRIES = 3L
internal const val DEFAULT_INTERVAL = 1L

internal val TIMEOUT: RETRY_PREDICATE = { it is SocketTimeoutException }
internal val NETWORK: RETRY_PREDICATE = { it is IOException }
internal val SERVICE_UNAVAILABLE: RETRY_PREDICATE = { it is HttpException && it.code() == 503 }

internal fun <T> applyRetryPolicy(
    vararg predicates: RETRY_PREDICATE,
    maxRetries: Long = MAX_RETRIES,
    interval: Long = DEFAULT_INTERVAL,
    unit: TimeUnit = TimeUnit.SECONDS,
    resumeNext: (Throwable) -> SingleSource<T>
) = SingleTransformer<T, T> { source ->
    source.retryWhen { throwable ->
        Flowables.zip(
            throwable.attemptFilterSource(*predicates),
            Flowable.interval(interval, unit)
        ).map { (error, retryCount) ->
            if (retryCount >= maxRetries) throw error
        }
    }.onErrorResumeNext(resumeNext)
}

private fun Flowable<Throwable>.attemptFilterSource(
    vararg predicates: RETRY_PREDICATE
): Flowable<Throwable> = this.map { throwable ->
    if (predicates.count { it(throwable) } > 0) {
        throwable
    }
    else throw throwable
}

