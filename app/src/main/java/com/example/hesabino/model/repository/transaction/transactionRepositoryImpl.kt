package com.example.hesabino.model.repository.transaction

import android.util.Log
import com.example.hesabino.model.data.Category
import com.example.hesabino.model.data.Money
import com.example.hesabino.model.data.Transaction
import com.example.hesabino.model.db.dao.CategoryDao
import com.example.hesabino.model.db.dao.MoneyDao
import com.example.hesabino.model.db.dao.TransactionDao

class transactionRepositoryImpl(
    private val monyDao: MoneyDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
) : transactionRepository {
    override suspend fun getIncomeCategories(): List<Category> {
        return categoryDao.getIncomeCategories()
    }

    override suspend fun getExpenseCategories(): List<Category> {
        return categoryDao.getExpenseCategories()
    }

    override suspend fun updatatcategory(category: Category) {
        Log.v("tagw","up : $category")
        categoryDao.updatatcategory(category)
    }

    override suspend fun getTransactionById(id: Int): Category? {
      return categoryDao.getTransactionById(id)
    }

    override suspend fun getBudgetLimit(categoryid: Int): Long? {
        return categoryDao.getBudgetLimit(categoryid)
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    override suspend fun getAllMony(): Money {
        return monyDao.getAll()
    }

    override suspend fun updatatMany(money: Money) {
        monyDao.updatatMany(money)
    }

}