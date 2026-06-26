package mediaworking.GameSmiths.controllers;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerController {

    private static final Logger LOGGER = Logger.getLogger(ServerController.class.getName());
    private final int port;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isGameOver = false; // Flag to track if the game is over

    public ServerController(int port) {
        this.port = port;
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            LOGGER.log(Level.INFO, "Server started on port {0}", port);

            clientSocket = serverSocket.accept();
            LOGGER.log(Level.INFO, "Client connected");

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error starting server", e);
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

    public void stopServer() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
            LOGGER.log(Level.INFO, "Server stopped");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error stopping server", e);
        }
    }
}
