package com.cleannote.remote.test.factory

import com.cleannote.remote.model.UserModel

object UserFactory {
    const val USER_ID = "test#1"
    fun makeUserModel(userId: String, nick: String) = UserModel(userId, nick)
    fun makeUserModels(size: Int) = (0 until size).map { makeUserModel("#$it", "nick#$it") }.toList()
}
