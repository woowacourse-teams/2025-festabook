package com.daedan.festabook.data.datasource.remote

import retrofit2.Response

sealed class ApiResult<out T> {
    data class Success<T>(
        val data: T,
    ) : ApiResult<T>()

    data class ClientError(
        val code: Int,
        val message: String?,
    ) : ApiResult<Nothing>()

    data class ServerError(
        val code: Int,
        val message: String?,
    ) : ApiResult<Nothing>()

    data class NetworkError(
        val throwable: Throwable,
    ) : ApiResult<Nothing>()

    data object UnknownError : ApiResult<Nothing>()

    companion object {
        suspend fun <T> toApiResult(apiCall: suspend () -> Response<T>): ApiResult<T> =
            runCatching { apiCall() }
                .mapCatching { response ->
                    if (response.isSuccessful) {
                        response.body()?.let { Success(it) }
                            ?: UnknownError
                    } else {
                        when (response.code()) {
                            in 400..499 ->
                                ClientError(
                                    response.code(),
                                    response.message(),
                                )

                            in 500..599 ->
                                ServerError(
                                    response.code(),
                                    response.message(),
                                )

                            else -> UnknownError
                        }
                    }
                }.getOrElse { e ->
                    NetworkError(e)
                }
    }
}
