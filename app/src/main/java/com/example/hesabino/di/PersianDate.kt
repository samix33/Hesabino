package com.example.hesabino.di

import java.util.Calendar

data class PersianDate(
    val year: Int,
    val month: Int,
    val day: Int
)

fun getTodayPersianDate(): PersianDate {
    val calendar = Calendar.getInstance()
    val gy = calendar.get(Calendar.YEAR)
    val gm = calendar.get(Calendar.MONTH) + 1
    val gd = calendar.get(Calendar.DAY_OF_MONTH)

    return gregorianToPersian(gy, gm, gd)
}

fun gregorianToPersian(gy: Int, gm: Int, gd: Int): PersianDate {
    val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

    var gy2 = gy - 1600
    var gm2 = gm - 1
    var gd2 = gd - 1

    var gDayNo = 365 * gy2 + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400

    for (i in 0 until gm2) {
        gDayNo += gDaysInMonth[i]
    }

    if (gm2 > 1 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0))) {
        gDayNo++
    }

    gDayNo += gd2

    var jDayNo = gDayNo - 79
    val jNp = jDayNo / 12053
    jDayNo %= 12053

    var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
    jDayNo %= 1461

    if (jDayNo >= 366) {
        jy += (jDayNo - 1) / 365
        jDayNo = (jDayNo - 1) % 365
    }

    var jm = 0
    var i = 0
    while (i < 11 && jDayNo >= jDaysInMonth[i]) {
        jDayNo -= jDaysInMonth[i]
        i++
    }
    jm = i + 1
    val jd = jDayNo + 1

    return PersianDate(jy, jm, jd)
}