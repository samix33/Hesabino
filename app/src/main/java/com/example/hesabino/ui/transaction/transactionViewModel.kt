package com.example.hesabino.ui.transaction

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hesabino.model.data.Category
import com.example.hesabino.model.data.Money
import com.example.hesabino.model.data.Transaction
import com.example.hesabino.model.repository.transaction.transactionRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionViewModel(
    private val transactionRepository: transactionRepository
) : ViewModel() {
    val Income = MutableLiveData<List<Category>>()
    val Expense = MutableLiveData<List<Category>>()
    val Mony = MutableLiveData<Money>()


    init {
        refreshAllDataFromDb()
    }

    private fun refreshAllDataFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val answerIncome = async { transactionRepository.getIncomeCategories() }
            val answerExpense = async { transactionRepository.getExpenseCategories() }
            val answerMony = async { transactionRepository.getAllMony() }

            updataData(answerIncome.await(), answerExpense.await(), answerMony.await())


        }

    }

    private suspend fun updataData(
        answerIncome: List<Category>,
        answerExpense: List<Category>,
        answerMony: Money
    ) {
        withContext(Dispatchers.Main) {
            Income.value = answerIncome
            Expense.value = answerExpense
            Mony.value = answerMony

        }
    }

    fun updatatcategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.v("tage","ddd")
            transactionRepository.updatatcategory(category)
        }
    }

    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.insertTransaction(transaction)
        }
    }
    fun updatatMany(mony : Money) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.updatatMany(mony)
        }
    }
    suspend fun getBudgetLimit(categoryId: Int) : Long? {

            return transactionRepository.getBudgetLimit(categoryId)


    }
  suspend  fun getTransactionById(id: Int) : Category? {
      return transactionRepository.getTransactionById(id)

    }


}