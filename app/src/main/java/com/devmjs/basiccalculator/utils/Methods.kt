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

package com.devmjs.basiccalculator.utils

import android.annotation.SuppressLint
import android.view.View
import android.view.Window
import com.devmjs.math.*

object Methods
{
    const val NO_CHANGE_SELECTION = -99
    const val CHANGE_SELECTION = -55
    private const val DEF_VALUE = -33

    fun check(text: String, values: Array<String>, ends: Boolean = true): Boolean
    {
        if (ends)
        {
            for (i in values)
                if (text.endsWith(i))
                    return true
        } else
        {
            for (i in values)
                if (text.startsWith(i))
                    return true
        }
        return false
    }

    fun checkTextSelected(startStr1: String,
                          endStr1: String,
                          startStr2: String = "",
                          endStr2: String = "",
                          start: Int,
                          end: Int,
                          selectionStart: Int,
                          selectionEnd: Int,
                          startS: Boolean = false): Int
    {
        var internal = internalCompare(startStr1, endStr1)
        if (internal == DEF_VALUE)
        {
            internal = when
            {
                !((check(startStr2, arrayOf("${Constants.sin}(", "${Constants.cos}(",
                                            "${Constants.tan}(")) && (if (startS) start else end)
                        == (if (startS) selectionStart else selectionEnd) - 2) || (check(endStr2,
                                                                                         arrayOf(Constants.sin,
                                                                                                 Constants.cos,
                                                                                                 Constants.tan),
                                                                                         false) && (if (startS) start else end) ==
                        (if (startS) selectionStart else selectionEnd) + 2)) && ((startStr1.endsWith(
                        "si") && endStr1.startsWith("n(")) ||
                        (startStr1.endsWith("co") && endStr1.startsWith("s(")) ||
                        (startStr1.endsWith("ta") && endStr1.startsWith("n("))) ->
                {
                    if (selectionEnd == selectionStart)
                    {
                        if (selectionStart > start) -2 else 2
                    } else if (selectionEnd > start || selectionStart > start) -2 else 2
                }
                ((check(startStr2, arrayOf("${Constants.sin}(", "${Constants.cos}(",
                                           "${Constants.tan}(")) && (if (startS) start else end)
                        == (if (startS) selectionStart else selectionEnd) - 2) || (check(endStr2,
                                                                                         arrayOf(Constants.sin,
                                                                                                 Constants.cos,
                                                                                                 Constants.tan),
                                                                                         false) && start == selectionStart + 2)) ->
                    CHANGE_SELECTION
                else -> NO_CHANGE_SELECTION
            }
        }
        return internal
    }

    private fun internalCompare(startStr1: String, endStr1: String, fromLines: Boolean = false): Int =
        when
        {
            (startStr1.endsWith('a') && endStr1.startsWith("rc${Constants.sin}(")) ||
                    (startStr1.endsWith('a') && endStr1.startsWith("rc${Constants.cos}(")) ||
                    (startStr1.endsWith('a') && endStr1.startsWith("rc${Constants.tan}(")) ->
                -1
            (startStr1.endsWith("ar") && endStr1.startsWith("c${Constants.sin}(")) ||
                    (startStr1.endsWith("ar") && endStr1.startsWith("c${Constants.cos}(")) ||
                    (startStr1.endsWith("ar") && endStr1.startsWith("c${Constants.tan}(")) ->
                -2
            (startStr1.endsWith("arc") && endStr1.startsWith("${Constants.sin}(")) ||
                    (startStr1.endsWith("arc") && endStr1.startsWith("${Constants.cos}(")) ||
                    (startStr1.endsWith("arc") && endStr1.startsWith("${Constants.tan}(")) ->
                -3
            (startStr1.endsWith("arcs") && endStr1.startsWith("in(")) ||
                    (startStr1.endsWith("arcc") && endStr1.startsWith("os(")) ||
                    (startStr1.endsWith("arct") && endStr1.startsWith("an(")) ->
                if (!fromLines) 3 else -4
            (startStr1.endsWith("arcsi") && endStr1.startsWith("n(")) ||
                    (startStr1.endsWith("arcco") && endStr1.startsWith("s(")) ||
                    (startStr1.endsWith("arcta") && endStr1.startsWith("n(")) ->
                if (!fromLines) 2 else -5
            (startStr1.endsWith(Constants.arcSin) && endStr1.startsWith('(')) ||
                    (startStr1.endsWith(Constants.arcCos) && endStr1.startsWith('(')) ||
                    (startStr1.endsWith(Constants.arcTan) && endStr1.startsWith('(')) ->
                if (!fromLines) 1 else -6
            (startStr1.endsWith('s') && endStr1.startsWith("in(")) ||
                    (startStr1.endsWith('c') && endStr1.startsWith("os(")) ||
                    (startStr1.endsWith('t') && endStr1.startsWith("an(")) ->
                -1
            (startStr1.endsWith(Constants.sin) && endStr1.startsWith('(')) ||
                    (startStr1.endsWith(Constants.cos) && endStr1.startsWith('(')) ||
                    (startStr1.endsWith(Constants.tan) && endStr1.startsWith('(')) ->
                if (!fromLines) 1 else -3
            startStr1.endsWith('l') && (endStr1.startsWith("n(") ||
                    endStr1.startsWith("g(")) -> -1
            (startStr1.endsWith("ln") || startStr1.endsWith("lg")) &&
                    endStr1.startsWith('(') -> if (!fromLines) 1 else -2
            else -> DEF_VALUE
        }

