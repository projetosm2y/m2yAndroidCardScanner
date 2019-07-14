package br.com.m2yandroidcardscanner.ui.cardscanner

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.core.content.ContextCompat
import br.com.m2yandroidcardscanner.R
import br.com.m2yandroidcardscanner.extensions.setVisible
import br.com.m2yandroidcardscanner.models.CardModel
import br.com.m2yandroidcardscanner.ui.base.BaseActivity
import br.com.m2yandroidcardscanner.utils.Constants
import br.com.m2yandroidcardscanner.utils.FirebaseMLManager
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import kotlinx.android.synthetic.main.activity_card_scanner.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.longToast

/**
 * Activity that performs the scanning of card
 */

class CardScannerActivity : BaseActivity(), CardScannerContract.View{

    /** SETUP PRESENTER */
    private val presenter: CardScannerContract.Presenter by lazy {
        val p = CardScannerPresenter()
        p.attachView(this)
        p
    }

    /** LIFE CYCLE METHODS */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_scanner)
        FirebaseMLManager.initialize(this@CardScannerActivity)
        presenter.onActivityCreated(intent?.extras?.getSerializable(Constants.EXTRA_CARD_SCANNER_CONFIG) as CardScannerConfig)
        camera.setLifecycleOwner(this)
        setListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    /** VIEWS RELATED METHODS */
    private fun setListeners() {
        camera.addCameraListener(object: CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                presenter.onCameraViewResult(result)
            }
        })
    }

    /**
     * These 3 functions are related to layout setup
     * of card information container
     *
     * @param color must be a reference to a color resource
     * */

    override fun setBtnBackgroundColor(color: Int) {
        val drawable = ContextCompat.getDrawable(this, R.drawable.bg_rounded_corners_accent)
        drawable?.setColorFilter(ContextCompat.getColor(this, color), PorterDuff.Mode.SRC_ATOP)

        cardScannerContinueBtn.backgroundDrawable = drawable
    }

    override fun setBtnTextColor(color: Int) {
        cardScannerContinueBtn.setTextColor(color)
    }

    override fun setInputTextsTextColor(color: Int) {
        cardScannerCardNumberEt.textColor = ContextCompat.getColor(this, color)
        cardScannerExpiresDateEt.textColor = ContextCompat.getColor(this, color)
        cardScannerIssueDateEt.textColor = ContextCompat.getColor(this, color)
        cardScannerSecurityCodeEt.textColor = ContextCompat.getColor(this, color)
    }


    override fun displayCard(card: CardModel) {
        showCardContainer()

        cardScannerCardNumberEt.text = card.cardNumber ?: ""
        cardScannerExpiresDateEt.text = card.expiresDate ?: ""
        cardScannerIssueDateEt.text = card.issueDate ?: ""
        cardScannerSecurityCodeEt.text = card.securityCode ?: ""
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

    /** FEEDBACK METHODS */
    /**
     * Display the loading performed by progress bar
     *
     * @param show must be a boolean. If it is true, it shows the progress bar,
     *             otherwise, it does not.
     */
    override fun displayLoading(show: Boolean) {
        cardScannerProgressBarContainerLl.setVisible(show)
    }

    /**
     * Display a image in the TestImageView
     *
     * @param bp must be a non-null bitmap
     */
    override fun displayTestImage(bp: Bitmap) {
        testImageView.setImageBitmap(bp)
    }

    /**
     * Display a long toast
     *
     * @param bp must be the String to be shown in the toast
     */
    override fun displayToast(text: String) {
        longToast(text)
    }

    /** CAMERA VIEW METHODS */
    override fun captureImage() {
        camera.takePicture()
    }

    /** CLICK METHODS */
    /**
     * Set a click function to all screen of activity
     * It is done by setting the onClickListener to
     * ActivityClickableAreaView, which is a view that
     * occupies all the screen
     */
    override fun setCameraViewOnClickListener(onClick: () -> Unit) {
        activityClickableAreaView.setOnClickListener {
            onClick()
        }
    }

    /**
     * Set a click function to the ContinueButton
     * that is displayed in the bottom of the container of card information
     */
    override fun setContinueBtnOnClickListener(onClick: ()->Unit) {
        cardScannerContinueBtn.setOnClickListener {
            onClick()
        }
    }

    /** NAVIGATION METHODS */

    /**
     * It finishes the activiy and returns the
     * identified card model
     */
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