package com.example.hesabino.di

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.example.hesabino.model.db.Mydatabase
import com.example.hesabino.model.repository.home.HomeRepository
import com.example.hesabino.model.repository.home.HomeRepositoryImpl
import com.example.hesabino.model.repository.initialBalance.initialBalanceRepository
import com.example.hesabino.model.repository.initialBalance.initialBalanceRepositoryImpl
import com.example.hesabino.model.repository.transaction.analysisRepository
import com.example.hesabino.model.repository.transaction.analysisRepositoryImpl
import com.example.hesabino.model.repository.transaction.transactionRepository
import com.example.hesabino.model.repository.transaction.transactionRepositoryImpl
import com.example.hesabino.ui.analysis.AnalysisViewModel
import com.example.hesabino.ui.home.HomeViewModel
import com.example.hesabino.ui.initialBalance.InitialBalanceViewModel
import com.example.hesabino.ui.transaction.TransactionViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import kotlin.jvm.java

class Myapp : Application() {
    companion object{
        var t1 = false
        var scrollY = 0
        var scrollY2 = 0

    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        val myModuled = module {
            single {
                Room.databaseBuilder(
                    androidContext(),
                    Mydatabase::class.java,
                    "myDatabase.db"
                ).build()
            }
            single<HomeRepository> { HomeRepositoryImpl(
                get<Mydatabase>().moneyDao,
                get<Mydatabase>().categoryDao,
                get<Mydatabase>().transactionDao,
            ) }

            single<initialBalanceRepository> { initialBalanceRepositoryImpl(
                get<Mydatabase>().moneyDao,
                get<Mydatabase>().categoryDao,
                get<Mydatabase>().transactionDao,
            ) }
            single <transactionRepository>{ transactionRepositoryImpl(
                get<Mydatabase>().moneyDao,
                get<Mydatabase>().categoryDao,
                get<Mydatabase>().transactionDao,
            ) }



            single <analysisRepository>{ analysisRepositoryImpl(
                get<Mydatabase>().moneyDao,
                get<Mydatabase>().categoryDao,
                get<Mydatabase>().transactionDao,
            ) }
            viewModel { HomeViewModel(get()) }
            viewModel { InitialBalanceViewModel(get()) }
            viewModel { TransactionViewModel(get()) }
            viewModel { AnalysisViewModel(get()) }

        }
        startKoin {
            androidContext(this@Myapp)
            modules(myModuled)
        }
    }


}