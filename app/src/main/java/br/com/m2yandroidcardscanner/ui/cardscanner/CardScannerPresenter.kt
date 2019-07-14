package br.com.m2yandroidcardscanner.ui.cardscanner

import android.graphics.Bitmap
import android.os.Handler
import br.com.m2yandroidcardscanner.models.CardModel
import br.com.m2yandroidcardscanner.utils.CardBuilder
import br.com.m2yandroidcardscanner.utils.FirebaseMLManager
import com.otaliastudios.cameraview.PictureResult

/**
 * Presenter related to CardScannerContract
 */
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


    /**
     * It must be ran in the creation of activity
     * It hides de card information container
     * Set the layout according to configuration
     * Set the delay time of taking a picture since the activity is launched
     * Set whether it is possible to click on the activity screen to take a picture
     * @param cardScannerConfig is the configuration of activity
     * */

    override fun onActivityCreated(cardScannerConfig: CardScannerConfig?) {
        setConfig(cardScannerConfig)

        view?.hideCardContainer()

        setupLayout()

        Handler().postDelayed({
            performImageCapture()
        }, config.captureImageDelay)

        if(config.allowClickCapture) {
            view?.setCameraViewOnClickListener {
                performImageCapture()
            }
        }
    }

    /**
     * According to the configuration, it sets the Recognizer Text
     * It determines whether the recognition must be done
     * on device or by cloud
     * */

    private fun setTextRecognizer(){
        when(config.recognizerType){
            FirebaseRecognizerType.ON_DEVICE_RECOGNIZER ->
                FirebaseMLManager.withOnDeviceTextRecognizer()
            FirebaseRecognizerType.CLOUD_RECOGNIZER ->
                FirebaseMLManager.withOnDeviceTextRecognizer()
        }
    }

    /**
     * It takes the a bitmap and performs the image processing
     * by using the FirebaseMlManager
     * @param bp is the image as bitmap to be processed
     * */

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

    /**
     * It handles the camera view result
     * It tries to transform it into a bitmap
     * If not null, it is sent to the processing
     * And if the config showImageTaken property is true,
     * it displays the image into the TestImage
     * @param result is the result of camera view
     * */

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

    /**
     * It handles the result string from the FirebaseMLManager and builds a card
     * If it option showResultOnScreen is true, it displays the card
     * Otherwise, just finish the activity sending back de card model
     * @param scan is the result to build the card model
     * */
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


    private fun performImageCapture(){
        view?.displayLoading(true)
        view?.captureImage()
    }

    private fun setupLayout(){
        view?.setBtnTextColor(config.layout.btnTextColor)
        view?.setBtnBackgroundColor(config.layout.btnBackgroundColor)
        view?.setInputTextsTextColor(config.layout.inputTextsTextColor)
    }

    override fun setConfig(cardScannerConfig: CardScannerConfig?) {
        cardScannerConfig?.let {
            config = it
        }
    }
}