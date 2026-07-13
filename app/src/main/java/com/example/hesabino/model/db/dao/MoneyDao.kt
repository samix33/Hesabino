package com.example.hesabino.model.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.hesabino.model.data.Money


@Dao
interface MoneyDao {

    @Insert
    fun insertMany(money : Money)

    @Update
    fun updatatMany(money : Money)

    @Delete
    fun deleteTransaction(money: Money)


    @Query("SELECT * FROM table_mony")
    fun getAll() : Money
}