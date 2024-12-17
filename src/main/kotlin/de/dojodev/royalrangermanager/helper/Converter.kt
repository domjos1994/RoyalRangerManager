package de.dojodev.royalrangermanager.helper

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
    }
}