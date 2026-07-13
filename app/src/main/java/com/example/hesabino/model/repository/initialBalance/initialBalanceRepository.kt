package com.example.hesabino.model.repository.initialBalance

import com.example.hesabino.model.data.Category
import com.example.hesabino.model.data.Money
import com.example.hesabino.model.data.Transaction

interface initialBalanceRepository {


    suspend fun insertMony(money: Money)
    suspend fun insertAllCategory(category: List<Category>)
    suspend fun updataCategory(category:Category)

    suspend fun inserTransaction(transaction: Transaction)

}