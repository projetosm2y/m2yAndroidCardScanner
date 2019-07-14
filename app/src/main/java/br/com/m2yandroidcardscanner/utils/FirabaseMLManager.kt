package br.com.m2yandroidcardscanner.utils

import android.content.Context
import android.graphics.Bitmap
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer

object FirabaseMLManager {
    var fbVisionImage: FirebaseVisionImage? = null
    var fbVisionTextRecognizer: FirebaseVisionTextRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer

    fun initialize(context: Context){
        FirebaseApp.initializeApp(context)
    }

    fun imageSource(bitmap: Bitmap) : FirabaseMLManager {
        fbVisionImage = FirebaseVisionImage.fromBitmap(bitmap)
        return this
    }

    fun withOnDeviceTextRecognizer(): FirabaseMLManager {
        fbVisionTextRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer
        return this
    }

    fun withCloudTextRecognizer(): FirabaseMLManager {
        fbVisionTextRecognizer = FirebaseVision.getInstance().cloudTextRecognizer
        return this
    }

    fun processImage(firebaseTextHandler: ((FirebaseVisionText) -> Unit)){
        fbVisionImage?.let{ img->
            fbVisionTextRecognizer.processImage(img).addOnSuccessListener {
                firebaseTextHandler(it)
            }

        }
    }
}