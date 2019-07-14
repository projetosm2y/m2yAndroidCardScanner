package br.com.m2yandroidcardscanner.ui.widget

import android.content.Context
import android.content.res.TypedArray
import android.support.design.widget.TextInputLayout
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import br.com.m2yandroidcardscanner.R
import br.com.m2yandroidcardscanner.utils.TextMask
import br.com.m2yandroidcardscanner.utils.insertMask
import br.com.m2yandroidcardscanner.utils.validations.IsCnpj
import br.com.m2yandroidcardscanner.utils.validations.IsCpf
import br.com.m2yandroidcardscanner.utils.validations.IsEmail
import org.jetbrains.anko.textColor
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.regex.Pattern

private const val TYPE_CUSTOM = 0
private const val TYPE_NAME = 1
private const val TYPE_MAIL = 2
private const val TYPE_DATE = 3
private const val TYPE_PHONE = 4
private const val TYPE_CEP = 5
private const val TYPE_CREDIT_CARD = 6
private const val TYPE_PASSWORD = 7
private const val TYPE_MATCHING = 8
private const val TYPE_CPF = 9
private const val TYPE_CNPJ = 10
private const val TYPE_CPF_OR_CNPJ = 11
private const val TYPE_CREDIT_CARD_DATE = 12
private const val TYPE_NUMBER = 13

