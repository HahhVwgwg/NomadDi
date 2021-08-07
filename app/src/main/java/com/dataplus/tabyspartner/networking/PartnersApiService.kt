package com.dataplus.tabyspartner.networking

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface PartnersApiService {

    @FormUrlEncoded
    @POST("/api/provider/register_otp")
    fun getUser(@FieldMap params: HashMap<String, Any>): Call<OtpResponse>

    @FormUrlEncoded
    @POST("/api/provider/oauth/token")
    fun getParks(@FieldMap params: HashMap<String, Any>): Call<List<ParkElement>>

    @FormUrlEncoded
    @POST("/api/provider/oauth/token")
    fun loginByOtp(@FieldMap params: HashMap<String, Any>): Call<TokenOtp>

    @GET("/api/provider/profile")
    fun getProfile(
        @Query("device_type") device_type: String,
        @Query("version") version: String
    ): Call<ProfileOtp>

    @POST("/api/provider/providercard")
    fun getProviderCard(): Call<List<CardOtp>>

    @FormUrlEncoded
    @POST("/api/provider/providercard/delete")
    fun deleteCard(@FieldMap params: HashMap<String, Any>): Call<MessageOtp>

    @FormUrlEncoded
    @POST("/api/provider/providercard/store")
    fun addCard(@FieldMap params: HashMap<String, Any>): Call<MessageOtp>

    @GET("/api/provider/wallettransaction")
    fun getWalletTransaction(): Call<WalletTransactions>

    @GET("/api/provider/notifications")
    fun getNews(): Call<List<NotificationElement>>

    @GET("/api/provider/wallettransaction/details")
    fun getHistoryById(@Query("id") id: Int): Call<HistoryOtp>

    @GET("/api/provider/transferlist")
    fun getTransferList(): Call<TransferLists>

    @FormUrlEncoded
    @POST("/api/provider/requestamount")
    fun withdraw(@FieldMap params: HashMap<String, Any>): Call<MessageOtp>

    @FormUrlEncoded
    @POST("/api/provider/commission")
    fun commission(@FieldMap params: HashMap<String, Any>): Call<Commission>
}

class NotificationElement(
    val id: Long,

    @SerializedName("notify_type")
    val notifyType: String,

    val image: String,
    val title: String,
    val description: String,

    @SerializedName("expiry_date")
    val expiryDate: String,

    val status: String,

    @SerializedName("viewed_users_json")
    val viewedUsersJSON: String,

    @SerializedName("error")
    val error: String
) {
    override fun toString(): String {
        return "NotificationElement(id=$id, notifyType='$notifyType', image='$image', title='$title', description='$description', expiryDate='$expiryDate', status='$status', viewedUsersJSON='$viewedUsersJSON', error='$error')"
    }
}


class HistoryOtp(
    @SerializedName("wallet_details")
    val walletDetails: WalletDetails,

    @SerializedName("error")
    var error: String? = null

) {
    override fun toString(): String {
        return "HistoryOtp(walletDetails=$walletDetails)"
    }
}

class WalletDetails(
    val id: Long,

    @SerializedName("from_id")
    val fromID: String,

    @SerializedName("fleet_id")
    val fleetID: Long,

    @SerializedName("card_number")
    val cardNumber: String,

    @SerializedName("card_name")
    val cardName: String,

    @SerializedName("transaction_id")
    val transactionID: Long,

    @SerializedName("transaction_alias")
    val transactionAlias: String,

    @SerializedName("transaction_desc")
    val transactionDesc: String,

    @SerializedName("transaction_recept")
    val transactionRecept: String,

    val type: String,
    val amount: Double,

    @SerializedName("open_balance")
    val openBalance: Double,

    @SerializedName("close_balance")
    val closeBalance: Double,

    @SerializedName("payment_mode")
    val paymentMode: String,

    val status: String,

    @SerializedName("created_at")
    val createdAt: String
) {
    override fun toString(): String {
        return "WalletDetails(id=$id, fromID='$fromID', fleetID=$fleetID, cardNumber='$cardNumber', cardName='$cardName', transactionID=$transactionID, transactionAlias='$transactionAlias', transactionDesc='$transactionDesc', transactionRecept='$transactionRecept', type='$type', amount=$amount, openBalance=$openBalance, closeBalance=$closeBalance, paymentMode='$paymentMode', status='$status', createdAt='$createdAt')"
    }
}


