package eu.euronavigate.ui.utils

fun Double.formatSpeed(): String = "${"%.2f".format(this)} m/s"

fun Float.formatSpeed(): String = this.toDouble().formatSpeed()
