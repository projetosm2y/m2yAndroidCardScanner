package br.com.m2yandroidcardscanner.utils

object Constants {
    /* Regex Constants */
    const val PACKAGE_NAME="br.com.m2yandroidcardscanner"

    const val CARD_NUMBER_REGEX = "([0-9]{16})"
    const val CARD_DATE_REGEX = "(([0][1-9])|([1][1-2]))(\\/)([0-9]{2})"
    const val EMPTY_CHARACTERS = "( +)|(\n)"
    const val NON_DIGIT = "\\D"
    const val SECURITY_CODE = "^[0-9]{3}$"

    /* Extras */
    const val EXTRA_CARD_SCANNER_CONFIG = "$PACKAGE_NAME.EXTRA_CARD_SCANNER_CONFIG"
    const val EXTRA_CARD_MODEL = "$PACKAGE_NAME.EXTRA_CARD_MODEL"
}