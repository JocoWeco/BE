package com.jocoweco.foodsommelier.data.feat.restaurant

data class Location(
    val lat: Double,
    val lon: Double
)

data class Keyword(
    val keyword1: String? = null,
    val keyword2: String? = null,
    val keyword3: String? = null,
    val keyword4: String? = null
)

data class BestMenu(
    val menu1: String? = null,
    val menu2: String? = null
)

data class Restaurant(
    val name: String,
    val address: String,
    val location: Location,
    val keyword: Keyword,
    val bestMenu: BestMenu
)