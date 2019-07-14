package br.com.m2yandroidcardscanner.extensions

import android.content.Context
import androidx.core.content.ContextCompat

fun Context.getColorRes(idRes: Int): Int {
    return ContextCompat.getColor(this, idRes)
}