package com.example.hesabino.ui.transaction


import android.app.Dialog

import android.os.Bundle
import android.util.Log

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getColor
import androidx.core.graphics.toColorInt

import com.example.hesabino.R



import androidx.lifecycle.lifecycleScope

import com.example.hesabino.di.FragmentNavigator
import com.example.hesabino.di.MoneyTextWatcher
import com.example.hesabino.model.data.Category
import com.example.hesabino.model.data.Money
import com.example.hesabino.model.data.Transaction
import com.example.hesabino.databinding.FragmentTransactionBinding
import com.example.hesabino.di.getTodayPersianDate

import com.example.hesabino.ui.home.home_Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.System
import kotlin.properties.Delegates


class transaction_Fragment : androidx.fragment.app.DialogFragment() {
    lateinit var binding: FragmentTransactionBinding
    lateinit var category: String
    lateinit var categorytype: String
    var categoryid by Delegates.notNull<Int>()
    var ischek by Delegates.notNull<Boolean>()
    var deposit by Delegates.notNull<Boolean>()
    var newIncome by Delegates.notNull<Int>()
    var newExpense by Delegates.notNull<Int>()
    var newbalance by Delegates.notNull<Int>()
     var objeckt : List<String> = listOf()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentTransactionBinding.inflate(layoutInflater, null, false)
        val dialog = AlertDialog.Builder(binding.root.context)
        dialog.setView(binding.root)
        val viewModel: TransactionViewModel by viewModel()

        viewModel.Income.observe(this){

            objeckt = it.map { it.name }
            Log.v("hhgc","dds : " +objeckt)
            val  adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                objeckt
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spCategory.adapter = adapter
        }

        deposit = true
        ischek = true
        binding.btnIncome.setBackgroundColor(getColor(binding.root.context, R.color.green))




        binding.etAmount.addTextChangedListener(MoneyTextWatcher(binding.etAmount))


        binding.btnIncome.setOnClickListener {

            binding.btnIncome.setBackgroundColor(getColor(binding.root.context, R.color.green))
            binding.btnExpense.setBackgroundColor("#FEE2E2".toColorInt())
            deposit = true
            if (deposit){
                viewModel.Income.observe(this){
                    objeckt = it.map { it.name }
                }
            }else{
                viewModel.Expense.observe(this){
                    objeckt = it.map { it.name }
                }
            }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                objeckt
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spCategory.adapter = adapter
        }
        binding.btnExpense.setOnClickListener {

            binding.btnExpense.setBackgroundColor(getColor(binding.root.context, R.color.red))
            binding.btnIncome.setBackgroundColor("#444CAF50".toColorInt())
            deposit = false
            if (deposit){
                viewModel.Income.observe(this){
                    objeckt = it.map { it.name }
                }
            }else{
                viewModel.Expense.observe(this){
                    objeckt = it.map { it.name }
                }
            }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                objeckt
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spCategory.adapter = adapter
        }





        binding.spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (deposit){
                    viewModel.Income.observe(this@transaction_Fragment){
                        val selectedItem = it[position]
                        category = selectedItem.name
                        categoryid = selectedItem.id
                        categorytype  =selectedItem.type
                    }


                }else{
                    viewModel.Expense.observe(this@transaction_Fragment){
                        val selectedItem = it[position]
                        category = selectedItem.name
                        categoryid = selectedItem.id
                        categorytype  =selectedItem.type
                    }
                }


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.tvSelectedDate.text = getTodayPersianDatePretty()
        binding.btnIncome.isSelected = true
        binding.btnSave.setOnClickListener {
            viewModel.Mony.observe(this){
                if (binding.etAmount.text!!.isEmpty()  || !ischek) {

                    Toast.makeText(binding.root.context, "تمام فیلد هارو پر کنید", Toast.LENGTH_SHORT)
                        .show()
                }else if(!deposit) {

                    if ( binding.etAmount.text.toString().replace(".", "").toInt() > it.Balance.toInt()){
                        showSuccessSnack("موجودی کافی نمیباشد")
                    }else{
                        addtransaction(viewModel)
                    }

                } else {

                    addtransaction(viewModel)
                }
            }



        }

