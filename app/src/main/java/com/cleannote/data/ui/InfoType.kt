package com.cleannote.data.ui

sealed class InfoType{
    object Confirm: InfoType()
    object Warning: InfoType()
    object Question: InfoType()
}