package com.daedan.festabook.data.util

import com.daedan.festabook.data.datasource.remote.ApiResult

fun <T> ApiResult<T>.toResult(): Result<T> =
    when (this) {
        is ApiResult.Success -> Result.success(data)
        is ApiResult.ClientError -> Result.failure(Exception("Client error: $code $message $errorBody"))
        is ApiResult.ServerError -> Result.failure(Exception("Server error: $code $message $errorBody"))
        is ApiResult.NetworkError -> Result.failure(throwable)
        is ApiResult.UnknownError -> Result.failure(Exception("Unknown error"))
    }
