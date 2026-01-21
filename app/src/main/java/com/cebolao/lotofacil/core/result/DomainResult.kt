package com.cebolao.lotofacil.core.result

import com.cebolao.lotofacil.core.error.AppError

sealed interface DomainResult<out T> {
    fun <R> fold(onSuccess: (T) -> R, onFailure: (AppError) -> R): R = when (this) {
        is Success -> onSuccess(value)
        is Failure -> onFailure(error)
    }

    data class Success<out T>(val value: T) : DomainResult<T>
    data class Failure(val error: AppError) : DomainResult<Nothing>
}

fun <T> T.toSuccess(): DomainResult.Success<T> = DomainResult.Success(this)
fun AppError.toFailure(): DomainResult.Failure = DomainResult.Failure(this)
