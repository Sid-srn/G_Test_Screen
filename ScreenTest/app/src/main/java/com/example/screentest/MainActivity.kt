package com.example.screentest

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.screentest.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

const val ROW_COUNT = "ROW_COUNT"
const val COL_COUNT = "COL_COUNT"
const val TIME_OUT_COUNT = "TIME_OUT_COUNT"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var register: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        register = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                showSnackBar("Teste Sucesso", true)
            } else {
                showSnackBar("Teste Falha", false)
            }
        }
        binding.btnStartTest.setOnClickListener {
            val intent = Intent(this, TestActivity::class.java)

            intent.putExtra(ROW_COUNT, binding.rowsCount.text.toString())
            intent.putExtra(COL_COUNT, binding.columsCount.text.toString())
            intent.putExtra(TIME_OUT_COUNT, binding.timeOutCount.text.toString())

            register.launch(intent)
            //startActivity(Intent(this, TestActivity::class.java))
        }
    }

    private fun showSnackBar(message: String, success: Boolean) {
        val snackBar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        snackBar.setBackgroundTint(if (success) Color.GREEN else Color.RED)
        snackBar.show()
    }
}