package com.cleannote.data.ui

sealed class UIType{
    object Toast: UIType()
    object Dialog: UIType()
    object Input: UIType()
}