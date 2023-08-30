package com.wavecell.sample.app.extensions

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.wavecell.sample.app.R

@BindingAdapter("avatar")
fun ImageView.setAvatar(avatarUrl: String?) {
    val avatar = if (avatarUrl.isNullOrBlank()) R.drawable.avatar_batman else avatarUrl
    Glide.with(context).load(avatar).into(this)
}