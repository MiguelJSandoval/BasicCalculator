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

package com.devmjs.basiccalculator.usecases.calculator.customviews

import android.content.Context
import android.util.AttributeSet
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import com.devmjs.basiccalculator.R

class CustomEditText(context: Context, attrs: AttributeSet?):
        AppCompatEditText(context, attrs)
{
    private var listener: OnEditTextChangeListener? = null
    private var isCutText = false
    private var isPastedText = false
    /*private var textAux = ""
    private var reformatLines = false
    private var selectionStartAux = 0
    private var linesBreakInDisplay = 0*/

    override fun onSelectionChanged(selStart: Int, selEnd: Int)
    {
        super.onSelectionChanged(selStart, selEnd)
        listener?.onSelectionChanged(selStart, selEnd/*, reformatLines, selectionStartAux*/)
//        reformatLines = false
        isCursorVisible = true
        /*viewTreeObserver.addOnGlobalLayoutListener {
            if (text.toString() != textAux)
            {
                reorganizeLines()
                textAux = text.toString()
            }
        }*/
    }

    /*private fun reorganizeLines()
    {
        println("reorganize")
        var next = true
        if (linesBreakInDisplay > 0)
        {
            next = false
            val sel = selectionStart - linesBreakInDisplay
            selectionStartAux = if (sel < 0) selectionStart else sel
            linesBreakInDisplay = 0
            reformatLines = true
            setText(text.toString().replace("\n", ""))
        }
        if (next)
        {
            var lines = ArrayList<String>()
            var indexOfSelection = 0
            for (i in 0 until lineCount)
            {
                val start = layout.getLineStart(i)
                val end = layout.getLineEnd(i)
                if (selectionStart in start..end)
                    indexOfSelection = i
                lines.add(text.toString().substring(start, end))
            }
            var ban = true
            var linesBreak = 0
            selectionStartAux = selectionStart
            println("SEL_AUX: $selectionStartAux")
            var resetText = false
            while (ban)
            {
                for ((index, value) in lines.withIndex())
                {
                    if (index + 1 < lines.size)
                    {
                        val sub = Methods.compareLines(value, lines[index + 1])
                        if (sub != 0)
                        {
                            resetText = true
                            val aux = ArrayList<String>()
                            for (i in 0 until index)
                                aux.add(lines[i])
                            println(value)
                            println("sub: $sub")
                            val line2 =
                                value.substring(value.length + sub, value.length) + lines[index + 1]
                            val line1 = value.substring(0, value.length + sub) + "\n"
                            linesBreak++
                            aux.add(line1)
                            aux.add(line2)
                            lines = aux
                            break
                        }
                    } else
                        ban = false
                    if (index == indexOfSelection)
                        selectionStartAux += linesBreak
                }
            }
            if (resetText)
            {
                linesBreakInDisplay = linesBreak
                val builder = StringBuilder()
                for (i in lines)
                    builder.append(i)
                println("CHANGING_TEXT")
                println("NEW_SELECTION: $selectionStartAux")
                textAux = builder.toString()
                reformatLines = true
                setText(textAux)
            }
        }
    }

    private var removingLinesBreak = false
    private var lengthAfterAux = 0*/

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int)
    {
        /*var next = true
        if (linesBreakInDisplay > 0 && (isCutText || isPastedText))
        {
            linesBreakInDisplay = 0
            removingLinesBreak = true
            next = false
            selectionStartAux = start
            lengthAfterAux = lengthAfter
            setText(text.toString().replace("\n", ""))
        }
        if (next)
        {*/
//            if (!removingLinesBreak)
                listener?.onTextChanged(isCutText, isPastedText, start, lengthBefore, lengthAfter)
//            else
//            {
//                listener?.onTextChanged(isCutText, isPastedText, selectionStartAux,
//                                        lengthBefore, lengthAfterAux)
//                removingLinesBreak = false
//            }
            isCutText = false
            isPastedText = false
//        }
    }

    override fun onTextContextMenuItem(id: Int): Boolean
    {
//        var execute = true
        when (id)
        {
            android.R.id.cut -> isCutText = true
            android.R.id.paste -> isPastedText = true
            android.R.id.copy ->
//            {
//                execute = false
//                val clipboard =
//                    context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
//                val clipData = ClipData.newPlainText("Text", (text.toString()
//                    .substring(selectionStart, selectionEnd)
//                    .replace("\n", "")))
//                clipboard.setPrimaryClip(clipData)
//                setSelection(selectionEnd)
                Toast.makeText(context, context.getString(R.string.text_copied),
                               Toast.LENGTH_SHORT).show()
//            }
        }
        return /*if (execute) */super.onTextContextMenuItem(id) //else false
    }

    fun setOnEditTextChangedListener(listener: OnEditTextChangeListener)
    {
        this.listener = listener
    }
}