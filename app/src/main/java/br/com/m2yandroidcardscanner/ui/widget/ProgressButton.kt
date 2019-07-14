package br.com.m2yandroidcardscanner.ui.widget

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import br.com.m2yandroidcardscanner.R
import br.com.m2yandroidcardscanner.extensions.getColorRes
import br.com.m2yandroidcardscanner.extensions.setVisible
import kotlinx.android.synthetic.main.progress_button.view.*
import org.jetbrains.anko.backgroundResource

/**
 * Created by azul on 29/11/17.
 */
class ProgressButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attrs, defStyle) {

    private var disabledBgColor: Int = -1
    private var enabledBgColor: Int = -1
    private var btnBackgroundRes: Int = -1

    init {
        LayoutInflater.from(context).inflate(R.layout.progress_button, this, true)
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.ProgressButton, 0, 0)

            //ENABLED
            val enabled = typedArray.getBoolean(R.styleable.ProgressButton_btnEnabled, true)
            isEnabled = enabled


            val dislabedBgColorRes = typedArray.getResourceId(R.styleable.ProgressButton_btnDisabledBackgroundColor, -1)
            disabledBgColor = if (dislabedBgColorRes != -1) {
                dislabedBgColorRes
            } else {
                R.color.color_gray_default
            }

            //BACKGROUND
            val backgroundRes = typedArray.getResourceId(R.styleable.ProgressButton_btnBackground, -1)
            if (backgroundRes != -1) {
                setBackground(backgroundRes)
                btnBackgroundRes = backgroundRes
            }
            val btnBackgroundColor = typedArray.getResourceId(R.styleable.ProgressButton_btnBackgroundColor, -1)
            if (btnBackgroundColor != -1) {
                enabledBgColor = btnBackgroundColor
                setBtnBackgroundColor(btnBackgroundColor)
            }

            //ICON
            val icon = typedArray.getResourceId(R.styleable.ProgressButton_btnIcon, -1)
            if (icon != -1) progressButtonImageView.setImageDrawable(ContextCompat.getDrawable(context, icon))

            //PROGRESS BAR
            val progressColor = typedArray.getResourceId(R.styleable.ProgressButton_btnProgressColor, -1)
            if (progressColor != -1) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    val wrapDrawable = DrawableCompat.wrap(progressButtonProgressBar.indeterminateDrawable)
                    //todo check this line
                    DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(getContext(), android.R.color.holo_green_light))
                    progressButtonProgressBar.indeterminateDrawable = DrawableCompat.unwrap<Drawable>(wrapDrawable)
                } else {
                    progressButtonProgressBar.indeterminateDrawable.setColorFilter(context.getColorRes(progressColor), PorterDuff.Mode.SRC_IN)
                }
            }

            val rippleBackgroundRes = typedArray.getResourceId(R.styleable.ProgressButton_btnCustomSelectableItemBackground, -1)
            foreground = if (rippleBackgroundRes != -1) {
                ContextCompat.getDrawable(context, rippleBackgroundRes)
            } else {
                val typedValue = TypedValue()
                context.theme.resolveAttribute(R.attr.selectableItemBackground, typedValue, true)
                ContextCompat.getDrawable(context, typedValue.resourceId)
            }

            //TEXT
            progressButtonTextView.text = typedArray.getString(R.styleable.ProgressButton_btnText)
            val textColorRes = typedArray.getResourceId(R.styleable.ProgressButton_btnTextColor, -1)
            val textSizeRes = typedArray.getDimensionPixelSize(R.styleable.ProgressButton_btnTextSize, -1)
            val textAllCaps = typedArray.getBoolean(R.styleable.ProgressButton_btnAllCaps, true)
            if (textColorRes != -1) progressButtonTextView.setTextColor(ContextCompat.getColor(context, textColorRes))
            if (textSizeRes != -1) setTextSize(textSizeRes)
            progressButtonTextView.setAllCaps(textAllCaps)
            typedArray.recycle()
        }
    }

    fun setBtnBackgroundColor(@ColorRes backgroundColor: Int) {
        val coloredBgDrawable = background.mutate()
        coloredBgDrawable.setColorFilter(context.getColorRes(backgroundColor), PorterDuff.Mode.SRC_IN)
        background = coloredBgDrawable
    }

    fun setDimens(height: Int, width: Int) {
        layoutParams.height = height
        layoutParams.width = width
    }

    fun setText(text: String) {
        progressButtonImageView.setVisible(false, true)
        progressButtonTextView.setVisible(true, true)
        progressButtonTextView.text = text
    }

    fun setDrawable(drawable: Drawable) {
        progressButtonTextView.setVisible(false, true)
        progressButtonImageView.setVisible(true, true)
        progressButtonTextView.text = ""
        progressButtonImageView.setImageDrawable(drawable)
    }

    fun setTextColor(@ColorRes colorRes: Int) {
        progressButtonTextView.setTextColor(ContextCompat.getColor(context, colorRes))
        invalidate()
        requestLayout()
    }

    fun setBackground(backgroundRes: Int) {
        backgroundResource = backgroundRes
    }

    fun setBtnEnabled(enabled: Boolean) {
        isEnabled = enabled
        if (enabled) {
            if(btnBackgroundRes != -1) {
                setBackground(btnBackgroundRes)
                if(enabledBgColor != -1) {
                    setBtnBackgroundColor(enabledBgColor)
                }
            }
        } else {
            setBtnBackgroundColor(disabledBgColor)
        }
    }

    fun setLoading(loading: Boolean) {
        isEnabled = !loading
        progressButtonProgressBar.setVisible(loading)
        if (progressButtonTextView.text.isNullOrBlank()) {
            progressButtonImageView.setVisible(!loading, true)
        } else {
            progressButtonTextView.setVisible(!loading, true)
        }
    }

    private fun setTextSize(size: Int) {
        progressButtonTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size.toFloat())
        invalidate()
        requestLayout()
    }
}