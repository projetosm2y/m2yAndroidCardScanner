package br.com.m2yandroidcardscanner.ui.cardscanner

import android.graphics.Bitmap
import br.com.m2yandroidcardscanner.models.CardModel
import br.com.m2yandroidcardscanner.ui.base.BasePresenter
import br.com.m2yandroidcardscanner.ui.base.BaseView
import com.otaliastudios.cameraview.PictureResult


interface CardScannerContract {
    interface View : BaseView<Presenter>{
        fun captureImage()
        fun displayCard(card: CardModel)
        fun displayLoading(show: Boolean)
        fun hideCardContainer()
        fun showCardContainer()
        fun displayToast(text: String)
        fun displayTestImage(bp: Bitmap)
        fun setCameraViewOnClickListener(onClick: ()->Unit)
        fun setContinueBtnOnClickListener(onClick: ()->Unit)
        fun finishActivity(cardModel: CardModel)
        fun setInputTextsTextColor(color: Int)
        fun setBtnBackgroundColor(color: Int)
        fun setBtnTextColor(color: Int)
    }
    interface Presenter : BasePresenter<View>{
        fun onActivityCreated(cardScannerConfig: CardScannerConfig?)
        fun setConfig(cardScannerConfig: CardScannerConfig?)
        fun onCardScannerResult(scan: String)
        fun onCameraViewResult(result: PictureResult)

    }
}