    /*fun compareLines(line1: String, line2: String): Int
    {
        var internal = internalCompare(line1, line2, true)
        if (internal == DEF_VALUE)
        {
            internal = if ((line1.endsWith("si") && line2.startsWith("n(")) ||
                    (line1.endsWith("co") && line2.startsWith("s(")) ||
                    (line1.endsWith("ta") && line2.startsWith("n(")))
                -2
            else 0
        }
        return internal
    }*/

    fun endsWithNumber(text: String): Boolean
    {
        return if (text.length == 1)
            text.matches(Regex("\\d"))
        else
            text.substring(text.length - 1, text.length).matches(Regex("\\d"))
    }

    fun isSpecialFun(text: String): Boolean =
        text == "${Constants.sin}(" || text == "${Constants.arcSin}("
                || text == "${Constants.cos}(" || text == "${Constants.arcCos}("
                || text == "${Constants.tan}(" || text == "${Constants.arcTan}("
                || text == "${Constants.lg}(" || text == "${Constants.ln}("

    fun endsWithSpecialFun(text: String): Boolean
    {
        var ban = true
        var str = text
        var atLeastOne = false
        while (ban)
        {
            if (str.isNotEmpty())
            {
                when
                {
                    check(str, arrayOf("${Constants.arcSin}(", "${Constants.arcCos}(",
                                       "${Constants.arcTan}(")) ->
                    {
                        atLeastOne = true
                        str = str.substring(0, str.length - 7)
                    }
                    check(str, arrayOf("${Constants.sin}(", "${Constants.cos}(",
                                       "${Constants.tan}(")) ->
                    {
                        atLeastOne = true
                        str = str.substring(0, str.length - 4)
                    }
                    check(str, arrayOf("${Constants.lg}(", "${Constants.ln}(")) ->
                    {
                        atLeastOne = true
                        str = str.substring(0, str.length - 3)
                    }
                    else -> ban = false
                }
            } else
                ban = false
        }
        return atLeastOne
    }

    fun endsWithSqrt(text: String): Boolean
    {
        var ban = true
        var str = text
        var atLeastOne = false
        var countPar = 0
        var countTotal = 0
        while (ban)
        {
            if (str.isNotEmpty())
            {
                if (check(str, arrayOf("(", "√")))
                {
                    countTotal++
                    if (str[str.length - 1] == '(')
                        countPar++
                    atLeastOne = true
                    str = str.substring(0, str.length - 1)
                } else
                    ban = false
            } else
                ban = false
        }
        return atLeastOne && countTotal != countPar
    }

    fun checkPoint(str: String, reverse: Boolean): Boolean
    {
        var rtn = false
        val text = if (reverse) str.reversed() else str
        val endIndex = text.indexOfAny("!()+-×÷^√π\uD835\uDC52".toCharArray())
        val subString = text.substring(0, if (endIndex >= 0) endIndex
        else str.length)
        if (subString.contains('.'))
            rtn = true
        return rtn
    }

    fun trimToLastPosition(_start: String): Array<String>
    {
        var start = _start
        var sub = 0
        if (start != "")
        {
            when
            {
                check(start, arrayOf("${Constants.arcSin}(",
                                     "${Constants.arcCos}(",
                                     "${Constants.arcTan}(")) ->
                {
                    start = start.substring(
                            0, start.length - 7)
                    sub = 7
                }
                check(start, arrayOf("${Constants.sin}(", "${Constants.cos}(",
                                     "${Constants.tan}(")) ->
                {
                    start = start.substring(
                            0, start.length - 4)
                    sub = 4
                }
                check(start, arrayOf("${Constants.ln}(", "${Constants.lg}(")) ->
                {
                    start = start.substring(
                            0, start.length - 3)
                    sub = 3
                }
                else ->
                {
                    sub = if (start.endsWith(Constants.e)) 2 else 1
                    start = start.substring(0, start.length - sub)
                }
            }
        }
        return arrayOf(start, sub.toString())
    }

    @Suppress("deprecation")
    fun setDecorViewStatusBarColor(window: Window,
                                   decorViewColor: Int,
                                   statusBarColor: Int,
                                   clear: Boolean)
    {
        window.decorView.setBackgroundColor(decorViewColor)
        window.statusBarColor = statusBarColor
        if (clear)
            window.decorView.systemUiVisibility = 0
        else
        {
            var flags = window.decorView.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.decorView.systemUiVisibility = flags
        }
    }

    @SuppressLint("DefaultLocale")
    @Suppress("deprecation")
    fun toLowerCase(str: String): String = str.toLowerCase()
}