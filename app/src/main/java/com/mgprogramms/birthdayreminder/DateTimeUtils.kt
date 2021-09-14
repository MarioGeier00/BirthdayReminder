package com.mgprogramms.birthdayreminder

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@RequiresApi(Build.VERSION_CODES.O)
fun createFriendlyDate(
    date: String,
    parsePattern: String = "yyyy-MM-dd",
    formatStyle: FormatStyle = FormatStyle.LONG
): String {
    val parseResult = parseDate(date);

    return if (parseResult.yearless) {
        parseResult.parsedDate.format(DateTimeFormatter.ofPattern("dd. MMMM"))
    } else {
        parseResult.parsedDate.format(DateTimeFormatter.ofLocalizedDate(formatStyle))
    }
}

const val YEARLESS_DATE_PATTERN = "MM-dd"

fun parseDate(date: String, parsePattern: String = "yyyy-MM-dd"): BirthDate {
    return when (date.length) {
        parsePattern.length -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                BirthDate(LocalDate.parse(date, DateTimeFormatter.ofPattern(parsePattern)), false)
            } else {
                TODO("VERSION.SDK_INT < O")
            }
        }
        YEARLESS_DATE_PATTERN.length -> {
            BirthDate(parseDate("2020-$date").parsedDate, true)
        }
        else -> {
            BirthDate(parseDate("2020-" + date.removeRange(0, 2)).parsedDate, true)
        }
    }
}

data class BirthDate(val parsedDate: LocalDate, val yearless: Boolean)
