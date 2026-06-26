package mediaworking.GameSmiths.controllers;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientController {

    private static final Logger LOGGER = Logger.getLogger(ClientController.class.getName());
    private final String ipAddress;
    private final int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isGameOver = false; // Flag to track if the game is over

    public ClientController(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public void connectToServer() {
        try {
            socket = new Socket(ipAddress, port);
            LOGGER.log(Level.INFO, "Connected to server at {0}:{1}", new Object[]{ipAddress, port});

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error connecting to server", e);
        }
    }

    public void sendGameUpdate(String message) {
        if (isGameOver) {
            LOGGER.log(Level.WARNING, "Attempt to send message after game over: {0}", message);
            return; // Prevent sending messages after the game is over
        }
        if (out != null) {
            out.println(message);
            LOGGER.log(Level.INFO, "Message sent: {0}", message);
        }
    }

    public String receiveGameUpdate() {
        try {
            if (in != null) {
                String message = in.readLine(); // Block until a message is received
                LOGGER.log(Level.INFO, "Message received: {0}", message);

                // Stop further updates if the game is over
                if (message.startsWith("WINNER:") || message.equals("GAME_OVER")) {
                    isGameOver = true;
                }
                return message;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error receiving game update", e);
        }
        return null;
    }

    public void sendChatMessage(String message) {
        if (out != null) {
            out.println("CHAT:" + message);
            LOGGER.log(Level.INFO, "Chat message sent: {0}", message);
        }
    }

    public String receiveChatMessage() {
        try {
            if (in != null) {
                String message = in.readLine();
                if (message.startsWith("CHAT:")) {
                    LOGGER.log(Level.INFO, "Chat message received: {0}", message.substring(5));
                    return message.substring(5);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error receiving chat message", e);
        }
        return null;
    }

    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            LOGGER.log(Level.INFO, "Disconnected from server");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error disconnecting from server", e);
        }
    }
}
