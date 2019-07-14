package br.com.m2yandroidcardscanner.ui.cardscanner

import java.io.Serializable

open class CardScannerConfig : Serializable {
    open var capturedImageWidth = 3000
    open var capturedImageHeight = 3000
    open var recognizerType = FirebaseRecognizerType.ON_DEVICE_RECOGNIZER
    open var showResultOnScreen = false
    open var captureImageDelay = 8000L
    open var allowClickCapture = false
    open var test: TestConfig = TestConfig()
}

open class TestConfig(
    var showImageTaken: Boolean = false,
    var showRecognizedText: Boolean = false
) : Serializable

enum class FirebaseRecognizerType(val value: String){
    ON_DEVICE_RECOGNIZER("device"),
    CLOUD_RECOGNIZER("cloud")
}