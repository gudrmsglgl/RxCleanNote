package com.cleannote.remote.mapper

import com.cleannote.data.model.UserEntity
import com.cleannote.remote.model.UserModel
import javax.inject.Inject

open class UserEntityMapper @Inject constructor(): EntityMapper<UserModel, UserEntity> {

    override fun mapFromRemote(type: UserModel): UserEntity = UserEntity(type.userId, type.nick)

}