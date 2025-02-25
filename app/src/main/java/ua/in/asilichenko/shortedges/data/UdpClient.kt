package ua.`in`.asilichenko.shortedges.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UdpClient(private val serverAddress: String, private val serverPort: Int) {

    // Start the UDP client
    fun start() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val address = InetAddress.getByName(serverAddress)
                val socket = DatagramSocket()

                // Send initial message
                val initialMessage = "Hello UDP Server"
                sendMessage(socket, address, serverPort, initialMessage)

                // Listen for incoming messages continuously
                val receiveData = ByteArray(1024)
                while (true) {
                    val receivePacket = DatagramPacket(receiveData, receiveData.size)
                    socket.receive(receivePacket)
                    val receivedMessage = String(receivePacket.data, 0, receivePacket.length)
                    Log.d("UDP Client", "Received command: $receivedMessage")

                    // Process received command
                    handleCommand(receivedMessage, socket, address, serverPort)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Send message to the server
    private fun sendMessage(socket: DatagramSocket, address: InetAddress, port: Int, message: String) {
        try {
            val sendData = message.toByteArray()
            val packet = DatagramPacket(sendData, sendData.size, address, port)
            socket.send(packet)
            Log.d("UDP Client", "Sent message: $message")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Handle received command and respond accordingly
    private fun handleCommand(command: String, socket: DatagramSocket, address: InetAddress, port: Int) {
        when (command.trim().toLowerCase()) {
            "hello" -> sendMessage(socket, address, port, "Hello Client")
            "bye" -> sendMessage(socket, address, port, "Goodbye")
            "status" -> sendMessage(socket, address, port, "Client is active")
            else -> sendMessage(socket, address, port, "Unknown command")
        }
    }
}