package com.dist.roundsimageloadertask.core

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showToast(@StringRes messageRes: Int) {
    Toast.makeText(applicationContext, messageRes, Toast.LENGTH_SHORT).show()
}