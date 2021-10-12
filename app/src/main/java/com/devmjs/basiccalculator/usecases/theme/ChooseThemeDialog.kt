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

package com.devmjs.basiccalculator.usecases.theme

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.devmjs.basiccalculator.R
import com.devmjs.basiccalculator.provider.PreferencesKey
import com.devmjs.basiccalculator.provider.PreferencesProvider
import java.lang.Exception

class ChooseThemeDialog: DialogFragment()
{
    private var selected: Int = 0
    private lateinit var listener: OnThemeChangedListener

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        try
        {
            listener = context as OnThemeChangedListener
            selected = getSelected()
        } catch (e: Exception)
        {
            Log.e("Choose theme dialog: ", e.toString())
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.choose_theme))
            .setSingleChoiceItems(R.array.theme_options, selected)
            { _, which ->
                selected = which
            }
            .setPositiveButton(R.string.ok)
            { _, _ ->
                handleChange()
            }
            .setNegativeButton(R.string.cancel)
            { _, _ ->
                dismiss()
            }
        return builder.create()
    }

    private fun handleChange()
    {
        val preference = getSelected()
        if (preference != selected)
            listener.onThemeChanged(selected)
    }

    private fun getSelected(): Int
    {
        val provider = PreferencesProvider(requireContext())
        return when (provider.getString(PreferencesKey.UI_MODE,
                                        PreferencesKey.SYSTEM_DEFAULT.key))
        {
            PreferencesKey.LIGHT_MODE.key -> 0
            PreferencesKey.DARK_MODE.key -> 1
            PreferencesKey.SYSTEM_DEFAULT.key -> 2
            else -> 0
        }
    }
}