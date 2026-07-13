package com.example.hesabino.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("table_transaction")
data class Transaction (
    @PrimaryKey(autoGenerate = true)
    val id : Int? = null,
    val balance : String,
    val Category : String,
    val detail : String,
    val date : String,
    val createdAt: Long = System.currentTimeMillis(),
    val deposit : Boolean,


)