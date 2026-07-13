package com.example.hesabino.ui.analysis

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hesabino.di.MoneyTextWatcher
import com.example.hesabino.R
import com.example.hesabino.model.adapter.CategoryAdapter
import com.example.hesabino.model.db.dao.CategoryDao
import com.example.hesabino.model.db.dao.TransactionDao
import com.example.hesabino.model.data.Category
import com.example.hesabino.model.data.calculatePercentChange
import com.example.hesabino.model.data.getDaysPassedFromStart
import com.example.hesabino.model.data.getLastPersianMonthRange
import com.example.hesabino.model.data.getLastWeekRange
import com.example.hesabino.model.data.getPersianWeekDayName
import com.example.hesabino.model.data.getThisPersianMonthRange
import com.example.hesabino.model.data.getThisWeekRange
import com.example.hesabino.model.data.getTodayRange
import com.example.hesabino.model.data.getYesterdayRange
import com.example.hesabino.databinding.FragmentAnalysisBinding
import com.example.hesabino.di.FragmentNavigator
import com.example.hesabino.di.Myapp
import com.example.hesabino.model.data.TodayDashboardResult
import com.example.hesabino.ui.home.HomeViewModel
import com.example.hesabino.ui.home.home_Fragment
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs
import kotlin.properties.Delegates


@Suppress("IMPLICIT_CAST_TO_ANY")
class analysis_Fragment : Fragment(), CategoryAdapter.CategoryEvents {

    lateinit var binding: FragmentAnalysisBinding
    lateinit var adapter: CategoryAdapter

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAnalysisBinding.inflate(layoutInflater)
        val viewModel: AnalysisViewModel by viewModel()
        viewModel.getCategoriesSortedByMoney.observe(viewLifecycleOwner) {
            Log.v("tags", "t : $it")
            adapter = CategoryAdapter(it.filter { !it.name.contains("درآمد") }, this, viewModel)
            binding.rvCategoryAnalytics.adapter = adapter
            binding.rvCategoryAnalytics.layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
        }
        showHomeTutorialSequence()

        loadMonthlyReport(viewModel)

        viewModel.getCategoryWithMaxTransactionNumber.observeForever {
            val cleanAmount = if (it!!.mony!!.contains(".")) {
                it.mony!!.replace(".", "")
            } else {
                it.mony
            }
            showdata(cleanAmount, viewModel)
        }



        loadDayData(viewModel)
        loadSpendingBehaviorCard(viewModel)

        binding.tvReportDailyAverage


