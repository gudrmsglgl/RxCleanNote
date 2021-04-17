package com.cleannote.injection.module

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.cleannote.UiThread
import com.cleannote.app.R
import com.cleannote.domain.interactor.executor.PostExecutionThread
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class UiModule {
    @Binds
    abstract fun bindPostExecutionThread(uiThread: UiThread): PostExecutionThread

    @Module
    companion object {

        @JvmStatic
        @Singleton
        @Provides
        fun provideGRequestOptions() = RequestOptions
            .placeholderOf(R.drawable.ic_placeholder)
            .error(R.drawable.error)
            .fallback(R.drawable.empty_holder)
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.NONE)

        @JvmStatic
        @Singleton
        @Provides
        fun provideGlideRequestManager(
            context: Context,
            requestOptions: RequestOptions
        ) = Glide.with(context).setDefaultRequestOptions(requestOptions)
    }
}
