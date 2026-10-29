package com.example.spendwise.utils

import android.content.Context
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyHelper {
    private const val PREFS_NAME = "SpendWisePrefs"
    private const val KEY_CURRENCY = "currency_code"

    fun getCurrencyCode(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_CURRENCY, "default") ?: "default"
    }

    fun getCurrencyFormat(context: Context): NumberFormat {
        val code = getCurrencyCode(context)
        return getFormatForCode(code)
    }

    fun getFormatForCode(code: String): NumberFormat {
        if (code == "default") {
            return NumberFormat.getCurrencyInstance(Locale.getDefault())
        }
        return try {
            val curr = Currency.getInstance(code)
            val targetLocale = when (code) {
                "USD" -> Locale.US
                "EUR" -> Locale.GERMANY
                "GBP" -> Locale.UK
                "JPY" -> Locale.JAPAN
                "CAD" -> Locale.CANADA
                "AUD" -> Locale.forLanguageTag("en-AU")
                "INR" -> Locale.forLanguageTag("en-IN")
                "PKR" -> Locale.forLanguageTag("en-PK")
                "BDT" -> Locale.forLanguageTag("bn-BD")
                else -> Locale.getDefault()
            }
            NumberFormat.getCurrencyInstance(targetLocale).apply {
                currency = curr
            }
        } catch (e: Exception) {
            NumberFormat.getCurrencyInstance(Locale.getDefault())
        }
    }

    fun format(context: Context, amount: Double): String {
        return getCurrencyFormat(context).format(amount)
    }
}

