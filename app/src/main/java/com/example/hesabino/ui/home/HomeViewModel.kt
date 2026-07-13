package com.example.hesabino.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hesabino.model.data.Category
import com.example.hesabino.model.data.Money
import com.example.hesabino.model.data.Transaction
import com.example.hesabino.model.repository.home.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val homeRepository: HomeRepository,
) : ViewModel() {

    val monyData = MutableLiveData<Money>()
    val transactionData = MutableLiveData<List<Transaction>>()

    init {
        refreshAllDataFromDb()
    }

    private fun refreshAllDataFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val answerMony = async { homeRepository.getMony() }
            val answerTransaction = async { homeRepository.getTransaction() }
            updatadata(answerMony.await(), answerTransaction.await())


        }
    }

    private suspend fun updatadata(money: Money, transaction: List<Transaction>) {
        withContext(Dispatchers.Main) {
            transactionData.value = transaction
            monyData.value = money
        }


    }

    suspend fun getCategoryByName(transaction: Transaction): Category {

        return homeRepository.getCategoryByName(transaction)!!

    }

    suspend fun getTransactionById(id : Int): Category {

        return homeRepository.getTransactionById(id)

    }

    suspend fun getBudgetLimit(id : Int): Long {

        return homeRepository.getBudgetLimit(id)!!


    }

    fun updataCaregory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            homeRepository.updataCategory(category)
        }


    }

    fun updataMony(money: Money) {
        viewModelScope.launch(Dispatchers.IO) {
            homeRepository.updataMony(money)
        }
    }

    fun deletdTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            homeRepository.deletedTransaction(transaction)
        }

    }
}