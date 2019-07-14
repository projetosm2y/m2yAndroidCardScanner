package br.com.m2yandroidcardscanner.utils

import br.com.m2yandroidcardscanner.extensions.generateDateFromString
import br.com.m2yandroidcardscanner.models.CardModel
import java.util.*

/**
 * It is the class that builds a card model from a String
 */


object CardBuilder {
    var textSource: String = ""
    var cardNumber: String = ""
    var expiresDate: String = ""
    var issueDate: String = ""
    var securityCode: String = ""


    /**
     * It gets the sub-string that can be a card date (mm/yy) from the text
     * It also replaces a 'o' or a 'O'to a '0', which is a commom error for text recognition
     * @param text is the source to get a possible card date
     */
    fun getCardDate(text: String): MatchResult? {
        return Constants.CARD_DATE_REGEX.toRegex()
            .find(text
                .replace(Constants.EMPTY_CHARACTERS.toRegex(), "")
                .replace("O", "0")
                .replace("o", "0"))
    }

    /**
     * This functions initializes the textSource property of this object
     * It must be called before the process of building itself
     * @param text is the source for CardBuilder
     */
    fun source(text: String): CardBuilder {
        textSource = text
        return this
    }

    /**
     * This functions receives a string, from which it tries
     * to get a substring that matches some requirements to be card number
     * (some REGEXs and length)
     * @param text is the source for generation a card number
     */
    fun generateCardNumber(text: String = textSource): String {
        cardNumber = Constants.CARD_NUMBER_REGEX.toRegex()
            .find(
                text.replace(Constants.SPACES_CHARACTERS.toRegex(), "")
            )?.value ?: ""

        if(cardNumber.isBlank() || cardNumber.length < 16) {
            cardNumber = Constants.CARD_NUMBER_REGEX.toRegex()
                .find(
                    text.replace(Constants.EMPTY_CHARACTERS.toRegex(), "")
                )?.value ?: ""
        }

        return cardNumber
    }

    /**
     * This functions receives a string, from which it tries
     * to get a date with the card date format.
     * If it gets, it verifies whether the date has passed or not
     * If not, it can be a expires date.
     * The function tries it twice
     * @param text is the source for generation of a date
     */
    fun generateExpiresDate(text: String): String {
        getCardDate(text)?.let {firstMatch ->
            val currentDate = Calendar.getInstance()
            var readDate = firstMatch.value.generateDateFromString()
            if(readDate > currentDate) {
                expiresDate = firstMatch.value
            } else {
                firstMatch.next()?.let {secondMatch ->
                    readDate = secondMatch.value.generateDateFromString()
                    if(readDate > currentDate){
                        expiresDate = secondMatch.value
                    }
                }
            }
        }
        return expiresDate
    }

    /**
     * This functions receives a string, from which it tries
     * to get a date with the card date format.
     * If it gets, it verifies whether the date has passed or not
     * If so, it can be a expires date.
     * The function tries it twice
     * @param text is the source for generation of a date
     */
    fun generateIssuesDate(text: String): String {
        getCardDate(text)?.let {firstMatch ->
            val currentDate = Calendar.getInstance()
            var readDate = firstMatch.value.generateDateFromString()
            if(readDate < currentDate) {
                issueDate = firstMatch.value
            } else {
                firstMatch.next()?.let {secondMatch ->
                    readDate = secondMatch.value.generateDateFromString()
                    if(readDate < currentDate){
                        issueDate = secondMatch.value
                    }
                }
            }
        }
        return issueDate
    }

    /**
     * This functions receives a string, from which it tries
     * to get a security code
     * It tries by getting a substring with length equals to 3,
     * if there is not, it tries with a string split by \n,
     * and then, by a " " (a single space)
     * @param text is the source for generation of a security code
     */

    fun generateSecurityCode(text: String): String {
        securityCode = Constants.SECURITY_CODE.toRegex().find(text)?.value
            ?: ""

        var words = text.split(" ")
        if(securityCode.isBlank()) {
            words.forEach {
                securityCode = Constants.SECURITY_CODE.toRegex().find(it)?.value
                    ?: ""
            }
        }

        if(securityCode.isBlank()) {
            words = text.split("\n")
            words.forEach {
                securityCode = Constants.SECURITY_CODE.toRegex().find(it)?.value
                    ?: ""
            }
        }

        return securityCode
    }

    /**
     * This function builds the card model
     * after setting the text source
     */
    fun build(): CardModel {
        clearFields()

        generateCardNumber(textSource)
        generateExpiresDate(textSource)
        generateIssuesDate(textSource)
        generateSecurityCode(textSource)

        val cardModel  = CardModel()

        if(cardNumber.isNotBlank()){
            cardModel.cardNumber = cardNumber
        }

        if(expiresDate.isNotBlank()){
            cardModel.expiresDate = expiresDate
        }

        if(issueDate.isNotBlank()) {
            cardModel.issueDate = issueDate
        }

        if(securityCode.isNotBlank()){
            cardModel.securityCode = securityCode
        }

        return cardModel
    }

    fun clearFields() {
        cardNumber = ""
        expiresDate = ""
        issueDate = ""
        securityCode = ""
    }
}