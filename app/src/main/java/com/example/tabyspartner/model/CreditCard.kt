package com.example.tabyspartner.model

data class CreditCard(
    var id: Int = 0,
    var creditCardName: String = "",
    var creditCardNumber: String = ""
)

class ToDoModel {
    var id = 0
    var status = 0
    var task: String? = null
}