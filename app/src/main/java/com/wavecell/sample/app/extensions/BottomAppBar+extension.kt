package com.wavecell.sample.app.extensions

import androidx.databinding.BindingAdapter
import com.google.android.material.bottomappbar.BottomAppBar
import com.wavecell.sample.app.R
import com.wavecell.sample.app.presentation.model.ActivityMainViewModel

@BindingAdapter("initBottomAppBar")
fun BottomAppBar.initBottomAppBar(viewModel: ActivityMainViewModel) {
    setNavigationOnClickListener {
        viewModel.isProfileBottomSheetVisible.set(false)
    }

    setOnMenuItemClickListener { item ->
        when (item.itemId) {
            R.id.action_settings -> {
                viewModel.isProfileBottomSheetVisible.set(true)
            }
        }
        true
    }
}