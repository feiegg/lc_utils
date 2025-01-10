package com.galaxyeye.fcsdk


import io.flutter.plugin.common.MethodChannel

enum class FCNativeMethod {
    connectBluetooth,//Map<String, Any?>
    receiveDirective,//指令 {'directiveType': directiveType, 'directive': directive}
    configureAudioSession
}

enum class FCFlutterFuncEn {
    setupUserAndDevice,
    bluetoothNotification
}

interface FCFlutterMethod {
    fun callMethod(method: String, data: Any?): Any
}

interface FCFlutterDataCallback {
    fun onData(
        method: String, data: Any?, success: (data: Any?) -> Unit, fail: (String?) -> Unit
    )

    fun onMethodRegistered(channelMap: Map<String, FCFlutterMethod>) {

    }

    fun onLoaded() {

    }
}

object FlutterFuncHandle {
    var dataCallback: FCFlutterDataCallback? = null
    var onDataBackFunc: (String, Any?) -> Unit = { method, data -> }
    val channelMap = mutableMapOf<String, MethodChannel>()
    private val fcMethodMap = mutableMapOf<String, FCFlutterMethod>()
    val channelList = mutableListOf<String>()
    fun registerDataCallback(callback: FCFlutterDataCallback) {
        dataCallback = callback
    }

    fun callFlutterMethod(method: FCFlutterFuncEn, data: Any?): Any? {
        return channelMap["com.galaxyeye.funcompanion_module/bridge"]?.invokeMethod(
            method.name,
            data
        )
    }

    fun registerMethodCall(
        channel: String
    ) {
        channelList.add(channel)
    }

    fun release() {
        channelMap.clear()
        dataCallback?.onMethodRegistered(emptyMap())
    }

    fun onCreate() {
        channelMap.forEach {
            fcMethodMap[it.key] = object : FCFlutterMethod {
                override fun callMethod(method: String, data: Any?): Any {
                    return it.value.invokeMethod(method, data)
                }
            }
        }
        dataCallback?.onMethodRegistered(fcMethodMap)
    }
}