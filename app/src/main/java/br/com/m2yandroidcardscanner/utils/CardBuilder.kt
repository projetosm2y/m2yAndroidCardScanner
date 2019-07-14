package br.com.m2yandroidcardscanner.utils

import br.com.m2yandroidcardscanner.extensions.generateDateFromString
import br.com.m2yandroidcardscanner.models.CardModel
import java.util.*

object CardBuilder {
    var textSource: String = ""
    var cardNumber: String = ""
    var expiresDate: String = ""
    var issueDate: String = ""
    var securityCode: String = ""

    fun getCardDate(text: String): MatchResult? {
        return Constants.CARD_DATE_REGEX.toRegex()
            .find(text
                .replace(Constants.EMPTY_CHARACTERS.toRegex(), "")
                .replace("O", "0")
                .replace("o", "0"))
    }

    fun source(text: String): CardBuilder {
        textSource = text
        return this
    }

    fun generateCardNumber(text: String = textSource): String {
        cardNumber = Constants.CARD_NUMBER_REGEX.toRegex()
            .find(
                text.replace(Constants.EMPTY_CHARACTERS.toRegex(), "")
            )?.value
            ?: ""
        return cardNumber
    }

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

    fun generateSecurityCode(text: String): String {
        securityCode = Constants.SECURITY_CODE.toRegex().find(text)?.value
                        ?: ""
        return securityCode
    }

    fun build(): CardModel {
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
}