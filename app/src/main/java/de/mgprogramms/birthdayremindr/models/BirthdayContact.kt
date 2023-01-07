package de.mgprogramms.birthdayremindr.models

import de.mgprogramms.birthdayremindr.providers.BirthDate
import de.mgprogramms.birthdayremindr.providers.parseDate
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class BirthdayContact(
    val id: Int,
    val birthDate: BirthDate,
    val daysUntilBirthday: Long,
    val name: String,
)


fun Contact.toBirthdayContact(): BirthdayContact {
    val birthdate = parseDate(date)
    return BirthdayContact(
        id = id,
        birthDate = birthdate,
        daysUntilBirthday = daysUntilNextBirthday(birthdate),
        name = name
    )
}


fun daysUntilNextBirthday(birthdate: BirthDate): Long {
    val currentDate = LocalDate.now()

    var birthdayInCurrentYear =
        LocalDate.of(currentDate.year, birthdate.parsedDate.monthValue, birthdate.parsedDate.dayOfMonth)

    if (birthdayInCurrentYear < currentDate) {
        birthdayInCurrentYear = birthdayInCurrentYear.plusYears(1)
    }
    return ChronoUnit.DAYS.between(currentDate, birthdayInCurrentYear)
}
