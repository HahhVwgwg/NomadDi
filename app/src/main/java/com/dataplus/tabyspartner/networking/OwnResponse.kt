package com.dataplus.tabyspartner.networking

data class OwnNewsResponse(
    val id: String?,
    val zagolovok: String?,
    val text: String?,
    val date_in: String?,
    val date_out: String?,
)

data class OwnBaseResponse(
    val success: String?,
    val error: String?
)

