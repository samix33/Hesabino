package com.example.hesabino.model.repository.home

import android.app.Application
import android.content.Context
import com.example.hesabino.model.data.Category
import com.example.hesabino.model.data.Money
import com.example.hesabino.model.data.Transaction
import com.example.hesabino.model.db.Mydatabase
import com.example.hesabino.model.db.dao.CategoryDao
import com.example.hesabino.model.db.dao.MoneyDao
import com.example.hesabino.model.db.dao.TransactionDao


class HomeRepositoryImpl(
    private val monyDao: MoneyDao,
    private val catrgoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
) : HomeRepository {

    override suspend fun getMony(): Money{
        val data = monyDao.getAll()
        return data
    }


    override suspend fun getTransaction(): List<Transaction> {
        val data = transactionDao.getAll()

        return data
    }

    override suspend fun updataMony(money: Money) {
        monyDao.updatatMany(money)
    }

    override suspend fun deletedTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }

    override suspend fun getCategoryByName(transaction: Transaction) : Category? {
        val category = catrgoryDao.getCategoryByName(transaction.Category)
        return category


    }

    override suspend fun updataCategory(category: Category) {
     catrgoryDao.updatatcategory(category)
    }

    override suspend fun getTransactionById(categoryid: Int): Category {
    return catrgoryDao.getTransactionById(categoryid)!!

    }

    override suspend fun getBudgetLimit(categoryid: Int): Long? {
       return catrgoryDao.getBudgetLimit(categoryid)
    }


}