package br.com.m2yandroidcardscanner.extensions

import java.util.*

fun String.generateDateFromString(): Calendar {
    val splitText = this.split("/")
    val readDate = Calendar.getInstance()
    readDate.set("20${splitText[1]}".toInt(), splitText[0].toInt(), 1)
    return readDate
}