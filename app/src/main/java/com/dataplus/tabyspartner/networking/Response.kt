package com.dataplus.tabyspartner.networking

import com.google.gson.annotations.SerializedName

data class GetSomethingRequest(val query: Query) {
    data class Query(val park: Park) {
        data class Park(val id: String)
    }
}


class DriverProfilesResponse (
        @SerializedName("driver_profiles") val driversList: List<DriverProfilesItem>
        )

data class DriverProfilesItem(
    val accounts: List<Account>,
    val driver_profile: DriverProfile
)
data class DriverProfile(
    val first_name: String,
    val id: String,
    val last_name: String,
    val phones: List<String>,
)
data class Account(
    val balance: String,
    val balance_limit: String,
    val currency: String,
    val id: String,
    val last_transaction_date: String,
    val type: String
)

///////////
data class WithdrawResponse(
    val amount: String,
    val category_id: String,
    val created_by: CreatedBy,
    val currency_code: String,
    val description: String,
    val driver_profile_id: String,
    val event_at: String,
    val park_id: String
)

data class CreatedBy(
    val client_id: String,
    val identity: String,
    val key_id: String
)

data class WithdrawBodyRequest(
    val amount: String,
    val category_id: String,
    val description: String,
    val driver_profile_id: String,
    val park_id: String
)



