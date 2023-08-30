package com.wavecell.sample.app.adapter

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wavecell.sample.app.presentation.model.RowRecentCallViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object RecentCallBindingAdapter {

    @JvmStatic
    @BindingAdapter(value = ["recentCallList", "recentClickListener"], requireAll = true)
    fun setAdapter(recyclerView: RecyclerView,
                   calls: List<RowRecentCallViewModel>,
                   listener: RecentRecycleAdapter.ClickListener) {
        (recyclerView.adapter as? RecentRecycleAdapter)?.submitList(calls) ?: run {
            val adapter = RecentRecycleAdapter(listener)
            recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
            recyclerView.adapter = adapter
        }
    }

    @JvmStatic
    @BindingAdapter("duration")
    @SuppressLint("SetTextI18n")
    fun setDuration(textView: TextView, duration: Long?) {
        duration?.let {
            if (it <= 0L) {
                textView.text = "00:00"
                return@let
            }
            val seconds = it.div(1000)
            val min = seconds / 60
            val sec = seconds % 60
            val minutesString = if (min < 10) "0$min" else "$min"
            val secondsString = if (sec < 10) "0$sec" else "$sec"
            textView.text = "$minutesString:$secondsString"
        }
    }

    @JvmStatic
    @BindingAdapter("dateTime")
    @SuppressLint("SimpleDateFormat")
    fun setTime(textView: TextView, time: Long?) {
        time?.let {
            val formatter: DateFormat = SimpleDateFormat("h:mm a MM/dd")
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = it

            textView.text = formatter.format(calendar.time)
        }
    }
}
