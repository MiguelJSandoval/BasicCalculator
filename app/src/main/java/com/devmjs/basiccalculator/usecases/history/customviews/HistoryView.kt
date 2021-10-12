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
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.devmjs.basiccalculator.R
import com.devmjs.basiccalculator.databinding.LayoutHistoryBinding
import com.devmjs.basiccalculator.usecases.history.utils.HistoryItem
import com.devmjs.basiccalculator.usecases.history.utils.OnItemClickedListener

@SuppressLint("ViewConstructor")
class HistoryView(context: Context,
                  private val isDarkTheme: Boolean,
                  private val onItemClickedListener: OnItemClickedListener):
        ConstraintLayout(context)
{
    private val root: LayoutHistoryBinding =
        LayoutHistoryBinding.inflate(LayoutInflater.from(context), this, true)

    fun setData(date: String, array: ArrayList<HistoryItem>)
    {
        root.date.text = date
        root.date.setTextColor(context.getColor(if (isDarkTheme) R.color.white else R.color.black))
        if (array.size > 0)
        {
            for ((index, i) in array.withIndex())
            {
                val historyItemView = HistoryItemView(context, isDarkTheme, onItemClickedListener)
                historyItemView.setData(i.operations, i.result)
                root.dateList.addView(historyItemView)
                if (index < array.size - 1)
                {
                    val view = View(context)
                    view.setBackgroundColor(context.getColor(R.color.dark_gray))
                    val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                        resources.getDimensionPixelOffset(R.dimen._2sdp))
                    root.dateList.addView(view, params)
                }
            }
        }
    }
}