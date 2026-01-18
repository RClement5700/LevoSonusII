package com.clementcorporation.levosonusii.domain.models

data class NavTileData(
    val title: String = "",
    val navigate: () -> Unit = {}
)
