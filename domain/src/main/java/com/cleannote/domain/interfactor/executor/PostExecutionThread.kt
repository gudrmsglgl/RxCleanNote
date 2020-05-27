package com.cleannote.domain.interfactor.executor

import io.reactivex.Scheduler

interface PostExecutionThread {
    val scheduler: Scheduler
}