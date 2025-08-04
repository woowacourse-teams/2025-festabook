package com.daedan.festabook.data.util

sealed class ApiResultException : Exception() {
    data class ClientException(
        val code: Int,
        val msg: String?,
        val errorBody: String?,
    ) : ApiResultException()

    data class ServerException(
        val code: Int,
        val msg: String?,
        val errorBody: String?,
    ) : ApiResultException()

    data class NetworkException(
        val throwable: Throwable,
    ) : ApiResultException()

    data class UnknownException(
        val msg: String?,
    ) : ApiResultException()
}
