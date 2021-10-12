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

import org.apache.commons.math3.special.Gamma
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sqrt

/**
 * Class that represents a factorial function object in the hierarchy of
 * the tree of operations to be performed.
 */
class Factorial(private val content: MathNode): MathNode
{
    /**
     * The maximum number to which its factorial can be calculated
     * (due to the limitations of a Double number).
     */
    private val globalLimit = 170

    /**
     * Standard way to calculate the factorial function of a number.
     * And return result as String.
     */
    override fun calculate(): String
    {
        /*
        * First check if the number is an Integer, if yes, cycle from
        * 1 to the value of the number and multiply all these values.
        */
        val value = isInteger()
        return if (value != null)
        {
            val limit = value.toInt()
            var res = 1.0
            for (i in 1..limit)
                res *= i
            res.toString()
        } else
        {
            val valueAux = content.calculate()
            /*
            * After this check if the number is negative, if yes, return
            * NaN, else if the number is not an Integer but is in range
            * (0 > number < 170) return the value of this in the Gamma Γ(x)
            * function, the Gamma function is defined as follows:
            *      Γ(x) = integral( t^(x-1) e^(-t), t = 0 .. infinity)
            * this because the Gamma function is a generalization
            * of the factorial, with this function can calculate the factorial
            * for numbers non Integer (for example PI, E, 1/2, etc.), to
            * calculate the Gamma function of the number need to add one to this.
            * If the number is already Infinity or bigger than 170 return
            * Infinity.
            * In any other case return NaN.
            */
            try
            {
                if (valueAux.toDouble() < 0)
                    "NaN"
                else if (valueAux.toDouble() + 1 > 0 && valueAux.toDouble() + 1 <= 141)
                    Gamma.gamma(valueAux.toDouble() + 1).toString()
                else if (valueAux.toDouble() + 1 > 141 && valueAux.toDouble() + 1 <= 171)
                    gamma(valueAux.toDouble() + 1).toString()
                else if (valueAux == Constants.infinity || (valueAux.contains("E") && !valueAux.contains("E-")) || valueAux.toDouble() > globalLimit)
                    Constants.infinity
                else
                    "NaN"
            } catch (e: Exception)
            {
                "NaN"
            }
        }
    }

    /**
     * Check if the given number is an Integer, if yes, return this
     * else return null.
     */
    private fun isInteger(): String?
    {
        var value = content.calculate()
        if (value.endsWith("."))
            value = value.substring(0, value.length - 1)
        else if (value.endsWith(".0") && value != ".0")
            value = value.substring(0, value.length - 2)
        else if (value == ".0")
            value = "0"

        return if (value.contains(Constants.infinity) || value.contains("E-"))
            null
        else if (value.matches(Regex("\\d+")) && value.toInt() >= 0 && value.toInt() <= globalLimit)
            value
        else null
    }

    /**
     * @return The value of logGamma(x).
     */
    private fun logGamma(x: Double): Double
    {
        val tmp = (x - 0.5) * ln(x + 4.5) - (x + 4.5)
        val ser = 1.0 + 76.18009173 / (x + 0)- (86.50532033 / (x + 1))+(24.01409822 / (x + 2)) - (1.231739516 / (x + 3))+(0.00120858003 / (x + 4)) - (0.00000536382 / (x + 5))
        return tmp + ln(ser * sqrt((2 * Math.PI)))
    }

    /**
     * @return The value of gamma(x) function.
     */
    private fun gamma(x: Double): Double
    {
        return exp(logGamma(x))
    }
}