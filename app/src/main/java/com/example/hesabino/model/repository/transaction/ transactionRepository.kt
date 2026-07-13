package com.example.hesabino.model.repository.transaction

import com.example.hesabino.model.data.Category
import com.example.hesabino.model.data.Money
import com.example.hesabino.model.data.Transaction

interface transactionRepository {


    suspend fun getIncomeCategories() :List<Category>
    suspend fun getExpenseCategories() :List<Category>
    suspend fun updatatcategory(category: Category)


    suspend fun getTransactionById(id:Int) : Category?
    suspend fun getBudgetLimit(categoryid: Int) : Long?
    suspend fun insertTransaction(transaction: Transaction)


    suspend fun getAllMony() :Money
    suspend fun updatatMany(money: Money)



}