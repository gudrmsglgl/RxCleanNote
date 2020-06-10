package com.cleannote.presentation.mapper

import com.cleannote.domain.model.User
import com.cleannote.presentation.model.UserView
import javax.inject.Inject

open class UserMapper @Inject constructor(): Mapper<UserView, User>{
    override fun mapToView(type: User): UserView = UserView(type.userId, type.nickName)
}