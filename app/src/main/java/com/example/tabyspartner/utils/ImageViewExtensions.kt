package com.example.tabyspartner.utils

import android.content.ContentResolver
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.setImageFromRaw(rawId: Int) {
    val uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + rawId)
    Glide.with(context).load(uri).into(this)
}