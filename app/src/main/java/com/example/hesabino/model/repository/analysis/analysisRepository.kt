package com.example.hesabino.model.repository.transaction

import com.example.hesabino.model.data.Category
import com.example.hesabino.model.data.CategoryAmount
import com.example.hesabino.model.data.Money
import com.example.hesabino.model.data.Transaction

interface analysisRepository {

    // category
    suspend fun getCategoryWithMaxTransactionNumber() : Category?
    suspend fun getCategoriesSortedByMoney() : List<Category>
    suspend fun updatatcategory(category: Category)
    suspend fun getTodayExpenseCategoryWithMaxTransactionNumber(start : Long,end :Long) : Category?
    suspend fun getTodayTotalExpense(start : Long,end :Long) : Long?





    //transaction
    suspend fun getTotalExpenseBetween(todayStart : Long,todayEnd :Long) : Long?
    suspend fun getTodayAverageAmount(start : Long,end :Long) : Double?
    suspend fun getAllTotalExpense() : Long?
    suspend fun getAllTransactionCount() : Int
    suspend fun getAllTopExpenseCategory() : String?
    suspend fun getAllExpenseAverage() : Double?
    suspend fun getAllExpenseByCategory(categoryName : String) : Long?
    suspend fun getTodayTransactionCount(start : Long,end :Long) : Int
    suspend fun getTodayTopExpenseCategory(start : Long,end :Long) : String?
    suspend fun getTodayExpenseByCategory(categoryName: String,start : Long,end :Long) : Long?
    suspend fun getTopExpenseCategoryBetween(start : Long,end :Long) : CategoryAmount?
    suspend fun getLowExpenseCategoryBetween(start : Long,end :Long) : CategoryAmount?
    suspend fun getExpenseTransactionsBetween(start : Long,end :Long) : List<Transaction>






}