package br.com.m2yandroidcardscanner.models

import java.io.Serializable

/**
 * Model of a Card
 * It must be Serializable because it it passed thought intent
 */
class CardModel : Serializable {

    var cardNumber: String? = null
    var expiresDate: String? = null
    var issueDate: String? = null
    var securityCode: String? = null

}