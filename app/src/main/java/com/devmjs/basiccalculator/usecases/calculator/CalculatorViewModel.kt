/*
 * Copyright (c) 2021, DevMJS. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this code.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Please contact DevMJS on contact.devmjs@gmail.com if you need
 * additional information or have any questions.
 */

package com.devmjs.basiccalculator.usecases.calculator

import android.content.Context
import androidx.lifecycle.ViewModel
import com.devmjs.basiccalculator.R
import com.devmjs.basiccalculator.provider.DateProvider
import com.devmjs.basiccalculator.provider.PreferencesKey
import com.devmjs.basiccalculator.provider.PreferencesProvider
import com.devmjs.basiccalculator.usecases.history.utils.HistoryItem
import com.devmjs.basiccalculator.usecases.history.utils.HistoryProvider
import com.devmjs.basiccalculator.utils.Methods
import com.devmjs.math.*
import java.lang.Exception

class CalculatorViewModel: ViewModel()
{
    lateinit var textResult: String
    lateinit var textNumbers: String
    lateinit var textResultAux: String
    private var isEqualPressed = false
    var errorText = ""
    var mathError = ""
    var isAllOpen = false
    var isSecondPressed = false
    lateinit var mathMode: MathMode
    var selectionStart = 0
    var selectionEnd = 0
    var addingText = false

    fun initVars(context: Context)
    {
        val provider = PreferencesProvider(context)
        textNumbers = provider.getString(PreferencesKey.TEXT_NUMBERS, "")!!
        textResult = provider.getString(PreferencesKey.TEXT_RESULT, "")!!
        textResultAux = textResult
        isEqualPressed = provider.getBoolean(PreferencesKey.EQUAL_PRESSED)
        isAllOpen = provider.getBoolean(PreferencesKey.IS_ALL_OPEN)
        mathMode =
            MathMode.valueOf(provider.getString(PreferencesKey.MATH_MODE, MathMode.DEG.name)!!)
        isSecondPressed = provider.getBoolean(PreferencesKey.IS_SECOND_PRESSED)
        selectionStart = textNumbers.length
        selectionEnd = textNumbers.length
        errorText = context.getString(R.string.error)
        mathError = context.getString(R.string.math_error)
        if (verifyToCalculate())
        {
            val res = calculate()
            textResult = if (res != "") "=$res" else res
        } else if (textNumbers == "") textResult = ""
    }

    fun savePreferences(context: Context)
    {
        val provider = PreferencesProvider(context)
        provider.putString(PreferencesKey.TEXT_NUMBERS, textNumbers)
        provider.putString(PreferencesKey.TEXT_RESULT, textResult)
        provider.putString(PreferencesKey.MATH_MODE, mathMode.name)
        provider.putBoolean(PreferencesKey.EQUAL_PRESSED, isEqualPressed)
        provider.putBoolean(PreferencesKey.IS_ALL_OPEN, isAllOpen)
        provider.putBoolean(PreferencesKey.IS_SECOND_PRESSED, isSecondPressed)
    }

    fun reset()
    {
        isEqualPressed = false
        textNumbers = ""
        textResult = ""
        selectionStart = 0
        selectionEnd = 0
    }

    fun delete(): Int
    {
        val start = textNumbers.substring(0, selectionStart)
        if (isEqualPressed && !((start.matches(Regex("\\(*"))
                        || Methods.endsWithSpecialFun(start)
                        || Methods.endsWithSqrt(start))))
        {
            isEqualPressed = false
            textNumbers = ""
            textResult = ""
            addingText = true
            selectionStart = 0
            selectionEnd = 0
            return 0
        } else if (start != "")
            return 1
        return -1
    }

    fun actionSymbols(symbol: String,
                      conditionsStart: Array<String>?,
                      verifyEqual: Boolean = false): Boolean
    {
        var start = textNumbers.substring(0, selectionStart)
        var end = textNumbers.substring(selectionEnd, textNumbers.length)
        val operators = arrayOf("+", "-", "×", "÷", "^")
        var execute = false

        if (conditionsStart == null || !Methods.check(start, conditionsStart))
        {
            if (!(symbol != "-" && (operators.contains(symbol) && start == "")))
            {
                execute = true
                if (verifyEqual && isEqualPressed)
                {
                    start = ""
                    end = ""
                } else if (Methods.check(start, arrayOf(".")))
                    start += "0"
                selectionStart = start.length + symbol.length
            }
        } else if (operators.contains(symbol) && Methods.check(start,
                                                               operators) && start.isNotEmpty()
                && start != "-" && !Methods.check(start, arrayOf("(-", "+-", "×-", "÷-", "^-"))
        )
        {
            execute = true
            start = start.substring(0, start.length - 1)
            selectionStart = start.length + 1
        }
        if (execute)
        {
            isEqualPressed = false
            selectionEnd = selectionStart
            textNumbers = "$start$symbol$end"
            val res = calculate()
            textResult = if (res != "") "=$res" else res
            addingText = true
            return true
        }
        return false
    }

