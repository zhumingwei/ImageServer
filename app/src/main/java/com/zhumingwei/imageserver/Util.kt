package com.zhumingwei.imageserver

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager


object Util {
    fun getLocalIPAddress(context: Context): String? {
        val wifiManager =
            context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifiManager != null) {
            @SuppressLint("MissingPermission") val wifiInfo =
                wifiManager.connectionInfo
            return intIP2StringIP(wifiInfo.ipAddress)
        }
        return ""
    }

    fun intIP2StringIP(ip: Int): String {
        return (ip and 0xFF).toString() + "." +
                (ip shr 8 and 0xFF) + "." +
                (ip shr 16 and 0xFF) + "." +
                (ip shr 24 and 0xFF)
    }
}