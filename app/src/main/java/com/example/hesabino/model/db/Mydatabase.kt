package com.example.hesabino.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.hesabino.model.db.dao.CategoryDao
import com.example.hesabino.model.db.dao.MoneyDao
import com.example.hesabino.model.db.dao.TransactionDao
import com.example.hesabino.model.data.Category
import com.example.hesabino.model.data.Money
import com.example.hesabino.model.data.Transaction

@Database(entities = [Money::class, Transaction::class, Category::class ], version = 1, exportSchema = false)
abstract class Mydatabase : RoomDatabase() {

    abstract val moneyDao : MoneyDao
    abstract val transactionDao : TransactionDao

    abstract val categoryDao : CategoryDao



}