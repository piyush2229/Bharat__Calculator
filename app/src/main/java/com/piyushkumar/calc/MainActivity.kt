package com.piyushkumar.calc
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var canAddOperation = false
    private var canAddDecimal = true

    private lateinit var workingsTV: TextView
    private lateinit var resultsTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentTheme == Configuration.UI_MODE_NIGHT_YES) {
            // Dark theme is active
            setTheme(R.style.Base_Theme_Calc)
        } else {
            // Light theme is active
            setTheme(R.style.AppTheme)
        }

        setContentView(R.layout.activity_main)

        workingsTV = findViewById(R.id.workingsTV)
        resultsTV = findViewById(R.id.resultsTV)
    }

    fun numberAction(view: View) {
        if (view is Button) {
            val lastChar = workingsTV.text.toString().takeLast(1)
            if (view.text == ".") {
                if (canAddDecimal && !workingsTV.text.contains(".")) {
                    workingsTV.append(view.text)
                    canAddDecimal = false
                }
            } else if (view.text == "-" && (workingsTV.text.isEmpty() || lastChar == "+" || lastChar == "-" || lastChar == "x" || lastChar == "/")) {
                workingsTV.append(view.text)
                canAddOperation = false
            } else {
                workingsTV.append(view.text)
                canAddOperation = true
            }
        }
    }


    fun operationAction(view: View) {
        if (view is Button) {
            val lastChar = workingsTV.text.toString().takeLast(1)

            if (workingsTV.text.isNotEmpty() || view.text == "-" || view.text == "^") {
                if (workingsTV.text.toString().trim() != "-" && lastChar != "^") {
                    // Check if workingsTV is not just "-" and the last character is not "^"
                    if (lastChar == "+" || lastChar == "-" || lastChar == "x" || lastChar == "÷" || lastChar == "^") {
                        // Replace the last operator with the new one
                        workingsTV.text = workingsTV.text.dropLast(1)
                    }
                    workingsTV.append(view.text)
                    canAddOperation = false
                    canAddDecimal = true
                }
            }
        }
    }



    fun allClearAction(view: View) {
        workingsTV.text = ""
        resultsTV.text = ""
        canAddOperation = false
        canAddDecimal = true
    }

    fun backSpaceAction(view: View) {
        val length = workingsTV.length()
        if (length > 0)
            workingsTV.text = workingsTV.text.subSequence(0, length - 1)
    }

    fun equalsAction(view: View) {
        val workingsText = workingsTV.text.toString().trim()
        if (workingsText != "-" && workingsText != "+") {
            resultsTV.text = calculateResults()
        }
    }


    private fun calculateResults(): String {
        val digitsOperators = digitsOperators()
        if (digitsOperators.isEmpty()) return ""

        val timesDivision = timesDivisionCalculate(digitsOperators)
        if (timesDivision.isEmpty()) return ""

        val result = addSubtractPowerCalculate(timesDivision)
        return result.toString()
    }

    private fun addSubtractPowerCalculate(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex) {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                when (operator) {
                    '+' -> result += nextDigit
                    '-' -> result -= nextDigit
                    '^' -> result = Math.pow(result.toDouble(), nextDigit.toDouble()).toFloat()
                }
            }
        }

        return result
    }


    fun powerCalculate(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex) {
                val prevDigit = passedList[i - 1] as Float
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                when (operator) {
                    '^' -> {
                        result = Math.pow(prevDigit.toDouble(), nextDigit.toDouble()).toFloat()
                    }
                }
            } else if (i == 0) {
                result = passedList[i] as Float
            }
        }

        return result
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains('x') || list.contains('÷')) {
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex && i < restartIndex) {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                when (operator) {
                    'x' -> {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }

                    '÷' -> {
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                    }

                    else -> {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }

            if (i > restartIndex)
                newList.add(passedList[i])
        }

        return newList
    }

    private fun digitsOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for (character in workingsTV.text) {
            if (character.isDigit() || character == '.' || (character == '-' && currentDigit.isEmpty())) {
                currentDigit += character
            } else {
                if (!currentDigit.isEmpty()) {
                    list.add(currentDigit.toFloat())
                    currentDigit = ""
                }
                list.add(character)
            }
        }

        if (!currentDigit.isEmpty())
            list.add(currentDigit.toFloat())

        return list
    }
}




