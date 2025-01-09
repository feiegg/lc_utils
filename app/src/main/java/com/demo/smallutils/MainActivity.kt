package com.demo.smallutils

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.galaxyeye.fcsdk.FCAct
import com.galaxyeye.fcsdk.FCFlutterDataCallback
import com.galaxyeye.fcsdk.FCFlutterFuncEn
import com.galaxyeye.fcsdk.FCFlutterMethod
import com.galaxyeye.fcsdk.FCNativeMethod
import com.galaxyeye.fcsdk.FlutterFuncHandle
import com.smallcake.utils.buildCDTimer
import com.smallcake.utils.buildSpannableString
import kotlin.math.absoluteValue
import kotlin.random.Random

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
                    startFACTActivity()
                }
            }
        }
        speedChartView.setProgress(80f)
    }
    fun  startFACTActivity(){
        //注册原生方法回调
        FlutterFuncHandle.registerDataCallback(object : FCFlutterDataCallback {
            override fun onData(
                method: String,
                data: Any?,
                success: (data: Any?) -> Unit,
                fail: (String?) -> Unit
            ) {
                when (method) {
                    FCNativeMethod.connectBluetooth.name -> {
                        Log.d("connectBluetooth", data.toString())
                        success(emptyMap<String, Any>())
                    }

                    FCNativeMethod.receiveDirective.name -> {
                        val directiveData = data as Map<String, Any?>
                        Log.d("receiveDirective", data.toString())
                        //指令类型
                        val directiveType = directiveData["directiveType"] as String
                        //指令
                        val directive = directiveData["directive"] as String
                        success(emptyMap<String, Any>())
                    }

                    FCNativeMethod.configureAudioSession.name -> {
                        Log.d("configureAudioSession", data.toString())
                        success(emptyMap<String, Any>())
                    }

                    else -> {
                        Log.d("onData", "$method $data")
                        success(emptyMap<String, Any>())
                    }
                }
            }

            override fun onMethodRegistered(channelMap: Map<String, FCFlutterMethod>) {
                //接收注册成功的Flutter回调
                Log.d("onMethodRegistered", channelMap.toString())
            }

            override fun onLoaded() {
                val result = FlutterFuncHandle.callFlutterMethod(
                    FCFlutterFuncEn.setupUserAndDevice, mapOf<String, Any?>(
                        "uid" to Random(System.currentTimeMillis()).nextInt().absoluteValue.toString(),
                        "terminalId" to "bma1070400048363732992",
                        "wxAppId" to "xxx"
                    )
                )
                FlutterFuncHandle.callFlutterMethod(
                    FCFlutterFuncEn.bluetoothNotification, mapOf<String, Any?>("type" to "1")
                )
            }
        })

        //启动SDK界面
        val i = Intent(this, FCAct::class.java)
        startActivity(i)
    }
}