package com.labstyle.coralogixloggersampleapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.labstyle.coralogixlogger.CoralogixLogger
import com.labstyle.coralogixlogger.models.CoralogixSeverity
import com.labstyle.coralogixloggersampleapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoralogixLogger.initializeApp(
            context = applicationContext,
            privateKey = "de51aff6-e7f4-6b4a-d1de-18e116882dc9",
            applicationName = "dario_health",
            subsystemName = "STG")

        binding.outputText.text = "Logger initialized. Adding more logs..."

        for (i in 0..99) {
            CoralogixLogger.log(CoralogixSeverity.DEBUG, "test debug $i")
        }

        binding.outputText.text = "Logs added. Appending more..."
        Handler(Looper.getMainLooper()).postDelayed({
            CoralogixLogger.log(CoralogixSeverity.INFO, "test info")
            binding.outputText.text = "Logs added."
        }, 1500)
    }


}