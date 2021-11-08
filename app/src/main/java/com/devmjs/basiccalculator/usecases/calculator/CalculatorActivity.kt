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

package com.devmjs.basiccalculator.usecases.calculator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.PopupMenu
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.devmjs.basiccalculator.R
import com.devmjs.basiccalculator.databinding.ActivityCalculatorBinding
import com.devmjs.basiccalculator.provider.PreferencesKey
import com.devmjs.basiccalculator.provider.PreferencesProvider
import com.devmjs.basiccalculator.provider.SpanProvider
import com.devmjs.basiccalculator.usecases.calculator.customviews.OnEditTextChangeListener
import com.devmjs.basiccalculator.usecases.history.HistoryActivity
import com.devmjs.basiccalculator.usecases.theme.ChooseThemeDialog
import com.devmjs.basiccalculator.usecases.theme.OnThemeChangedListener
import com.devmjs.basiccalculator.utils.Methods
import com.devmjs.math.Constants
import com.devmjs.math.MathMode

class CalculatorActivity: AppCompatActivity(),
        OnEditTextChangeListener,
        OnThemeChangedListener
{
    private var numbers: Array<Button> = emptyArray()
    private lateinit var r: ActivityCalculatorBinding
    private lateinit var viewModel: CalculatorViewModel
    private var currentTheme = 0
    private val getData =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK && it.data != null)
            {
                val str = it.data!!.getStringExtra(HistoryActivity.KEY_ON_ITEM_CLICKED)
                if (!str.isNullOrBlank() && viewModel.actionSymbols(str, null))
                    setTexts()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        val provider = PreferencesProvider(applicationContext)
        val preferenceUi =
            provider.getString(PreferencesKey.UI_MODE, PreferencesKey.SYSTEM_DEFAULT.key)
        val uiMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when
        {
            preferenceUi == PreferencesKey.LIGHT_MODE.key || (preferenceUi == PreferencesKey.SYSTEM_DEFAULT.key
                    && uiMode == Configuration.UI_MODE_NIGHT_NO) ->
            {
                currentTheme = R.style.AppTheme_NoActionBar
                application.setTheme(currentTheme)
                setTheme(currentTheme)
                window.decorView.setBackgroundColor(Color.WHITE)
            }
            preferenceUi == PreferencesKey.DARK_MODE.key || (preferenceUi == PreferencesKey.SYSTEM_DEFAULT.key
                    && uiMode == Configuration.UI_MODE_NIGHT_YES) ->
            {
                currentTheme = R.style.AppTheme_NoActionBar_Dark
                application.setTheme(currentTheme)
                setTheme(currentTheme)
                window.decorView.setBackgroundColor(Color.BLACK)
            }
        }
        super.onCreate(savedInstanceState)
        r = ActivityCalculatorBinding.inflate(layoutInflater)
        setContentView(r.root)
        viewModel = ViewModelProvider(this).get(CalculatorViewModel::class.java)
        numbers = arrayOf(r.n0, r.n1, r.n2, r.n3, r.n4, r.n5, r.n6, r.n7, r.n8, r.n9)
        viewModel.initVars(applicationContext)
        textsMode()
        textsSecond()
        if (viewModel.isAllOpen)
        {
            r.container.progress = 1.0F
            r.animation.progress = 1.0F
        }
        setActions()
        r.scroll.post { r.scroll.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    override fun onPause()
    {
        viewModel.savePreferences(applicationContext)
        super.onPause()
    }

    override fun onCreateContextMenu(menu: ContextMenu?,
                                     v: View?,
                                     menuInfo: ContextMenu.ContextMenuInfo?)
    {
        if (viewModel.textResult != "0" && viewModel.textResult != viewModel.errorText &&
                viewModel.textResult != viewModel.mathError)
        {
            if (viewModel.selectionStart != viewModel.selectionEnd)
            {
                r.display.setSelection(viewModel.selectionEnd)
                viewModel.selectionStart = viewModel.selectionEnd
            }
            r.display.isCursorVisible = false

            viewModel.textResultAux = viewModel.textResult
            val color =
                when (PreferencesProvider(applicationContext).getString(PreferencesKey.UI_MODE,
                                                                        PreferencesKey.LIGHT_MODE.key))
                {
                    PreferencesKey.DARK_MODE.key -> getColor(R.color.transparent_white)
                    PreferencesKey.LIGHT_MODE.key -> getColor(R.color.transparent_gray)
                    else -> 0
                }
            r.textResult.text = SpanProvider.backgroundColorSpan(viewModel.textResult,
                                                                 if (viewModel.textResult.startsWith(
                                                                                 "=")) 1 else 0,
                                                                 viewModel.textResult.length,
                                                                 color)
            menuInflater.inflate(R.menu.options, menu)
            val item = menu?.getItem(0)
            item?.title = SpanProvider.foregroundColorSpan(item?.title?.toString()!!,
                                                           0, item.title.length, Color.BLACK)
        }
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean
    {
        if (item.itemId == R.id.copy)
        {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Result", viewModel.textResult.substring(
                    if (viewModel.textResult.startsWith("=")) 1 else 0,
                    viewModel.textResult.length))
            clipboard.setPrimaryClip(clipData)
            Toast.makeText(applicationContext, getString(R.string.text_copied),
                           Toast.LENGTH_SHORT).show()
        }
        return super.onContextItemSelected(item)
    }

    override fun onContextMenuClosed(menu: Menu)
    {
        r.display.setSelection(viewModel.selectionStart, viewModel.selectionEnd)
        r.display.isCursorVisible = true
        r.textResult.text = viewModel.textResultAux
        super.onContextMenuClosed(menu)
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
                    switchColorMode(2)
                }
            }
        }
        super.onConfigurationChanged(newConfig)
    }

    private fun setActions()
    {
        otherActions()
        specialFunActions()
        operatorsActions()
        numbersActions()
    }

    private fun otherActions()
    {
        r.menu.setOnClickListener {
            val popUp = PopupMenu(this, r.anchor)
            popUp.menuInflater.inflate(R.menu.menu_op, popUp.menu)
            popUp.setOnMenuItemClickListener { item ->
                when (item.itemId)
                {
                    R.id.history ->
                    {
                        val i = Intent(applicationContext, HistoryActivity::class.java)
                        i.putExtra("IS_DARK_THEME",
                                   currentTheme == R.style.AppTheme_NoActionBar_Dark)
                        getData.launch(i)
                    }
                    R.id.choose_theme ->
                    {
                        val dialog = ChooseThemeDialog()
                        dialog.show(supportFragmentManager, "THEME_DIALOG")
                    }
                }
                true
            }
            popUp.show()
        }
        registerForContextMenu(r.textResult)
        r.display.showSoftInputOnFocus = false
        r.display.requestFocus()
        setTexts()
        r.display.setSelection(viewModel.selectionStart, viewModel.selectionEnd)
        r.display.setOnEditTextChangedListener(this)
        specialFunActions()
        r.showAll.setOnClickListener {
            if (viewModel.isAllOpen)
            {
                r.container.transitionToStart()
                r.animation.transitionToStart()
            } else
            {
                r.container.transitionToEnd()
                r.animation.transitionToEnd()
            }
            viewModel.isAllOpen = !viewModel.isAllOpen
        }
    }

    private fun operatorsActions()
    {
        r.reset.setOnClickListener {
            viewModel.reset()
            setTexts()
        }
        r.delete.setOnClickListener {
            when (viewModel.delete())
            {
                0 -> setTexts()
                1 ->
                {
                    viewModel.deleteLastPosition()
                    setTexts()
                }
                else ->
                {
                }
            }
        }
        r.plus.setOnClickListener {
            if (viewModel.textNumbers != "" &&
                    viewModel.actionSymbols("+", arrayOf("+", "-", "(", "×", "÷", "^", "√")))
                setTexts()
        }
        r.minus.setOnClickListener {
            if (viewModel.actionSymbols("-", arrayOf("-", "+-", "(-", ")-", "×-", "÷-", "^-", "√")))
                setTexts()
        }
        r.multiplication.setOnClickListener {
            if (viewModel.textNumbers != "" &&
                    viewModel.actionSymbols("×", arrayOf("+", "-", "(", "×", "÷", "^", "√")))
                setTexts()
        }
        r.parLeft.setOnClickListener {
            if (viewModel.actionSymbols("(", null))
                setTexts()
        }
        r.parRight.setOnClickListener {
            if (viewModel.textNumbers != "" &&
                    viewModel.actionSymbols(")", arrayOf("+", "-", "(", "×", "÷", "^", "√")))
                setTexts()
        }
        r.division.setOnClickListener {
            if (viewModel.textNumbers != "" &&
                    viewModel.actionSymbols("÷", arrayOf("+", "-", "(", "×", "÷", "^", "√")))
                setTexts()
        }
        r.sqrt.setOnClickListener {
            if (viewModel.actionSymbols("√", null))
                setTexts()
        }
        r.pow.setOnClickListener {
            if (viewModel.textNumbers != "" &&
                    viewModel.actionSymbols("^", arrayOf("(", "×", "÷", "^", "√", "-", "+")))
                setTexts()
        }
        r.equal.setOnClickListener {
            if (viewModel.equal(applicationContext))
                setTexts()
        }
    }

    private fun specialFunActions()
    {
        r.display.setOnKeyListener { _, keyCode, event ->
            when (keyCode)
            {
                KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT,
                KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN,
                KeyEvent.KEYCODE_BACK ->
                    super.onKeyUp(keyCode, event)
                else -> true
            }
        }
        r.sin.setOnClickListener {
            val symbol =
                if (r.sin.text == getString(R.string.sin)) "${Constants.sin}(" else "${Constants.arcSin}("
            if (viewModel.actionSymbols(symbol, null)) setTexts()
        }
        r.cos.setOnClickListener {
            val symbol =
                if (r.cos.text == getString(R.string.cos)) "${Constants.cos}(" else "${Constants.arcCos}("
            if (viewModel.actionSymbols(symbol, null)) setTexts()
        }
        r.tan.setOnClickListener {
            val symbol =
                if (r.tan.text == getString(R.string.tan)) "${Constants.tan}(" else "${Constants.arcTan}("
            if (viewModel.actionSymbols(symbol, null)) setTexts()
        }
        r.log.setOnClickListener {
            val ban = if (r.log.text == getString(R.string.lg))
                viewModel.actionSymbols("${Constants.lg}(", null)
            else
                viewModel.actionSymbols("10^", null, true)
            if (ban) setTexts()
        }
        r.ln.setOnClickListener {
            val ban = if (r.ln.text == getString(R.string.ln))
                viewModel.actionSymbols("${Constants.ln}(", null)
            else
                viewModel.actionSymbols("${Constants.e}^", null, true)
            if (ban) setTexts()
        }
        r.fact.setOnClickListener {
            if (viewModel.fact()) setTexts()
        }
        r.mode.setOnClickListener {
            switchMode()
        }
        r.textModeBtn.setOnClickListener {
            switchMode()
        }
        r.second.setOnClickListener {
            viewModel.isSecondPressed = !viewModel.isSecondPressed
            textsSecond()
        }
    }

    private fun numbersActions()
    {
        for ((i, value) in numbers.withIndex())
            value.setOnClickListener {
                viewModel.actionNumbers(i)
                setTexts()
            }
        r.point.setOnClickListener {
            if (viewModel.point()) setTexts()
        }
        r.pi.setOnClickListener {
            if (viewModel.actionSymbols("π", null, true)) setTexts()
        }
        r.e.setOnClickListener {
            if (viewModel.actionSymbols(Constants.e, null, true)) setTexts()
        }
    }

    private fun switchMode()
    {
        viewModel.mathMode = when (viewModel.mathMode)
        {
            MathMode.DEG -> MathMode.RAD
            MathMode.RAD -> MathMode.DEG
        }
        textsMode()
        if (viewModel.textNumbers != "")
        {
            val res = viewModel.calculate()
            viewModel.textResult = if (res != "") "=$res" else res
            r.textResult.text = viewModel.textResult
        }
    }

    private fun textsMode()
    {
        when (viewModel.mathMode)
        {
            MathMode.DEG ->
            {
                r.textModeBtn.text = getString(R.string.deg)
                r.mode.text = getString(R.string.rad)
            }
            MathMode.RAD ->
            {
                r.textModeBtn.text = getString(R.string.rad)
                r.mode.text = getString(R.string.deg)
            }
        }
    }

    private fun textsSecond()
    {
        val txtSin = getString(R.string.sin)
        val txtCos = getString(R.string.cos)
        val txtTan = getString(R.string.tan)
        val txtLg = getString(R.string.lg)
        val txtLn = getString(R.string.ln)
        if (viewModel.isSecondPressed)
        {
            val txtLg2 = "10"
            val txtLn2 = Constants.e
            val superScript = "-1"
            val superScript2 = "x"
            r.sin.text = SpanProvider.scriptSpan(txtSin, superScript)
            r.cos.text = SpanProvider.scriptSpan(txtCos, superScript)
            r.tan.text = SpanProvider.scriptSpan(txtTan, superScript)
            r.log.text = SpanProvider.scriptSpan(txtLg2, superScript2)
            r.ln.text = SpanProvider.scriptSpan(txtLn2, superScript2)
        } else
        {
            r.sin.text = txtSin
            r.cos.text = txtCos
            r.tan.text = txtTan
            r.log.text = txtLg
            r.ln.text = txtLn
        }
    }

    private fun setTexts()
    {
        r.display.setText(viewModel.textNumbers)
        r.textResult.text = viewModel.textResult
    }

    private fun switchColorMode(theme: Int)
    {
        val provider = PreferencesProvider(applicationContext)
        var isDarkMode = false
        when (theme)
        {
            0 -> provider.putString(PreferencesKey.UI_MODE, PreferencesKey.LIGHT_MODE.key)
            1 ->
            {
                provider.putString(PreferencesKey.UI_MODE, PreferencesKey.DARK_MODE.key)
                isDarkMode = true
            }
            2 ->
            {
                provider.putString(PreferencesKey.UI_MODE, PreferencesKey.SYSTEM_DEFAULT.key)
                val configuration: Configuration = resources.configuration
                if (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)
                    isDarkMode = true
            }
        }

        var reload = false
        var decorViewColor = 0
        var statusBarColor = 0
        var clear = false
        if ((currentTheme == R.style.AppTheme_NoActionBar && theme == 1) ||
                (currentTheme == R.style.AppTheme_NoActionBar && isDarkMode))
        {
            currentTheme = R.style.AppTheme_NoActionBar_Dark
            application.setTheme(currentTheme)
            setTheme(currentTheme)
            decorViewColor = Color.BLACK
            statusBarColor = getColor(R.color.dark_gray)
            clear = true
            reload = true
        } else if ((currentTheme == R.style.AppTheme_NoActionBar_Dark && theme == 0) ||
                (currentTheme == R.style.AppTheme_NoActionBar_Dark && !isDarkMode))
        {
            currentTheme = R.style.AppTheme_NoActionBar
            application.setTheme(currentTheme)
            setTheme(currentTheme)
            decorViewColor = Color.WHITE
            statusBarColor = Color.WHITE
            reload = true
        }

        if (reload)
        {
            r = ActivityCalculatorBinding.inflate(layoutInflater)
            setContentView(r.root)

            Methods.setDecorViewStatusBarColor(window, decorViewColor, statusBarColor, clear)

            numbers = arrayOf(r.n0, r.n1, r.n2, r.n3, r.n4, r.n5, r.n6, r.n7, r.n8, r.n9)
            textsMode()
            textsSecond()
            if (viewModel.isAllOpen)
            {
                r.container.post {
                    r.container.progress = 1.0F
                }
                r.animation.post {
                    r.animation.progress = 1.0F
                }
            }
            setActions()
        }
    }

    override fun onSelectionChanged(start: Int, end: Int)
    {
        val compare = viewModel.handleOnSelectionChanged(start, end)

        if (!compare)
            r.display.setSelection(viewModel.selectionStart, viewModel.selectionEnd)

        if (!viewModel.addingText && compare)
        {
            viewModel.selectionStart = start
            viewModel.selectionEnd = end
        }
        viewModel.addingText = false
    }

    override fun onTextChanged(isCutText: Boolean,
                               isPastedText: Boolean,
                               start: Int,
                               lengthBefore: Int,
                               lengthAfter: Int)
    {
        if (viewModel.handleOnTextChanged(isCutText, isPastedText, start,
                                          lengthAfter, r.display.text.toString()))
            r.textResult.text = viewModel.textResult
        r.display.requestFocus()
        r.display.setSelection(viewModel.selectionStart, viewModel.selectionEnd)
    }

    override fun onThemeChanged(theme: Int)
    {
        switchColorMode(theme)
    }
}