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

/**
 * Calculates operations with a given array of operators which
 * can be signs, numbers or math nodes, and perform the operations
 * by adding them to a tree with a hierarchy of operations.
 * @return A math node with the hierarchy of operations of the
 * given array.
 */
fun calculateWithHierarchy(arrayOperators: ArrayList<Any?>, mathMode: MathMode): MathNode
{
    var adding = true

    /*
    * First verify if the array contains the trigonometric functions
    * and add the number or node as a Trigonometric Function Node for
    * each function to the tree.
    */
    arrayOperators.reverse()
    while (adding)
    {
        for ((index, value) in arrayOperators.withIndex())
        {
            when (value)
            {
                Constants.sin, Constants.arcSin, Constants.cos, Constants.arcCos,
                Constants.tan, Constants.arcTan, Constants.lg, Constants.ln ->
                {
                    setSpecialFunctionsNodes(arrayOperators, value as String, index, mathMode)
                    break
                }
                else ->
                    if (index == arrayOperators.size - 1)
                        adding = false
            }
        }
    }

    /*
    * Next verify if the array contains the sqrt operator add the
    * following number or node as a Pow Node.
    */
    adding = true
    while (adding)
    {
        for ((index, value) in arrayOperators.withIndex())
        {
            if (value == "√")
            {
                setNumberNodes(arrayOperators, "√", index, false)
                break
            } else if (index == arrayOperators.size - 1)
                adding = false
        }
    }

    /*
    * Next verify if the array contains the factorial operator after
    * this add the following number or node as Factorial Node.
    */
    adding = true
    arrayOperators.reverse()
    while (adding)
    {
        for ((index, value) in arrayOperators.withIndex())
        {
            if (value == Constants.fact)
            {
                setSpecialFunctionsNodes(arrayOperators, Constants.fact, index, mathMode)
                break
            } else if (index == arrayOperators.size - 1)
                adding = false
        }
    }

    /*
    * Next verify if the array contains the pow operator after
    * this add a Pow Node with the specific numbers (or nodes).
    */
    adding = true
    arrayOperators.reverse()
    while (adding)
    {
        for ((index, value) in arrayOperators.withIndex())
        {
            if (value == "^")
            {
                setNumberNodes(arrayOperators, "^", index, true)
                break
            } else if (index == arrayOperators.size - 1)
                adding = false
        }
    }

    /*
    * Next verify if the array contains the division operator
    * and after that add the previous and next number or nodes
    * to a Division Node.
    */
    adding = true
    arrayOperators.reverse()
    while (adding)
    {
        for ((index, value) in arrayOperators.withIndex())
        {
            if (value == "÷")
            {
                setNumberNodes(arrayOperators, "÷", index, false)
                break
            } else if (index == arrayOperators.size - 1)
                adding = false
        }
    }

    /*
    * Next reverse the array to go to the end to the start of the
    * array to correctly add the multiplication hierarchy after this
    * verify if the array contains the multiplication operator and add
    * the previous and next number or node to a Multiplication Node.
    */
    adding = true
    arrayOperators.reverse()
    while (adding)
    {
        for ((index, value) in arrayOperators.withIndex())
        {
            if (value == "×")
            {
                setNumberNodes(arrayOperators, "×", index, true)
                break
            } else if (value is MathNode || (value is String && isNumber(value)))
            {
                if (index - 1 >= 0 && (arrayOperators[index - 1] is MathNode || isNumber(
                                arrayOperators[index - 1] as String)))
                {
                    setImplicitMultiplicationNodes(arrayOperators, index)
                    break
                }
            }
            if (index == arrayOperators.size - 1)
                adding = false
        }
    }

    /*
    * Next return the array to the initial order and check
    * if there are addition or subtraction operators and add
    * the previous and next number or node to a Addition Node
    * or Subtraction Node respectively.
    */
    arrayOperators.reverse()
    adding = true
    while (adding)
    {
        for ((index, value) in arrayOperators.withIndex())
        {
            when (value)
            {
                "+", "-" ->
                {
                    setNumberNodes(arrayOperators, value as String, index, false)
                    break
                }
                else -> if (index == arrayOperators.size - 1)
                    adding = false
            }
        }
    }

    /*
    * Finally check if there are still nodes together to add
    * them as a Multiplication Node.
    */
    arrayOperators.reverse()
    adding = true
    while (adding)
    {
        for ((index, value) in arrayOperators.withIndex())
        {
            if (value is MathNode || (value is String && isNumber(value)))
            {
                if (index - 1 >= 0 && (arrayOperators[index - 1] is MathNode || isNumber(
                                arrayOperators[index - 1] as String)))
                {
                    setImplicitMultiplicationNodes(arrayOperators, index)
                    break
                }
            }
            if (index == arrayOperators.size - 1)
                adding = false
        }
    }

    return if (arrayOperators[0] is String && (arrayOperators[0] as String).length > 0)
        Number(arrayOperators[0] as String)
    else
        if (arrayOperators[0] is MathNode)
            arrayOperators[0] as MathNode
        else
            Number(arrayOperators[0] as String)
}

