package de.mgprogramms.birthdayremindr.ui.contactdetail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.mgprogramms.birthdayremindr.R
import de.mgprogramms.birthdayremindr.models.BirthdayContact
import de.mgprogramms.birthdayremindr.providers.BirthDate
import java.time.LocalDate

@Composable
fun BirthdayDetailInfo(contact: BirthdayContact) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.ic_cake), "Geburtstag")
            Text("${contact.daysUntilBirthday} Tage")
        }

        if (!contact.birthDate.noYear) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_schedule), "Alter")
                Spacer(Modifier.size(4.dp))
                val age = remember { contact.birthDate.parsedDate.until(LocalDate.now()) }
                Text("${age.years} Jahre alt")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BirthdayDetailInfoPreview() {
    BirthdayDetailInfo(
        BirthdayContact(1, BirthDate(LocalDate.now(), false), 2, "Max Mustermann")
    )
}