class AppTextInputLayout : TextInputLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.AppTextInputLayout, 0, 0).let {
            values = it
            initialize(it)
        }
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.AppTextInputLayout, 0, 0).let {
            values = it
            initialize(it)
        }
    }

    interface EditTextListener {
        fun onError()
        fun onValid()
        fun onFocusChange(isFocused: Boolean?)
    }

    private lateinit var mEditText: EditText

    var text: String
        get() = mEditText.text.toString()
        set(text) {
            mEditText.setText(text)
        }

    var textColor: Int = -1
        set(value) {
            field = value
            mEditText.setTextColor(value)
        }

    val unmaskedText: String
        get() = TextMask.unmask(text)

    val isFieldValid: Boolean
        get() = !isErrorEnabled

    private var inputType: Int = 0
    private var editTextListener: EditTextListener? = null
    private var fieldNeedsValidation: Boolean = false
    private var errorBackground: Int = 0
    private var defaultBackground: Int = 0
    private var emptinessIsValid: Boolean = false
    private var emptyErrorText: String? = null
    private var regexErrorText = ""
    private var invalidErrorText: String? = null
    private var minLength: Int = 0
    private var matchingReference: EditText? = null
    private var filterCharSequence: String? = null
    private var mask: String? = null
    private var isRegexValid = false
    private var values: TypedArray? = null
    private var regex: String? = null
    private var isInitialized = false

    private fun initialize(values: TypedArray) {
        values.run {
            fieldNeedsValidation = getBoolean(R.styleable.AppTextInputLayout_validation, true)
            emptyErrorText = if (getString(R.styleable.AppTextInputLayout_emptyErrorText) == null) "" else getString(R.styleable.AppTextInputLayout_emptyErrorText)
            regexErrorText = if (getString(R.styleable.AppTextInputLayout_regexErrorText) == null) "" else getString(R.styleable.AppTextInputLayout_emptyErrorText)
            invalidErrorText = if (getString(R.styleable.AppTextInputLayout_invalidErrorText) == null) "" else getString(R.styleable.AppTextInputLayout_invalidErrorText)
            inputType = getInt(R.styleable.AppTextInputLayout_inputTextType, TYPE_CUSTOM)
            minLength = getInt(R.styleable.AppTextInputLayout_minLength, 0)
            mask = if (getString(R.styleable.AppTextInputLayout_customMask) == null) null else getString(R.styleable.AppTextInputLayout_customMask)
            errorBackground = getResourceId(R.styleable.AppTextInputLayout_errorBackground, -1)
            defaultBackground = getResourceId(R.styleable.AppTextInputLayout_defaultBackground, -1)
            emptinessIsValid = getBoolean(R.styleable.AppTextInputLayout_emptinessIsValid, false)
            filterCharSequence = getString(R.styleable.AppTextInputLayout_filterCharacterSet)
            regex = if (getString(R.styleable.AppTextInputLayout_pattern) == null) "" else getString(R.styleable.AppTextInputLayout_pattern)
            recycle()
        }
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        super.addView(child, index, params)
        if (editText != null && !isInitialized) {
            isInitialized = true
            setInputType()
        }
    }

    override fun setOnClickListener(l: View.OnClickListener?) {
        mEditText.setOnClickListener(l)
        mEditText.isFocusable = false
        mEditText.isClickable = true
        isClickable = true
        isFocusable = true
        mEditText.isEnabled = true
        super.setOnClickListener(l)
    }

    private fun setInputType() {
        mEditText = editText!!
        setEditTextListener()
        mEditText.inputType = when (inputType) {
            TYPE_PASSWORD -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            TYPE_MAIL -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            TYPE_PHONE -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_CLASS_PHONE
            TYPE_DATE, TYPE_CREDIT_CARD_DATE -> InputType.TYPE_DATETIME_VARIATION_DATE
            TYPE_CEP, TYPE_CREDIT_CARD, TYPE_CPF, TYPE_CNPJ, TYPE_NUMBER, TYPE_CPF_OR_CNPJ -> InputType.TYPE_CLASS_NUMBER
            else -> InputType.TYPE_CLASS_TEXT
        }
        mask = when (inputType) {
            TYPE_CUSTOM -> mask
            TYPE_DATE -> TextMask.DATE_MASK
            TYPE_PHONE -> TextMask.PHONE_MASK
            TYPE_CEP -> TextMask.CEP_MASK
            TYPE_CREDIT_CARD -> TextMask.CREDIT_CARD_MASK
            TYPE_CPF -> TextMask.CPF_MASK
            TYPE_CNPJ -> TextMask.CNPJ_MASK
            TYPE_CREDIT_CARD_DATE -> TextMask.CREDIT_CARD_DATE_MASK
            TYPE_CPF_OR_CNPJ -> TextMask.CPF_OR_CNPJ_MASK
            else -> null
        }
        mask?.let {
            mEditText.insertMask(it)
        }
        filterCharSequence?.let {
            setInputFilter(it)
        }
    }

    private fun setInputFilter(blockCharacterSet: String) {
        val filter = InputFilter { charSequence, _, _, _, _, _ ->
            charSequence?.toString()?.let { string ->
                var textFieldText = string
                blockCharacterSet.forEach { char ->
                    textFieldText = textFieldText.replace(char.toString(), "")
                }
                return@InputFilter textFieldText
            }
            null
        }
        mEditText.filters = arrayOf(filter)
    }

    private fun setEditTextListener() {
        mEditText.onFocusChangeListener = OnFocusChangeListener { _, focused ->
            editTextListener?.onFocusChange(focused)
            if (fieldNeedsValidation) {
                if (focused) {
                    if (defaultBackground >= 0)
                        this@AppTextInputLayout.setBackgroundResource(defaultBackground)

                    isErrorEnabled = false
                } else {
                    validate()
                }
            }
        }
    }

    fun setErrorListener(listener: EditTextListener) {
        editTextListener = listener
    }

    fun setRegex(regex: String, isRegexValid: Boolean) {
        this.regex = regex
        this.isRegexValid = isRegexValid
    }

    fun setParams(inputTextType: Int, hint: String?, emptinessValid: Boolean, minLength: Int) {
        inputType = inputTextType
        setInputType()
        hint?.let { setHint(it) }
        emptinessIsValid = emptinessValid
        this.minLength = minLength

        if (emptyErrorText.isNullOrEmpty())
            emptyErrorText = context.getString(R.string.apptextinputlayout_invalid_field, getHint().toString())
    }

    fun validate() {
        validateText(true)
    }

    fun validateWithoutError() {
        validateText(false)
    }

    private fun validateText(showError: Boolean) {
        if (fieldNeedsValidation) {
            //Type validations
            val isFieldValid = when (inputType) {
                TYPE_DATE -> isDateValid()
                TYPE_CREDIT_CARD_DATE -> isCardDateValid()
                TYPE_CNPJ -> isCnpjValid()
                TYPE_MAIL -> isMailValid()
                TYPE_MATCHING -> isMatchingValid()
                TYPE_CPF -> isCpfValid()
                TYPE_CEP -> isCepValid()
                TYPE_CPF_OR_CNPJ -> isCpfValid() || isCnpjValid()
                else -> true
            }

            if (defaultBackground >= 0)
                this.setBackgroundResource(defaultBackground)

            //Emptiness validation
            if (showError) {
                if (!isNotEmpty()) {
                    setEmptyErrorText()
                } else if (!isFieldValid) {
                    setInvalidErrorText()
                } else if (!isPatternValid()) {
                    setRegexErrorText()
                } else if (!isLengthValid()) {
                    if (mask.isNullOrBlank()) {
                        showMinLengthErrorText()
                    } else {
                        setInvalidErrorText()
                    }
                } else {
                    //if got here it's because field is valid
                    editTextListener?.onValid()
                    isErrorEnabled = false
                }
            }

        }
    }


    private fun isCepValid(): Boolean = mEditText.text.length == 9

    private fun isDateValid(): Boolean {
        val format = SimpleDateFormat("dd/MM/yyyy")
        return try {
            format.isLenient = false
            format.parse(mEditText.text.toString())
            true
        } catch (e: ParseException) {
            false
        }
    }

    private fun isCardDateValid(): Boolean {
        val format = SimpleDateFormat("MM/yy")
        return try {
            format.isLenient = false
            format.parse(mEditText.text.toString())
            true
        } catch (e: ParseException) {
            false
        }
    }

    //Returns true only if there' a regex and the Pattern matches
    private fun isPatternValid(): Boolean = regex.isNullOrEmpty() || Pattern.matches(regex, mEditText.text) == isRegexValid

    private fun isLengthValid(): Boolean {
        if (!mask.isNullOrEmpty()) {
            if (mask == TextMask.PHONE_MASK)
                return hasMinLengthOrMore(TextMask.PHONE_MASK.length - 1) || hasMinLengthOrMore(TextMask.CEL_PHONE_MASK.length)
            if (mask == TextMask.CPF_OR_CNPJ_MASK)
                return true
            minLength = mask!!.length
        }
        return hasMinLengthOrMore(minLength)
    }

    private fun isCpfValid(): Boolean = IsCpf.isValid(mEditText.text.toString())

    private fun isCnpjValid(): Boolean = IsCnpj.isValid(mEditText.text.toString())

    private fun isNotEmpty(): Boolean = emptinessIsValid || mEditText.text.isNotBlank()

    private fun isMailValid(): Boolean = IsEmail.isValid(mEditText.text.toString())

    private fun isMatchingValid(): Boolean = try {
        matchingReference?.text.toString() == mEditText.text.toString()
    } catch (e: NullPointerException) {
        true
    }

    private fun hasMinLengthOrMore(minLength: Int): Boolean {
        return minLength == 0 || mEditText.text.length >= minLength
    }

    fun setCustomError(errorMsg: String) {
        displayErrorBackground()
        editTextListener?.onError()
        error = if (errorMsg.isEmpty()) context.getString(R.string.apptextinputlayout_invalid_field, hint) else errorMsg
    }

    private fun setEmptyErrorText() {
        displayErrorBackground()
        editTextListener?.onError()
        error = if (emptyErrorText.isNullOrBlank()) context.getString(R.string.apptextinputlayout_empty_field, hint) else emptyErrorText
    }

    private fun setRegexErrorText() {
        displayErrorBackground()
        editTextListener?.onError()
        error = if (regexErrorText.isEmpty()) context.getString(R.string.apptextinputlayout_regex_field, hint) else regexErrorText
    }

    private fun setInvalidErrorText() {
        displayErrorBackground()
        editTextListener?.onError()
        error = if (invalidErrorText.isNullOrEmpty()) context.getString(R.string.apptextinputlayout_invalid_field, hint) else invalidErrorText
    }

    private fun showMinLengthErrorText() {
        displayErrorBackground()
        editTextListener?.onError()
        error = if (invalidErrorText.isNullOrEmpty()) context.getString(R.string.apptextinputlaoyut_password_field, hint, minLength) else invalidErrorText
    }

    private fun displayErrorBackground() {
        if (errorBackground >= 0)
            this.setBackgroundResource(errorBackground)
    }

    fun setMatchingReference(matchingReference: EditText) {
        this.matchingReference = matchingReference
    }

}
