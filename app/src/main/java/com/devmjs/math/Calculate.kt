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

package com.devmjs.math

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.ArrayList

/**
 *  Loop the String looking from the pairs of inner parentheses
 *  to the outermost ones, adding everything that is inside to
 *  a Parenthesis Node with their respective operations, if there
 *  is no parenthesis, everything passes to a hierarchy of operations
 *  as a Parenthesis Node too.
 *  @return The result of the operations in form of String.
 */
fun calculateParenthesis(_operations: String, mathMode: MathMode): String
{
    /*
    * First it verifies that the parentheses are in the correct
    * order and if parentheses are missing it adds them.
    */
    val operations = regularizeParentheses(_operations)

    /*
    * After this, split the array of operations and save it in
    * form of an array.
    */
    val array =
        operations.split(Regex("(?<=${Constants.arcSin})|(?<=${Constants.arcCos})|(?<=${Constants.arcTan})|(?<=${Constants.sin})|(?<=${Constants.cos})|(?<=${Constants.tan})|(?<=${Constants.lg})|(?<=${Constants.ln})|(?<=${Constants.fact})|(?<=\\()|(?<=\\))|(?<=\\+)|(?<=-)|(?<=÷)|(?<=×)|(?<=\\^)|(?<=√)|(?<=π)|(?<=${Constants.e})|(?<=∞)"))
    var arrayOperators: ArrayList<Any?> = arrayListOf()

    /*
    * It removes the terminal elements of the already separated
    * String elements and adds it as a new element of the array.
    */
    for (i in array)
    {
        if (i != "")
        {
            val st = endsWithParenthesisOrSign(i)
            if (st == null)
            {
                when
                {
                    i.endsWith(Constants.arcSin) || i.endsWith(Constants.arcCos) || i.endsWith(
                            Constants.arcTan) ->
                    {
                        val aux = i.substring(0, i.indexOf("a"))
                        if (aux.isNotEmpty())
                            arrayOperators.add(aux)
                        arrayOperators.add(i.substring(i.indexOf("a"), i.length))
                    }
                    i.endsWith(Constants.sin) || i.endsWith(Constants.cos) || i.endsWith(Constants.tan) ->
                    {
                        val aux = i.substring(0, i.indexOfAny("sct".toCharArray()))
                        if (aux.isNotEmpty())
                            arrayOperators.add(aux)
                        arrayOperators.add(i.substring(i.indexOfAny("sct".toCharArray()), i.length))
                    }
                    i.endsWith(Constants.lg) || i.endsWith(Constants.ln) ->
                    {
                        val aux = i.substring(0, i.indexOf("l"))
                        if (aux.isNotEmpty())
                            arrayOperators.add(aux)
                        arrayOperators.add(i.substring(i.indexOf("l"), i.length))
                    }
                    else ->
                    {
                        if (i == "∞")
                            arrayOperators.add(Constants.infinity)
                        else
                            arrayOperators.add(i)
                    }
                }
            } else
            {
                val char: String = if (i.endsWith(Constants.e))
                    Constants.e
                else
                    i[i.length - 1].toString()
                arrayOperators.add(st)
                arrayOperators.add(char)
            }
        }
    }

    /*
    * Here it is verified if the array contains the number "PI" or
    * the number "E" or if there is an operation of the form
    * {"...√-...", "...^-...", "...+-...", "...÷-...", "...×-...", "...(-..."}
    * for which new operations will be added and in failing that a
    * Number Node with the negative value of the number.
    * Special cases:
    * * When the following number is a factorial function.
    * * When the following operations has the factorial operator ("!").
    * * When the following operations has an implicit multiplication (number
    * or PI or E).
    */
    var check = true
    while (check)
    {
        check = false
        for ((index, value) in arrayOperators.withIndex())
        {
            when (value)
            {
                "π" -> arrayOperators[index] = Parenthesis(Number(Math.PI.toString()))
                Constants.e -> arrayOperators[index] = Parenthesis(Number(Math.E.toString()))
                "" -> arrayOperators[index] = null
                "-" ->
                {
                    /*
                    * Verify if exists a number like 1.26451E-5
                    */
                    if ((index - 1 >= 0 && index + 1 < arrayOperators.size)
                            && (arrayOperators[index - 1] is String &&
                                    ((arrayOperators[index - 1] as String)
                                        .matches(Regex("\\d*.?\\d+E|\\d+.?\\d*E"))
                                            && (arrayOperators[index + 1] as String)
                                        .matches(Regex("\\d+")))))
                    {
                        arrayOperators[index - 1] =
                            "${arrayOperators[index - 1]}$value${arrayOperators[index + 1]}"
                        arrayOperators[index] = null
                        arrayOperators[index + 1] = null
                    } else
                    {
                        for (i in (0 until index).reversed())
                        {
                            when (arrayOperators[i])
                            {
                                "√", "^", "+", "÷", "×", "(" ->
                                {
                                    if (index + 1 <= arrayOperators.size - 1)
                                    {
                                        val aux = arrayOperators[index + 1] as String
                                        val isSpecialFuncAux = isSpecialFunction(aux)
                                        val isNumber = aux.matches(Regex("\\d*.?\\d+|\\d+.?\\d*?"))
                                        val isPiE = aux == "π" || aux == Constants.e

                                        if ((isNumber || isPiE) && (index + 2 >= arrayOperators.size
                                                        || (index + 2 <= arrayOperators.size - 1
                                                        && !isFactorial(arrayOperators[index + 2] as String))))
                                        {
                                            if (isPiE)
                                            {
                                                arrayOperators[index] =
                                                    "$value${if (aux == "π") Math.PI.toString() else Math.E.toString()}"
                                                arrayOperators[index + 1] = null
                                            } else
                                            {
                                                arrayOperators[index] =
                                                    "$value${arrayOperators[index + 1]}"
                                                arrayOperators[index + 1] = null
                                            }
                                        } else
                                        {
                                            if (isNumber || isPiE)
                                            {
                                                val arrayAux = arrayListOf<Any?>()
                                                for (j in 0 until index)
                                                    arrayAux.add(arrayOperators[j])
                                                arrayAux.add("(")
                                                arrayAux.add("0")
                                                arrayAux.add(arrayOperators[index])
                                                arrayAux.add(arrayOperators[index + 1])

                                                var added = false
                                                for (j in index + 2 until arrayOperators.size)
                                                {
                                                    val pos = arrayOperators[j]
                                                    val isFact = isFactorial(pos as String)

                                                    if (!added && isFact)
                                                        arrayAux.add(pos)
                                                    else
                                                    {
                                                        if (!added)
                                                        {
                                                            arrayAux.add(")")
                                                            added = true
                                                        }
                                                        arrayAux.add(pos)
                                                    }
                                                }

                                                arrayOperators = arrayAux
                                                check = true
                                            } else
                                            {
                                                var isFactorial = false
                                                var indexOfFact = 0
                                                if ((aux == "(" || isSpecialFuncAux) && index + 2 < arrayOperators.size)
                                                {
                                                    var canContinue = true
                                                    var ignore = 0
                                                    for (j in index + 2 until arrayOperators.size)
                                                    {
                                                        if (canContinue && arrayOperators[j] == ")" && ignore == 0)
                                                        {
                                                            if (j + 1 < arrayOperators.size)
                                                            {
                                                                var canCompare = true

                                                                for (k in j + 1 until arrayOperators.size)
                                                                {
                                                                    if (canCompare && isFactorial(
                                                                                    arrayOperators[k] as String))
                                                                    {
                                                                        var indexAux = k
                                                                        while (indexAux + 1 < arrayOperators.size
                                                                                && isFactorial(
                                                                                        arrayOperators[indexAux + 1] as String))
                                                                            indexAux++

                                                                        canContinue = false
                                                                        canCompare = false
                                                                        indexOfFact = indexAux
                                                                        isFactorial = true
                                                                    } else if (arrayOperators[k] != ")")
                                                                        canCompare = false
                                                                }
                                                            } else
                                                                canContinue = false
                                                        } else if (isSpecialFunction(arrayOperators[j] as String))
                                                            ignore++
                                                        if (ignore > 0 && arrayOperators[j] == ")")
                                                            ignore--
                                                    }
                                                }
                                                val arrayAux = arrayListOf<Any?>()

                                                for (j in 0 until index)
                                                    arrayAux.add(arrayOperators[j])
                                                arrayAux.add("(")
                                                arrayAux.add("0")

                                                if (isFactorial || (aux != "(" && !isSpecialFuncAux
                                                                && (index + 2 <= arrayOperators.size - 1
                                                                && arrayOperators[index + 2] as String == Constants.fact)))
                                                {
                                                    if (isFactorial)
                                                    {
                                                        for (j in index..indexOfFact)
                                                            arrayAux.add(arrayOperators[j])
                                                        arrayAux.add(")")

                                                        if (indexOfFact + 1 < arrayOperators.size)
                                                            for (j in indexOfFact + 1 until arrayOperators.size)
                                                                arrayAux.add(arrayOperators[j])
                                                    } else
                                                    {
                                                        arrayAux.add(arrayOperators[index])
                                                        arrayAux.add(arrayOperators[index + 1])
                                                        arrayAux.add(arrayOperators[index + 2])
                                                        arrayAux.add(")")
                                                        if (index + 3 < arrayOperators.size)
                                                            for (j in index + 3 until arrayOperators.size)
                                                                arrayAux.add(arrayOperators[j])
                                                    }
                                                } else
                                                {
                                                    var added = false
                                                    var ignore = -1
                                                    for (j in index until arrayOperators.size)
                                                    {
                                                        if ((!added && arrayOperators[j] == ")" && ignore == 0) || (!added && j == arrayOperators.size - 1))
                                                        {
                                                            added = true
                                                            arrayAux.add(arrayOperators[j])
                                                            arrayAux.add(")")
                                                        } else
                                                        {
                                                            arrayAux.add(arrayOperators[j])
                                                            val isPar =
                                                                !isSpecialFunction(arrayOperators[j - 1] as String) && (arrayOperators[j] as String) == "("
                                                            if (isPar || isSpecialFunction(
                                                                            arrayOperators[j] as String))
                                                                ignore++
                                                        }
                                                        if (ignore > 0 && arrayOperators[j] == ")")
                                                            ignore--
                                                    }
                                                }
                                                arrayOperators = arrayAux
                                                check = true
                                            }
                                        }
                                    } else
                                        arrayOperators[index] =
                                            when (arrayOperators[i])
                                            {
                                                "√", "+", "(" -> "0"
                                                "÷", "×", "^" -> "1"
                                                else -> ""
                                            }
                                    break
                                }
                                null, "" -> continue
                                else -> break
                            }
                        }
                    }
                }
            }
            if (check) break
        }
    }
    arrayOperators.removeAll(arrayOfNulls(1))

    /*
    * Finally, it looks for the lower order parentheses in the
    * hierarchy and adds them one by one, in case there are no
    * parentheses, it adds all the resulting operations to a new
    * Parenthesis Node and returns its result in form of String.
    */
    var adding = true
    var index = 0
    var addingWithOutParenthesis = false
    var addAtLeastOne = false
    while (adding)
    {
        if (index < arrayOperators.size && arrayOperators.size > 1)
        {
            if (arrayOperators[index] is String && arrayOperators[index] == "(" || index == 0)
            {
                var next = false
                var indexNext = 0
                for (i in index + 1 until arrayOperators.size)
                {
                    if (arrayOperators[i] is String && arrayOperators[i] != "(" || arrayOperators[i] is MathNode)
                    {
                        if (arrayOperators[i] == ")")
                        {
                            indexNext = i
                            next = true
                            break
                        } else if (index == 0 && i == arrayOperators.size - 1)
                        {
                            addingWithOutParenthesis = true
                            indexNext = i + 1
                            next = true
                        }
                    } else
                        break
                }
                if (next)
                {
                    val content = arrayListOf<Any?>()
                    if (addingWithOutParenthesis && (arrayOperators[0] !is String || (arrayOperators[0] as String) != "("))
                        content.add(arrayOperators[0])
                    for (i in index + 1 until indexNext)
                    {
                        content.add(arrayOperators[i])
                    }

                    val nodeAux = if (content.size > 0) calculateWithHierarchy(content,
                                                                               mathMode) else throw ArgumentException()
                    setNodes(arrayOperators, nodeAux, index, indexNext)

                    addAtLeastOne = true

                    if (arrayOperators.size <= 1)
                        adding = false
                }
            }

            if (adding && arrayOperators.size > 1)
                index++
            else
                adding = false
        } else if (index <= arrayOperators.size)
        {
            if (arrayOperators.size > 1 && addAtLeastOne)
            {
                addAtLeastOne = false
                index = 0
            } else
                adding = false
        }
    }
    return if (arrayOperators[0] is MathNode)
    {
        val result = (arrayOperators[0] as MathNode).calculate()
        if (result.contains("E") || result.contains(Constants.infinity))
            result
        else
            round(result) //Optional
    } else
        arrayOperators[0] as String
}

