package com.devozz.evodeme.util

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.squareup.picasso.Picasso

fun Context.toast(message: String) {
    Toast.makeText(this, message, LENGTH_LONG).show()
}

fun ImageView.loadImage(url: String) {
    Picasso.get().load(url).into(this)
}

fun View.visible() {
    visibility = VISIBLE
}

fun View.gone() {
    visibility = GONE
}

infix fun View.visibleIf(bool: Boolean) = if (bool) visible() else gone()