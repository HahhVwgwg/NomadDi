package com.example.tabyspartner.otp

import java.util.*

class Otp {
    fun OTP(len: Int): String {
        val numbers = "0123456789"
        val rndm_method = Random()
        val otp = CharArray(len)
        for (i in 0 until len) {
            otp[i] = numbers[rndm_method.nextInt(numbers.length)]
        }
        return otp.joinToString("");
    }
}