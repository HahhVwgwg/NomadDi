package com.dataplus.tabyspartner.networking

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TokenOtp {
    @SerializedName("access_token")
    @Expose
    var accessToken: String? = null
}