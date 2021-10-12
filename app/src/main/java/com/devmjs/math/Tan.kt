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

import kotlin.math.tan

/**
 * Class that represents a tangent function object in the hierarchy of
 * the tree of operations to be performed.
 */
class Tan(private val content: MathNode, private val mode: MathMode): MathNode
{
    /**
     * Standard way to calculate the tangent function of a number verifying
     * the mode (radians or degree) and return result as String.
     */
    override fun calculate(): String
    {
        return when (mode)
        {
            MathMode.RAD ->
            {
                val value = content.calculate()
                tan(value.toDouble()).toString()
            }
            MathMode.DEG ->
            {
                val value = (content.calculate().toDouble() * Math.PI) / 180
                tan(value).toString()
            }
        }
    }
}