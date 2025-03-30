package eu.euronavigate.ui.utils

fun Double.formatSpeed(): String = "${"%.2f".format(this)} m/s"

fun List<Double>.averageOrNull(): Double? =
    if (isNotEmpty()) average() else null
