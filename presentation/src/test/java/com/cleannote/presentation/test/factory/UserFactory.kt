package com.cleannote.presentation.test.factory

import com.cleannote.domain.model.User
import com.cleannote.presentation.model.UserView

object UserFactory {
    const val USER_ID = "test#1"

    fun makeUser() = User(USER_ID, "testNick")
    fun makeUserView() = UserView(USER_ID, "testNick")

    fun makeUsers() = (0 until 1).map { makeUser() }
    fun makeUserViews() = (0 until 1).map { makeUserView() }
    fun makeEmptyUsers() = emptyList<User>()
}
