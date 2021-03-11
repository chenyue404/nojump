package com.chenyue404.nojump

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

class Utils {

}

fun Long.timeToStr(): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).format(this)

fun Int.dp2Px(context: Context): Int {
    return (this * context.resources.displayMetrics.density + 0.5f).toInt()
}