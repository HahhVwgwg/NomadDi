package com.example.tabyspartner.networking

data class BukhtaFeeResponse(
    val contract_source_id : Int,
    val amount_sent : String,
    val amount_fee : String,
    val amount_total : String
)

data class FeeRequest(
    val contract_source_id : Int,
    val external_ref_id: String,
    val card_number: String,
    val amount : String
)