fun regularizeParentheses(_operations: String): String
{
    var res = removeTerminalSymbols(_operations)
    for (i in res.reversed())
        if (i == '(')
            res = res.substring(0, res.length - 1)
        else
            break

    val stack = arrayListOf<String>()
    var counterOpen = 0

    for (i in res)
    {
        if (i == '(')
            stack.add("(")
        else if (i == ')')
        {
            if (stack.size > 0 && stack[stack.size - 1] == "(")
                stack.removeAt(stack.size - 1)
            else
                counterOpen++
        }
    }

    for (i in 0 until stack.size)
        res += ")"

    for (i in 0 until counterOpen)
        res = "($res"
    return res
}

private fun removeTerminalSymbols(text: String): String
{
    var ban = true
    var str = text
    while (ban)
    {
        if (str.isNotEmpty())
        {
            if (ends(str, arrayOf("+", "-", "^", "(", "×", "÷")))
            {
                str = str.substring(0, str.length - 1)
            } else
                ban = false
        } else
            ban = false
    }
    return str
}

private fun ends(text: String, values: Array<String>): Boolean
{
    for (i in values)
        if (text.endsWith(i))
            return true
    return false
}

private fun setNodes(_array: ArrayList<Any?>, node: MathNode, start: Int, end: Int)
{
    var limit = end
    if (end >= _array.size)
        limit--
    _array[start] = Parenthesis(node)
    for (i in start + 1..limit)
        _array[i] = null
    _array.removeAll(arrayOfNulls(1))
}

