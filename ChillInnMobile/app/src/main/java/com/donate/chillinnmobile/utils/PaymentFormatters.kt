package com.donate.chillinnmobile.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Collection of TextWatcher implementations for payment-related input formatting.
 * Provides formatters for credit card numbers, expiry dates, and CVV codes.
 */
object PaymentFormatters {

    /**
     * Apply credit card number formatter to an EditText
     * @param editText EditText to apply formatter to
     * @param textInputLayout Optional TextInputLayout for error display
     */
    fun applyCardNumberFormatter(editText: EditText, textInputLayout: TextInputLayout? = null) {
        editText.addTextChangedListener(CardNumberTextWatcher(editText, textInputLayout))
    }

    /**
     * Apply expiry date formatter to an EditText
     * @param editText EditText to apply formatter to
     * @param textInputLayout Optional TextInputLayout for error display
     */
    fun applyExpiryDateFormatter(editText: EditText, textInputLayout: TextInputLayout? = null) {
        editText.addTextChangedListener(ExpiryDateTextWatcher(editText, textInputLayout))
    }

    /**
     * Apply CVV formatter to an EditText
     * @param editText EditText to apply formatter to
     * @param cardTypeProvider Function to get current card type
     * @param textInputLayout Optional TextInputLayout for error display
     */
    fun applyCvvFormatter(
        editText: EditText,
        cardTypeProvider: () -> PaymentValidator.CardType,
        textInputLayout: TextInputLayout? = null
    ) {
        editText.addTextChangedListener(CvvTextWatcher(editText, cardTypeProvider, textInputLayout))
    }

    /**
     * TextWatcher for credit card number formatting
     */
    private class CardNumberTextWatcher(
        private val editText: EditText,
        private val textInputLayout: TextInputLayout? = null
    ) : TextWatcher {
        private var current = ""
        private var isDeleting = false
        private var cursorPosition = 0

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            cursorPosition = editText.selectionStart
            isDeleting = count > after
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Not used
        }

        override fun afterTextChanged(s: Editable?) {
            if (s.toString() != current) {
                val userInput = s.toString().replace("[^\\d]".toRegex(), "")
                if (userInput.length <= 16) {
                    val formatted = PaymentValidator.formatCardNumber(userInput)
                    
                    // Update text without triggering the watcher
                    current = formatted
                    editText.removeTextChangedListener(this)
                    s?.replace(0, s.length, current)
                    editText.addTextChangedListener(this)
                    
                    // Determine new cursor position
                    var newCursorPosition = cursorPosition
                    if (!isDeleting) {
                        // If input length has increased and a space has been added, move cursor forward
                        val spacesBeforeCursor = current.substring(0, minOf(cursorPosition, current.length))
                            .count { it == ' ' }
                        val originalSpacesBeforeCursor = s.toString().substring(0, minOf(cursorPosition, s.toString().length))
                            .count { it == ' ' }
                        if (spacesBeforeCursor > originalSpacesBeforeCursor) {
                            newCursorPosition++
                        }
                    }
                    
                    // Set cursor position
                    if (newCursorPosition <= current.length) {
                        editText.setSelection(newCursorPosition)
                    } else {
                        editText.setSelection(current.length)
                    }
                    
                    // Update card type hint
                    val cardType = PaymentValidator.getCardType(userInput)
                    textInputLayout?.let {
                        if (userInput.isEmpty()) {
                            it.hint = "Card Number"
                        } else if (cardType != PaymentValidator.CardType.UNKNOWN) {
                            it.hint = "Card Number (${cardType.name.capitalize()})"
                        } else {
                            it.hint = "Card Number"
                        }
                    }
                }
            }
        }
    }

    /**
     * TextWatcher for expiry date formatting
     */
    private class ExpiryDateTextWatcher(
        private val editText: EditText,
        private val textInputLayout: TextInputLayout? = null
    ) : TextWatcher {
        private var current = ""
        private var isDeleting = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            isDeleting = count > after
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Not used
        }

        override fun afterTextChanged(s: Editable?) {
            if (s.toString() != current) {
                val userInput = s.toString().replace("[^\\d]".toRegex(), "")
                if (userInput.length <= 4) {
                    val formatted = StringBuilder()
                    
                    for (i in userInput.indices) {
                        if (i == 2 && i < userInput.length) {
                            formatted.append("/")
                        }
                        formatted.append(userInput[i])
                    }
                    
                    // Update text without triggering the watcher
                    current = formatted.toString()
                    editText.removeTextChangedListener(this)
                    s?.replace(0, s.length, current)
                    editText.addTextChangedListener(this)
                    
                    // Set cursor position
                    if (current.length == 3 && !isDeleting) {
                        editText.setSelection(current.length)
                    } else if (isDeleting && current.length == 2) {
                        editText.setSelection(current.length)
                    } else {
                        // Move cursor to end of input
                        editText.setSelection(current.length)
                    }
                }
            }
        }
    }

    /**
     * TextWatcher for CVV formatting
     */
    private class CvvTextWatcher(
        private val editText: EditText,
        private val cardTypeProvider: () -> PaymentValidator.CardType,
        private val textInputLayout: TextInputLayout? = null
    ) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not used
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Not used
        }

        override fun afterTextChanged(s: Editable?) {
            val cardType = cardTypeProvider()
            val maxLength = when (cardType) {
                PaymentValidator.CardType.AMEX -> 4
                else -> 3
            }
            
            // Update hint
            textInputLayout?.let {
                if (cardType == PaymentValidator.CardType.AMEX) {
                    it.hint = "Security Code (4 digits)"
                } else {
                    it.hint = "CVV (3 digits)"
                }
            }
            
            // Limit input to digits only and to the correct length
            val userInput = s.toString().replace("[^\\d]".toRegex(), "")
            if (userInput != s.toString() || userInput.length > maxLength) {
                val newInput = userInput.take(maxLength)
                editText.removeTextChangedListener(this)
                s?.replace(0, s.length, newInput)
                editText.addTextChangedListener(this)
                editText.setSelection(newInput.length)
            }
        }
    }

    /**
     * Extension function to capitalize first letter of a string
     */
    private fun String.capitalize(): String {
        return if (this.isEmpty()) this
        else this.lowercase().replaceFirstChar { it.uppercase() }
    }
} 