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

package com.devmjs.basiccalculator.usecases.history.customviews

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devmjs.basiccalculator.R
import com.devmjs.basiccalculator.provider.SpanProvider
import com.devmjs.basiccalculator.usecases.history.utils.OnItemClickedListener

@SuppressLint("ViewConstructor")
class HistoryItemView(context: Context,
                      private val isDarkTheme: Boolean,
                      private val listener: OnItemClickedListener):
        LinearLayout(context, null, 0, if (isDarkTheme) R.style.AppTheme_NoActionBar_Dark
        else R.style.AppTheme_NoActionBar)
{
    private val theme = if (isDarkTheme) R.style.AppTheme_NoActionBar_Dark
    else R.style.AppTheme_NoActionBar
    private val operations = EditText(context, null, 0, theme)
    private val result = EditText(context, null, 0, theme)
    private val anchor1 = View(context)
    private val anchor2 = View(context)
    private val textsAux: Array<String> = arrayOf("", "")

    init
    {
        operations.setHorizontallyScrolling(true)
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL
        formatEditText(operations, false)
        formatEditText(result, true)
        val params = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                  resources.getDimensionPixelOffset(R.dimen.height_txt))
        val paramsAnchor = LayoutParams(1, 1)
        paramsAnchor.gravity = Gravity.END
        this.addView(operations, params)
        this.addView(anchor1, paramsAnchor)
        this.addView(result, params)
        this.addView(anchor2, paramsAnchor)
    }

    fun setData(operations: String, result: String)
    {
        textsAux[0] = operations
        textsAux[1] = result
        this.operations.setText(operations)
        this.result.setText(result)
    }

    private fun formatEditText(editText: EditText, isResult: Boolean)
    {
        val typeFace = Typeface.create("sans-serif", Typeface.BOLD)
        editText.typeface = typeFace
        editText.isAllCaps = false
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                             resources.getDimension(R.dimen.dim_operations_res_text))
        editText.isSingleLine = true
        editText.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        editText.includeFontPadding = false
        editText.setBackgroundResource(R.drawable.back_btn)
        editText.isFocusable = false
        editText.isClickable = true
        editText.isCursorVisible = false
        if (isResult)
            editText.setTextColor(context.getColor(if (isDarkTheme) R.color.transparent_white else R.color.transparent_gray))
        editText.setOnClickListener {
            listener.onItemClicked(editText.text.toString())
        }
        editText.setOnLongClickListener {
            val color =
                context.getColor(if (isDarkTheme) R.color.transparent_white else R.color.transparent_gray)
            editText.setText(SpanProvider.backgroundColorSpan(editText.text.toString(), 0,
                                                              editText.text.toString().length,
                                                              color))
            val popUpMenu = PopupMenu(context, if (isResult) anchor2 else anchor1)
            popUpMenu.menuInflater.inflate(R.menu.options, popUpMenu.menu)
            popUpMenu.setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.copy)
                {
                    val clipboard =
                        context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("Result", editText.text.toString())
                    clipboard.setPrimaryClip(clipData)
                    Toast.makeText(context, context.getString(R.string.text_copied),
                                   Toast.LENGTH_SHORT).show()
                }
                true
            }
            popUpMenu.setOnDismissListener {
                editText.setText(if (isResult) textsAux[1] else textsAux[0])
            }
            popUpMenu.show()
            true
        }
    }
}