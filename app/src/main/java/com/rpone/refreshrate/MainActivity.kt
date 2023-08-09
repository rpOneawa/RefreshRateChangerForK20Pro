package com.rpone.refreshrate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val refreshRateText = findViewById<TextView>(R.id.refresh_rate_text)
        val btn60 = findViewById<Button>(R.id.refresh_rate_60)
        val btn75 = findViewById<Button>(R.id.refresh_rate_75)
        val btn90 = findViewById<Button>(R.id.refresh_rate_90)

        var currentRefreshRate = ""

        fun runCommand(command: String, returnOrNot: Boolean): String {
            val runtime = Runtime.getRuntime()
            val catchingProc: Process = runtime.exec("su")
            val os = DataOutputStream(catchingProc.outputStream)
            os.writeBytes("$command\n")
            os.flush()
            if (returnOrNot) {
                return catchingProc.inputStream.bufferedReader().readLine()
            } else {
                return ""
            }
        }

        fun setRefreshRate(refreshRate: String, number: String, screenColor: String) {
            runCommand("service call SurfaceFlinger 1035 i32 $number", false)
            runCommand("echo $screenColor > /sys/devices/platform/kcal_ctrl.0/kcal", false)
            refreshRateText.text = "当前刷新率：$refreshRate Hz"
        }

        currentRefreshRate = runCommand("su -c 'dumpsys SurfaceFlinger | grep cur:[0-9]* -o | cut -f2 -d \":\"'", true)
        refreshRateText.text = "当前刷新率：$currentRefreshRate Hz"

        btn60.setOnClickListener {setRefreshRate("60", "0", "256 256 256")}
        btn75.setOnClickListener {setRefreshRate("75", "2", "248 236 256")}
        btn90.setOnClickListener {setRefreshRate("90", "1", "242 218 256")}
    }
}