package com.galaxyeye.fcsdk

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel

fun Context.createAppEngine(initRoute: String = "/pre", isClean: Boolean = false): FlutterEngine {
    val flutterEngine = FlutterEngine(this)
    // Configure an initial route.
    flutterEngine.navigationChannel.setInitialRoute(initRoute);
    // Start executing Dart code to pre-warm the FlutterEngine.
    flutterEngine.dartExecutor.executeDartEntrypoint(
        DartExecutor.DartEntrypoint.createDefault()
    )
    if (FlutterEngineCache.getInstance().get("appEngine") != null && !isClean) {
        return FlutterEngineCache.getInstance().get("appEngine")!!.also {
            it.navigationChannel.setInitialRoute(initRoute)
        }
    }
    FlutterEngineCache.getInstance().get("appEngine")?.destroy()
    // Cache the FlutterEngine to be used by FlutterActivity or FlutterFragment.
    FlutterEngineCache.getInstance().put("appEngine", flutterEngine)
    return flutterEngine.also {
        it.sdkInit(FlutterFuncHandle.onDataBackFunc)
    }
}

private fun FlutterEngine.sdkInit(onDataBack: (String, Any?) -> Unit = { method, data -> }) {
    val flutterEngine = this
    val channelList =
        listOf(
            "com.galaxyeye.bluetooth_broadcast",
            "com.galaxyeye.funcompanion/platform",
            "com.galaxyeye.funcompanion_module/bridge"
        )
    channelList.forEach {
        FlutterFuncHandle.registerMethodCall(it)
    }
    FlutterFuncHandle.channelList.forEach {
        FlutterFuncHandle.channelMap[it] = MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger, it
        ).apply {
            setMethodCallHandler { call, result ->
                call.arguments<Any?>()?.let { it ->
                    FlutterFuncHandle.dataCallback?.onData(call.method, data = it, success = {
                        result.success(it)
                    }, fail = {
                        result.error("1", it, null)
                    })
                    onDataBack(call.method, it)
                }
            }
        }
    }
    FlutterFuncHandle.onCreate()

}

class FCAct : FlutterActivity() {

    private var broadcastChannel: MethodChannel? = null
    private var systemChannel: MethodChannel? = null
    override fun provideFlutterEngine(context: Context): FlutterEngine {
        val engine = application.createAppEngine()
        return engine
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        application.createAppEngine()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                0
            ) {
                flutterEngine?.navigationChannel?.popRoute()
            }
        }
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        FlutterFuncHandle.onDataBackFunc = { method, data ->
            onDataBack(method, data)
        }
    }

    override fun onFlutterUiDisplayed() {
        super.onFlutterUiDisplayed()
        FlutterFuncHandle.dataCallback?.onLoaded()
    }

    private fun onDataBack(method: String, data: Any?) {
        Log.d("onDataBack", "$method $data")
        when (method) {
            "closeFlutterPage" -> {
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        FlutterFuncHandle.release()
    }
}