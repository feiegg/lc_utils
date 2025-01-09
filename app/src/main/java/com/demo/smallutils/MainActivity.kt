package com.demo.smallutils

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.smallcake.utils.CDTimer
import com.smallcake.utils.ToastUtil
import com.smallcake.utils.buildCDTimer
import com.smallcake.utils.buildSpannableString
import com.smallcake.utils.showToast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView = findViewById<TextView>(R.id.text_view)
        val speedChartView = findViewById<SpeedChartView>(R.id.speed_chart)
        val cbTimer = textView.buildCDTimer()
        textView.buildSpannableString(true) {
            addText("你好11")
            addText("World1!"){
                isItalic = true
                isBold = true
                textColor = Color.RED
                onClick{
                    cbTimer.start()
                }
            }
        }
        speedChartView.setProgress(80f)
        ToastUtil.showShort("nihaoua")
    }
}