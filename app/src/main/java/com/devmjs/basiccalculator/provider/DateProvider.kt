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

package com.devmjs.basiccalculator.provider

import android.content.Context
import com.devmjs.basiccalculator.R
import java.text.SimpleDateFormat
import java.util.*

object DateProvider
{
    private fun format(date: Date): String
    {
        val dateFormat = "dd/MM/yyy"
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale("es"))
        return simpleDateFormat.format(date)
    }

    fun getDate(): String = format(Date())

    fun parseDate(date: String, context: Context): String
    {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val yesterday = calendar.time
        calendar.add(Calendar.DAY_OF_MONTH, 2)
        val tomorrow = calendar.time
        return when (date)
        {
            getDate() ->
                context.getString (R.string.today)
            format(yesterday) ->
                context.getString(R.string.yesterday)
            format(tomorrow) ->
                context.getString(R.string.tomorrow)
            else -> date
        }
    }
}