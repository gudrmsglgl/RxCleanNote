package com.cleannote.remote.test.factory

import com.cleannote.data.model.UserEntity
import com.cleannote.remote.model.UserModel

object UserFactory {
    const val USER_ID = "test#1"

    fun makeUserModel() = UserModel(USER_ID, "testNick")
    fun makeUserEntity() = UserEntity(USER_ID, "testNick")

    fun makeUserModels() = (0 until 1).map { makeUserModel() }
    fun makeUserEntities() = (0 until 1).map { makeUserEntity() }

}