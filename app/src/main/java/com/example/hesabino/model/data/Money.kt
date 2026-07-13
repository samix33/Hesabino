package com.example.hesabino.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("table_mony")
data class Money(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val Balance: String,
    val Income: String,
    val Expense: String,
    val transaction_nummber : Int
)

