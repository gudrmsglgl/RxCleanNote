package com.cleannote.data.mapper

import com.cleannote.data.model.UserEntity
import com.cleannote.domain.model.User
import javax.inject.Inject

class UserMapper @Inject constructor(): Mapper<UserEntity, User> {

    override fun mapFromEntity(type: UserEntity): User = User(
        type.userId, type.nickName
    )

    override fun mapToEntity(type: User): UserEntity = UserEntity(type.userId, type.nickName)

}