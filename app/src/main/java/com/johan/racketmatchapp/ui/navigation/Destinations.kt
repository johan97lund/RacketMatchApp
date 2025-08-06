package com.johan.racketmatchapp.ui.navigation

import com.johan.racketmatchapp.core.data.model.SportType

/**
 * Object that defines the navigation destinations for the application.
 *
 * Contains predefined routes for the main menu, sport selection, match screen,
 * and settings screen. Each route is represented by a [Route] instance, which
 * includes the route string and an optional base route for parameterized paths.
 */
object Destinations {

    /**
     * Represents a navigation route with an optional base route for parameterized paths.
     *
     * @property route The full route string used for navigation.
     * @property baseRoute The base route string, defaulting to the value of [route].
     */
    val Main             = Route("main")
    val SportSelection   = Route("sport_selection")
    // BaseRoute är ”match” och i composable lägger vi till /{sportType}
    val Game             = Route("match", baseRoute = "match")
    val Settings         = Route("settings")

    fun gameRoute(sport: SportType) = "${Game.baseRoute}/${sport.name}"

    data class Route(val route: String, val baseRoute: String = route)
}
