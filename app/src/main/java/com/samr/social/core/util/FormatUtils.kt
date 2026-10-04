package com.samr.social.core.util

import java.util.Locale

object FormatUtils {

    fun formatCount(count: Int, isArabic: Boolean): String {
        return when {
            count < 1000 -> count.toString()
            count < 1_000_000 -> {
                val value = count / 1000.0
                if (isArabic) {
                    val formatted = String.format(Locale.forLanguageTag("ar"), "%.1f", value)
                    "${formatted.replace(".0", "").replace("٫0", "")} ألف"
                } else {
                    val formatted = String.format(Locale.US, "%.1f", value)
                    "${formatted.replace(".0", "")}k"
                }
            }
            else -> {
                val value = count / 1_000_000.0
                if (isArabic) {
                    val formatted = String.format(Locale.forLanguageTag("ar"), "%.1f", value)
                    "${formatted.replace(".0", "").replace("٫0", "")} مليون"
                } else {
                    val formatted = String.format(Locale.US, "%.1f", value)
                    "${formatted.replace(".0", "")}M"
                }
            }
        }
    }

    fun formatRelativeTime(minutesAgo: Int, isArabic: Boolean): String {
        return when {
            minutesAgo <= 1 -> if (isArabic) "الآن" else "Just now"
            minutesAgo < 60 -> if (isArabic) "منذ $minutesAgo د" else "${minutesAgo}m ago"
            minutesAgo < 1440 -> {
                val hours = minutesAgo / 60
                if (isArabic) {
                    when (hours) {
                        1 -> "منذ ساعة"
                        2 -> "منذ ساعتين"
                        in 3..10 -> "منذ $hours ساعات"
                        else -> "منذ $hours ساعة"
                    }
                } else {
                    "${hours}h ago"
                }
            }
            else -> {
                val days = minutesAgo / 1440
                if (isArabic) {
                    when (days) {
                        1 -> "منذ يوم"
                        2 -> "منذ يومين"
                        in 3..10 -> "منذ $days أيام"
                        else -> "منذ $days يوماً"
                    }
                } else {
                    "${days}d ago"
                }
            }
        }
    }
}
