package com.cebolao.lotofacil.core.result

import com.cebolao.lotofacil.core.error.AppError

sealed interface AppResult<out T> {


    data class Success<out T>(val value: T) : AppResult<T>
    data class Failure(val error: AppError) : AppResult<Nothing>
}

fun <T> T.toSuccess(): AppResult.Success<T> = AppResult.Success(this)

