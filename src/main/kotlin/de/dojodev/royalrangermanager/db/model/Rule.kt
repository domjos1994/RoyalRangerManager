package de.dojodev.royalrangermanager.db.model

import java.util.*

data class Rule(
    var id: Int = 0,
    var childNumber: Int = -1,
    var price: Double = 0.0,
    var state: Date? = null
)
