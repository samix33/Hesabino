package com.example.hesabino.ui.analysis

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hesabino.model.data.Category
import com.example.hesabino.model.data.CategoryAmount
import com.example.hesabino.model.data.Transaction

import com.example.hesabino.model.repository.transaction.analysisRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnalysisViewModel(
    private val analysisRepository: analysisRepository
) : ViewModel() {
    val getCategoryWithMaxTransactionNumber = MutableLiveData<Category?>()
    val getCategoriesSortedByMoney = MutableLiveData<List<Category>>()
    val getAllTotalExpense = MutableLiveData<Long?>()
    val getAllTransactionCount = MutableLiveData<Int?>()
    val getAllTopExpenseCategory = MutableLiveData<String?>()
    val getAllExpenseAverage = MutableLiveData<Double?>()
    val getAllExpenseByCategory = MutableLiveData<Long>()


    init {
        refreshAllDataFromDb()
    }

      private fun refreshAllDataFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val getCategoryWithMaxTransactionNumber = async { analysisRepository.getCategoryWithMaxTransactionNumber() }
            val getCategoriesSortedByMoney = async { analysisRepository.getCategoriesSortedByMoney() }
            val getAllTotalExpense = async { analysisRepository.getAllTotalExpense() }
            val getAllTransactionCount = async { analysisRepository.getCategoryWithMaxTransactionNumber() }
            val getAllTopExpenseCategory = async { analysisRepository.getAllTopExpenseCategory() }
            val getAllExpenseAverage = async { analysisRepository.getAllExpenseAverage() }

            updataDeta(
                getCategoryWithMaxTransactionNumber.await(),
                getCategoriesSortedByMoney.await(),
                getAllTotalExpense.await(),
                getAllTransactionCount.await(),
                getAllTopExpenseCategory.await(),
                getAllExpenseAverage.await()
            )

        }

    }
    fun refreshData() {
        refreshAllDataFromDb()
    }
    private suspend fun updataDeta(
        await: Category?,
        await1: List<Category>,
        await2: Long?,
        await3: Category?,
        await4: String?,
        await5: Double?
    ) {
        withContext(Dispatchers.Main) {
            getCategoryWithMaxTransactionNumber.value = await
            getCategoriesSortedByMoney.value = await1
            getAllTotalExpense.value = await2
            getCategoryWithMaxTransactionNumber.value = await3
            getAllTopExpenseCategory.value = await4
            getAllExpenseAverage.value = await5
        }

    }


    suspend fun updatacategory(category: Category) {

        analysisRepository.updatatcategory(category)

    }

    suspend fun getTotalExpenseBetween(s: Long, e: Long): Long? {
        return analysisRepository.getTotalExpenseBetween(s, e)

    }

    suspend fun getTodayAverageAmount(s: Long, e: Long): Double? {
        return analysisRepository.getTodayAverageAmount(s, e)

    }

    suspend fun getAllExpenseByCategory(catrgoryname: String): Long? {
        return analysisRepository.getAllExpenseByCategory(catrgoryname)

    }

    suspend fun getTodayTransactionCount(s: Long, e: Long): Int {
        return analysisRepository.getTodayTransactionCount(s, e)

    }

    suspend fun getTodayTopExpenseCategory(s: Long, e: Long): String? {
        return analysisRepository.getTodayTopExpenseCategory(s, e)

    }

    suspend fun getTodayExpenseByCategory(catrgoryname: String, s: Long, e: Long): Long? {
        return analysisRepository.getTodayExpenseByCategory(catrgoryname, s, e)

    }

    suspend fun getTodayExpenseCategoryWithMaxTransactionNumber(s: Long, e: Long): Category? {
        return analysisRepository.getTodayExpenseCategoryWithMaxTransactionNumber(s, e)

    }
    suspend fun getTodayTotalExpense(s: Long, e: Long): Long? {
        return analysisRepository.getTodayTotalExpense(s, e)

    }
    suspend fun getExpenseTransactionsBetween(s: Long, e: Long): List<Transaction> {
        return analysisRepository.getExpenseTransactionsBetween(s, e)

    }
    suspend fun getTopExpenseCategoryBetween(s: Long, e: Long): CategoryAmount? {
        return analysisRepository.getTopExpenseCategoryBetween(s, e)

    }
    suspend fun getLowExpenseCategoryBetween(s: Long, e: Long): CategoryAmount? {
        return analysisRepository.getLowExpenseCategoryBetween(s, e)

    }

}