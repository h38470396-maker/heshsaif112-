package com.example.ui

object Localization {
    val categories = listOf(
        "بيع منتج",
        "رواتب ومكافآت",
        "إيجار ومرافق",
        "مشتريات بضاعة",
        "مصاريف تسويق",
        "صيانة وأدوات",
        "مصاريف عامة",
        "أخرى"
    )

    fun getMonthNameArabic(index: Int): String {
        return when (index) {
            0 -> "يناير"
            1 -> "فبراير"
            2 -> "مارس"
            3 -> "أبريل"
            4 -> "مايو"
            5 -> "يونيو"
            6 -> "يوليو"
            7 -> "أغسطس"
            8 -> "سبتمبر"
            9 -> "أكتوبر"
            10 -> "نوفمبر"
            11 -> "ديسمبر"
            else -> "غير معروف"
        }
    }

    // Helper to format currency
    fun formatCurrency(amount: Double): String {
        return String.format("%.2f", amount) + " ر.س"
    }
}
