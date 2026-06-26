package mediaworking.GameSmiths;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Chat {
    private static final Logger LOGGER = Logger.getLogger(Chat.class.getName());
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;
    private Consumer<String> onMessageReceived;

    public Chat(Socket socket, Consumer<String> onMessageReceived) {
        this.socket = socket;
        this.onMessageReceived = onMessageReceived;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error creating streams", e);
        }
    }

    public void startListening() {
        new Thread(() -> {
            try {
                while (true) {
                    String message = (String) in.readObject();
                    onMessageReceived.accept(message);
                }
            } catch (IOException | ClassNotFoundException e) {
                LOGGER.log(Level.SEVERE, "Error receiving message", e);
            }
        }).start();
    }

    public void sendMessage(String message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error sending message", e);
        }
    }

    public void close() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error closing chat resources", e);
        }
    }
}