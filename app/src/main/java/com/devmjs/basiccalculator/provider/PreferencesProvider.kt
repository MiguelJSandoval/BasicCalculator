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

enum class PreferencesKey(val key: String)
{
    PREFERENCES("com.dev_mjs.CALCULATOR_PREFERENCES"),
    UI_MODE("UI_MODE"),
    LIGHT_MODE("LIGHT"),
    DARK_MODE("DARK"),
    SYSTEM_DEFAULT("SYSTEM"),
    TEXT_NUMBERS("TXT_NUMBERS"),
    TEXT_RESULT("TXT_RESULT"),
    EQUAL_PRESSED("EQUAL_PRESSED"),
    IS_ALL_OPEN("IS_ALL_OPEN"),
    MATH_MODE("MATH_MODE"),
    IS_SECOND_PRESSED("IS_SECOND_PRESSED"),
    HISTORY("HISTORY")
}

class PreferencesProvider(context: Context)
{
    private var preferences =
        context.getSharedPreferences(PreferencesKey.PREFERENCES.key, Context.MODE_PRIVATE)
    private val editor = preferences.edit()

    fun putString(key: PreferencesKey, value: String)
    {
        editor.putString(key.key, value).apply()
    }

    fun getString(key: PreferencesKey, default: String): String?
    {
        return preferences.getString(key.key, default)
    }

    fun putBoolean(key: PreferencesKey, value: Boolean)
    {
        editor.putBoolean(key.key, value).apply()
    }

    fun getBoolean(key: PreferencesKey): Boolean
    {
        return preferences.getBoolean(key.key, false)
    }
}