package com.dist.roundsimageloadertask.base

import org.json.JSONObject
import java.net.HttpURLConnection

sealed class NetworkResponse<out R>(val code: Int) {
    data class SuccessResponse<out R>(
        val value: R,
        private val responseCode: Int
    ) :
        NetworkResponse<R>(responseCode)

    data class EmptyBodySuccessResponse<out R>(
        private val responseCode: Int
    ) : NetworkResponse<R>(responseCode)

    data class ErrorResponse(
        val message: String,
        private val errorCode: Int
    ) : NetworkResponse<Nothing>(errorCode) {
        val isUnAuthorizedRequest: Boolean get() = errorCode == HttpURLConnection.HTTP_UNAUTHORIZED
        val isBadRequestError: Boolean get() = errorCode == HttpURLConnection.HTTP_BAD_REQUEST
        val isServerError: Boolean get() = errorCode == HttpURLConnection.HTTP_INTERNAL_ERROR
    }

    val isSuccess get() = this is SuccessResponse<R>

    val isFailure get() = this is ErrorResponse

    fun <T, L, R> Response<L, R>.flatMap(fn: (R) -> Response<L, T>): Response<L, T> =
        when (this) {
            is Response.Failure -> Response.Failure(failure)
            is Response.Success -> fn(value)
        }

}

fun <R> retrofit2.Response<R>.parseResponse(): NetworkResponse<R> {
    val response = this
    return if (response.isSuccessful) {
        val code = response.code()
        when {
            response.body() == null || code == HttpURLConnection.HTTP_NO_CONTENT -> {
                NetworkResponse.EmptyBodySuccessResponse(code)
            }
            else -> NetworkResponse.SuccessResponse(
                response.body()!!,
                response.code()
            )
        }
    } else {
        val responseCode = response.code()
        val msg = if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            "Session has expired"
        } else {
            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
            jsonObj.getString("status") ?: response.message()
        }
        NetworkResponse.ErrorResponse(msg, response.code())
    }
}


suspend fun <R> NetworkResponse<R>.doIfSuccess(action: suspend (R) -> Unit): NetworkResponse<R> {
    if (this is NetworkResponse.SuccessResponse)
        action(this.value)

    return this
}