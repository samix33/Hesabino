package com.example.hesabino.model.repository.initialBalance

import android.app.Application
import android.content.Context
import com.example.hesabino.model.data.Category
import com.example.hesabino.model.data.Money
import com.example.hesabino.model.data.Transaction
import com.example.hesabino.model.db.Mydatabase
import com.example.hesabino.model.db.dao.CategoryDao
import com.example.hesabino.model.db.dao.MoneyDao
import com.example.hesabino.model.db.dao.TransactionDao
import com.example.hesabino.model.repository.home.HomeRepository


class initialBalanceRepositoryImpl(
    private val monyDao: MoneyDao,
    private val catrgoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
) : initialBalanceRepository {



    override suspend fun inserTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)

    }

    override suspend fun insertAllCategory(category: List<Category>) {
        catrgoryDao.insertAllcategory(category)

    }

    override suspend fun updataCategory(category: Category) {
        catrgoryDao.insertcategory(category)
    }

    override suspend fun insertMony(money: Money) {
        monyDao.insertMany(money)

    }


}