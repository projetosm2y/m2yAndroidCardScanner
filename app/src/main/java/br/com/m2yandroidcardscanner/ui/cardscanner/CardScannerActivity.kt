package br.com.m2yandroidcardscanner.ui.cardscanner

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import br.com.m2yandroidcardscanner.R
import br.com.m2yandroidcardscanner.extensions.setVisible
import br.com.m2yandroidcardscanner.models.CardModel
import br.com.m2yandroidcardscanner.ui.base.BaseActivity
import br.com.m2yandroidcardscanner.utils.FirebaseMLManager
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import kotlinx.android.synthetic.main.activity_card_scanner.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.longToast

class CardScannerActivity : BaseActivity(), CardScannerContract.View{
    private val presenter: CardScannerContract.Presenter by lazy {
        val p = CardScannerPresenter()
        p.attachView(this)
        p
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_scanner)
        FirebaseMLManager.initialize(this@CardScannerActivity)
//        FirebaseApp.initializeApp(this@CardScannerActivity)
        presenter.onActivityResumed()
        camera.setLifecycleOwner(this)

        cardScannerCardInformationContainerLl
            .animate()
            .translationY(cardScannerCardInformationContainerLl.height.toFloat())
            .setDuration(500)
            .alpha(0.0f)
            .setListener(object : AnimatorListenerAdapter() {})
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun displayCard(card: CardModel) {
        cardScannerCardInformationContainerLl.setVisible(true)
        cardScannerCardInformationContainerLl
            .animate()
            .translationY(0f)
            .setDuration(500)
            .alpha(1.0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    cardScannerCardInformationContainerLl.setVisible(true)
                }
            })
        cardScannerCardNumberEt.text = card.cardNumber ?: ""
        cardScannerExpiresDateEt.text = card.expiresDate ?:  ""
        cardScannerIssueDateEt.text = card.issueDate ?: ""
        cardScannerSecurityCodeEt.text = card.securityCode ?: ""
    }

    override fun displayLoading(show: Boolean) {
        cardScannerProgressBarContainerLl.setVisible(show)
    }

    override fun captureImage() {
        camera.addCameraListener(object: CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                result.toBitmap(2000, 2000){bp ->
                    runOnUiThread {
                        bp?.let {
                            testImageView.setImageBitmap(it)
                            getCardDetails(it)
                        }
                    }
                }
            }
        })
        camera.takePicture()
        displayLoading(true)
    }

    private fun getCardDetails(image: Bitmap) {
        FirebaseMLManager
            .imageSource(image)
            .withOnDeviceTextRecognizer()
            .processImage {
                longToast(it.text)
                presenter.onCardScannerResult(it.text)
            }
//        }
    }
}


fun Context.createCardScannerIntent() =
    intentFor<CardScannerActivity>()