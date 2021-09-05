package com.mgprogramms.birthdayreminder.birthday

import android.content.Context

sealed class BirthdayProviderFactory {
    companion object {
        fun buildProvider(context: Context): BirthdayProvider {
            return if (shouldUseTestPerson(context)) {
                BirthdayProviderChain(
                    context,
                    StaticBirthdayProvider.forToday(context, "Test Person"),
                    ContactsBirthdayProvider(context)
                )
            } else {
                ContactsBirthdayProvider(context)
            }
        }

        private const val USE_TEST_PERSON = "useTestPerson"
        fun shouldUseTestPerson(context: Context): Boolean {
            return with(context.getSharedPreferences(BirthdayProviderFactory.toString(), Context.MODE_PRIVATE)) {
                getBoolean(USE_TEST_PERSON, false)
            }
        }

        fun setUseTestPerson(context: Context, activated: Boolean) {
            with(context.getSharedPreferences(BirthdayProviderFactory.toString(), Context.MODE_PRIVATE).edit()) {
                putBoolean(USE_TEST_PERSON, activated)
                apply()
            }
        }
    }

    private class BirthdayProviderChain(context: Context, vararg val providers: BirthdayProvider) :
        BirthdayProvider(context) {

        override fun getBirthdays(): Array<BirthdayData> {
            val birthdays = emptyList<BirthdayData>().toMutableList()
            for (provider in providers) {
                birthdays.addAll(provider.getBirthdays())
            }
            return birthdays.toTypedArray()
        }

    }
}

