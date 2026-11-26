package com.example.erdbeeren.core

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateFormatter {
    private val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", Locale.forLanguageTag("es"))
        .withZone(ZoneId.systemDefault())

    fun format(instant: Instant?): String {
        if (instant == null) return ""
        val formatted = dateFormatter.format(instant)
        return formatted.replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase(Locale.forLanguageTag("es")) else it.toString() 
        }
    }
}