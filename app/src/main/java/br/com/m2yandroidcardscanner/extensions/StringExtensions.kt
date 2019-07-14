package br.com.m2yandroidcardscanner.extensions

import java.util.*

/**
 * This function generates a Calendar
 * with the date set according to the string
 * it is expected the format dd/mm/yyyy
 */

fun String.generateDateFromString(): Calendar {
    val splitText = this.split("/")
    val readDate = Calendar.getInstance()
    readDate.set("20${splitText[1]}".toInt(), splitText[0].toInt(), 1)
    return readDate
}