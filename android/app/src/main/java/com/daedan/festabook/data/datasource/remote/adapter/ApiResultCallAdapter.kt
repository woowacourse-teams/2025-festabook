package com.daedan.festabook.data.datasource.remote.adapter

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class ApiResultCallAdapter<T>(
    private val responseType: Type,
) : CallAdapter<T, Call<ApiResult<T>>> {
    override fun responseType(): Type = responseType

    override fun adapt(call: Call<T>): Call<ApiResult<T>> =
        object : Call<ApiResult<T>> {
            override fun enqueue(callback: Callback<ApiResult<T>>) {
                val apiResultCall = this
                call.enqueue(
                    object : Callback<T> {
                        override fun onResponse(
                            call: Call<T>,
                            response: Response<T>,
                        ) {
                            val result: ApiResult<T> =
                                when {
                                    response.isSuccessful -> {
                                        response.body()?.let { ApiResult.Success(it) }
                                            ?: ApiResult.UnknownError
                                    }

                                    response.code() in 400..499 -> {
                                        ApiResult.ClientError(response.code(), response.message())
                                    }

                                    response.code() >= 500 -> {
                                        ApiResult.ServerError(response.code(), response.message())
                                    }

                                    else -> ApiResult.UnknownError
                                }

                            callback.onResponse(apiResultCall, Response.success(result))
                        }

                        override fun onFailure(
                            call: Call<T>,
                            t: Throwable,
                        ) {
                            val result = ApiResult.NetworkError(t)
                            callback.onResponse(apiResultCall, Response.success(result))
                        }
                    },
                )
            }

            override fun clone(): Call<ApiResult<T>> = adapt(call.clone())

            override fun execute(): Response<ApiResult<T>> = throw UnsupportedOperationException()

            override fun isExecuted(): Boolean = call.isExecuted

            override fun cancel() = call.cancel()

            override fun isCanceled(): Boolean = call.isCanceled

            override fun request(): Request = call.request()

            override fun timeout(): Timeout = call.timeout()
        }
}
