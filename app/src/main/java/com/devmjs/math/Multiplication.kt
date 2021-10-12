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
 * Class that represents a multiplication object in the hierarchy of
 * the tree of operations to be performed.
 */
class Multiplication(private val operator1: MathNode, private val operator2: MathNode) : MathNode
{
    /**
     * Standard way to multiply two numbers and return result as String.
     */
    override fun calculate(): String
    {
        val result = (operator1.calculate().toDouble() * operator2.calculate().toDouble()).toString()
        return if (result.isEmpty() || result == ".0") "0" else result
    }
}