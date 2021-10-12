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

package com.devmjs.basiccalculator.usecases.history.utils

import android.content.Context
import com.devmjs.basiccalculator.provider.PreferencesKey
import com.devmjs.basiccalculator.provider.PreferencesProvider
import com.google.gson.Gson

class HistoryProvider(context: Context)
{
    private val provider = PreferencesProvider(context)

    fun getHistory(): History
    {
        val array = provider.getString(PreferencesKey.HISTORY, Gson().toJson(History(hashMapOf())))
        return Gson().fromJson(array, History::class.java)
    }

    fun saveHistoryItem(date: String, historyItem: HistoryItem)
    {
        val history = getHistory()
        if (history.map[date] == null)
            history.map[date] = arrayListOf()
        history.map[date]?.add(historyItem)
        val json = Gson().toJson(history)
        provider.putString(PreferencesKey.HISTORY, json)
    }

    fun clearHistory()
    {
        val history = History(hashMapOf())
        val json = Gson().toJson(history)
        provider.putString(PreferencesKey.HISTORY, json)
    }
}