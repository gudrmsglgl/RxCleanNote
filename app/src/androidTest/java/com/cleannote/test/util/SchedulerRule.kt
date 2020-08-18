package com.cleannote.test.util

import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

open class SchedulerRule: TestRule {
    val immediate = object: Scheduler() {

        override fun createWorker(): Worker {
            return ExecutorScheduler.ExecutorWorker(Executor{it.run()}, true)
        }
        /* override fun createWorker(): Worker =
             ExecutorScheduler.ExecutorWorker(object : ScheduledThreadPoolExecutor(1){
                 override fun execute(command: Runnable) { command.run() }
             }, true)*/
    }
    override fun apply(base: Statement?, description: Description?): Statement {
        return object : Statement(){
            @Throws(Throwable::class)
            override fun evaluate() {
                RxJavaPlugins.setIoSchedulerHandler { immediate }
                RxJavaPlugins.setComputationSchedulerHandler { immediate }
                RxJavaPlugins.setNewThreadSchedulerHandler { immediate }
                RxJavaPlugins.setSingleSchedulerHandler { immediate }
                RxAndroidPlugins.setInitMainThreadSchedulerHandler { immediate }
                try{
                    base?.evaluate()
                } finally {
                    RxJavaPlugins.reset()
                    RxAndroidPlugins.reset()
                }
            }
        }


    }
}