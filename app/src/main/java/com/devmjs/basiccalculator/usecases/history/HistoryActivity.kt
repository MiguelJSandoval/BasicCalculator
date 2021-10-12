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

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devmjs.basiccalculator.R
import com.devmjs.basiccalculator.databinding.ActivityHistoryBinding
import com.devmjs.basiccalculator.provider.DateProvider
import com.devmjs.basiccalculator.provider.PreferencesKey
import com.devmjs.basiccalculator.provider.PreferencesProvider
import com.devmjs.basiccalculator.usecases.history.utils.History
import com.devmjs.basiccalculator.usecases.history.utils.HistoryProvider
import com.devmjs.basiccalculator.usecases.history.customviews.HistoryView
import com.devmjs.basiccalculator.usecases.history.utils.OnItemClickedListener
import com.devmjs.basiccalculator.utils.Methods
import java.lang.Exception

class HistoryActivity: AppCompatActivity(), OnItemClickedListener
{
    private lateinit var root: ActivityHistoryBinding
    private var isDarkTheme = false
    private lateinit var history: History
    private var clicked = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        isDarkTheme = intent.getBooleanExtra("IS_DARK_THEME", false)
        setTheme()
        super.onCreate(savedInstanceState)
        init()
    }

    override fun onConfigurationChanged(newConfig: Configuration)
    {
        val provider = PreferencesProvider(applicationContext)
        val key = provider.getString(PreferencesKey.UI_MODE, PreferencesKey.SYSTEM_DEFAULT.key)
        if (key == PreferencesKey.SYSTEM_DEFAULT.key)
        {
            when (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK)
            {
                Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_YES ->
                {
                    switchColorMode()
                }
            }
        }
        super.onConfigurationChanged(newConfig)
    }

    override fun onBackPressed()
    {
        super.onBackPressed()
        finish()
    }

    private fun setTheme()
    {
        val decorViewColor: Int
        val statusBarColor: Int
        var clear = false
        if (isDarkTheme)
        {
            application.setTheme(R.style.AppTheme_NoActionBar_Dark)
            setTheme(R.style.AppTheme_NoActionBar_Dark)
            decorViewColor = Color.BLACK
            statusBarColor = getColor(R.color.dark_gray)
            clear = true
        } else
        {
            application.setTheme(R.style.AppTheme_NoActionBar)
            setTheme(R.style.AppTheme_NoActionBar)
            decorViewColor = Color.WHITE
            statusBarColor = Color.WHITE
        }
        Methods.setDecorViewStatusBarColor(window, decorViewColor, statusBarColor, clear)
    }

    private fun init()
    {
        root = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(root.root)
        history = HistoryProvider(applicationContext).getHistory()

        root.back.setOnClickListener {
            onBackPressed()
        }
        root.opts.setOnClickListener {
            val popUp = PopupMenu(this, root.anchor)
            popUp.menuInflater.inflate(R.menu.menu_history, popUp.menu)
            popUp.setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.clear && history.map.size > 0)
                {
                    val clearDialog = ClearHistoryConfirmDialog()
                    clearDialog.show(supportFragmentManager, "CLEAR_DIALOG")
                    true
                } else
                {
                    Toast.makeText(applicationContext,
                                   getString(R.string.no_history_to_clear),
                                   Toast.LENGTH_SHORT).show()
                    false
                }
            }
            popUp.show()
        }
        setData(history)
        root.scrollHistory.post { root.scrollHistory.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    private fun switchColorMode()
    {
        isDarkTheme = !isDarkTheme
        setTheme()
        init()
    }

    private fun setData(history: History)
    {
        try
        {
            if (history.map.size > 0)
            {
                for (i in history.map.toSortedMap())
                {
                    val historyView = HistoryView(applicationContext, isDarkTheme, this)
                    historyView.setData(DateProvider.parseDate(i.key, applicationContext), i.value)
                    root.list.addView(historyView)
                }
            } else
                root.noHistory.visibility = View.VISIBLE
        } catch (e: Exception)
        {
            HistoryProvider(applicationContext).clearHistory()
            root.noHistory.visibility = View.VISIBLE
        }
    }

    override fun onItemClicked(text: String)
    {
        if (!clicked)
        {
            clicked = true
            val i = Intent().apply { putExtra("TEXT", text) }
            setResult(RESULT_OK, i)
            finish()
        }
    }
}