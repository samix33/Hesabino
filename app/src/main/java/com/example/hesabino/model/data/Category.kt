package com.example.hesabino.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("category_table")
data class Category(
    @PrimaryKey()
    val id: Int,
    val name: String,
    val mony: String,
    val transaction_nummber: Int,
    val budgetLimit: Long,
    val type: String, // "expense" یا "income"
)