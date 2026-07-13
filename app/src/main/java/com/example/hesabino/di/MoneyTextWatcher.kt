package com.example.hesabino.di

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class MoneyTextWatcher(private val editText: EditText) : TextWatcher {

    private var isEditing = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (isEditing) return
        if (s.isNullOrEmpty()) return

        isEditing = true

        val cleanString = s.toString().replace(".", "").replace(",", "")

        if (cleanString.isEmpty()) {
            isEditing = false
            return
        }

        try {
            val number = cleanString.toLong()

            val symbols = DecimalFormatSymbols(Locale.US).apply {
                groupingSeparator = '.'
            }

            val formatter = DecimalFormat("#,###", symbols)
            val formatted = formatter.format(number).replace(",", ".")

            editText.setText(formatted)
            editText.setSelection(formatted.length)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        isEditing = false
    }
}