package com.dataplus.tabyspartner.networking

data class OwnNewsResponse(
    val id: String?,
    val zagolovok: String?,
    val text: String?,
    val date_in: String?,
    val date_out: String?,
    val photo: String?
)

data class OwnRefResponse(
    val id: String?,
    val out_id: String?,
    val in_id: String?,
    val summa: String?,
    val create_at: String?
)

data class OwnBaseResponse(
    val success: String?,
    val error: String?
)

