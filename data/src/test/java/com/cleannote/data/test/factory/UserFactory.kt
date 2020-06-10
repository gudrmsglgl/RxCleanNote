package com.cleannote.data.test.factory

import com.cleannote.data.model.UserEntity
import com.cleannote.domain.model.User

object UserFactory {
    const val USER_ID = "testUser"

    fun makeUserEntity() = UserEntity(USER_ID, "testNick")

    fun userEntities() = (0 until 1).map { makeUserEntity() }

    fun makeUser() = User(USER_ID, "testNick")

    fun users() = (0 until 1).map { makeUser() }
}