package br.com.m2yandroidcardscanner.ui.cardscanner

import android.graphics.Bitmap
import android.os.Handler
import br.com.m2yandroidcardscanner.models.CardModel
import br.com.m2yandroidcardscanner.utils.CardBuilder
import br.com.m2yandroidcardscanner.utils.FirebaseMLManager
import com.otaliastudios.cameraview.PictureResult

class CardScannerPresenter : CardScannerContract.Presenter{
    private var view: CardScannerContract.View? = null
    private var cardModel = CardModel()
    private lateinit var config: CardScannerConfig

    override fun attachView(mvpView: CardScannerContract.View?) {
        view = mvpView
    }
    override fun detachView() {
        view = null
    }

    override fun onActivityCreated(cardScannerConfig: CardScannerConfig?) {
        setConfig(cardScannerConfig)
        view?.hideCardContainer()

        Handler().postDelayed({
            performImageCapture()
        }, config.captureImageDelay)

        if(config.allowClickCapture) {
            view?.setCameraViewOnClickListener {
                performImageCapture()
            }
        }
    }


    override fun onCardScannerResult(scan: String) {
        cardModel = CardBuilder.source(scan).build()
        view?.displayLoading(false)
        if(config.showResultOnScreen){
            view?.displayCard(cardModel)
            view?.setContinueBtnOnClickListener {
                view?.finishActivity(cardModel)
            }
        } else {
            view?.finishActivity(cardModel)
        }
    }

    override fun onCameraViewResult(result: PictureResult) {
        result.toBitmap(config.capturedImageWidth, config.capturedImageHeight){
            it?.let { bitmap ->
                if(config.test?.showImageTaken == true) {
                    view?.displayTestImage(bitmap)
                }
                processBitmapResult(bitmap)
            }

        }
    }

    private fun processBitmapResult(bp: Bitmap){
        FirebaseMLManager
            .imageSource(bp)
        setTextRecognizer()
        FirebaseMLManager.processImage {visionText ->
                if(config.test?.showRecognizedText == true){
                    view?.displayToast(visionText.text)
                }
                onCardScannerResult(visionText.text)
            }
    }

    private fun setTextRecognizer(){
        when(config.recognizerType){
            FirebaseRecognizerType.ON_DEVICE_RECOGNIZER ->
                FirebaseMLManager.withOnDeviceTextRecognizer()
            FirebaseRecognizerType.CLOUD_RECOGNIZER ->
                FirebaseMLManager.withOnDeviceTextRecognizer()
        }
    }

    private fun performImageCapture(){
        view?.displayLoading(true)
        view?.captureImage()
    }

    override fun setConfig(cardScannerConfig: CardScannerConfig?) {
        cardScannerConfig?.let {
            config = it
        }
    }
}