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

package com.devmjs.basiccalculator.usecases.history

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.devmjs.basiccalculator.R
import com.devmjs.basiccalculator.usecases.history.utils.HistoryProvider

class ClearHistoryConfirmDialog: DialogFragment()
{
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = createDialog()

    private fun createDialog(): AlertDialog
    {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.clear_question)
            .setPositiveButton(R.string.clear)
            { _, _ ->
                clear()
            }
            .setNegativeButton(R.string.cancel)
            { _, _ ->
                dismiss()
            }
        return builder.create()
    }

    private fun clear()
    {
        HistoryProvider(requireContext()).clearHistory()
        dismiss()
        requireActivity().finish()
    }
}