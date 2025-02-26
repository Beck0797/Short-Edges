package ua.`in`.asilichenko.shortedges.data

import android.util.Log
import kotlinx.coroutines.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UdpClient @Inject constructor() {

    private var receiveSocket: DatagramSocket? = null
    private var sendSocket: DatagramSocket? = null
    private val serverAddress: InetAddress = InetAddress.getByName("192.168.0.29") // Server IP
    private val receivePort: Int = 15002 // Listening port
    private val sendPort: Int = 15001 // Destination port

    private var messageCallback: ((String) -> Unit)? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun start(onMessageReceived: (String) -> Unit) {
        messageCallback = onMessageReceived
        coroutineScope.launch {
            try {
                receiveSocket =
                    DatagramSocket(receivePort) // Bind socket to port 15002 for receiving
                sendSocket = DatagramSocket() // Create sending socket

                val receiveData = ByteArray(1024)
                while (true) {
                    val receivePacket = DatagramPacket(receiveData, receiveData.size)
                    receiveSocket?.receive(receivePacket)
                    val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                    Log.d("UDP Client", "Received: $receivedMessage")

                    // Notify ViewModel via callback
                    messageCallback?.invoke(receivedMessage)
                }
            } catch (e: Exception) {
                Log.e("UDP Client", "Error: ${e.message}", e)
            }
        }
    }

    fun sendMessage(message: String) {
        coroutineScope.launch {
            try {
                val sendData = message.toByteArray()
                val packet = DatagramPacket(sendData, sendData.size, serverAddress, sendPort)
                sendSocket?.send(packet) // Send to port 15001
                Log.d("UDP Client", "Sent: $message to $sendPort")
            } catch (e: Exception) {
                Log.e("UDP Client", "Send error: ${e.message}", e)
            }
        }
    }

    fun closeSockets() {
        coroutineScope.launch {
            receiveSocket?.close()
            sendSocket?.close()
            Log.d("UDP Client", "Sockets closed")
        }
    }
}
