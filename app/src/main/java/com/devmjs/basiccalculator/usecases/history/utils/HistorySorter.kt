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

object HistorySorter
{
    fun sort(history: History): Array<Pair<String, ArrayList<HistoryItem>>>
    {
        val n = history.map.size
        //Convert map into a typed array
        val array = history.map.let {
            val arr = Array(n) { Pair("", arrayListOf<HistoryItem>()) }
            var idx = 0
            for (i in it)
                arr[idx++] = Pair(i.key, i.value)
            arr
        }
        //Bubble sort
        for (i in 0 until n - 1)
        {
            for (j in 0 until n - i - 1)
            {
                if (isNewerDate(array[j].first, array[j + 1].first))
                {
                    val tmp = array[j]
                    array[j] = array[j + 1]
                    array[j + 1] = tmp
                }
            }
        }
        return array
    }

    private fun isNewerDate(first: String, second: String): Boolean
    {
        val split = first.split('/')
        val split2 = second.split('/')

        return if (split[1].toInt() >= split2[1].toInt() && split[2].toInt() >= split2[2].toInt())
            true
        else split[2].toInt() > split2[2].toInt()
    }
}