package br.com.m2yandroidcardscanner.ui.cardscanner

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import br.com.m2yandroidcardscanner.R
import br.com.m2yandroidcardscanner.extensions.setVisible
import br.com.m2yandroidcardscanner.models.CardModel
import br.com.m2yandroidcardscanner.ui.base.BaseActivity
import br.com.m2yandroidcardscanner.utils.Constants
import br.com.m2yandroidcardscanner.utils.FirebaseMLManager
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import kotlinx.android.synthetic.main.activity_card_scanner.*
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
        presenter.onActivityCreated(intent?.extras?.getSerializable(Constants.EXTRA_CARD_SCANNER_CONFIG) as CardScannerConfig)
        camera.setLifecycleOwner(this)
        setListeners()
    }

    private fun setListeners() {
        camera.addCameraListener(object: CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                presenter.onCameraViewResult(result)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun displayCard(card: CardModel) {
        showCardContainer()

        if(card.cardNumber?.isNotBlank() == true){
            cardScannerCardNumberEt.text = card.cardNumber!!
        }

        if(card.expiresDate?.isNotBlank() == true){
            cardScannerExpiresDateEt.text = card.expiresDate!!
        }

        if(card.issueDate?.isNotBlank() == true){
            cardScannerIssueDateEt.text = card.issueDate!!
        }

        if(card.securityCode?.isNotBlank() == true){
            cardScannerSecurityCodeEt.text = card.securityCode!!
        }
    }

    override fun displayLoading(show: Boolean) {
        cardScannerProgressBarContainerLl.setVisible(show)
    }

    override fun captureImage() {
        camera.takePicture()
    }


    override fun displayTestImage(bp: Bitmap) {
        testImageView.setImageBitmap(bp)
    }

    override fun displayToast(text: String) {
        longToast(text)
    }

    override fun hideCardContainer() {
        cardScannerCardInformationContainerLl
            .animate()
            .translationY(cardScannerCardInformationContainerLl.height.toFloat())
            .setDuration(500)
            .alpha(0.0f)
            .setListener(object : AnimatorListenerAdapter() {})
    }

    override fun showCardContainer() {
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
    }

    override fun setCameraViewOnClickListener(onClick: () -> Unit) {
        camera.isClickable = true
        camera.isFocusable = true
        camera.setOnClickListener {
            onClick()
        }
    }

    override fun setContinueBtnOnClickListener(onClick: ()->Unit) {
        cardScannerContinueBtn.setOnClickListener {
            onClick()
        }
    }

    override fun finishActivity(cardModel: CardModel) {
        intent.putExtra(Constants.EXTRA_CARD_MODEL, cardModel)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}


fun Context.createCardScannerIntent(config: CardScannerConfig = CardScannerConfig()): Intent{
    val intent = Intent( this, CardScannerActivity::class.java)
    intent.putExtra(Constants.EXTRA_CARD_SCANNER_CONFIG, config)
    return intent
}