        return dialog.create()
    }



    fun getTodayPersianDatePretty(): String {
        val date = getTodayPersianDate()

        val monthNames = listOf(
            "فروردین", "اردیبهشت", "خرداد",
            "تیر", "مرداد", "شهریور",
            "مهر", "آبان", "آذر",
            "دی", "بهمن", "اسفند"
        )

        return "${date.day} ${monthNames[date.month - 1]} ${date.year}"
    }
    private fun showSuccessSnack(message: String) {
        val snackbar = Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_SHORT
        )

        snackbar.setBackgroundTint(getColor(requireContext(), R.color.red))
        snackbar.setTextColor(getColor(requireContext(), R.color.white))

        snackbar.show()
    }
    private fun addtransaction(

    viewModel: TransactionViewModel
        ) {
        val transaction = Transaction(
            balance = binding.etAmount.text.toString(),
            Category = category,
            detail = binding.etNote.text.toString(),
            deposit = deposit,
            date = getTodayPersianDatePretty(),
            createdAt = System.currentTimeMillis()

        )
        viewModel.insertTransaction(transaction)
        viewModel.Mony.observe(this) { moneyData ->
            // 1. بررسی ایمن مقادیر UI
            val rawValue = binding.etAmount.text.toString().replace(".", "")
            val rawValueOne = moneyData.Balance.replace(".", "")
            val rawValueTwo = moneyData.Income.replace(".", "")
            val rawValueTree = moneyData.Expense.replace(".", "")

            val newbalancee = rawValue.toLongOrNull() ?: 0L
            val oldbalancee = rawValueOne.toLongOrNull() ?: 0L
            val newIncomee = rawValueTwo.toLongOrNull() ?: 0L
            val newExpensee = rawValueTree.toLongOrNull() ?: 0L

            // متغیرهای شما (فرضی بر اینکه بیرون تعریف شده‌اند)
            if (deposit) {
                newbalance = newbalancee.toInt() + oldbalancee.toInt()
                newIncome = newIncomee.toInt() + newbalancee.toInt()
                newExpense = newExpensee.toInt()
            } else {
                newbalance = oldbalancee.toInt() - newbalancee.toInt()
                newExpense = newExpensee.toInt() + newbalancee.toInt()
                newIncome = newIncomee.toInt()
            }

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    // استفاده امن از داده‌های دیتابیس بدون زورگویی (!! )
                    val currentTransaction = viewModel.getTransactionById(id = categoryid)
                    val currentBudgetLimit = viewModel.getBudgetLimit(categoryid)

                    // اگر رکورد پیدا نشد، ادامه نده تا اپلیکیشن فریز نشود
                    if (currentTransaction == null || currentBudgetLimit == null) {
                        Log.e("tagx", "Transaction or BudgetLimit is null, cannot update!")
                        return@withContext
                    }

                    val rawValueFive = currentTransaction.mony.replace(".", "")
                    val oldMony = rawValueFive.toLongOrNull() ?: 0L
                    val categoryMoney = newbalancee.toInt() + oldMony.toInt()

                    val category = Category(
                        id = categoryid,
                        name = category, // متغیر سراسری یا اکتیویتی
                        mony = categoryMoney.toString(),
                        transaction_nummber = currentTransaction.transaction_nummber + 1,
                        budgetLimit = currentBudgetLimit,
                        type = categorytype
                    )

                    Log.v("tagx", "category : $category")
                    viewModel.updatatcategory(category)
                }
            }
        }


        viewModel.Mony.observe(this){
            val mony = Money(
                id = 1,
                Balance = newbalance.toString(),
                Income = newIncome.toString(),
                Expense = newExpense.toString(),
                transaction_nummber = it.transaction_nummber + 1

            )
            viewModel.updatatMany(mony)
            FragmentNavigator.replace(
                parentFragmentManager,
                R.id.rootgragment,
                false,
                home_Fragment(),
                withAnimation = false
            )
        }


        dismiss()

    }

}