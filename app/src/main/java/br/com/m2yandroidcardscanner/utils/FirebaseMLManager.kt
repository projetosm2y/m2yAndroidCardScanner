package br.com.m2yandroidcardscanner.utils

import android.content.Context
import android.graphics.Bitmap
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer

/**
 * It is a class that helps the Text Recognition by using firebase
 */

object FirebaseMLManager {
    var fbVisionImage: FirebaseVisionImage? = null
    var fbVisionTextRecognizer: FirebaseVisionTextRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer

    fun initialize(context: Context){
        FirebaseApp.initializeApp(context)
    }

    /**
     * It gets the firebase vision image from a bitmap
     * @param bitmap is the image source
     */
    fun imageSource(bitmap: Bitmap) : FirebaseMLManager {
        fbVisionImage = FirebaseVisionImage.fromBitmap(bitmap)
        return this
    }

    /**
     * It sets the recognizer to be done by the device
     */
    fun withOnDeviceTextRecognizer(): FirebaseMLManager {
        fbVisionTextRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer
        return this
    }

    /**
     * It sets the recognizer to be done by cloud
     */
    fun withCloudTextRecognizer(): FirebaseMLManager {
        fbVisionTextRecognizer = FirebaseVision.getInstance().cloudTextRecognizer
        return this
    }

    /**
     * It performs the processing of image
     * @param firebaseTextHandler is the function added as success listener
     * of firebase processing
     */
    fun processImage(firebaseTextHandler: ((FirebaseVisionText) -> Unit)){
        fbVisionImage?.let{ img->
            fbVisionTextRecognizer.processImage(img).addOnSuccessListener {
                firebaseTextHandler(it)
            }

        }
    }
}