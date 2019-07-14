package br.com.m2yandroidcardscanner.ui.cardscanner

import br.com.m2yandroidcardscanner.models.CardModel
import br.com.m2yandroidcardscanner.ui.base.BasePresenter
import br.com.m2yandroidcardscanner.ui.base.BaseView


interface CardScannerContract {
    interface View : BaseView<Presenter>{
        fun captureImage()
        fun displayCard(card: CardModel)
        fun displayLoading(show: Boolean)
    }
    interface Presenter : BasePresenter<View>{
        fun onActivityResumed()
        fun onCardScannerResult(scan: String)
    }
}