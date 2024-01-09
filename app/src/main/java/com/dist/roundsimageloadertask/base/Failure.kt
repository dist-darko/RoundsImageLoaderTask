package com.dist.roundsimageloadertask.base

sealed class Failure {
    object ServerError : Failure()
}