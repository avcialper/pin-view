package com.avcialper.pinview

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.avcialper.pinview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pinView.setOnPinCompletedListener { pin ->
            if (pin == "123456") {
                makeToastMassage("Correct Password")
                true
            } else {
                makeToastMassage("Wrong Password")
                false
            }
        }
    }

    private fun makeToastMassage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}