package com.cleannote.cache

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesHelper @Inject constructor(context: Context){

    companion object {
        private val PREF_NOTE_PACKAGE_NAME = "com.cleannote.cache.preferences"
        private val PREF_KEY_LAST_CACHE = "last_cache_page_"
    }

    private val notePref: SharedPreferences

    init {
        notePref = context.getSharedPreferences(PREF_NOTE_PACKAGE_NAME, Context.MODE_PRIVATE)
    }

    fun setLastCacheTime(lastCacheTime: Long, page: Int){
        notePref.edit().putLong(PREF_KEY_LAST_CACHE+page, lastCacheTime).apply()
    }

    fun getLastCacheTime(page: Int): Long =
        notePref.getLong(PREF_KEY_LAST_CACHE+page,0)

}