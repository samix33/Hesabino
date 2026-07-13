package com.example.hesabino.model.repository.transaction

import android.util.Log
import com.example.hesabino.model.data.Category
import com.example.hesabino.model.data.CategoryAmount
import com.example.hesabino.model.data.Money
import com.example.hesabino.model.data.Transaction
import com.example.hesabino.model.db.dao.CategoryDao
import com.example.hesabino.model.db.dao.MoneyDao
import com.example.hesabino.model.db.dao.TransactionDao
import java.util.TreeMap

class analysisRepositoryImpl(
    private val monyDao: MoneyDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
) : analysisRepository {
    override suspend fun getCategoryWithMaxTransactionNumber(): Category? {
      return categoryDao.getCategoryWithMaxTransactionNumber()
    }

    override suspend fun getCategoriesSortedByMoney(): List<Category> {
        Log.v("taer","d : ${categoryDao.getCategoriesSortedByMoney()}")

        return categoryDao.getCategoriesSortedByMoney()
    }

    override suspend fun updatatcategory(category: Category) {
        categoryDao.updatatcategory(category)
    }

    override suspend fun getTodayExpenseCategoryWithMaxTransactionNumber(
        start: Long,
        end: Long
    ): Category? {
       return categoryDao.getTodayExpenseCategoryWithMaxTransactionNumber(start,end)
    }

    override suspend fun getTodayTotalExpense(start: Long, end: Long): Long? {
        return transactionDao.getTodayTotalExpense(start,end)
    }



    override suspend fun getTotalExpenseBetween(todayStart: Long, todayEnd: Long): Long? {
        return transactionDao.getTotalExpenseBetween(todayStart,todayEnd)
    }

    override suspend fun getTodayAverageAmount(start: Long, end: Long): Double? {
        return  transactionDao.getTodayAverageAmount(start,end)
    }

    override suspend fun getAllTotalExpense(): Long? {
      return  transactionDao.getAllTotalExpense()
    }

    override suspend fun getAllTransactionCount(): Int {
        return transactionDao.getAllTransactionCount()
    }

    override suspend fun getAllTopExpenseCategory(): String? {
        return transactionDao.getAllTopExpenseCategory()
    }

    override suspend fun getAllExpenseAverage(): Double? {
        return transactionDao.getAllExpenseAverage()
    }

    override suspend fun getAllExpenseByCategory(categoryName : String): Long? {
       return transactionDao.getAllExpenseByCategory(categoryName)
    }

    override suspend fun getTodayTransactionCount(start: Long, end: Long): Int {
        return transactionDao.getTodayTransactionCount(start,end)
    }

    override suspend fun getTodayTopExpenseCategory(start: Long, end: Long): String? {
        return transactionDao.getTodayTopExpenseCategory(start,end)
    }

    override suspend fun getTodayExpenseByCategory(
        categoryName: String,
        start: Long,
        end: Long
    ): Long? {
        return transactionDao.getTodayExpenseByCategory(categoryName,start,end)
    }

    override suspend fun getTopExpenseCategoryBetween(start: Long, end: Long): CategoryAmount? {
        return transactionDao.getTopExpenseCategoryBetween(start,end)
    }

    override suspend fun getLowExpenseCategoryBetween(start: Long, end: Long): CategoryAmount? {
        return transactionDao.getLowExpenseCategoryBetween(start,end)
    }

    override suspend fun getExpenseTransactionsBetween(start: Long, end: Long): List<Transaction> {
        return transactionDao.getExpenseTransactionsBetween(start,end)
    }



}