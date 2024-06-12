package com.example.nsdsample

import android.annotation.SuppressLint
import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

/**
 * Service Discovery (on the mobile device):
 *
 * Discovers services on the network.
 */
@SuppressLint("LogNotTimber")
class NsdServiceMobile(
    context: Context,
) {

    private val nsdManager by lazy { context.getSystemService(Context.NSD_SERVICE) as NsdManager }
    private var nsdSocket: NsdClientSocket? = null

    fun start() {
        nsdManager.discoverServices(NsdServiceConfig.SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, NsdDiscovery())
    }

    fun sendMessage(message: String) {
       Thread {
           nsdSocket?.sendMessageToCtv(message)
       }.start()
    }

    private fun connectToService(serviceInfo: NsdServiceInfo) {
        Thread {
            NsdClientSocket(serviceInfo)
                .also { nsdSocket = it }
                .connectToTv()
        }.start()
    }

    private inner class NsdDiscovery : NsdManager.DiscoveryListener {

        override fun onDiscoveryStarted(regType: String) {
            Log.d("NSD", "Service discovery started")
        }

        override fun onServiceFound(service: NsdServiceInfo) {
            Log.d("NSD", "Service discovery success: $service")

            if (service.serviceType != NsdServiceConfig.SERVICE_TYPE) {
                return
            }

            nsdManager.resolveService(service, object : NsdManager.ResolveListener {
                override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                    Log.e("NSD", "Resolve failed: $errorCode")
                }

                override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                    Log.d("NSD", "Service resolved: $serviceInfo")
                    connectToService(serviceInfo)
                }
            })
        }

        override fun onServiceLost(service: NsdServiceInfo) {
            Log.e("NSD", "Service lost: $service")
        }

        override fun onDiscoveryStopped(serviceType: String) {
            Log.d("NSD", "Discovery stopped: $serviceType")
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e("NSD", "Discovery failed: $errorCode")
            nsdManager.stopServiceDiscovery(this)
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e("NSD", "Discovery failed: $errorCode")
            nsdManager.stopServiceDiscovery(this)
        }
    }
}

/**
 * Client Socket (on the Mobile device)
 */
private class NsdClientSocket(
    tvServiceInfo: NsdServiceInfo,
) {

    private val socket = Socket(tvServiceInfo.host, tvServiceInfo.port)
    private val input = BufferedReader(InputStreamReader(socket.inputStream))
    private val output = PrintWriter(socket.outputStream, true)

    fun connectToTv() {
        sendMessageToCtv("Hey TV! I am your mobile device.")

        while (true) {
            val message = input.readLine()
            handleReceivedMessageFromCtv(message)
        }
    }

    fun sendMessageToCtv(message: String) {
        output.println(message)
    }

    private fun handleReceivedMessageFromCtv(message: String) {
        // Handle the message received from the ctv device
        Log.d("NSD", "Received message: $message")
    }
}

internal object NsdServiceConfig {

    const val SERVICE_NAME = "YourServiceName"
    const val SERVICE_TYPE = "_http._tcp."
    const val SERVICE_PORT = 54321
}