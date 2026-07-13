package com.example.hesabino.model.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.hesabino.model.data.CategoryAmount
import com.example.hesabino.model.data.Transaction


@Dao
interface TransactionDao {

    @Insert
    fun insertTransaction(transaction : Transaction)

    @Update
    fun updatatTransaction(transaction : Transaction)

    @Delete
    fun deleteTransaction(transaction: Transaction)
    @Query("SELECT * FROM table_transaction")
    fun getAll() : List<Transaction>
/// Transaction to day
    @Query("""
    SELECT AVG(CAST(REPLACE(balance, '.', '') AS INTEGER))
    FROM table_transaction
    WHERE createdAt BETWEEN :start AND :end
""")
    fun getTodayAverageAmount(start: Long, end: Long): Double?
    @Query("""
    SELECT SUM(CAST(REPLACE(balance, '.', '') AS INTEGER))
    FROM table_transaction
    WHERE deposit = 0
    AND createdAt BETWEEN :start AND :end
""")
     fun getTodayTotalExpense(start: Long, end: Long): Long?
    @Query("""
    SELECT COUNT(*)
    FROM table_transaction
    WHERE deposit = 0
    AND createdAt BETWEEN :start AND :end
""")
     fun getTodayExpenseCount(start: Long, end: Long): Int
    @Query("""
    SELECT Category
    FROM table_transaction
    WHERE deposit = 0
    AND createdAt BETWEEN :start AND :end
    GROUP BY Category
    ORDER BY SUM(CAST(REPLACE(balance, '.', '') AS INTEGER)) DESC
    LIMIT 1
""")
     fun getTodayTopExpenseCategory(start: Long, end: Long): String?
    @Query("""
    SELECT COUNT(*)
    FROM table_transaction
    WHERE createdAt BETWEEN :start AND :end
""")
     fun getTodayTransactionCount(start: Long, end: Long): Int
    @Query("""
    SELECT SUM(CAST(REPLACE(balance, '.', '') AS INTEGER))
    FROM table_transaction
    WHERE deposit = 0
    AND Category = :categoryName
    AND createdAt BETWEEN :start AND :end
""")
    fun getTodayTopCategoryAmount(
        categoryName: String,
        start: Long,
        end: Long
    ): Long?
    @Query("""
    SELECT SUM(CAST(REPLACE(balance, '.', '') AS INTEGER))
    FROM table_transaction
    WHERE deposit = 0
    AND Category = :categoryName
    AND createdAt BETWEEN :startOfDay AND :endOfDay
""")
    fun getTodayExpenseByCategory(
        categoryName: String,
        startOfDay: Long,
        endOfDay: Long
    ): Long?
    @Query("""
    SELECT SUM(CAST(REPLACE(balance, '.', '') AS INTEGER))
    FROM table_transaction
    WHERE deposit = 0
    AND createdAt BETWEEN :start AND :end
""")
    fun getTotalExpenseBetween(start: Long, end: Long): Long?


    @Query("""
    SELECT 
        Category AS name,
        SUM(CAST(REPLACE(balance, '.', '') AS INTEGER)) AS amount
    FROM table_transaction
    WHERE deposit = 0
    AND createdAt BETWEEN :start AND :end
    GROUP BY Category
    ORDER BY amount DESC
    LIMIT 1
""")
    fun getTopExpenseCategoryBetween(start: Long, end: Long): CategoryAmount?
    @Query("""
    SELECT 
        Category AS name,
        SUM(CAST(REPLACE(balance, '.', '') AS INTEGER)) AS amount
    FROM table_transaction
    WHERE deposit = 0
    AND createdAt BETWEEN :start AND :end
    GROUP BY Category
    HAVING amount > 0
    ORDER BY amount ASC
    LIMIT 1
""")
    fun getLowExpenseCategoryBetween(start: Long, end: Long): CategoryAmount?

    @Query("""
    SELECT SUM(CAST(REPLACE(balance, '.', '') AS INTEGER))
    FROM table_transaction
    WHERE deposit = 0
""")
    fun getAllTotalExpense(): Long?
    @Query("""
    SELECT COUNT(*)
    FROM table_transaction
""")
    fun getAllTransactionCount(): Int

    @Query("""
    SELECT Category
    FROM table_transaction
    WHERE deposit = 0
    GROUP BY Category
    ORDER BY SUM(CAST(REPLACE(balance, '.', '') AS INTEGER)) DESC
    LIMIT 1
""")
    fun getAllTopExpenseCategory(): String?


    @Query("""
    SELECT * FROM table_transaction
    WHERE deposit = 0
    AND createdAt BETWEEN :start AND :end
    ORDER BY createdAt DESC
""")
    fun getExpenseTransactionsBetween(
        start: Long,
        end: Long
    ): List<Transaction>

    @Query("""
    SELECT SUM(CAST(REPLACE(balance, '.', '') AS INTEGER))
    FROM table_transaction
    WHERE deposit = 0
    AND Category = :categoryName
""")
    fun getAllExpenseByCategory(categoryName: String): Long?

    @Query("""
    SELECT AVG(CAST(REPLACE(balance, '.', '') AS INTEGER))
    FROM table_transaction


""")
    fun getAllExpenseAverage(): Double?
}