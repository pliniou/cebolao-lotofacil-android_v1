package com.cebolao.lotofacil.core.error

/**
 * Converts low‑level exceptions into [AppError] instances and human‑readable
 * messages.  Centralising this logic avoids duplication in repositories and
 * use cases.
 */
object ErrorMapper {

    /**
     * Convert any [Throwable] into an [AppError].  Unknown exceptions
     * default to [UnknownError].
     */
    fun toAppError(throwable: Throwable): AppError = when (throwable) {
        is java.net.SocketTimeoutException, is java.net.UnknownHostException ->
            NetworkError(throwable)
        is java.io.IOException -> PersistenceError(throwable)
        is AppError -> throwable
        else -> UnknownError(throwable)
    }

    /**
     * Provide a user‑friendly message for an [AppError].  UI layers can
     * internationalise these messages by mapping them to string resources.
     */
    fun messageFor(error: AppError): String = when (error) {
        is NetworkError -> "Sem conexão com o servidor. Tente novamente."
        is PersistenceError -> "Erro ao acessar o armazenamento local."
        is ParsingError -> "Erro ao processar dados remotos."
        is EmptyHistoryError -> "Histórico indisponível."
        is UnknownError -> "Ocorreu um erro inesperado."
        else -> "Erro inesperado."
    }
}
