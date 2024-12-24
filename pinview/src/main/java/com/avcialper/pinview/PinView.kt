package com.avcialper.pinview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.avcialper.pin_view.R
import com.avcialper.pin_view.databinding.PinViewBinding
import com.avcialper.pinview.utils.PinBoxType

@SuppressLint("CustomViewStyleable")
class PinView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private val pinBoxList: MutableList<EditText> by lazy {
        val binding = getBinding()
        mutableListOf(
            binding.firstChar.pinBox,
            binding.secondChar.pinBox,
            binding.thirdChar.pinBox,
            binding.fourthChar.pinBox,
            binding.fifthChar.pinBox,
            binding.sixthChar.pinBox,
            binding.seventhChar.pinBox,
            binding.eightChar.pinBox
        )
    }

    // Keyboard manager.
    private var inputMethodManager: InputMethodManager

    // OnPinCompleted listener for
    private var onPinCompleted: ((String) -> Boolean)? = null

    // PinBoxGradiantDrawable
    private var pinBoxUnselectedView: GradientDrawable
    private var pinBoxSelectedView: GradientDrawable
    private var pinBoxSuccessView: GradientDrawable
    private var pinBoxErrorView: GradientDrawable

    // attrs
    private var pinAttrs: TypedArray
    private var textSize: Float
    private var textColor: Int
    private var width: Int
    private var height: Int
    private var borderWidth: Int
    private var marginHorizontal: Int
    private var boxCount: Int
    private var selectedPinBackgroundColor: Int
    private var selectedPinBorderColor: Int
    private var unselectedPinBackgroundColor: Int
    private var unselectedPinBorderColor: Int
    private var successPinBackgroundColor: Int
    private var successPinBorderColor: Int
    private var errorPinBackgroundColor: Int
    private var errorPinBorderColor: Int

    // pin status
    private var pin: String = ""

    /**
     * Sets a listener that will be triggered when the PIN input is completed.
     *
     * @param onSubmit A lambda function that receives the completed PIN as a String
     *                 and returns a Boolean indicating whether the PIN is valid or not.
     */
    fun setOnPinCompletedListener(onSubmit: (String) -> Boolean) {
        onPinCompleted = onSubmit
    }

    init {
        inflate(context, R.layout.pin_view, this)
        val defBackgroundColor = getColor(R.color.default_background)

        pinAttrs = context.obtainStyledAttributes(attrs, R.styleable.pin_view, defStyle, 0)

        pinBoxUnselectedView = getDrawable(R.drawable.pin) as GradientDrawable
        pinBoxSelectedView = getDrawable(R.drawable.pin_selected) as GradientDrawable
        pinBoxSuccessView = getDrawable(R.drawable.pin_success) as GradientDrawable
        pinBoxErrorView = getDrawable(R.drawable.pin_error) as GradientDrawable

        textSize = getAttrDimen(
            R.styleable.pin_view_android_textSize, getDimen(R.dimen.input_text_size)
        )

        textColor = getAttrColor(R.styleable.pin_view_android_textColor, getColor(R.color.black))

        width = getAttrDimen(R.styleable.pin_view_width, getDimen(R.dimen.width_height)).toInt()

        height = getAttrDimen(R.styleable.pin_view_height, getDimen(R.dimen.width_height)).toInt()

        borderWidth = getAttrDimen(
            R.styleable.pin_view_pin_border_width, getDimen(R.dimen.border_width)
        ).toInt()

        marginHorizontal = getAttrDimen(
            R.styleable.pin_view_margin_horizontal, getDimen(R.dimen.margin_horizontal)
        ).toInt()

        boxCount = getAttrInt(R.styleable.pin_view_box_count, 6)
        boxCount = if (boxCount < 4)
            4
        else if (boxCount > 8)
            8
        else
            boxCount

        selectedPinBackgroundColor = getAttrColor(
            R.styleable.pin_view_selected_background_color, defBackgroundColor
        )

        selectedPinBorderColor = getAttrColor(
            R.styleable.pin_view_selected_border_color, getColor(R.color.main)
        )

        unselectedPinBackgroundColor = getAttrColor(
            R.styleable.pin_view_unselected_background_color, defBackgroundColor
        )

        unselectedPinBorderColor = getAttrColor(
            R.styleable.pin_view_unselected_border_color, getColor(R.color.default_border)
        )

        successPinBackgroundColor = getAttrColor(
            R.styleable.pin_view_success_background_color, defBackgroundColor
        )

        successPinBorderColor = getAttrColor(
            R.styleable.pin_view_success_border_color, getColor(R.color.default_success_border)
        )

        errorPinBackgroundColor = getAttrColor(
            R.styleable.pin_view_error_background_color, defBackgroundColor
        )

        errorPinBorderColor = getAttrColor(
            R.styleable.pin_view_error_border_color, getColor(R.color.default_error_border)
        )

        pinAttrs.recycle()

        inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        setPinBoxCount()
        setPinBoxViews()
        changePinBoxListBackground(PinBoxType.UNSELECTED)
        setPinBoxListStyle()
        setPinBoxListeners()
    }

    private fun setPinBoxCount() {
        for (index in 7 downTo boxCount) {
            pinBoxList[index].visibility = View.GONE
            pinBoxList.removeAt(index)
        }
    }

    private fun setPinBoxViews() {
        pinBoxUnselectedView.setColorAndStroke(
            unselectedPinBackgroundColor, unselectedPinBorderColor
        )
        pinBoxSelectedView.setColorAndStroke(selectedPinBackgroundColor, selectedPinBorderColor)
        pinBoxSuccessView.setColorAndStroke(successPinBackgroundColor, successPinBorderColor)
        pinBoxErrorView.setColorAndStroke(errorPinBackgroundColor, errorPinBorderColor)
    }

    private fun GradientDrawable.setColorAndStroke(backgroundColor: Int, borderColor: Int) {
        setColor(backgroundColor)
        setStroke(borderWidth, borderColor)
    }

    private fun getBinding(): PinViewBinding = PinViewBinding.bind(this)

    private fun setPinBoxListStyle() {
        pinBoxList.forEach { pinBox ->
            pinBox.background = pinBoxUnselectedView
            pinBox.setTextColor(textColor)
            pinBox.textSize = textSize

            val params = pinBox.layoutParams as MarginLayoutParams
            params.width = width
            params.height = height
            params.setMargins(marginHorizontal, 0, marginHorizontal, 0)
            pinBox.layoutParams = params
        }
    }

    private fun changePinBoxBackground(view: EditText, pinBoxType: PinBoxType) {
        view.background = when (pinBoxType) {
            PinBoxType.UNSELECTED -> pinBoxUnselectedView
            PinBoxType.SELECTED -> pinBoxSelectedView
            PinBoxType.SUCCESS -> pinBoxSuccessView
            PinBoxType.ERROR -> pinBoxErrorView
        }
    }

    private fun changePinBoxListBackground(pinBoxType: PinBoxType) {
        pinBoxList.forEachIndexed { _, pinBox ->
            changePinBoxBackground(pinBox, pinBoxType)
        }
    }

    private fun changePinBoxEditableStatus(isEnable: Boolean) {
        pinBoxList.forEach { pinBox ->
            pinBox.isEnabled = isEnable
        }
    }

    private fun setPinBoxListeners() {
        pinBoxList.forEachIndexed { index, pinBox ->
            setPinBoxFocusChangeListener(index, pinBox)
            addPinBoxTextChangedListener(pinBox)
            setPinBoxOnKeyListener(pinBox)
        }
    }

    private fun setPinBoxFocusChangeListener(index: Int, pinBox: EditText) {
        pinBox.setOnFocusChangeListener { _, _ ->
            val pinLength = pin.length
            if (pinLength == index)
                changePinBoxBackground(pinBox, PinBoxType.SELECTED)
            else if (pinLength != pinBoxList.size) {
                pinBoxList[pinLength].requestFocus()
                changeKeyboardVisibility(true, pinBox)
                changePinBoxBackground(pinBox, PinBoxType.UNSELECTED)
            }
        }
    }

    private fun addPinBoxTextChangedListener(pinBox: EditText) {
        pinBox.addTextChangedListener { text ->
            pin += text
            if (pin.length == pinBoxList.size) {
                changeKeyboardVisibility(false, this)
                changePinBoxEditableStatus(false)
                onPinCompleted?.let { pinCompleted ->
                    val isCorrect = pinCompleted.invoke(pin)
                    if (isCorrect) changePinBoxListBackground(PinBoxType.SUCCESS)
                    else changePinBoxListBackground(PinBoxType.ERROR)
                }
                changePinBoxEditableStatus(true)
            } else pinBoxList[pin.length].requestFocus()
        }
    }

    private fun setPinBoxOnKeyListener(pinBox: EditText) {
        pinBox.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.action == KeyEvent.ACTION_DOWN) {

                val backgroundConstantState = pinBox.background.constantState
                val pinError =
                    ContextCompat.getDrawable(context, R.drawable.pin_error)!!.constantState
                val pinSuccess =
                    ContextCompat.getDrawable(context, R.drawable.pin_success)!!.constantState

                if (backgroundConstantState == pinError || backgroundConstantState == pinSuccess)
                    changePinBoxListBackground(PinBoxType.UNSELECTED)

                if (pin.isNotEmpty()) {
                    pin = pin.substring(0, pin.lastIndex)
                    pinBoxList[pin.length].text.clear()
                    if (pin.isNotEmpty()) pinBox.onEditorAction(EditorInfo.IME_ACTION_PREVIOUS)
                }
                true
            } else false
        }
    }

    private fun getDrawable(id: Int) = ContextCompat.getDrawable(context, id)

    private fun getAttrDimen(index: Int, defValue: Float) = pinAttrs.getDimension(index, defValue)

    private fun getAttrColor(index: Int, defValue: Int) = pinAttrs.getColor(index, defValue)

    private fun getAttrInt(index: Int, defValue: Int) = pinAttrs.getInt(index, defValue)

    private fun getDimen(index: Int) = resources.getDimension(index)

    private fun getColor(index: Int) = resources.getColor(index, null)

    private fun changeKeyboardVisibility(isOpen: Boolean, view: View) {
        if (isOpen)
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        else
            inputMethodManager.hideSoftInputFromWindow(
                view.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY
            )
    }
}