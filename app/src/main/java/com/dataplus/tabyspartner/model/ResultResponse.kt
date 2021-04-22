package com.dataplus.tabyspartner.model

sealed class ResultResponse<out T : Any> {

    object Loading : ResultResponse<Nothing>()
    data class Success<out T : Any>(val data: T) : ResultResponse<T>()
    data class Error(val message: String, val code: Int = 0) : ResultResponse<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$message, code=$code]"
            is Loading -> "Loading"
        }
    }
}