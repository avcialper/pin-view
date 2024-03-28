package com.avcialper.pinview

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.GridLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.avcialper.pin_view.R
import com.avcialper.pin_view.databinding.PinViewBinding

class PinView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : GridLayout(context, attrs, defStyle) {

    private lateinit var editTextList: Array<EditText>
    private lateinit var inputMethodManager: InputMethodManager

    // attr
    private var selectedBorderColor: Int
    private var unselectedBorderColor: Int
    private var errorBorderColor: Int
    private var background: Int
    private var borderWidth: Int
    private var textColor: Int

    // pin box styles
    private lateinit var pinSelected: GradientDrawable
    private lateinit var pinUnselected: GradientDrawable
    private lateinit var pinError: GradientDrawable

    // pin status
    private var pin: String = ""

    // to be used to inform listeners
    private var listener: PinViewListener? = null

    init {
        inflate(context, R.layout.pin_view, this)

        val pinAttr = context.obtainStyledAttributes(attrs, R.styleable.pin_view, 0, 0)

        selectedBorderColor = pinAttr.getColor(
            R.styleable.pin_view_selected_border_color,
            ContextCompat.getColor(context, R.color.main)
        )

        unselectedBorderColor = pinAttr.getColor(
            R.styleable.pin_view_unselected_border_color,
            ContextCompat.getColor(context, R.color.default_border)
        )

        errorBorderColor = pinAttr.getColor(
            R.styleable.pin_view_error_border_color,
            ContextCompat.getColor(context, R.color.default_error_border)
        )

        background = pinAttr.getColor(
            R.styleable.pin_view_pin_background,
            ContextCompat.getColor(context, R.color.default_background)
        )

        borderWidth = pinAttr.getInt(
            R.styleable.pin_view_border_width,
            6
        )

        textColor = pinAttr.getColor(
            R.styleable.pin_view_android_textColor,
            ContextCompat.getColor(context, R.color.main)
        )

        pinAttr.recycle()

        initUI()
    }

    private fun initUI() {

        pinSelected =
            ContextCompat.getDrawable(context, R.drawable.pin_selected) as GradientDrawable
        pinUnselected = ContextCompat.getDrawable(context, R.drawable.pin) as GradientDrawable
        pinError = ContextCompat.getDrawable(context, R.drawable.pin_error) as GradientDrawable

        pinSelected.setStroke(borderWidth, selectedBorderColor)
        pinUnselected.setStroke(borderWidth, unselectedBorderColor)
        pinError.setStroke(borderWidth, errorBorderColor)

        pinSelected.setColor(background)
        pinUnselected.setColor(background)
        pinError.setColor(background)

        inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager


        setEditTextList()
    }

    private fun setEditTextList() {
        val binding = PinViewBinding.bind(this)
        binding.apply {
            editTextList = arrayOf(
                firstChar,
                secondChar,
                thirdChar,
                fourthChar,
                fifthChar,
                sixthChar
            )
            setStyles()
            setListeners()
        }
    }

    fun setPinListener(listener: PinViewListener) {
        this.listener = listener
    }

    // to change the pin box background according to the error status
    fun changePinBoxBackground(isError: Boolean) {
        editTextList.forEach { editText ->
            editText.background =
                if (isError)
                    pinError
                else
                    pinUnselected
        }
    }

    // when this function is called, information is passed to the listeners
    private fun onPinEntryCompleted() {
        listener?.onPinEntryCompleted(pin)
    }

    // setting the pin box's styles
    private fun setStyles() {
        editTextList.forEach { editText ->
            editText.background = pinUnselected
            editText.setTextColor(textColor)
        }
    }

    // add listeners to all edit text elements
    private fun setListeners() {
        editTextList.forEach { editText ->
            editText.clearFocus()
            editText.setOnFocusChangeListener { view, hasFocus ->
                // to set the pin box background color
                if (pin.length != 6) {
                    if (hasFocus)
                        editText.background = pinSelected
                    else
                        editText.background = pinUnselected
                }
                // to focus on the current box
                if (hasFocus && pin.length < 6) {
                    editTextList[pin.length].requestFocus()
                    changeKeyboardVisibility(true, view)
                }
                // if the input size equal to 6 and password not matched
                else if (hasFocus && pin.length == 6) {
                    editTextList[pin.lastIndex].requestFocus()
                    changeKeyboardVisibility(true, view)
                }
            }

            editText.addTextChangedListener { text ->
                pin += text.toString()

                // check password if input length is equal to 6
                if (pin.length == 6) {
                    editText.clearFocus()
                    changeKeyboardVisibility(false, editText)
                    onPinEntryCompleted()
                } else
                    editText.onEditorAction(EditorInfo.IME_ACTION_NEXT)
            }

            // to handle delete button press
            editText.setOnKeyListener { _, keyCode, keyEvent ->
                if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.action == KeyEvent.ACTION_DOWN) {

                    // change to selector background if the edit text background is error background
                    if (editText.background.constantState === ContextCompat.getDrawable(
                            context,
                            R.drawable.pin_error
                        )!!.constantState
                    ) {
                        changePinBoxBackground(false)
                    }

                    // delete last element in input
                    if (pin.isNotEmpty()) {
                        pin = pin.substring(0, pin.lastIndex)
                        editTextList[pin.length].text.clear()
                        editText.onEditorAction(EditorInfo.IME_ACTION_PREVIOUS)
                    }

                    true
                } else
                    false
            }
        }
    }

    private fun changeKeyboardVisibility(isOpen: Boolean, view: View) {
        if (isOpen)
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        else
            inputMethodManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
    }

}