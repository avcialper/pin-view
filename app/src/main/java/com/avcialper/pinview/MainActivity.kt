package com.avcialper.pinview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.avcialper.pinview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), PinViewListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.pinView.setPinListener(this)
    }

    override fun onPinEntryCompleted(pin: String) {
        if (pin == "123456")
            Toast.makeText(applicationContext, "Correct password!", Toast.LENGTH_SHORT).show()
        else
            binding.pinView.changePinBoxBackground(true)
    }
}