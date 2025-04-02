package com.example.screentest

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.GridLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.screentest.databinding.ActivityTestBinding
import kotlinx.coroutines.launch

class TestActivity : AppCompatActivity() {

    private val viewModel: TestViewModel by viewModels()
    private lateinit var binding: ActivityTestBinding

    private var cols = 5
    private var rows = 7
    private var timeOut: Long = 10

    private var touchedCells = Array(rows) { BooleanArray(cols) }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra(ROW_COUNT)?.toIntOrNull()?.let {
            rows = it
        }
        intent.getStringExtra(COL_COUNT)?.toIntOrNull()?.let {
            cols = it
        }
        intent.getStringExtra(TIME_OUT_COUNT)?.toIntOrNull()?.let {
            timeOut = it.toLong()
        }
        binding.gridLayout.columnCount = cols
        binding.gridLayout.rowCount = rows

        touchedCells = Array(rows) { BooleanArray(cols) }
        observeViewModel()

        addViewsToGrid()
        binding.gridLayout.setOnTouchListener { _, motionEvent ->
            touchGridEvent(motionEvent)
        }
        viewModel.startTest(timeOut)
    }

    private fun addViewsToGrid() {
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val cell = View(this).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 0
                        columnSpec = GridLayout.spec(col, 1f)
                        rowSpec = GridLayout.spec(row, 1f)
                        setMargins(0, 0, 0, 0)
                    }
                    setBackgroundColor(Color.TRANSPARENT)
                }
                binding.gridLayout.addView(cell)
            }
        }
    }

    private fun touchGridEvent(motionEvent: MotionEvent): Boolean {
        val cellWidth = binding.gridLayout.width / cols
        val cellHeigth = binding.gridLayout.height / rows

        val col = (motionEvent.x / cellWidth).toInt().coerceIn(0, cols - 1)
        val row = (motionEvent.y / cellHeigth).toInt().coerceIn(0, rows - 1)

        val cellIndex = row * cols + col
        if (cellIndex < binding.gridLayout.childCount) {
            val cell = binding.gridLayout.getChildAt(cellIndex)
            cell.setBackgroundColor(Color.GREEN)
        }

        touchedCells[row][col] = true

        if (touchedCells.all() { rowCells -> rowCells.all { it } }) {
            viewModel.completeTest()
        }

        return true
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.testState.collect { state ->
                when (state) {
                    is TestState.Success -> {
                        //Toast.makeText(this@TestActivity, "Teste passou!", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    }

                    is TestState.Failure -> {
                        //Toast.makeText(this@TestActivity, "Teste falhou!", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_CANCELED)
                        finish()
                    }

                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            viewModel.timeRemaining.collect { time ->
                binding.txtTimeRemaning.text = "Tempo restante: ${time}s"
            }
        }
    }

}