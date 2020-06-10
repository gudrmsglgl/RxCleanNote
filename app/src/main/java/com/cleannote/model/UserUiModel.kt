package com.cleannote.model

class UserUiModel(val userId: String, val nick: String){
    override fun toString(): String {
        return "userId: ${this.userId}, nick: ${this.nick}"
    }
}