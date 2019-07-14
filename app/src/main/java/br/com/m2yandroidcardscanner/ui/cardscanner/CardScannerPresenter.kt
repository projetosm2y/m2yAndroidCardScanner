package br.com.m2yandroidcardscanner.ui.cardscanner

import android.os.Handler
import br.com.m2yandroidcardscanner.models.CardModel
import br.com.m2yandroidcardscanner.utils.CardBuilder

class CardScannerPresenter : CardScannerContract.Presenter{
    private var view: CardScannerContract.View? = null
    private var cardModel = CardModel()

    override fun attachView(mvpView: CardScannerContract.View?) {
        view = mvpView
    }
    override fun detachView() {
        view = null
    }
    override fun onActivityResumed() {
        Handler().postDelayed({
            view?.captureImage()
        }, 8000)
    }

    override fun onCardScannerResult(scan: String) {
        cardModel = CardBuilder.source(scan).build()
        view?.displayLoading(false)
        view?.displayCard(cardModel)
    }
}