package com.dataplus.tabyspartner.networking

data class MobizonResponse(
    val code: Int,
    val data: Data,
    val message: String
)
data class Data(
        val campaignId: String,
        val messageId: String,
        val status: Int
)