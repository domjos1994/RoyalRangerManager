package de.dojodev.royalrangermanager.db.model

import java.util.Locale

data class AgeGroup(
    var id: Int = 0,
    var nameEn: String,
    var nameDe: String,
    var minAge: Int,
    var maxAge: Int
) {
    override fun toString(): String {
        return if(Locale.getDefault().language == Locale.GERMAN.language) {
            "$nameDe($minAge-$maxAge)"
        } else {
            "$nameEn($minAge-$maxAge)"
        }
    }
}
