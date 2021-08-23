package com.mgprogramms.birthdayreminder

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@RequiresApi(Build.VERSION_CODES.O)
fun createFriendlyDate(date: String, parsePattern: String = "yyyy-MM-dd", formatStyle: FormatStyle = FormatStyle.LONG): String {
    val parseResult = parseDate(date);

    return if (parseResult.yearless) {
        parseResult.parsedDate.format(DateTimeFormatter.ofPattern("dd. MMMM"))
    } else {
        parseResult.parsedDate.format(DateTimeFormatter.ofLocalizedDate(formatStyle))
    }
}

const val YEARLESS_DATE_PATTERN = "MM-dd"

@RequiresApi(Build.VERSION_CODES.O)
fun parseDate(date: String, parsePattern: String = "yyyy-MM-dd"): DateParseResult {
    return if (date.length == parsePattern.length) {
        DateParseResult(LocalDate.parse(date, DateTimeFormatter.ofPattern(parsePattern)), false)
    } else if (date.length == YEARLESS_DATE_PATTERN.length) {
        DateParseResult(parseDate("2020-$date").parsedDate, true)
    } else {
        DateParseResult(parseDate("2020-" + date.removeRange(0, 2)).parsedDate, true)
    }
}

class DateParseResult(result: LocalDate, var yearless: Boolean) {
    var parsedDate: LocalDate = result;
}