    fun equal(context: Context): Boolean
    {
        if (!isEqualPressed)
        {
            var ban = true
            try
            {
                val num1 = calculate(false).toDouble()
                val num2 = regularizeParentheses(textNumbers).toDouble()
                if (num1 == num2)
                    ban = false
            } catch (e: Exception)
            {
            }
            if (ban)
            {
                isEqualPressed = true
                if (verifyToCalculate())
                {
                    val res = calculate()
                    if (res != errorText && res != mathError)
                    {
                        val provider = HistoryProvider(context)
                        provider.saveHistoryItem(DateProvider.getDate(), HistoryItem(
                                regularizeParentheses(textNumbers
                                        /*.replace("\n", "")*/), res))
                        textNumbers = res
                        textResult = ""
                    } else
                        textResult = res
                    selectionStart = textNumbers.length
                    selectionEnd = selectionStart
                } else if (Methods.endsWithSqrt(textNumbers) || textNumbers.matches(Regex("\\(+"))
                        || Methods.isSpecialFun(textNumbers) || Methods.endsWithSpecialFun(
                                textNumbers)
                        || (textNumbers.length == 1 && Methods.check(textNumbers,
                                                                     arrayOf("-", "(", "√"))))
                {
                    textResult = errorText
                }
                addingText = true
                return true
            }
        }
        return false
    }

    fun fact(): Boolean
    {
        val start = textNumbers.substring(0, selectionStart)
        if (start != "" && (Methods.endsWithNumber(start)
                        || Methods.check(start, arrayOf(")", "!", Constants.e, "π"))))
            return actionSymbols("!", arrayOf("+", "-", "×", "÷", "^", "(", "√"))
        return false
    }

    fun calculate(ignore: Boolean = true): String
    {
        var res: String
        var ban = true
        try
        {
            res =
                if (!Methods.endsWithSqrt(textNumbers)) calculateParenthesis(textNumbers, mathMode)
                else errorText

            try
            {
                val num1 = res.toDouble()
                val num2 = regularizeParentheses(textNumbers).toDouble()
                if (num1 == num2)
                    ban = false
            } catch (e: Exception)
            {
            }
            when
            {
                res.startsWith('.') -> res = "0$res"
                res.endsWith('.') -> res = res.substring(0, res.length - 1)
                res.startsWith("-.") -> res = "-0${res.substring(1, res.length)}"
                res.endsWith(".0") -> res = res.substring(0, res.length - 2)
                res.contains(Constants.infinity) -> res = res.replace(Constants.infinity, "∞")
                res == "NaN" -> res = mathError
                !isNumber(res) -> res = errorText
            }
        } catch (e: Exception)
        {
            res = errorText
        }
        if ((!isEqualPressed && (!isNumber(res) && res != "∞") || (ignore && !ban)))
            res = ""
        return res
    }

    fun actionNumbers(i: Int)
    {
        val start = textNumbers.substring(0, selectionStart)
        val end = textNumbers.substring(selectionEnd, textNumbers.length)
        if ((!isEqualPressed) || (isEqualPressed &&
                        (start.matches(Regex("\\(*"))
                                || Methods.endsWithSpecialFun(start)
                                || Methods.endsWithSqrt(start)
                                || textResult == errorText
                                || textResult == mathError)))
        {
            selectionStart = start.length + 1
            selectionEnd = selectionStart
            textNumbers = "$start$i$end"
            isEqualPressed = false
            val res = calculate()
            textResult = if (res != "") "=$res" else res
        } else if (isEqualPressed)
        {
            isEqualPressed = false
            selectionStart = 1
            selectionEnd = 1
            textNumbers = i.toString()
            textResult = ""
        }
        addingText = true
    }

    fun point(): Boolean
    {
        var compute = false
        if (isEqualPressed)
        {
            isEqualPressed = false
            addingText = true
            selectionStart = 2
            selectionEnd = 2
            textNumbers = "0."
            textResult = ""
            compute = true
        } else
        {
            var start = textNumbers.substring(0, selectionStart)
            val end = textNumbers.substring(selectionEnd, textNumbers.length)
            val point = Methods.checkPoint(start, true) || Methods.checkPoint(end, false)
            if (!point && (textNumbers == "" || Methods.check(start, arrayOf("+", "-", "×", "÷",
                                                                             "^", "√", "(", ")",
                                                                             "!", "π",
                                                                             Constants.e))))
            {
                start += "0."
                compute = true
            } else if (!point && !Methods.check(start, arrayOf("π", Constants.e, ")")))
            {
                start += "."
                compute = true
            }
            if (compute)
            {
                selectionStart = start.length
                selectionEnd = selectionStart
                addingText = true
                textNumbers = "$start$end"
                val res = calculate()
                textResult = if (res != "") "=$res" else res
            }
        }
        return compute
    }

