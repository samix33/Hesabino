package com.example.hesabino.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hesabino.R
import com.example.hesabino.databinding.HomeFragmentBinding
import com.example.hesabino.di.FragmentNavigator
import com.example.hesabino.model.adapter.TransactionAdapter
import com.example.hesabino.model.data.Category
import com.example.hesabino.model.data.Money
import com.example.hesabino.model.data.Transaction
import com.example.hesabino.ui.analysis.analysis_Fragment
import com.example.hesabino.ui.transaction.transaction_Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel


class home_Fragment : Fragment(), TransactionAdapter.TransactioeEvint {
    lateinit var binding: HomeFragmentBinding
    lateinit var adapter: TransactionAdapter
    lateinit var mony : Money


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = HomeFragmentBinding.inflate(layoutInflater)
        val viewModel: HomeViewModel  by viewModel()
        viewModel.transactionData.observe(viewLifecycleOwner) {
            adapter = TransactionAdapter(it as ArrayList, this,viewModel)
        }


        showdata(viewModel)




        binding.fabAdd.setOnClickListener {
            val transactionFragment = transaction_Fragment()
            transactionFragment.show(parentFragmentManager, null)

        }
        binding.tvAnalyticsAction.setOnClickListener {
            viewModel.monyData.observe(viewLifecycleOwner){
                if (it.Balance == "0"){
                    showAddTransactionDialog()

                }else{
                    FragmentNavigator.replace(
                        parentFragmentManager,
                        R.id.rootgragment,
                        true,
                        analysis_Fragment(),


                        )
                }
            }


        }


        return binding.root

    }

    private fun showAddTransactionDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("تراکنشی وجود ندارد")
            .setMessage("اول یک تراکنش ثبت کن.")
            .setPositiveButton("باشه") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    @SuppressLint("SetTextI18n")
    private fun showdata(viewModel: HomeViewModel) {
        viewModel.monyData.observe(viewLifecycleOwner){
            binding.tvBalance.text = formatMoney(it.Balance.toLong()) + " تومان"
            binding.tvIncome.text = formatMoney(it.Income.toLong()) + " تومان"
            binding.tvExpense.text = formatMoney(it.Expense.toLong()) + " تومان"



            binding.rvTransactions.adapter = adapter
            binding.rvTransactions.layoutManager =
                LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, true)
        }





    }

    private fun String.toPersianDigits(): String {
        val englishDigits = arrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

        var result = this
        for (i in englishDigits.indices) {
            result = result.replace(englishDigits[i], persianDigits[i])
        }
        return result
    }

    @SuppressLint("DefaultLocale")
    private fun formatMoney(number: Long): String {
        return String.format("%,d", number)

            .replace(",", ".")
            .toPersianDigits()
    }

    override fun onTransactionClicked() {

    }

    @SuppressLint("SetTextI18n")
    override fun onTransactionLongClicked(transaction: Transaction, viewModel: ViewModel) {
        val viewModel: HomeViewModel  by viewModel()

        viewModel.monyData.observe(viewLifecycleOwner){
            mony = it

        }

        var newIncome: Int = mony.Income.toInt()
        var newExpense: Int = mony.Expense.toInt()
        var newbalance: Int = mony.Balance.toInt()
        val view = layoutInflater.inflate(R.layout.bottom_sheet_delete_transaction, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(view)

        val tvInfo = view.findViewById<TextView>(R.id.tvTransactionInfo)
        val btnDelete = view.findViewById<MaterialButton>(R.id.btnDeleteTransaction)
        val btnCancel = view.findViewById<MaterialButton>(R.id.btnCancelDelete)

        tvInfo.text = "${transaction.Category} • ${transaction.balance.toPersianDigits()} تومان"

        btnDelete.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.deletdTransaction(transaction)

                dialog.dismiss()
            }

            val formatbalance = transaction.balance
                .replace(".", "")
                .toLongOrNull() ?: 0L

            if (transaction.deposit) {
                newIncome = mony.Income.toInt() - formatbalance.toInt()
                newbalance = mony.Balance.toInt() - formatbalance.toInt()
            } else {
                newExpense = mony.Expense.toInt() - formatbalance.toInt()
                newbalance = mony.Balance.toInt() + formatbalance.toInt()
            }
            val mony = Money(
                id = 1,
                Balance = newbalance.toString(),
                Income = newIncome.toString(),
                Expense = newExpense.toString(),
                transaction_nummber = mony.transaction_nummber - 1

            )
            viewModel.updataMony(mony)
            viewLifecycleOwner.lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val categoryy =viewModel.getCategoryByName(transaction)
                    val newemony = categoryy.mony.toInt() - formatbalance.toInt()
                    val category = Category(
                        id = categoryy.id,
                        name = transaction.Category,
                        mony = newemony.toString(),

                        transaction_nummber = viewModel.getTransactionById(categoryy.id).transaction_nummber - 1,
                        budgetLimit = viewModel.getBudgetLimit(categoryy.id),
                        type = categoryy.type
                    )
                    viewModel.updataCaregory(category)
                }


            }

            FragmentNavigator.replace(
                parentFragmentManager,
                R.id.rootgragment,
                false,
                home_Fragment(),
                withAnimation = false
            )
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()
    }


}