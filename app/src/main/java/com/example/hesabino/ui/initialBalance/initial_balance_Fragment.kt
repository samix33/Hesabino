package com.example.hesabino.ui.initialBalance

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.hesabino.R
import com.example.hesabino.databinding.FragmentInitialBalanceBinding
import com.example.hesabino.di.FragmentNavigator
import com.example.hesabino.di.MoneyTextWatcher
import com.example.hesabino.di.getTodayPersianDate
import com.example.hesabino.model.data.Category
import com.example.hesabino.model.data.Money
import com.example.hesabino.model.data.Transaction

import com.example.hesabino.ui.home.home_Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel

import kotlin.getValue

class initial_balance_Fragment : Fragment() {
    lateinit var binding: FragmentInitialBalanceBinding
    private val viewModel: InitialBalanceViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInitialBalanceBinding.inflate(layoutInflater)

        binding.etInitialBalance.addTextChangedListener(
            MoneyTextWatcher(binding.etInitialBalance)
        )

        val categories = listOf(
            Category(1, "🍔 غذا", mony = "0", transaction_nummber = 0, budgetLimit = 0, "expense"),
            Category(
                2, "🚕 رفت و آمد", mony = "0", transaction_nummber = 0, budgetLimit = 0, "expense"
            ),
            Category(
                3, "💸 انتقال وجه", mony = "0", transaction_nummber = 0, budgetLimit = 0, "expense"
            ),
            Category(
                4, "📱 شارژ و قبض", mony = "0", transaction_nummber = 0, budgetLimit = 0, "expense"
            ),
            Category(5, "👕 پوشاک", mony = "0", transaction_nummber = 0, budgetLimit = 0, "expense"),
            Category(6, "🛒 مارکت", mony = "0", transaction_nummber = 0, budgetLimit = 0, "expense"),
            Category(
                7, "🏦 اقساط و وام", mony = "0", transaction_nummber = 0, budgetLimit = 0, "expense"
            ),
            Category(
                8,
                "☕ کافه و رستوران",
                mony = "0",
                transaction_nummber = 0,
                budgetLimit = 0,
                "expense"
            ),
            Category(
                10, "🎮 سرگرمی", mony = "0", transaction_nummber = 0, budgetLimit = 0, "expense"
            ),
            Category(
                12,
                "💊 درمان و سلامت ",
                mony = "0",
                transaction_nummber = 0,
                budgetLimit = 0,
                "expense"
            ),
            Category(
                13,
                "🏠 خانه و وسایل ",
                mony = "0",
                transaction_nummber = 0,
                budgetLimit = 0,
                "expense"
            ),
            Category(
                14,
                "🎁 هدیه و مناسب ",
                mony = "0",
                transaction_nummber = 0,
                budgetLimit = 0,
                "expense"
            ),
            Category(
                15,
                "📌 سایر هزینه‌ها ",
                mony = "0",
                transaction_nummber = 0,
                budgetLimit = 0,
                "expense"
            ),

            Category(9, "💰 درآمد", mony = "0", transaction_nummber = 0, budgetLimit = 0, "income"),
            Category(
                16,
                "💼 حقوق و دستمزد",
                mony = "0",
                transaction_nummber = 0,
                budgetLimit = 0,
                "income"
            ),
            Category(
                17,
                "🧑‍💻 درآمد آزادکاری",
                mony = "0",
                transaction_nummber = 0,
                budgetLimit = 0,
                "income"
            ),
            Category(
                18, "🛍️ فروش کالا", mony = "0", transaction_nummber = 0, budgetLimit = 0, "income"
            ),
            Category(
                19, "🎁 هدیه دریافتی", mony = "0", transaction_nummber = 0, budgetLimit = 0, "income"
            ),
            Category(
                20,
                "📈 سود سرمایه‌گذاری",
                mony = "0",
                transaction_nummber = 0,
                budgetLimit = 0,
                "income"
            ),
            Category(
                21,
                "🏘️ اجاره دریافتی",
                mony = "0",
                transaction_nummber = 0,
                budgetLimit = 0,
                "income"
            ),
            Category(
                22, "↩️ بازگشت پول", mony = "0", transaction_nummber = 0, budgetLimit = 0, "income"
            ),
            Category(
                23, "🏦 وام دریافتی", mony = "0", transaction_nummber = 0, budgetLimit = 0, "income"
            ),
            Category(
                24,
                "🎖️ پاداش و اضافه‌کار",
                mony = "0",
                transaction_nummber = 0,
                budgetLimit = 0,
                "income"
            ),
            Category(
                25, "💰 سایر درآمدها", mony = "0", transaction_nummber = 0, budgetLimit = 0, "income"
            )

        )

        binding.btnStart.setOnClickListener {

            viewModel.insertAllCategory(categories)

            val textinput = binding.etInitialBalance.text.toString()
            if (textinput.isEmpty()) Toast.makeText(
                binding.root.context,
                "لطفا مبلغ رو مشخص کنید!",
                Toast.LENGTH_SHORT
            ).show()
            else {
                insertdata(textinput, true, viewModel)
                val shared = requireActivity().getSharedPreferences(
                    "transaction", Context.MODE_PRIVATE
                )

                val result = shared.edit().putBoolean("first run", false).commit()

                val valueAfterSave = shared.getBoolean("first run", true)

                Log.d("PREF_TEST", "saved = $result")
                Log.d("PREF_TEST", "Fragment first run after save = $valueAfterSave")

            }
            FragmentNavigator.replace(
                parentFragmentManager, R.id.rootgragment, false, home_Fragment()
            )

        }
        binding.btnSkip.setOnClickListener {
            val shared = requireActivity().getSharedPreferences(
                "transaction", Context.MODE_PRIVATE
            )

            val result = shared.edit().putBoolean("first run", false).commit()

            val valueAfterSave = shared.getBoolean("first run", true)

            Log.d("PREF_TEST", "saved = $result")
            Log.d("PREF_TEST", "Fragment first run after save = $valueAfterSave")

            viewModel.insertAllCategory(categories)
            insertdata("0", false, viewModel)
            FragmentNavigator.replace(
                parentFragmentManager, R.id.rootgragment, false, home_Fragment()
            )
        }
        return binding.root

    }


    fun getTodayPersianDatePretty(): String {
        val date = getTodayPersianDate()

        val monthNames = listOf(
            "فروردین",
            "اردیبهشت",
            "خرداد",
            "تیر",
            "مرداد",
            "شهریور",
            "مهر",
            "آبان",
            "آذر",
            "دی",
            "بهمن",
            "اسفند"
        )

        return "${date.day} ${monthNames[date.month - 1]} ${date.year}"
    }

    fun insertdata(
        textinput: String, ischeck: Boolean, viewModel: InitialBalanceViewModel
    ) {
        val formattext = textinput.replace(".", "").toLongOrNull() ?: 0L
        if (ischeck) {
            val transaction = Transaction(

                balance = textinput,
                Category = "💰 درآمد",
                detail = "واریز موجودی اولیه",
                date = getTodayPersianDatePretty(),
                deposit = true,
                createdAt = System.currentTimeMillis()
            )
            viewModel.inserttransaction(transaction)

        }


        val category = Category(
            id = 9,
            name = "💰 درآمد",
            mony = formattext.toString(),
            transaction_nummber = 1,
            budgetLimit = 0,
            type = "income"
        )
        viewModel.updataCategory(category)


        val money = Money(
            Balance = formattext.toString(),
            Income = formattext.toString(),
            Expense = "0",
            transaction_nummber = 1,
        )
        viewModel.insertMony(money)
    }
}