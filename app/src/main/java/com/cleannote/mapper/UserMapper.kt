package com.cleannote.mapper

import com.cleannote.model.UserUiModel
import com.cleannote.presentation.model.UserView
import javax.inject.Inject

open class UserMapper @Inject constructor(): Mapper<UserUiModel, UserView> {

    override fun mapToUiModel(type: UserView): UserUiModel = UserUiModel(type.userId, type.nick)

    override fun mapToView(type: UserUiModel): UserView = UserView("", "")
}