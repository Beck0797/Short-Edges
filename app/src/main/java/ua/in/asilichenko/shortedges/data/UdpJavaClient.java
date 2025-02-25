package ua.in.asilichenko.shortedges.data;

        import android.util.Log;

        import java.net.DatagramPacket;
        import java.net.DatagramSocket;
        import java.net.InetAddress;
        import java.util.concurrent.ExecutorService;
        import java.util.concurrent.Executors;

        import javax.inject.Inject;
        import javax.inject.Singleton;

@Singleton
public class UdpJavaClient {

    private DatagramSocket socket;
    private final InetAddress serverAddress;
    private final int serverPort = 15002; // Replace with actual server port
    private MessageCallback messageCallback;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public interface MessageCallback {
        void onMessageReceived(String message);
    }


    public UdpJavaClient() throws Exception {
        this.serverAddress = InetAddress.getByName("192.168.0.29"); // Replace with actual server IP
    }

    public void start(MessageCallback callback) {
        this.messageCallback = callback;
        executorService.execute(() -> {
            try {
                socket = new DatagramSocket();
                sendMessage("Hello UDP Server");

                byte[] receiveData = new byte[1024];
                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket);
                    String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    Log.d("UDP Client", "Received: " + receivedMessage);

                    if (messageCallback != null) {
                        messageCallback.onMessageReceived(receivedMessage);
                    }
                }
            } catch (Exception e) {
                Log.e("UDP Client", "Error: ", e);
            }
        });
    }

    public void sendMessage(String message) {
        executorService.execute(() -> {
            try {
                byte[] sendData = message.getBytes();
                DatagramPacket packet = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
                socket.send(packet);
                Log.d("UDP Client", "Sent: " + message);
            } catch (Exception e) {
                Log.e("UDP Client", "Error sending message", e);
            }
        });
    }

    private void handleCommand(String command) {
        switch (command.trim().toLowerCase()) {
            case "hello":
                sendMessage("Hello Client");
                break;
            case "bye":
                sendMessage("Goodbye");
                break;
            case "status":
                sendMessage("Client is active");
                break;
            default:
                sendMessage("Unknown command");
                break;
        }
    }
}
