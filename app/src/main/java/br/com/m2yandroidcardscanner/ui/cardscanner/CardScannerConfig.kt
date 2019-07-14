package br.com.m2yandroidcardscanner.ui.cardscanner

import br.com.m2yandroidcardscanner.R
import java.io.Serializable

open class CardScannerConfig : Serializable {
    /**
     * The width of the image captured by the camera
     */
    open var capturedImageWidth = 3000

    /**
     * The height of the image captured by the camera
     */
    open var capturedImageHeight = 3000

    /**
     * This parameter sets if the recognition will be done by
     * the device or by cloud
     */
    open var recognizerType = FirebaseRecognizerType.ON_DEVICE_RECOGNIZER

    /**
     * The activity has a container for card information to be shown
     * if this parameter is true
     */
    open var showResultOnScreen = false

    /**
     * When the activity starts, there is a Handler that waits for
     * the time set by this parameters (in Milliseconds), then,
     * takes a picture
     */
    open var captureImageDelay = 8000L

    /**
     * There is a view that matches all the activity
     * If this parameter is true, when the user touches on the screen
     * a photo will be taken and processed to build a card model
     */
    open var allowClickCapture = false

    /**
     * Configuration to test and debug
     */
    open var test: TestConfig = TestConfig()

    /**
     * Configuration of layout
     */
    open var layout = LayoutConfig()
}

open class TestConfig(
    /**
     * If true, the activity will show a image view
     * (in the top-left of activity) with the result of camera
     */
    var showImageTaken: Boolean = false,

    /**
     * If true, the activity will show a toast
     * with the text recognized by FirebaseMLKit
     */
    var showRecognizedText: Boolean = false
) : Serializable

open class LayoutConfig(
    var inputTextsTextColor: Int = R.color.colorPrimary,
    var btnTextColor: Int = R.color.color_white,
    var btnBackgroundColor: Int = R.color.colorAccent
    ) : Serializable

enum class FirebaseRecognizerType(val value: String){
    ON_DEVICE_RECOGNIZER("device"),
    CLOUD_RECOGNIZER("cloud")
}