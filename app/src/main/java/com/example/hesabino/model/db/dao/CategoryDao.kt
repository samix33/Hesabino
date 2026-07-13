package com.example.hesabino.model.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.hesabino.model.data.Category


@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertcategory(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertAllcategory(categories: List<Category>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun updatatcategory(category: Category)

    @Query("SELECT * FROM category_table WHERE id = :id LIMIT 1")
    fun getTransactionById(id: Int): Category?

    @Delete
    fun deletecategory(category: Category)

    @Query("""SELECT * FROM category_table ORDER BY transaction_nummber DESC LIMIT 1""")
     fun getCategoryWithMaxTransactionNumber(): Category?

    @Query("SELECT budgetLimit FROM category_table WHERE id = :categoryId LIMIT 1")
     fun getBudgetLimit(categoryId: Int): Long?
    @Query("""SELECT * FROM category_table ORDER BY CAST(REPLACE(mony, '.', '') AS INTEGER) DESC""")
     fun getCategoriesSortedByMoney(): List<Category>

    @Query("""
    SELECT c.*
    FROM category_table c
    INNER JOIN table_transaction t
        ON c.name = t.Category
    WHERE t.deposit = 0
    AND t.createdAt BETWEEN :startOfDay AND :endOfDay
    GROUP BY c.id
    ORDER BY COUNT(t.id) DESC
    LIMIT 1
""")
    fun getTodayExpenseCategoryWithMaxTransactionNumber(
        startOfDay: Long,
        endOfDay: Long
    ): Category?
    @Query("SELECT * FROM category_table WHERE name = :categoryName LIMIT 1")
     fun getCategoryByName(categoryName: String): Category?
    @Query("SELECT * FROM category_table WHERE type = 'expense'")
    fun getExpenseCategories(): List<Category>

    @Query("SELECT * FROM category_table WHERE type = 'income'")
    fun getIncomeCategories(): List<Category>
    @Query("SELECT * FROM category_table")
    fun getAll(): List<Category>
}