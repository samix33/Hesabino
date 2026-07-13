package com.example.hesabino.model.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.hesabino.R
import com.example.hesabino.model.data.Transaction
import com.example.hesabino.databinding.ItemRclTransactionBinding
import com.example.hesabino.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class TransactionAdapter(
    private val data: ArrayList<Transaction>,
    private val transactioeEvint: TransactioeEvint,
    private val  viewModel: HomeViewModel
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    lateinit var binding: ItemRclTransactionBinding

    inner class TransactionViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        @SuppressLint("SetTextI18n", "ResourceAsColor")
        fun bindViews(data: Transaction) {

            binding.tvTitle.text = data.detail
            binding.tvDate.text = data.date
            val emoji = getCategoryEmoji(data.Category)
            val categoryName = getCategoryNameWithoutEmoji(data.Category)

            binding.iconBg.text = emoji
            binding.tvNote.text = categoryName

            binding.tvAmount.apply {
                text = data.balance.toPersianDigits() + if (data.deposit) "+" else "-"
                setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        if (data.deposit) R.color.green else R.color.red
                    )
                )
            }
            itemView.setOnLongClickListener {

                transactioeEvint.onTransactionLongClicked(data,viewModel)
                true

            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemRclTransactionBinding.inflate(inflater, parent, false)

        return TransactionViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
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


    interface TransactioeEvint {
        fun onTransactionClicked()
        fun onTransactionLongClicked(transaction: Transaction,viewModel : ViewModel)

    }

    fun getCategoryEmoji(category: String): String {
        return category.trim().split(" ").firstOrNull() ?: ""
    }

    fun getCategoryNameWithoutEmoji(category: String): String {
        return category.trim()
            .split(" ")
            .drop(1)
            .joinToString(" ")
    }

}