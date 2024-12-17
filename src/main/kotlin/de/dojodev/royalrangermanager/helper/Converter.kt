package de.dojodev.royalrangermanager.helper

import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class Converter {

    companion object {

        fun genderToInt(gender: String): Int {
            when(gender) {
                FXHelper.getBundle().getString("user.gender.m") -> return 1
                FXHelper.getBundle().getString("user.gender.f") -> return 2
                FXHelper.getBundle().getString("user.gender.ns") -> return 0
            }
            return 0
        }

        fun intToGender(gender: Int): String {
            when(gender) {
                1 -> return FXHelper.getBundle().getString("user.gender.m")
                2 -> return FXHelper.getBundle().getString("user.gender.f")
                0 -> return FXHelper.getBundle().getString("user.gender.ns")
            }
            return FXHelper.getBundle().getString("user.gender.ns")
        }

        fun dateToLocalDate(date: Date?): LocalDate? {
            if(date == null) {
                return null
            }

            return LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault())
        }

        fun localDateToDate(localDate: LocalDate?): Date? {
            if(localDate == null) {
                return null
            }

            val instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
            return Date.from(instant)
        }
    }
}