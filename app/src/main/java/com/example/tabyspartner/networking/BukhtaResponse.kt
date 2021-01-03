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


data class BukhtaWithDrawResponse(
    val amount_fee: String,
    val amount_sent: String,
    val amount_total: String,
    val card_number: String,
    val contract_source: ContractSource,
    val contract_source_id: Int,
    val external_ref_id: String,
    val id: Int,
    val posted_at: String,
    val public_id: String,
    val state: String
)

data class ContractSource(
    val created_at: String,
    val id: Int,
    val name: String,
    val operator_id: Int,
    val ref_id: Long,
    val type: Int
)