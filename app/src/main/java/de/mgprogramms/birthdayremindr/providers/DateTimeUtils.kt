package de.mgprogramms.birthdayreminder.providers

import java.time.LocalDate
import java.time.format.DateTimeFormatter


const val YEARLESS_DATE_PATTERN = "MM-dd"

fun parseDate(date: String, parsePattern: String = "yyyy-MM-dd"): BirthDate {
    return when (date.length) {
        parsePattern.length -> {
            BirthDate(LocalDate.parse(date, DateTimeFormatter.ofPattern(parsePattern)), false)
        }

        YEARLESS_DATE_PATTERN.length -> {
            BirthDate(parseDate("2020-$date").parsedDate, true)
        }

        else -> {
            BirthDate(parseDate("2020-" + date.removeRange(0, 2)).parsedDate, true)
        }
    }
}

data class BirthDate(val parsedDate: LocalDate, val noYear: Boolean)
