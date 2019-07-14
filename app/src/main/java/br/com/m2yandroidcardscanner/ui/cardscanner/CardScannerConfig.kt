package br.com.m2yandroidcardscanner.ui.cardscanner

import br.com.m2yandroidcardscanner.R
import java.io.Serializable

open class CardScannerConfig : Serializable {
    open var capturedImageWidth = 3000
    open var capturedImageHeight = 3000
    open var recognizerType = FirebaseRecognizerType.ON_DEVICE_RECOGNIZER
    open var showResultOnScreen = false
    open var captureImageDelay = 8000L
    open var allowClickCapture = false
    open var test: TestConfig = TestConfig()
    open var layout = LayoutConfig()
}

open class TestConfig(
    var showImageTaken: Boolean = false,
    var showRecognizedText: Boolean = false
) : Serializable

open class LayoutConfig(
    var inputTextsTextColor: Int = R.color.colorPrimary,
    var btnBackgroundColor: Int = R.color.color_white,
    var btnTextColor: Int = R.color.colorAccent
) : Serializable

enum class FirebaseRecognizerType(val value: String){
    ON_DEVICE_RECOGNIZER("device"),
    CLOUD_RECOGNIZER("cloud")
}