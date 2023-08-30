package com.wavecell.sample.app.extensions

import androidx.databinding.Observable


@Suppress("UNCHECKED_CAST")
fun <T, V> T.cast(): V = this as V

fun <T : Observable> T.addOnPropertyChanged(callback: (T) -> Unit) =
        addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable?, i: Int) =
                    callback(observable.cast())
        })