package com.devozz.evodeme.util

import android.widget.ImageView
import com.squareup.picasso.Picasso

fun ImageView?.loadImage(url: String) {
    this?.let {
        Picasso.get().load(url).into(it)
    }
}