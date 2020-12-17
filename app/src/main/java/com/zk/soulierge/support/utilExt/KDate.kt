package com.zk.soulierge.support.utilExt

import android.text.format.DateUtils
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

const val API_DATE_FORMAT = "dd-MM-yyyy HH:mm"

const val DISPLAY_DATE_FORMAT = "dd MMMM yyyy"

fun String.toDate(withFormat: String = API_DATE_FORMAT): Date {
    val dateFormat = SimpleDateFormat(withFormat, Locale.US)
    var convertedDate = Date()
    try {
        convertedDate = dateFormat.parse(this)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return convertedDate
}

fun Date.toDate(withFormat: String = "dd-MM-yyyy"): Date {
    val dateFormat = SimpleDateFormat(withFormat, Locale.US)
    var convertedDate = Date()
    try {
        convertedDate = dateFormat.format(this).toDate()
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return convertedDate
}

fun String?.toAPIDateFormat(withFormat: String = DISPLAY_DATE_FORMAT): String? {
    val dateFormat = SimpleDateFormat(withFormat, Locale.US)
    val apiateFormat = SimpleDateFormat(API_DATE_FORMAT, Locale.US)
    var date: String = ""
    if (this != null) {
        try {
            val convertedDate = dateFormat.parse(this) as Date
            date = apiateFormat.format(convertedDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
    return date
}

fun Date?.toAPIDateFormat(withFormat: String = API_DATE_FORMAT): String? {
    val apiateFormat = SimpleDateFormat(withFormat, Locale.US)
    var date: String = ""
    if (this != null) {
        val convertedDate = this
        try {
            date = apiateFormat.format(convertedDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
    return date
}

fun String?.toDisplayDateFormat(withFormat: String = API_DATE_FORMAT,locale: Locale? = Locale.getDefault()): String? {
    val dateFormat = SimpleDateFormat(withFormat,locale)
    val apiateFormat = SimpleDateFormat(DISPLAY_DATE_FORMAT,locale)
    var date: String = ""
    if (this != null) {
        try {
            val convertedDate = dateFormat.parse(this) as Date
            date = apiateFormat.format(convertedDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
    return date
}

fun Date?.toDisplayDateFormat(withFormat: String = DISPLAY_DATE_FORMAT,locale: Locale? = Locale.getDefault()): String? {
    val apiateFormat = SimpleDateFormat(withFormat,locale)
    var date: String = ""
    if (this != null) {
        try {
            val convertedDate = this
            date = apiateFormat.format(convertedDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
    return date
}

fun String?.toDisplayDateHijriFormat(withFormat: String = API_DATE_FORMAT): String? {
    val dateFormat = SimpleDateFormat(withFormat, Locale.US)
    val apiateFormat = SimpleDateFormat(DISPLAY_DATE_FORMAT)
    var date: String = ""
    if (this != null) {
        val convertedDate = dateFormat.parse(this) as Date
        try {
            date = apiateFormat.format(convertedDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
    return date
}

// Converts current date to proper provided format
fun Date.convertTo(format: String = "dd MMM, yyyy"): String? {
    var dateStr: String? = ""
    val df = SimpleDateFormat(format, Locale.US)
    try {
        dateStr = df.format(this)
    } catch (ex: Exception) {
        Log.d("date", ex.toString())
    }
    return dateStr
}

fun Date.convertToWithComma(format: String = "dd MMM, yyyy"): String? {
    var dateStr: String? = ""
    val df = SimpleDateFormat(format, Locale.US)
    try {
        dateStr = df.format(this)
    } catch (ex: Exception) {
        Log.d("date", ex.toString())
    }
    return dateStr
}

// Converts current date to Calendar
fun Date.toCalendar(): Calendar {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal
}

fun getDate(years: Int): Date {
    val cal = Calendar.getInstance()
    cal.add(Calendar.YEAR, -years) // to get previous year add -1
    cal.add(Calendar.DAY_OF_YEAR, 1)
    return cal.time
}

fun getCalendar(years: Int): Calendar {
    val cal = Calendar.getInstance()
    cal.add(Calendar.YEAR, -years) // to get previous year add -1
    return cal
}

fun Date.isFuture(): Boolean {
    return !Date().before(this)
}

fun Date.isPast(): Boolean {
    return Date().before(this)
}

fun Date.isToday(): Boolean {
    return DateUtils.isToday(this.time)
}

fun Date.getTime(format: String = "HH:mm"):String?{
    var dateStr: String? = ""
    val df = SimpleDateFormat(format, Locale.US)
    try {
        dateStr = df.format(this)
    } catch (ex: Exception) {
        Log.d("date", ex.toString())
    }
    return dateStr
}

fun Date.getUTCTime(format: String = "HH:mm"):String?{
    var dateStr: String? = ""
    val df = SimpleDateFormat(format, Locale.US)
    try {
        df.timeZone = TimeZone.getTimeZone("UTC")
        dateStr = df.format(this)
    } catch (ex: Exception) {
        Log.d("date", ex.toString())
    }
    return dateStr
}

fun Date.isYesterday(): Boolean {
    return DateUtils.isToday(this.time + DateUtils.DAY_IN_MILLIS)
}

fun Date.isTomorrow(): Boolean {
    return DateUtils.isToday(this.time - DateUtils.DAY_IN_MILLIS)
}

fun Date.today(): Date {
    return Date()
}

fun Date.yesterday(): Date {
    val cal = this.toCalendar()
    cal.add(Calendar.DAY_OF_YEAR, -1)
    return cal.time
}

fun Date.tomorrow(): Date {
    val cal = this.toCalendar()
    cal.add(Calendar.DAY_OF_YEAR, 1)
    return cal.time
}

fun Date.hour(): Int {
    return this.toCalendar().get(Calendar.HOUR)
}

fun Date.minute(): Int {
    return this.toCalendar().get(Calendar.MINUTE)
}

fun Date.second(): Int {
    return this.toCalendar().get(Calendar.SECOND)
}

fun Date.month(): Int {
    return this.toCalendar().get(Calendar.MONTH) +1
}

fun Date.monthName(locale: Locale? = Locale.getDefault()): String {
    return this.toCalendar().getDisplayName(Calendar.MONTH, Calendar.LONG, locale)
}

fun Date.year(): Int {
    return this.toCalendar().get(Calendar.YEAR)
}

fun Date.day(): Int {
    return this.toCalendar().get(Calendar.DAY_OF_MONTH)
}

fun Date.dayOfWeek(): Int {
    return this.toCalendar().get(Calendar.DAY_OF_WEEK)
}

fun Date.dayOfWeekName(locale: Locale? = Locale.getDefault()): String {
    return this.toCalendar().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale)
}

fun Date.dayOfYear(): Int {
    return this.toCalendar().get(Calendar.DAY_OF_YEAR)
}

fun Date.monthsBetweenDates(endDate: Date): Int {
    val start = Calendar.getInstance()
    start.time = this
    val end = Calendar.getInstance()
    end.time = endDate
    var monthsBetween = 0
    var dateDiff = end.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH)
    if (dateDiff < 0) {
        val borrrow = end.getActualMaximum(Calendar.DAY_OF_MONTH)
        dateDiff = end.get(Calendar.DAY_OF_MONTH) + borrrow - start.get(Calendar.DAY_OF_MONTH)
        monthsBetween--
        if (dateDiff > 0) {
            monthsBetween++
        }
    } else {
        monthsBetween++
    }
    monthsBetween += end.get(Calendar.MONTH) - start.get(Calendar.MONTH)
    monthsBetween += (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12
    return monthsBetween
}

fun String.hasTime(): Boolean {
    val endDate = this.toDate("dd/MM/yyyy hh:mm a")
    val currentDate = Date()
    val duration = endDate.time - currentDate.time
    return duration > 0
}