        return binding.root

    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun showdata(
        cleanAmount: String?,
        viewModel: AnalysisViewModel
    ) {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { group, checkedIds ->

            for (i in 0 until group.childCount) {
                val chip = group.getChildAt(i) as Chip

                if (checkedIds.contains(chip.id)) {
                    chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    chip.chipBackgroundColor =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.chip_selected_bg
                            )
                        )
                } else {
                    chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                    chip.chipBackgroundColor =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white
                            )
                        )
                }
            }
            if (checkedIds.isNotEmpty()) {
                val selectedChipId = checkedIds[0]




                when (selectedChipId) {
                    R.id.chipToday -> {
                        if (cleanAmount != null) {
                            loadDayData(viewModel)
                        }
                    }

                    R.id.chipWeek -> {
                        loadWeekData(viewModel)
                    }

                    R.id.chipMonth -> {
                        loadMonthData(viewModel)
                    }

                    R.id.chipAll -> {
                        // همه انتخاب شد
                        loadAllData(viewModel)
                    }
                }
            }
        }
        viewModel.getCategoriesSortedByMoney.observeForever {

            val filteredList = it.filter {
                !it.type.contains("income")
            }
            val adapter = CategoryAdapter(filteredList, this, viewModel)
            binding.rvCategoryAnalytics.adapter = adapter
            binding.rvCategoryAnalytics.layoutManager =
                LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
        }


    }

    @SuppressLint("DefaultLocale")
    fun formatMoney(number: Long): String {
        return String.format("%,d", number)
            .replace(",", ".")
            .toPersianDigits()
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun onClick(data: Category, viewModel: AnalysisViewModel) {

        val view = layoutInflater.inflate(R.layout.bottom_sheet_budget_limit, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(view)

        val tvCategoryName = view.findViewById<TextView>(R.id.tvCategoryName)
        val etBudgetLimit = view.findViewById<TextInputEditText>(R.id.etBudgetLimit)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSaveBudget)
        val btnCancel = view.findViewById<MaterialButton>(R.id.btnCancel)
        val tvCurrentStatus = view.findViewById<TextView>(R.id.tvCurrentStatus)
        val changemony = data.mony
            .replace(".", "")
            .toLongOrNull() ?: 0L
        val formatmony = formatMoney(changemony)
        tvCurrentStatus.text = "تا الان $formatmony تومان از این دسته خرج شده"

        tvCategoryName.text = "دسته‌بندی: ${data.name}"
        etBudgetLimit.addTextChangedListener(
            MoneyTextWatcher(etBudgetLimit)
        )
        btnSave.setOnClickListener {

            val limit = etBudgetLimit.text.toString()
                .replace(".", "")
                .toLongOrNull() ?: 0L
            val category = Category(
                data.id,
                data.name,
                data.mony,
                data.transaction_nummber,
                limit,
                type = data.type
            )
            viewLifecycleOwner.lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    viewModel.updatacategory(category)
                    viewModel.refreshData()
                }
            }

            val layoutManager = binding.rvCategoryAnalytics.layoutManager as LinearLayoutManager
            val position = layoutManager.findFirstVisibleItemPosition()
            Myapp.scrollY = position

            viewModel.getCategoriesSortedByMoney.observe(viewLifecycleOwner) {

                adapter.updateData(it.filter { !it.name.contains("درآمد") })

            }






            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun loadDayData(viewModel: AnalysisViewModel) {
        binding.tvtotal.text = "مجموع هزینه امروز"
        val (todayStart, todayEnd) = getTodayRange()
        val (yesterdayStart, yesterdayEnd) = getYesterdayRange()
        lifecycleScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                val todayExpense = viewModel.getTotalExpenseBetween(todayStart, todayEnd) ?: 0L

                val yesterdayExpense =
                    viewModel.getTotalExpenseBetween(yesterdayStart, yesterdayEnd) ?: 0L

                val diff = todayExpense - yesterdayExpense

                val percent = if (yesterdayExpense > 0) {
                    ((diff.toDouble() / yesterdayExpense.toDouble()) * 100).toInt()
                } else {
                    0
                }

                val resultText = when {
                    yesterdayExpense == 0L && todayExpense > 0L -> {
                        "امروز هزینه داشتی، اما دیروز هزینه‌ای ثبت نشده بود."
                    }

                    yesterdayExpense == 0L && todayExpense == 0L -> {
                        "امروز و دیروز هزینه‌ای ثبت نشده."
                    }

                    percent > 0 -> {
                        "امروز ${percent.toString().toPersianDigits()}٪ بیشتر از دیروز خرج کردی."
                    }

                    percent < 0 -> {
                        "امروز ${
                            abs(percent).toString().toPersianDigits()
                        }٪ کمتر از دیروز خرج کردی."
                    }

                    else -> {
                        "هزینه امروز و دیروز برابر بوده."
                    }
                }

                val (start, end) = getTodayRange()

                val avg = viewModel.getTodayAverageAmount(start, end) ?: 0.0
                val formatted = String.format("%,d", avg.toLong()).replace(",", ".")

                Triple(resultText, "میانگین امروز: $formatted", Pair(start, end))
            }

            binding.tvComparedToLastMonth.text = result.first
            binding.tvAverageDaily.text = result.second

            val start = result.third.first
            val end = result.third.second

            loadData(start, end, viewModel)
        }


    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun loadWeekData(viewModel: AnalysisViewModel) {
        binding.tvtotal.text = "مجموع هزینه این هفته"
        viewLifecycleOwner.lifecycleScope.launch {

            val result = withContext(Dispatchers.IO) {
                val (start, end) = getThisWeekRange()

                val avg = viewModel.getTodayAverageAmount(start, end) ?: 0.0
                val formatted = String.format("%,d", avg.toLong()).replace(",", ".")

                val averageText = "میانگین این هفته: $formatted"

                val (thisWeekStart, thisWeekEnd) = getThisWeekRange()
                val (lastWeekStart, lastWeekEnd) = getLastWeekRange()

                val thisWeekExpense = viewModel.getTotalExpenseBetween(
                    thisWeekStart,
                    thisWeekEnd
                ) ?: 0L

                val lastWeekExpense = viewModel.getTotalExpenseBetween(
                    lastWeekStart,
                    lastWeekEnd
                ) ?: 0L

                val diff = thisWeekExpense - lastWeekExpense

                val percent = if (lastWeekExpense > 0) {
                    ((diff.toDouble() / lastWeekExpense.toDouble()) * 100).toInt()
                } else {
                    0
                }

                val resultText = when {
                    lastWeekExpense == 0L && thisWeekExpense > 0L -> {
                        "این هفته هزینه داشتی، اما هفته قبل هزینه‌ای ثبت نشده بود."
                    }

                    lastWeekExpense == 0L && thisWeekExpense == 0L -> {
                        "این هفته و هفته قبل هزینه‌ای ثبت نشده."
                    }

                    percent > 0 -> {
                        "این هفته ${
                            percent.toString().toPersianDigits()
                        }٪ بیشتر از هفته قبل خرج کردی."
                    }

                    percent < 0 -> {
                        "این هفته ${
                            abs(percent).toString().toPersianDigits()
                        }٪ کمتر از هفته قبل خرج کردی."
                    }

                    else -> {
                        "هزینه این هفته و هفته قبل برابر بوده."
                    }
                }

                Triple(
                    averageText,
                    resultText,
                    Pair(start, end)
                )
            }

            // از اینجا به بعد روی Main Thread هستیم
            binding.tvAverageDaily.text = result.first
            binding.tvComparedToLastMonth.text = result.second

            val start = result.third.first
            val end = result.third.second

            loadData(start, end, viewModel)
        }


    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun loadMonthData(viewModel: AnalysisViewModel) {

        binding.tvtotal.text = "مجموع هزینه این ماه"
        viewLifecycleOwner.lifecycleScope.launch {

            val result = withContext(Dispatchers.IO) {
                val (start, end) = getThisPersianMonthRange()

                val avg = viewModel.getTodayAverageAmount(start, end) ?: 0.0
                val formatted = String.format("%,d", avg.toLong()).replace(",", ".")

                val averageText = "میانگین این ماه: $formatted"

                val (thisMonthStart, thisMonthEnd) = getThisPersianMonthRange()
                val (lastMonthStart, lastMonthEnd) = getLastPersianMonthRange()

                val thisMonthExpense = viewModel.getTotalExpenseBetween(
                    thisMonthStart,
                    thisMonthEnd
                ) ?: 0L

                val lastMonthExpense = viewModel.getTotalExpenseBetween(
                    lastMonthStart,
                    lastMonthEnd
                ) ?: 0L

                val diff = thisMonthExpense - lastMonthExpense

                val percent = if (lastMonthExpense > 0) {
                    ((diff.toDouble() / lastMonthExpense.toDouble()) * 100).toInt()
                } else {
                    0
                }

                val resultText = when {
                    lastMonthExpense == 0L && thisMonthExpense > 0L -> {
                        "این ماه هزینه داشتی، اما ماه قبل هزینه‌ای ثبت نشده بود."
                    }

                    lastMonthExpense == 0L && thisMonthExpense == 0L -> {
                        "این ماه و ماه قبل هزینه‌ای ثبت نشده."
                    }

                    percent > 0 -> {
                        "این ماه ${
                            percent.toString().toPersianDigits()
                        }٪ بیشتر از ماه قبل خرج کردی."
                    }

                    percent < 0 -> {
                        "این ماه ${
                            abs(percent).toString().toPersianDigits()
                        }٪ کمتر از ماه قبل خرج کردی."
                    }

                    else -> {
                        "هزینه این ماه و ماه قبل برابر بوده."
                    }
                }

                Triple(
                    averageText,
                    resultText,
                    Pair(start, end)
                )
            }

            // از اینجا به بعد روی Main Thread هستیم
            binding.tvAverageDaily.text = result.first
            binding.tvComparedToLastMonth.text = result.second

            val start = result.third.first
            val end = result.third.second

            loadData(start, end, viewModel)
        }

    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun loadAllData(viewModel: AnalysisViewModel) {

        binding.tvtotal.text = "مجموع هزینه کل"
        viewModel.getAllTotalExpense.observe(viewLifecycleOwner) {
            val totalExpense = it ?: 0L
            binding.tvTotalExpense.text = "${formatMoney(totalExpense)} تومان"
        }

        viewModel.getAllTransactionCount.observe(viewLifecycleOwner) {
            val transactionCount = it ?: 0
            binding.tvTransactionsCount.text = transactionCount.toString().toPersianDigits()
        }

        var topCategoryName: String? = null
        var topCategoryAmount = 0L

        fun updateTopCategoryUi() {
            if (topCategoryName != null) {
                binding.tvTopCategory.text = topCategoryName
                binding.tvTopCategoryAmount.text = "${formatMoney(topCategoryAmount)} تومان"
            } else {
                binding.tvTopCategory.text = "تراکنشی ثبت نشده"
                binding.tvTopCategoryAmount.text = "۰ تومان"
            }
        }

        viewModel.getAllTopExpenseCategory.observe(viewLifecycleOwner) {
            topCategoryName = it
            updateTopCategoryUi()
        }

        viewModel.getAllExpenseByCategory.observe(viewLifecycleOwner) {
            topCategoryAmount = if (topCategoryName != null) {
                it ?: 0L
            } else {
                0L
            }

            updateTopCategoryUi()
        }

        viewModel.getAllExpenseAverage.observe(viewLifecycleOwner) {
            val avg = it ?: 0.0
            val formatted = String.format("%,d", avg.toLong()).replace(",", ".")
            binding.tvAverageDaily.text = "میانگین کل: $formatted"
        }

        binding.tvComparedToLastMonth.text = ""

    }

    @SuppressLint("SetTextI18n")
    private fun loadData(start: Long, end: Long, viewModel: AnalysisViewModel) {

        viewLifecycleOwner.lifecycleScope.launch {

            val result = withContext(Dispatchers.IO) {
                val totalTodayExpense = viewModel.getTodayTotalExpense(start, end) ?: 0L

                val transactionCount = viewModel.getTodayTransactionCount(start, end)

                val topCategory = viewModel.getTodayTopExpenseCategory(start, end) ?: "ندارد"

                val topCategoryWithMaxTransaction =
                    viewModel.getTodayExpenseCategoryWithMaxTransactionNumber(start, end)

                val topCategoryAmount = if (topCategoryWithMaxTransaction?.name != null) {
                    viewModel.getTodayExpenseByCategory(
                        topCategoryWithMaxTransaction.name,
                        start,
                        end
                    ) ?: 0L
                } else {
                    0L
                }

                TodayDashboardResult(
                    totalTodayExpense = totalTodayExpense,
                    transactionCount = transactionCount,
                    topCategory = topCategory,
                    topCategoryAmount = topCategoryAmount
                )
            }

            // از اینجا به بعد روی Main Thread هستیم
            binding.tvTotalExpense.text = "${formatMoney(result.totalTodayExpense)} تومان"
            binding.tvTransactionsCount.text =
                result.transactionCount.toString().toPersianDigits()
            binding.tvTopCategory.text = result.topCategory
            binding.tvTopCategoryAmount.text =
                "${formatMoney(result.topCategoryAmount)} تومان"
        }

    }

    @SuppressLint("SetTextI18n")
    private fun loadMonthlyReport(viewModel: AnalysisViewModel) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {

            val (thisMonthStart, thisMonthEnd) = getThisPersianMonthRange()
            val (lastMonthStart, lastMonthEnd) = getLastPersianMonthRange()

            val thisMonthExpense =
                viewModel.getTotalExpenseBetween(thisMonthStart, thisMonthEnd) ?: 0L

            val lastMonthExpense =
                viewModel.getTotalExpenseBetween(lastMonthStart, lastMonthEnd) ?: 0L

            val topCategory =
                viewModel.getTopExpenseCategoryBetween(thisMonthStart, thisMonthEnd)

            val lowCategory =
                viewModel.getLowExpenseCategoryBetween(thisMonthStart, thisMonthEnd)

            val percentChange = calculatePercentChange(thisMonthExpense, lastMonthExpense)

            val daysPassed = getDaysPassedFromStart(thisMonthStart)

            val dailyAverage = if (daysPassed > 0) {
                thisMonthExpense / daysPassed
            } else {
                0L
            }

            val savingSuggestion = if (topCategory != null) {
                topCategory.amount * 10 / 100
            } else {
                0L
            }

            withContext(Dispatchers.Main) {

                if (thisMonthExpense == 0L) {
                    showEmptyMonthlyReport()
                    return@withContext
                }

                val changeText = when {
                    lastMonthExpense == 0L -> {
                        "ماه قبل هزینه‌ای ثبت نشده بود"
                    }

                    percentChange > 0 -> {
                        "${percentChange.toString().toPersianDigits()}٪ افزایش"
                    }

                    percentChange < 0 -> {
                        "${abs(percentChange).toString().toPersianDigits()}٪ کاهش"
                    }

                    else -> {
                        "بدون تغییر"
                    }
                }

                val reportText = buildString {
                    append("بیشترین هزینه شما این ماه مربوط به دسته ")
                    append(topCategory?.name ?: "نامشخص")
                    append(" بوده. ")

                    if (lastMonthExpense > 0) {
                        if (percentChange > 0) {
                            append("هزینه‌ها نسبت به ماه قبل ")
                            append(abs(percentChange).toString().toPersianDigits())
                            append("٪ افزایش داشته. ")
                        } else if (percentChange < 0) {
                            append("هزینه‌ها نسبت به ماه قبل ")
                            append(abs(percentChange).toString().toPersianDigits())
                            append("٪ کاهش داشته. ")
                        } else {
                            append("هزینه‌ها تقریباً برابر با ماه قبل بوده. ")
                        }
                    } else {
                        append("برای ماه قبل هزینه‌ای ثبت نشده بود. ")
                    }

                    append("میانگین خرج روزانه شما ")
                    append(formatMoney(dailyAverage))
                    append(" تومان بوده.")
                }

                binding.tvMonthlyReportText.text = reportText

                binding.tvReportTopCategory.text = topCategory?.name ?: "ندارد"

                binding.tvReportMonthChange.text = changeText

                binding.tvReportMonthChange.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        when {
                            percentChange > 0 -> R.color.red
                            percentChange < 0 -> R.color.green
                            else -> R.color.gray
                        }
                    )
                )

                binding.tvReportDailyAverage.text =
                    "${formatMoney(dailyAverage)} تومان"

                binding.tvReportLowCategory.text =
                    lowCategory?.name ?: "ندارد"

                binding.tvMonthlySuggestion.text =
                    if (topCategory != null && savingSuggestion > 0) {
                        "پیشنهاد: اگر هزینه دسته ${topCategory.name} را ۱۰٪ کمتر کنی، این ماه حدود ${
                            formatMoney(
                                savingSuggestion
                            )
                        } تومان صرفه‌جویی می‌کنی."
                    } else {
                        "هنوز داده کافی برای پیشنهاد صرفه‌جویی وجود ندارد."
                    }
            }
        }
    }

    private fun showEmptyMonthlyReport() {
        binding.tvMonthlyReportText.text =
            "هنوز هزینه‌ای برای این ماه ثبت نشده. با ثبت تراکنش‌ها، گزارش ماهانه اینجا نمایش داده می‌شود."

        binding.tvReportTopCategory.text = "ندارد"
        binding.tvReportMonthChange.text = "۰٪"
        binding.tvReportMonthChange.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.gray)
        )

        binding.tvReportDailyAverage.text = "۰ تومان"
        binding.tvReportLowCategory.text = "ندارد"
        binding.tvMonthlySuggestion.text =
            "بعد از ثبت چند هزینه، پیشنهادهای هوشمند برای مدیریت بهتر خرج‌ها نمایش داده می‌شود."
    }

    @SuppressLint("SetTextI18n")
    private fun loadSpendingBehaviorCard(viewModel: AnalysisViewModel) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {

            val (thisMonthStart, thisMonthEnd) = getThisPersianMonthRange()
            val (lastMonthStart, lastMonthEnd) = getLastPersianMonthRange()

            val expenses = viewModel.getExpenseTransactionsBetween(
                thisMonthStart,
                thisMonthEnd
            )

            val thisMonthTotal = expenses.sumOf {
                parseMoney(it.balance)
            }

            val lastMonthTotal = viewModel.getTotalExpenseBetween(
                lastMonthStart,
                lastMonthEnd
            ) ?: 0L

            withContext(Dispatchers.Main) {

                if (expenses.isEmpty()) {
                    showEmptySpendingBehavior()
                    return@withContext
                }

                // 1) بیشترین روز خرج
                val daySpending = expenses
                    .groupBy { getPersianWeekDayName(it.createdAt) }
                    .mapValues { entry ->
                        entry.value.sumOf { parseMoney(it.balance) }
                    }

                val mostSpendingDay = daySpending.maxByOrNull { it.value }

                // 2) پرتکرارترین دسته
                val categoryCount = expenses
                    .groupingBy { it.Category }
                    .eachCount()

                val mostFrequentCategory = categoryCount.maxByOrNull { it.value }

                // مبلغ همان دسته پرتکرار
                val mostFrequentCategoryAmount = if (mostFrequentCategory != null) {
                    expenses
                        .filter { it.Category == mostFrequentCategory.key }
                        .sumOf { parseMoney(it.balance) }
                } else {
                    0L
                }

                // 4) هشدار رفتار خرج: مقایسه با ماه قبل
                val diff = thisMonthTotal - lastMonthTotal

                val percentChange = if (lastMonthTotal > 0) {
                    ((diff.toDouble() / lastMonthTotal.toDouble()) * 100).toInt()
                } else {
                    0
                }

                // مقداردهی UI
                binding.tvMostSpendingDay.text =
                    mostSpendingDay?.key ?: "نامشخص"

                binding.tvMostSpendingDayAmount.text =
                    "${formatMoney(mostSpendingDay?.value ?: 0L)} تومان"

                binding.tvMostFrequentCategory.text =
                    mostFrequentCategory?.key ?: "ندارد"

                binding.tvMostFrequentCategoryCount.text =
                    "${mostFrequentCategory?.value?.toString()?.toPersianDigits() ?: "۰"} تراکنش"

                val mostSpendingDayName = mostSpendingDay?.key ?: "روزهای خاص"

                binding.tvSavingSuggestion.text =
                    "بیشترین خرج شما در $mostSpendingDayName‌ها ثبت می‌شود؛ اگر برای این روز سقف هزینه تعیین کنی، کنترل خرج راحت‌تر می‌شود."

                binding.tvSpendingWarning.text = when {
                    lastMonthTotal == 0L && thisMonthTotal > 0L -> {
                        "این ماه هزینه ثبت کرده‌ای، اما ماه قبل هزینه‌ای وجود نداشته؛ از این ماه می‌تونیم رفتار خرجت رو بهتر تحلیل کنیم."
                    }

                    lastMonthTotal == 0L && thisMonthTotal == 0L -> {
                        "هنوز هزینه‌ای برای مقایسه ثبت نشده."
                    }

                    percentChange > 0 -> {
                        "هزینه‌های این ماه نسبت به ماه قبل ${
                            percentChange.toString().toPersianDigits()
                        }٪ افزایش داشته؛ بهتر است دسته ${mostFrequentCategory?.key ?: "پرخرج"} را بیشتر مدیریت کنی."
                    }

                    percentChange < 0 -> {
                        "آفرین! هزینه‌های این ماه نسبت به ماه قبل ${
                            abs(percentChange).toString().toPersianDigits()
                        }٪ کمتر شده."
                    }

                    else -> {
                        "هزینه‌های این ماه تقریباً برابر با ماه قبل بوده."
                    }
                }
            }
        }
    }

    private fun showEmptySpendingBehavior() {
        binding.tvMostSpendingDay.text = "ندارد"
        binding.tvMostSpendingDayAmount.text = "۰ تومان"

        binding.tvMostFrequentCategory.text = "ندارد"
        binding.tvMostFrequentCategoryCount.text = "۰ تراکنش"

        binding.tvSavingSuggestion.text =
            "با ثبت چند تراکنش، پیشنهادهای صرفه‌جویی اینجا نمایش داده می‌شود."

        binding.tvSpendingWarning.text =
            "هنوز داده کافی برای تحلیل رفتار خرج وجود ندارد."
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

   private fun parseMoney(value: String): Long {
        return value
            .replace(".", "")
            .replace(",", "")
            .toLongOrNull() ?: 0L
    }

    private fun showHomeTutorialSequence() {
        val shared = requireContext().getSharedPreferences(
            "tutorials",
            Context.MODE_PRIVATE
        )

        if (shared.getBoolean("home_tutorial_seen", false)) return

        val targets = listOf(
            TapTarget.forView(
                binding.tvTopCategory,
                "بیشترین خرجت کجاست؟",
                "اینجا میتونی بفهمی بیشترین هزینت کجا بوده"
            )
                .outerCircleColor(R.color.purple)
                .targetCircleColor(android.R.color.white)
                .titleTextColor(android.R.color.white)
                .descriptionTextColor(android.R.color.white)
                .transparentTarget(true)
                .tintTarget(false),

            TapTarget.forView(
                binding.tvTransactionsCount,
                "چنتا تراکنش داشتم؟",
                "اینجا میتونی تمام تراکنش هاتو ببینی و بفهمی میانگینشون چقدر میشه"
            )
                .outerCircleColor(R.color.purple)
                .targetCircleColor(android.R.color.white)
                .titleTextColor(android.R.color.white)
                .descriptionTextColor(android.R.color.white)
                .transparentTarget(true)
                .tintTarget(false),

            TapTarget.forView(
                binding.teat,
                "روزی چقدر خرج میکنم؟",
                "از اینجا تشخیص بده میانگین روزی چقدر خرج کردی "
            )
                .outerCircleColor(R.color.purple)
                .targetCircleColor(android.R.color.white)
                .titleTextColor(android.R.color.white)
                .descriptionTextColor(android.R.color.white)
                .transparentTarget(true)
                .tintTarget(false)
        )

        TapTargetSequence(requireActivity())
            .targets(targets)
            .continueOnCancel(true)
            .listener(object : TapTargetSequence.Listener {
                @SuppressLint("UseKtx")
                override fun onSequenceFinish() {
                    shared.edit()
                        .putBoolean("home_tutorial_seen", true)
                        .apply()
                }

                override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
                    Log.v("e22s", "ss2ss")
                }

                @SuppressLint("UseKtx")
                override fun onSequenceCanceled(lastTarget: TapTarget?) {
                    shared.edit()
                        .putBoolean("home_tutorial_seen", true)
                        .apply()
                }
            })
            .start()
    }


}



