package com.example.finaltestphase2

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateConverter {
    @SuppressLint("ConstantLocale")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun dateToString(date: Date): String {
        return dateFormat.format(date)
    }

    fun stringToDate(dateString: String): Date {
        return dateFormat.parse(dateString) ?: Date()
    }
}