fun isNumber(_number: String): Boolean =
    _number.matches(Regex("-?\\d*.?\\d+(E-?\\d+)?|-?\\d+.?\\d*(E-?\\d+)?"))

private fun setImplicitMultiplicationNodes(
        arrayOperators: ArrayList<Any?>,
        index: Int,
        swap: Boolean = false)
{
    val neutral = "1"

    val num1 = if (arrayOperators[index] is MathNode)
        arrayOperators[index] as MathNode
    else
        Number(
                "" + if (arrayOperators[index] != "")
                    arrayOperators[index]
                else neutral
              )

    val num2 = try
    {
        if (arrayOperators[index - if (swap) -1 else 1] is MathNode)
            arrayOperators[index - if (swap) -1 else 1] as MathNode
        else
            Number(arrayOperators[index - if (swap) -1 else 1] as String)
    } catch (e: Exception)
    {
        Number(neutral)
    }

    arrayOperators[index] = Multiplication(num1, num2)
    arrayOperators[index - if (swap) -1 else 1] = null
    arrayOperators.removeAll(arrayOfNulls(1))
}

private fun setNumberNodes(
        arrayOperators: ArrayList<Any?>,
        type: String,
        index: Int,
        swap: Boolean)
{
    val neutral = when (type)
    {
        "×", "÷", "^" -> "1"
        "+", "-", "√" -> "0"
        else -> ""
    }
    var num1: MathNode = Number(neutral)
    try
    {
        num1 = if (arrayOperators[index + if (swap) 1 else -1] is String)
            Number(
                    "" + when
                    {
                        arrayOperators[index +
                                if (swap) 1 else -1] != "" -> arrayOperators[index + if (swap) 1 else -1]
                        type == "√" || type == "÷" || type == "^" -> throw ArgumentException()
                        else -> neutral
                    }
                  )
        else
            arrayOperators[index + if (swap) 1 else -1] as MathNode
    } catch (e: java.lang.Exception)
    {
        when (type)
        {
            "√", "÷", "^" -> throw ArgumentException()
        }
    }

    var num2: MathNode = Number(neutral)
    if (type != "√")
    {
        try
        {
            num2 = if (arrayOperators[index - if (swap) 1 else -1] is String)
                Number(
                        "" + if (arrayOperators[index -
                                        if (swap) 1 else -1] != "")
                            arrayOperators[index - if (swap) 1 else -1]
                        else neutral
                      )
            else
                arrayOperators[index - if (swap) 1 else -1] as MathNode
        } catch (e: Exception)
        {
        }
    }

    arrayOperators[index] = when (type)
    {
        "×" -> Multiplication(num1, num2)
        "÷" -> Division(num1, num2)
        "+" -> Addition(num1, num2)
        "-" -> Subtraction(num1, num2)
        "^" -> Pow(num1, num2)
        "√" -> Pow(num1, Number("0.5"))
        else -> Number(neutral)
    }

    if (index - 1 >= 0)
        arrayOperators[index - 1] = null
    if (index + 1 < arrayOperators.size && type != "√")
        arrayOperators[index + 1] = null
    arrayOperators.removeAll(arrayOfNulls(1))
}

private fun setSpecialFunctionsNodes(
        arrayOperators: ArrayList<Any?>,
        type: String,
        index: Int,
        mathMode: MathMode)
{
    val neutral = "0"

    val content = try
    {
        if (arrayOperators[index - 1] is MathNode)
            arrayOperators[index - 1] as MathNode
        else
            Number(
                    "" + if (arrayOperators[index - 1] != "")
                        arrayOperators[index - 1]
                    else neutral
                  )
    } catch (e: Exception)
    {
        Number(neutral)
    }

    arrayOperators[index] = when (type)
    {
        Constants.sin -> Sin(content, mathMode)
        Constants.arcSin -> ArcSin(content, mathMode)
        Constants.cos -> Cos(content, mathMode)
        Constants.arcCos -> ArcCos(content, mathMode)
        Constants.tan -> Tan(content, mathMode)
        Constants.arcTan -> ArcTan(content, mathMode)
        Constants.lg -> Base10Logarithm(content)
        Constants.ln -> NaturalLogarithm(content)
        Constants.fact -> Factorial(content)
        else -> content
    }

    arrayOperators[index - 1] = null
    arrayOperators.removeAll(arrayOfNulls(1))
}