package com.example.tabyspartner.model

data class History(
    var id : Int = 0,
    var history_card_number : String = "",
    var history_amount_sent : String = "",
    var history_amount_total : String = "",
    var history_amount_fee : String = "",
    var history_check_id : Int = 0,
    var history_date : String = "",
    var history_recipient : String = ""
)