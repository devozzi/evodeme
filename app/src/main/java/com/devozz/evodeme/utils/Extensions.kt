package com.devozz.evodeme.utils

import android.content.Context
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG

fun Context.toast(message: String) {
    Toast.makeText(this, message, LENGTH_LONG).show()
}