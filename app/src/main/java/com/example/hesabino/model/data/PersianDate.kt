package com.example.hesabino.model.data

import com.example.hesabino.di.gregorianToPersian
import java.util.Calendar

data class GregorianDate(
    val year: Int,
    val month: Int,
    val day: Int
)


fun isGregorianLeap(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}
fun getThisPersianMonthRange(): Pair<Long, Long> {
    val today = Calendar.getInstance()

    val gy = today.get(Calendar.YEAR)
    val gm = today.get(Calendar.MONTH) + 1
    val gd = today.get(Calendar.DAY_OF_MONTH)

    val persianToday = gregorianToPersian(gy, gm, gd)

    return getPersianMonthRange(
        persianToday.year,
        persianToday.month
    )

}
fun getLastPersianMonthRange(): Pair<Long, Long> {
    val today = Calendar.getInstance()

    val gy = today.get(Calendar.YEAR)
    val gm = today.get(Calendar.MONTH) + 1
    val gd = today.get(Calendar.DAY_OF_MONTH)

    val persianToday = gregorianToPersian(gy, gm, gd)

    val lastMonth: Int
    val lastMonthYear: Int

    if (persianToday.month == 1) {
        lastMonth = 12
        lastMonthYear = persianToday.year - 1
    } else {
        lastMonth = persianToday.month - 1
        lastMonthYear = persianToday.year
    }

    return getPersianMonthRange(lastMonthYear, lastMonth)
}

fun getTodayRange(): Pair<Long, Long> {
    val cal = Calendar.getInstance()

    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)

    val startOfDay = cal.timeInMillis

    cal.add(Calendar.DAY_OF_MONTH, 1)
    val endOfDay = cal.timeInMillis - 1

    return startOfDay to endOfDay
}
fun getYesterdayRange(): Pair<Long, Long> {
    val cal = Calendar.getInstance()

    cal.add(Calendar.DAY_OF_MONTH, -1)

    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)

    val start = cal.timeInMillis

    cal.add(Calendar.DAY_OF_MONTH, 1)
    val end = cal.timeInMillis - 1

    return start to end
}

fun calculatePercentChange(current: Long, previous: Long): Int {
    if (previous == 0L) return 0

    return (((current - previous).toDouble() / previous.toDouble()) * 100).toInt()
}
fun getDaysPassedFromStart(startOfMonth: Long): Int {
    val now = System.currentTimeMillis()
    val diff = now - startOfMonth

    val days = (diff / (1000 * 60 * 60 * 24)).toInt() + 1

    return if (days <= 0) 1 else days
}
fun getLastWeekRange(): Pair<Long, Long> {
    val cal = Calendar.getInstance()

    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)

    while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
        cal.add(Calendar.DAY_OF_MONTH, -1)
    }

    cal.add(Calendar.DAY_OF_MONTH, -7)
    val startOfLastWeek = cal.timeInMillis

    cal.add(Calendar.DAY_OF_MONTH, 7)
    val endOfLastWeek = cal.timeInMillis - 1

    return startOfLastWeek to endOfLastWeek
}
fun getThisWeekRange(): Pair<Long, Long> {
    val cal = Calendar.getInstance()

    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)

    // شروع هفته در ایران: شنبه
    while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
        cal.add(Calendar.DAY_OF_MONTH, -1)
    }

    val startOfWeek = cal.timeInMillis

    cal.add(Calendar.DAY_OF_MONTH, 7)
    val endOfWeek = cal.timeInMillis - 1

    return startOfWeek to endOfWeek
}

fun getPersianWeekDayName(timeMillis: Long): String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = timeMillis

    return when (cal.get(Calendar.DAY_OF_WEEK)) {
        Calendar.SATURDAY -> "شنبه"
        Calendar.SUNDAY -> "یکشنبه"
        Calendar.MONDAY -> "دوشنبه"
        Calendar.TUESDAY -> "سه‌شنبه"
        Calendar.WEDNESDAY -> "چهارشنبه"
        Calendar.THURSDAY -> "پنجشنبه"
        Calendar.FRIDAY -> "جمعه"
        else -> "نامشخص"
    }
}




fun persianToGregorian(jyInput: Int, jm: Int, jd: Int): GregorianDate {
    var jy = jyInput + 1595

    var days = -355668 +
            365 * jy +
            (jy / 33) * 8 +
            ((jy % 33 + 3) / 4) +
            jd

    days += if (jm < 7) {
        (jm - 1) * 31
    } else {
        ((jm - 7) * 30) + 186
    }

    var gy = 400 * (days / 146097)
    days %= 146097

    if (days > 36524) {
        days--
        gy += 100 * (days / 36524)
        days %= 36524

        if (days >= 365) {
            days++
        }
    }

    gy += 4 * (days / 1461)
    days %= 1461

    if (days > 365) {
        gy += (days - 1) / 365
        days = (days - 1) % 365
    }

    var gd = days + 1

    val gDaysInMonth = intArrayOf(
        0,
        31,
        if (isGregorianLeap(gy)) 29 else 28,
        31,
        30,
        31,
        30,
        31,
        31,
        30,
        31,
        30,
        31
    )

    var gm = 1
    while (gm <= 12 && gd > gDaysInMonth[gm]) {
        gd -= gDaysInMonth[gm]
        gm++
    }

    return GregorianDate(gy, gm, gd)
}
fun getPersianMonthRange(persianYear: Int, persianMonth: Int): Pair<Long, Long> {
    val startGregorian = persianToGregorian(
        persianYear,
        persianMonth,
        1
    )

    val nextMonthYear: Int
    val nextMonth: Int

    if (persianMonth == 12) {
        nextMonthYear = persianYear + 1
        nextMonth = 1
    } else {
        nextMonthYear = persianYear
        nextMonth = persianMonth + 1
    }

    val nextMonthGregorian = persianToGregorian(
        nextMonthYear,
        nextMonth,
        1
    )

    val startCal = Calendar.getInstance()
    startCal.set(
        startGregorian.year,
        startGregorian.month - 1,
        startGregorian.day,
        0,
        0,
        0
    )
    startCal.set(Calendar.MILLISECOND, 0)

    val endCal = Calendar.getInstance()
    endCal.set(
        nextMonthGregorian.year,
        nextMonthGregorian.month - 1,
        nextMonthGregorian.day,
        0,
        0,
        0
    )
    endCal.set(Calendar.MILLISECOND, 0)

    val startOfMonth = startCal.timeInMillis
    val endOfMonth = endCal.timeInMillis - 1

    return startOfMonth to endOfMonth
}