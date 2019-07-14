package br.com.m2yandroidcardscanner.utils

object Constants {
    /* Regex Constants */
    const val CARD_NUMBER_REGEX = "([0-9]{16})"
    const val CARD_DATE_REGEX = "(([0][1-9])|([1][1-2]))(\\/)([0-9]{2})"
    const val EMPTY_CHARACTERS = "( +)|(\n)"
    const val NON_DIGIT = "\\D"
    const val SECURITY_CODE = "^[0-9]{3}$"
}