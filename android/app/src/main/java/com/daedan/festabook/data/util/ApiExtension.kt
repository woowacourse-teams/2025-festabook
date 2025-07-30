package com.daedan.festabook.data.util

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.util.ApiResultException.ClientException
import com.daedan.festabook.data.util.ApiResultException.NetworkException
import com.daedan.festabook.data.util.ApiResultException.ServerException
import com.daedan.festabook.data.util.ApiResultException.UnknownException

fun <T> ApiResult<T>.toResult(): Result<T> =
    when (this) {
        is ApiResult.Success -> Result.success(data)
        is ApiResult.ClientError -> Result.failure(ClientException(code, message, errorBody))
        is ApiResult.ServerError -> Result.failure(ServerException(code, message, errorBody))
        is ApiResult.NetworkError -> Result.failure(NetworkException(throwable))
        is ApiResult.UnknownError -> Result.failure(UnknownException("Unknown Error"))
    }
