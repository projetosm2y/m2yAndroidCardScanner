package br.com.m2yandroidcardscanner.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

object TextMask {
    const val CEP_MASK = "#####-###"
    const val CPF_MASK = "###.###.###-##"
    const val CPF_OR_CNPJ_MASK = "###.###.###-###"
    const val PHONE_MASK = "(##) ####-#####"
    const val CEL_PHONE_MASK = "(##) # ####-####"
    const val CREDIT_CARD_MASK = "#### #### #### ####"
    const val DATE_MASK = "##/##/####"
    const val CNPJ_MASK = "##.###.###/####-##"
    const val HOUR_MASK = "##:##"
    const val CREDIT_CARD_DATE_MASK = "##/##"

    fun unmask(s: String): String {
        return s.replace("[.]".toRegex(), "").replace("[-]".toRegex(), "")
            .replace("[/]".toRegex(), "").replace("[(]".toRegex(), "")
            .replace("[)]".toRegex(), "").replace(" ".toRegex(), "")
            .replace("[:]".toRegex(), "").replace("[+]".toRegex(), "")
    }
}

fun EditText.insertMask(mask: String) {
    addTextChangedListener(object : TextWatcher {
        var isUpdating: Boolean = false
        var old = ""
        var maskAux = mask

        override fun onTextChanged(s: CharSequence, start: Int, before: Int,
                                   count: Int) {
            maskAux = when (mask) {
                TextMask.PHONE_MASK -> if (TextMask.unmask(s.toString()).length > 10) TextMask.CEL_PHONE_MASK else TextMask.PHONE_MASK
                TextMask.CPF_OR_CNPJ_MASK -> if (TextMask.unmask(s.toString()).length > 11) TextMask.CNPJ_MASK else TextMask.CPF_MASK
                else -> mask
            }
            val str = TextMask.unmask(s.toString())
            var mascara = ""
            if (isUpdating) {
                old = str
                isUpdating = false
                return
            }
            var i = 0
            for (char in maskAux.toCharArray()) {
                try {
                    val cha = str[i]
                    if (char == '#') {
                        mascara += cha
                        i++
                    } else {
                        mascara += char
                    }
                } catch (e: Exception) {
                    break
                }
            }
            isUpdating = true
            setText(mascara)
            setSelection(mascara.length)
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {}
    })
}