package com.johan.racketmatchapp.core.data.model

/**
 * SportType representerar de olika racketsporter appen stödjer.
 *
 * @property displayName Namnet som visas i UI:t.
 */

enum class SportType(val displayName: String) {
    PADEL("Padel"),
    TENNIS("Tennis"),
    PICKLEBALL("Pickleball"),
    BADMINTON("Badminton");

    companion object {
        /**
         * Hämtar en SportType baserat på display-namn (case-insensitive).
         * @throws IllegalArgumentException om inget matchar.
         */
        fun fromDisplayName(name: String): SportType =
            values().firstOrNull() { it.displayName.equals(name, ignoreCase = true) }
                ?: throw IllegalArgumentException("Okänd sport: $name")
    }
}