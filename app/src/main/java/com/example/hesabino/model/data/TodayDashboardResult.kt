package com.example.hesabino.model.data

data class TodayDashboardResult(
    val totalTodayExpense: Long,
    val transactionCount: Int,
    val topCategory: String,
    val topCategoryAmount: Long
)