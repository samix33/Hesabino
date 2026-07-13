package com.example.hesabino.ui.initialBalance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hesabino.model.data.Category
import com.example.hesabino.model.data.Money
import com.example.hesabino.model.data.Transaction
import com.example.hesabino.model.repository.initialBalance.initialBalanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InitialBalanceViewModel(private val initialBalanceRepository: initialBalanceRepository): ViewModel() {



     fun insertMony(money: Money){
         viewModelScope.launch(Dispatchers.IO) {
             initialBalanceRepository.insertMony(money)
         }

    }
    fun insertAllCategory(categories: List<Category>) {
        viewModelScope.launch(Dispatchers.IO) {
            initialBalanceRepository.insertAllCategory(categories)
        }
    }
    fun updataCategory(category:Category){
        viewModelScope.launch(Dispatchers.IO) {
            initialBalanceRepository.updataCategory(category)
        }
    }
     fun inserttransaction(transaction: Transaction){
         viewModelScope.launch(Dispatchers.IO) {
             initialBalanceRepository.inserTransaction(transaction)

         }
    }
}