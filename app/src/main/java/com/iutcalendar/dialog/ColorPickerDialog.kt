package com.iutcalendar.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import com.iutcalendar.math.MyMath
import com.univlyon1.tools.agenda.R
import kotlin.math.roundToInt

class ColorPickerDialog(
    context: Context,
    private val defaultRed: Int = 0, private val defaultGreen: Int = 0, private val defaultBlue: Int = 0,
    private val validateColor: (color: Int) -> Unit
) : Dialog(context) {
    private lateinit var redValue: TextView
    private lateinit var redSeek: SeekBar
    private lateinit var greenValue: TextView
    private lateinit var greenSeek: SeekBar
    private lateinit var blueValue: TextView
    private lateinit var blueSeek: SeekBar
    private lateinit var lumValue: TextView
    private lateinit var lumSeek: SeekBar

    private lateinit var submitBtn: Button
    private lateinit var cancelBtn: Button

    private lateinit var colorView: View

    private fun initVariable() {
        colorView = findViewById(R.id.colorViewer)
        redValue = findViewById(R.id.redValue)
        redSeek = findViewById(R.id.red)
        greenValue = findViewById(R.id.greenValue)
        greenSeek = findViewById(R.id.green)
        blueValue = findViewById(R.id.blueValue)
        blueSeek = findViewById(R.id.blue)
        lumValue = findViewById(R.id.lumValue)
        lumSeek = findViewById(R.id.lum)

        submitBtn = findViewById(R.id.submitBtn)
        cancelBtn = findViewById(R.id.cancelBtn)
    }

    init {
        setContentView(R.layout.dialog_color_picker)
        initVariable()

        initSeekBars()

        initBtns()

        previewColor()
    }

    private fun initBtns() {
        submitBtn.setOnClickListener {
            dismiss()
            validateColor(getColor())
        }
        cancelBtn.setOnClickListener { dismiss() }
    }


    private fun getColor(): Int {
        (lumSeek.progress / 100.0).let { light ->
            val lum = light + 1.0
            (greenSeek.progress * lum).roundToInt()
            val r = fillWithZero(Integer.toHexString(MyMath.between((redSeek.progress * lum).roundToInt(), 0, 255)))
            val g = fillWithZero(Integer.toHexString(MyMath.between((greenSeek.progress * lum).roundToInt(), 0, 255)))
            val b = fillWithZero(Integer.toHexString(MyMath.between((blueSeek.progress * lum).roundToInt(), 0, 255)))

            return Color.parseColor("#$r$g$b")
        }
    }

    private fun fillWithZero(string: String, nbZero: Int = 2): String {
        var newString = string
        while (newString.length < nbZero) {
            newString = "0$newString"
        }
        return newString
    }

    private fun initSeekBars() {
        redSeek.progress = defaultRed
        greenSeek.progress = defaultGreen
        blueSeek.progress = defaultBlue


        redSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                previewColor()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        blueSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                previewColor()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        greenSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                previewColor()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        lumSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                previewColor()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun previewColor() {
        redValue.text = redSeek.progress.toString()
        greenValue.text = greenSeek.progress.toString()
        blueValue.text = blueSeek.progress.toString()
        lumValue.text = (lumSeek.progress / 100.0).toString()
        colorView.setBackgroundColor(getColor())
    }
}
