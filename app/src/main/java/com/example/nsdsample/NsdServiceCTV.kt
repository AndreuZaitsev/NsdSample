package com.example.nsdsample

import android.annotation.SuppressLint
import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket

/**
 * Service Registration (on the TV device):
 *
 * Listens for incoming connection requests.
 *
 * to use emulators:
 * https://developer.android.com/studio/run/emulator-networking#connecting
 */
@SuppressLint("LogNotTimber")
class NsdServiceCTV(
    context: Context,
) {

    private val nsdManager by lazy { context.getSystemService(Context.NSD_SERVICE) as NsdManager }
    private val socket by lazy { NsdServerSocket() }

    fun start() {
        nsdManager.registerService(nsdServiceInfo, NsdManager.PROTOCOL_DNS_SD, NsdReg())
        Thread { socket.startServer() }.start()
    }

    fun sendMessage(message: String) {
       Thread {
           socket.sendMessageToMobile(message)
       }.start()
    }

    private inner class NsdReg : NsdManager.RegistrationListener {

        override fun onServiceRegistered(serviceInfo: NsdServiceInfo) {
            Log.d("NSD", "Service registered: ${serviceInfo.serviceName}")
        }

        override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            Log.e("NSD", "Service registration failed: $errorCode")
        }

        override fun onServiceUnregistered(arg0: NsdServiceInfo) {
            Log.d("NSD", "Service unregistered: ${arg0.serviceName}")
        }

        override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            Log.e("NSD", "Service unregistration failed: $errorCode")
        }
    }

    companion object {

        private val nsdServiceInfo = NsdServiceInfo().apply {
            serviceName = NsdServiceConfig.SERVICE_NAME
            serviceType = NsdServiceConfig.SERVICE_TYPE
            port = NsdServiceConfig.SERVICE_PORT
        }
    }
}

/**
 * On the TV Device (Server)
 */
private class NsdServerSocket {

    private val serverSocket = ServerSocket(NsdServiceConfig.SERVICE_PORT)
    private val clientSocket = serverSocket.accept()
    private val input = BufferedReader(InputStreamReader(clientSocket.inputStream))
    private val output = PrintWriter(clientSocket.outputStream, true)

    fun startServer() {
        while (true) {
            val message = input.readLine()
            handleReceivedMessageFromMobile(message)
        }
    }

    fun sendMessageToMobile(message: String) {
        output.println(message)
    }

    private fun handleReceivedMessageFromMobile(message: String) {
        // Handle the message received from the mobile device
        Log.d("NSD", "Received message: $message")

        sendMessageToMobile("Hey Mobile! I received your message!")
    }
}