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
    }
    interface Presenter : BasePresenter<View>{
        fun onActivityResumed()
        fun setConfig(cardScannerConfig: CardScannerConfig)
        fun onCardScannerResult(scan: String)
        fun onCameraViewResult(result: PictureResult)

    }
}