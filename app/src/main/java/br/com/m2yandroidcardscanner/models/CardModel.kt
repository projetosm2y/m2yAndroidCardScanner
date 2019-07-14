package br.com.m2yandroidcardscanner.models

import java.io.Serializable

class CardModel : Serializable {

    var cardNumber: String? = null
    var expiresDate: String? = null
    var issueDate: String? = null
    var securityCode: String? = null

}