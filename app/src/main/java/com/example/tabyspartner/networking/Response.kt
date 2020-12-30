package com.example.tabyspartner.networking

import com.google.gson.annotations.SerializedName
import java.util.*

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




