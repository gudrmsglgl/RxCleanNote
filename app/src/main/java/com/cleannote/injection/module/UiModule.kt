package com.cleannote.injection.module

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.bumptech.glide.request.RequestOptions
import com.cleannote.UiThread
import com.cleannote.app.R
import com.cleannote.domain.interactor.executor.PostExecutionThread
import com.cleannote.notelist.NoteListAdapter
import com.cleannote.notelist.SwipeAdapter
import com.cleannote.notelist.SwipeHelperCallback
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class UiModule {
    @Binds
    abstract fun bindPostExecutionThread(uiThread: UiThread): PostExecutionThread

    @Module
    companion object{

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

        @JvmStatic
        @Provides
        fun provideSwipeCallback(
            context: Context
        ) = SwipeHelperCallback(
            clamp = context.resources.getDimension(R.dimen.swipe_delete_clamp),
            extendClamp = context.resources.getDimension(R.dimen.swipe_clamp_extend)
        )

        @JvmStatic
        @Provides
        fun provideNoteAdapter(
            context: Context,
            glideReqManager: RequestManager,
            swipeHelperCallback: SwipeHelperCallback
        ) = NoteListAdapter(
            context,
            glideReqManager,
            swipeHelperCallback
        ).apply {
            setHasStableIds(true)
        }

    }
}