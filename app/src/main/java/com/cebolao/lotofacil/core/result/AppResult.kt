package com.cebolao.lotofacil.core.result

import com.cebolao.lotofacil.core.error.AppError

sealed interface AppResult<out T> {
    fun <R> fold(onSuccess: (T) -> R, onFailure: (AppError) -> R): R = when (this) {
        is Success -> onSuccess(value)
        is Failure -> onFailure(error)
    }

    data class Success<out T>(val value: T) : AppResult<T>
    data class Failure(val error: AppError) : AppResult<Nothing>
}

fun <T> T.toSuccess(): AppResult.Success<T> = AppResult.Success(this)
fun AppError.toFailure(): AppResult.Failure = AppResult.Failure(this)
