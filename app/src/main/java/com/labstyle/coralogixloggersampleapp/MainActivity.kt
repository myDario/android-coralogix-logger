package com.labstyle.coralogixloggersampleapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.labstyle.coralogixlogger.CoralogixLogger
import com.labstyle.coralogixlogger.models.CoralogixLogEntry
import com.labstyle.coralogixlogger.models.CoralogixRegion
import com.labstyle.coralogixlogger.models.CoralogixSeverity
import com.labstyle.coralogixloggersampleapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoralogixLogger.setDebug(true)
        CoralogixLogger.initializeApp(
            context = applicationContext,
            privateKey = "xxx",
            applicationName = "xxx",
            subsystemName = "xxx",
            region = CoralogixRegion.US1,
            persistence = true
        )

        binding.outputText.text = "Logger initialized. Adding more logs..."

        for (i in 0..99) {
            CoralogixLogger.log(CoralogixSeverity.DEBUG, "test debug $i")
        }

        binding.outputText.text = "Logs added. Appending more..."
        Handler(Looper.getMainLooper()).postDelayed({
            CoralogixLogger.log(CoralogixSeverity.INFO, "test info")
            binding.outputText.text = "Logs added."
        }, 1500)

        CoralogixLogger.logImmediate(listOf(
            CoralogixLogEntry(
                id = 0,
                timestamp = System.currentTimeMillis(),
                severity = CoralogixSeverity.DEBUG.code(),
                text = "immediate")
        )) { sent ->
            Log.d("CLX", "log sent: $sent")
        }
    }


}