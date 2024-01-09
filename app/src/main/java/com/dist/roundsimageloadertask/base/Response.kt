package com.dist.roundsimageloadertask.base

import com.dist.roundsimageloadertask.base.Response.Companion.success

sealed class Response<out L, out R> {
    /** * Represents the left side of [Response] class which by convention is a "Failure". */
    data class Failure<out L>(val failure: L) : Response<L, Nothing>()

    /** * Represents the right side of [Response] class which by convention is a "Success". */
    data class Success<out R>(val value: R) : Response<Nothing, R>()

    /**
     * Returns true if this is a Success, false otherwise.
     * @see Success
     */
    val isSuccess get() = this is Success<R>

    /**
     * Returns true if this is a Failure, false otherwise.
     * @see Failure
     */
    val isFailure get() = this is Failure<L>

    companion object {
        /**
         * Creates a Failure type.
         * @see Failure
         */
        fun <L> failure(a: L) = Failure(a)

        /**
         * Creates a Success type.
         * @see Success
         */
        fun <R> success(b: R) = Success(b)
    }

    /**
     * Applies fnL if this is a Left or fnR if this is a Right.
     * @see Failure
     * @see Success
     */
    fun fold(fnL: (L) -> Any, fnR: (R) -> Any): Any =
        when (this) {
            is Failure -> fnL(failure)
            is Success -> fnR(value)
        }

    fun doIfSuccess(fnSuccess: (R) -> Unit) {
        if (this is Success) fnSuccess(value)
    }

    fun doIfFailure(fnSuccess: (L) -> Unit) {
        if (this is Failure) fnSuccess(failure)
    }

    suspend fun doAsyncOnSuccess(fnSuccess: suspend (R) -> Unit) {
        if (this is Success) fnSuccess(value)
    }
}

/**
 * Composes 2 functions
 * See <a href="https://proandroiddev.com/kotlins-nothing-type-946de7d464fb">Credits to Alex Hart.</a>
 */
fun <A, B, C> ((A) -> B).c(f: (B) -> C): (A) -> C = {
    f(this(it))
}

/**
 * Right-biased flatMap() FP convention which means that Right is assumed to be the default case
 * to operate on. If it is Left, operations like map, flatMap, ... return the Left value unchanged.
 */
fun <T, L, R> Response<L, R>.flatMap(fn: (R) -> Response<L, T>): Response<L, T> =
    when (this) {
        is Response.Failure -> Response.Failure(failure)
        is Response.Success -> fn(value)
    }

/**
 * Right-biased flatMap() FP convention which means that Right is assumed to be the default case
 * to operate on. If it is Left, operations like map, flatMap, ... return the Left value unchanged.
 */
suspend fun <T, L, R> Response<L, R>.suspendableFlatMap(fn: suspend (R) -> Response<L, T>): Response<L, T> =
    when (this) {
        is Response.Failure -> Response.Failure(failure)
        is Response.Success -> fn(value)
    }

/**
 * Right-biased map() FP convention which means that Right is assumed to be the default case
 * to operate on. If it is Left, operations like map, flatMap, ... return the Left value unchanged.
 */
fun <T, L, R> Response<L, R>.map(fn: (R) -> (T)): Response<L, T> = this.flatMap(fn.c(::success))

/** Returns the value from this `Right` or the given argument if this is a `Left`.
 *  Right(12).getOrElse(17) RETURNS 12 and Left(12).getOrElse(17) RETURNS 17
 */
fun <L, R> Response<L, R>.getOrElse(value: R): R =
    when (this) {
        is Response.Failure -> value
        is Response.Success -> this.value
    }

fun <L, R> Response<L, R>.getOrNull(): R? =
    when (this) {
        is Response.Failure -> null
        is Response.Success -> this.value
    }

fun <L, R> Response<L, R>.getOrThrow(message: String = "Request failed with "): R =
    when (this) {
        is Response.Failure -> throw error("$message ${this.failure}")
        is Response.Success -> this.value
    }