class MessageOtp {
    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("success")
    @Expose
    var success: String? = null

    @SerializedName("pending")
    @Expose
    var pending: String? = null

    @SerializedName("error")
    @Expose
    var error: String? = null

    @SerializedName("url")
    @Expose
    var url: String? = null
}

class CardOtp {
    @SerializedName("id")
    val id = 0

    @SerializedName("user_id")
    val userID = 0

    @SerializedName("last_four")
    var lastFour: String? = null

    @SerializedName("card_name")
    val cardName: String? = null

    @SerializedName("brand")
    val brand: String? = null

    @SerializedName("is_default")
    val isDefault = 0

    @SerializedName("created_at")
    val createdAt: String? = null

    override fun toString(): String {
        return "CardOtp(id=$id, userID=$userID, lastFour=$lastFour, cardName=$cardName, brand=$brand, isDefault=$isDefault, createdAt=$createdAt)"
    }

}

class Success {
    @SerializedName("success")
    @Expose
    var message: String? = null
}


class OtpResponse {
    @SerializedName("otp")
    @Expose
    var otp: String? = null

    @SerializedName("mobile")
    @Expose
    var mobile: String? = null

    @SerializedName("error")
    @Expose
    var error: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null
    override fun toString(): String {
        return "OtpResponse(otp=$otp, mobile=$mobile, status=$status)"
    }
}

class ProfileOtp {
    @SerializedName("id")
    @Expose
    val id = 0

    @SerializedName("error")
    @Expose
    val error: String? = null

    @SerializedName("first_name")
    @Expose
    val firstName: String? = null

    @SerializedName("last_name")
    @Expose
    val lastName: String? = null

    val mobile: String? = null

    @SerializedName("service_country")
    @Expose
    val serviceCountry: String? = null

    @SerializedName("service_number")
    @Expose
    val serviceNumber: String? = null

    @SerializedName("service_car")
    @Expose
    val serviceCar: String? = null

    @SerializedName("service_color")
    @Expose
    val serviceColor: String? = null

    @SerializedName("service_year")
    @Expose
    val serviceYear: String? = null

    @SerializedName("service_model")
    @Expose
    val serviceModel: String? = null

    @SerializedName("status")
    @Expose
    val status = 0

    @SerializedName("current_status")
    @Expose
    val currentStatus: String? = null

    @SerializedName("working_status")
    @Expose
    val workingStatus: String? = null

    @SerializedName("fleet")
    @Expose
    val fleet: Fleet = Fleet()

    @SerializedName("wallet_balance")
    @Expose
    val walletBalance = 0

    @SerializedName("payment_type")
    @Expose
    val paymentType: String? = null

    @SerializedName("card_count")
    @Expose
    val cardCount = 0

    @SerializedName("force_update")
    @Expose
    val forceUpdate: Boolean = false

    @SerializedName("pending")
    @Expose
    val pending: Boolean = false

    @SerializedName("url")
    @Expose
    val url: String? = null

    @SerializedName("transaction_url")
    @Expose
    val transactionUrl: String? = null

    override fun toString(): String {
        return "ProfileOtp(id=$id, firstName=$firstName, lastName=$lastName, mobile=$mobile, serviceCountry=$serviceCountry, serviceNumber=$serviceNumber, serviceCar=$serviceCar, serviceColor=$serviceColor, serviceYear=$serviceYear, serviceModel=$serviceModel, status=$status, currentStatus=$currentStatus, workingStatus=$workingStatus, fleet=$fleet, walletBalance=$walletBalance, paymentType=$paymentType, cardCount=$cardCount)"
    }

}

class Fleet {
    @SerializedName("park_name")
    @Expose
    val parkName: String? = null

    @SerializedName("park_city")
    @Expose
    val parkCity: String? = null

    override fun toString(): String {
        return "Fleet(parkName=$parkName, parkCity=$parkCity)"
    }


}


class WalletTransactions(
    @SerializedName("wallet_transation")
    val walletTransation: List<WalletTransation>,

    @SerializedName("wallet_balance")
    val walletBalance: Int,

    @SerializedName("error")
    val error: String? = null
) {
    override fun toString(): String {
        return "WalletTransactions(walletTransation=$walletTransation, walletBalance=$walletBalance)"
    }
}

