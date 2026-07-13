package com.example.hesabino.model.adapter


import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hesabino.model.data.Category
import com.example.hesabino.databinding.ItemRclCategoryBinding
import com.example.hesabino.ui.analysis.AnalysisViewModel
import com.example.hesabino.ui.home.HomeViewModel


class CategoryAdapter(
    private val data: List<Category>,
    private val categoryEvents: CategoryEvents,
    private val viewModel: AnalysisViewModel
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {


    lateinit var binding: ItemRclCategoryBinding

    inner class CategoryViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        @SuppressLint("SetTextI18n", "ResourceAsColor")
        fun bindViews(data: Category) {


            val cleanAmount = if (data.mony.contains(".")) {
                data.mony.replace(".", "")
            } else {
                data.mony
            }

            binding.tvCategoryName.text = data.name
            binding.tvCategoryAmount.text = formatMoney(cleanAmount.toLong())
            itemView.setOnClickListener {

                categoryEvents.onClick(data,viewModel)
            }

            val percent = if (data.budgetLimit > 0) {
                (data.mony.toFloat() / data.budgetLimit.toFloat() * 100).toInt()
            } else 0

            binding.progressCategory.progress = percent
            binding.tvCategoryPercent.text = "$percent٪"
            val formatlimit = formatMoney(data.budgetLimit)
            binding.tvBudgetLimit.text = " سقف هزینه:${formatlimit} تومان"



            if (percent > 100) {
                binding.progressCategory.progressTintList = ColorStateList.valueOf(Color.RED)
            } else {
                binding.progressCategory.progressTintList =
                    ColorStateList.valueOf(Color.parseColor("#4F46E5"))
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemRclCategoryBinding.inflate(inflater, parent, false)

        return CategoryViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bindViews(data[position])
    }

    override fun getItemCount(): Int = data.size

    fun String.toPersianDigits(): String {
        val englishDigits = arrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

        var result = this
        for (i in englishDigits.indices) {
            result = result.replace(englishDigits[i], persianDigits[i])
        }
        return result
    }

    fun formatMoney(number: Long): String {
        return String.format("%,d", number)
            .replace(",", ".")
            .toPersianDigits()
    }


    interface CategoryEvents {
        fun onClick(data: Category, viewModel: AnalysisViewModel)
    }
    private var items = mutableListOf<Category>()

    fun updateData(newItems: List<Category>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }


}