package com.example.hesabino.model.repository.home

import com.example.hesabino.model.data.Category
import com.example.hesabino.model.data.Money
import com.example.hesabino.model.data.Transaction

interface HomeRepository {

    suspend fun getMony() : Money
    suspend fun updataMony(money: Money)
    suspend fun deletedTransaction(transaction: Transaction)
    suspend fun getCategoryByName(transaction: Transaction ): Category?
    suspend fun updataCategory(category: Category)
    suspend fun getTransactionById(categoryid: Int): Category
    suspend fun getBudgetLimit(categoryid: Int): Long?
    suspend fun getTransaction() : List<Transaction>

}