class WalletTransation(
    val id: Long,

    @SerializedName("from_id")
    val fromID: String,

    @SerializedName("fleet_id")
    val fleetID: Long,

    @SerializedName("card_number")
    val cardNumber: String? = null,

    @SerializedName("card_name")
    val cardName: String? = null,

    @SerializedName("transaction_id")
    val transactionID: Long,

    @SerializedName("transaction_alias")
    val transactionAlias: String,

    @SerializedName("transaction_desc")
    val transactionDesc: String,

    val type: String,
    val amount: Double,

    @SerializedName("open_balance")
    val openBalance: Double,

    @SerializedName("close_balance")
    val closeBalance: Double,

    @SerializedName("payment_mode")
    val paymentMode: String,

    val status: String,

    @SerializedName("created_at")
    val createdAt: String
) {
    override fun toString(): String {
        return "WalletTransation(id=$id, fromID='$fromID', fleetID=$fleetID, cardNumber=$cardNumber, cardName=$cardName, transactionID=$transactionID, transactionAlias='$transactionAlias', transactionDesc='$transactionDesc', type='$type', amount=$amount, openBalance=$openBalance, closeBalance=$closeBalance, paymentMode='$paymentMode', status='$status', createdAt='$createdAt')"
    }
}

class TransferLists(
    val pendinglist: List<Pendinglist>,

    @SerializedName("wallet_balance")
    val walletBalance: Int
) {
    override fun toString(): String {
        return "TransferLists(pendinglist=$pendinglist, walletBalance=$walletBalance)"
    }
}

class Pendinglist(
    val id: Long,

    @SerializedName("from_id")
    val fromID: String,

    @SerializedName("fleet_id")
    val fleetID: Long,

    @SerializedName("card_number")
    val cardNumber: String,

    @SerializedName("card_name")
    val cardName: String,

    @SerializedName("transaction_id")
    val transactionID: Long,

    @SerializedName("transaction_alias")
    val transactionAlias: String,

    @SerializedName("transaction_desc")
    val transactionDesc: String,

    val type: String,
    val amount: Double,

    @SerializedName("open_balance")
    val openBalance: Double,

    @SerializedName("close_balance")
    val closeBalance: Double,

    @SerializedName("payment_mode")
    val paymentMode: String,

    val status: String,

    @SerializedName("created_at")
    val createdAt: String
) {
    override fun toString(): String {
        return "Pendinglist(id=$id, fromID='$fromID', fleetID=$fleetID, cardNumber='$cardNumber', cardName='$cardName', transactionID=$transactionID, transactionAlias='$transactionAlias', transactionDesc='$transactionDesc', type='$type', amount=$amount, openBalance=$openBalance, closeBalance=$closeBalance, paymentMode='$paymentMode', status='$status', createdAt='$createdAt')"
    }
}



data class ParkElement (
    val id: Long,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    val email: String? = null,
    val mobile: String,

    @SerializedName("service_country")
    val serviceCountry: String,

    @SerializedName("service_number")
    val serviceNumber: String,

    @SerializedName("service_car")
    val serviceCar: String,

    @SerializedName("service_color")
    val serviceColor: String,

    @SerializedName("service_year")
    val serviceYear: String,

    @SerializedName("service_model")
    val serviceModel: String,

    val password: String,
    val avatar: String? = null,

    @SerializedName("yandex_account_id")
    val yandexAccountID: String,

    val rating: String,
    val status: Long,

    @SerializedName("current_status")
    val currentStatus: String,

    @SerializedName("working_status")
    val workingStatus: String,

    val fleet: Long,
    val latitude: String? = null,
    val longitude: String? = null,

    @SerializedName("login_by")
    val loginBy: String? = null,

    val otp: Long,

    @SerializedName("wallet_balance")
    val walletBalance: Double,

    @SerializedName("closed_balance")
    val closedBalance: Long,

    @SerializedName("referral_unique_id")
    val referralUniqueID: String? = null,

    @SerializedName("qrcode_url")
    val qrcodeURL: String? = null,

    @SerializedName("remember_token")
    val rememberToken: String? = null,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("park_name")
    val parkName: String
)

data class Commission (
    @SerializedName("amount_sent")
    val amountSent: Int,

    @SerializedName("amount_fee_driver")
    val amountFee: Int,

    @SerializedName("amount_total")
    val amountTotal: Int,

    @SerializedName("amount_park")
    val amountPark: Int,

    @SerializedName("free_transaction")
    val freeTransaction: Int,

    @SerializedName("error")
    val error: String
)

