package com.example.paint

import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.Window.FEATURE_NO_TITLE
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import petrov.kristiyan.colorpicker.ColorPicker

@Suppress("DEPRECATION")
class DrawActivity : AppCompatActivity(),
    View.OnClickListener,
    RadioGroup.OnCheckedChangeListener,
    SeekBar.OnSeekBarChangeListener,
    ColorPicker.OnChooseColorListener {

    private lateinit var drawView: DrawView
    private lateinit var clearBtn: FloatingActionButton
    private lateinit var radioGroup: RadioGroup
    private lateinit var thicknessBar: SeekBar
    private lateinit var thicknessTextView: TextView
    private lateinit var colorPickerBtn: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)
        requestWindowFeature(FEATURE_NO_TITLE)

        setContentView(R.layout.activity_draw)

        setupDrawView()
        setupClearButton()
        setupRadioGroup()
        setupThicknessBar()
        setupColorPicker()
    }

    private fun setupDrawView() {
        drawView = findViewById(R.id.drawView)
        drawView.requestFocus()

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        drawView.init(displayMetrics)
    }

    private fun setupClearButton() {
        clearBtn = findViewById(R.id.clearFAB)
        clearBtn.setOnClickListener(this)
    }

    private fun setupRadioGroup() {
        radioGroup = findViewById(R.id.radioGroup)
        radioGroup.check(R.id.normalModeRadio)
        radioGroup.setOnCheckedChangeListener(this)
    }

    private fun setupThicknessBar() {
        thicknessBar = findViewById(R.id.thicknessBar)
        thicknessTextView = findViewById(R.id.thicknessTextView)
        thicknessBar.setOnSeekBarChangeListener(this)
        thicknessBar.progress = 30
    }

    private fun setupColorPicker() {
        colorPickerBtn = findViewById(R.id.colorPickerFAB)
        colorPickerBtn.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            clearBtn -> drawView.clear()
            colorPickerBtn -> chooseColor()
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.normalModeRadio -> drawView.normalMode()
            R.id.embossModeRadio -> drawView.embossMode()
            R.id.blurModeRadio -> drawView.blurMode()
        }
    }

    private fun chooseColor() {
        val colorPicker = ColorPicker(this)
        val colors: ArrayList<String> = ArrayList(
            arrayListOf(
                "#ff000000", "#ff0000ff", "#ff00ffff", "#ff444444", "#ff888888",
                "#ff00ff00", "#ffcccccc", "#ffff00ff", "#ffff0000", "#ffffff00"
            )
        )

        colorPicker.setColors(colors)
            .setDefaultColorButton(Color.RED)
            .setColumns(5)
            .setRoundColorButton(true)
            .setOnChooseColorListener(this)
            .show()
    }

    override fun onChooseColor(position: Int, color: Int) {
        drawView.setColor(color)
    }

    override fun onCancel() {

    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        drawView.setStrokeSize(progress)
        thicknessTextView.text = progress.toString()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }


}