    fun deleteLastPosition()
    {
        val start = textNumbers.substring(0, selectionStart)
        val end = textNumbers.substring(selectionEnd, textNumbers.length)
        isEqualPressed = false
        if (start == textNumbers && (textNumbers.length == 1 || textNumbers == Constants.e
                        || Methods.isSpecialFun(textNumbers)))
        {
            textNumbers = ""
            textResult = ""
            addingText = true
            selectionEnd = 0
            selectionStart = 0
        } else
        {
            var array = arrayOf(start, "0")
            if (selectionStart == selectionEnd)
            {
                array = Methods.trimToLastPosition(start)
                selectionStart -= array[1].toInt()
            }
            selectionEnd = selectionStart
            textNumbers = "${array[0]}$end"
            val res = calculate()
            textResult = if (res != "") "=$res" else res
            addingText = true
        }
    }

    fun handleOnSelectionChanged(start: Int, end: Int): Boolean
    {
        if (!addingText)
        {
            if (start == end)
            {
                val startStr = textNumbers.substring(0, start)
                val endStr = textNumbers.substring(end, textNumbers.length)
                val startString = textNumbers.substring(0, selectionStart)
                val endString = textNumbers.substring(selectionEnd, textNumbers.length)

                val addition =
                    Methods.checkTextSelected(startStr, endStr, startString, endString, start,
                                              end, selectionStart, selectionEnd, true)
                when
                {
                    addition == Methods.CHANGE_SELECTION -> return false
                    addition != Methods.NO_CHANGE_SELECTION ->
                    {
                        selectionStart = start + addition
                        selectionEnd = selectionStart
                        return false
                    }
                }
            } else
            {
                val startStr1 = textNumbers.substring(0, start)
                val endStr1 = textNumbers.substring(start, textNumbers.length)
                val endStr2 = textNumbers.substring(selectionStart, textNumbers.length)
                val startStr2 = textNumbers.substring(0, selectionStart)
                val addition =
                    Methods.checkTextSelected(startStr1, endStr1, startStr2, endStr2, start, end,
                                              selectionStart, selectionEnd, true)
                when
                {
                    addition == Methods.CHANGE_SELECTION -> return false
                    addition != Methods.NO_CHANGE_SELECTION ->
                    {
                        val aux = start + addition
                        if (aux != selectionEnd)
                            selectionStart = aux
                        return false
                    }
                }
                val startString1 = textNumbers.substring(0, end)
                val endString1 = textNumbers.substring(end, textNumbers.length)
                val startString2 = textNumbers.substring(0, selectionEnd)
                val endString2 = textNumbers.substring(selectionEnd, textNumbers.length)
                val addition2 = Methods.checkTextSelected(startString1, endString1, startString2,
                                                          endString2, start, end,
                                                          selectionStart, selectionEnd)
                when
                {
                    addition2 == Methods.CHANGE_SELECTION -> return false
                    addition2 != Methods.NO_CHANGE_SELECTION ->
                    {
                        val aux = end + addition2
                        if (aux != selectionStart)
                            selectionEnd = aux
                        return false
                    }
                }
            }
        }
        return true
    }

    fun handleOnTextChanged(isCutText: Boolean,
                            isPastedText:Boolean,
                            start: Int,
                            lengthAfter: Int,
                            text: String): Boolean
    {
        if (isCutText)
        {
            selectionStart = start
            selectionEnd = start
            textNumbers = text
            var res = calculate()
            res = if (res != "") "=$res" else res
            textResult = if (textNumbers.isNotEmpty())
                res
            else
                ""
            return true
        } else if (isPastedText)
        {
            selectionStart = start + lengthAfter
            selectionEnd = selectionStart
            textNumbers = Methods.toLowerCase(text)
                .replace('*', '×')
                .replace('⋅', '×')
                .replace('/', '÷')
                .replace("sen", Constants.sin)
            var res = calculate()
            res = if (res != "") "=$res" else res
            textResult = if (textNumbers.isNotEmpty())
                res
            else
                ""
            return true
        }
        return false
    }

    private fun verifyToCalculate(): Boolean = textNumbers != ""
            && !(textNumbers.length == 1 && Methods.check(textNumbers, arrayOf("-", "(", "√")))
            && !textNumbers.matches(Regex("\\(*"))
            && !Methods.endsWithSqrt(textNumbers)
            && textResult != errorText
            && textResult != mathError
            && textResult != "=$errorText"
            && textResult != "=$mathError"
            && !Methods.isSpecialFun(textNumbers)
            && !Methods.endsWithSpecialFun(textNumbers)
}