private fun endsWithParenthesisOrSign(st: String): String?
{
    return if ((st.length > 1 && !(st.length == 2 && st == Constants.e)
                    && (st.contains('(') || st.contains(')')
                    || st.contains('+') || st.contains('-')
                    || st.contains('×') || st.contains('÷')
                    || st.contains('^') || st.contains('√')
                    || st.contains('π') || st.contains(Constants.e)
                    || st.contains('!'))))
        "" + st.substring(0, st.indexOfAny("!()+-×÷^√π${Constants.e}".toCharArray()))
    else
        null
}

private fun isSpecialFunction(_st: String): Boolean =
    _st == Constants.sin || _st == Constants.cos || _st == Constants.tan || _st == Constants.arcSin
            || _st == Constants.arcCos || _st == Constants.arcTan || _st == Constants.ln || _st == Constants.lg

private fun isFactorial(_st: String): Boolean = _st == Constants.fact

private fun round(_number: String): String
{
    val decimalFormatSymbols = DecimalFormatSymbols(Locale("en"))
    val decimalFormat = DecimalFormat(".#############", decimalFormatSymbols)
    decimalFormat.roundingMode = RoundingMode.HALF_UP
    val res = decimalFormat.format(_number.toDouble())
    return if (res.isEmpty() || res == ".0") "0" else res
}