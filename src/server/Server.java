package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import player.Player;
import player.PlayersHolder;
import thread.PlayerWrapperThread;

/**
 * Starts TCP Server
 */
public class Server {

    private static final int PORT = 3008;

    private PlayersHolder waitingPlayers;
    private ServerSocket serverSocket;

    /**
     * default constructor
     */
    public Server() {
        waitingPlayers = new PlayersHolder();
        new Thread(waitingPlayers).start();

    }

    public void start() throws IOException {

        serverSocket = new ServerSocket(PORT);
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("Waiting for new client...");
            Socket newClientSocket = serverSocket.accept();
            startNewPlayerThread(newClientSocket);
        }
    }

    /**
     * 
     * Entry point for java program that will initialize TCP Server
     * 
     * @param args
     */
    public static void main(String[] args) {

        Server tcpServer = new Server();
        try {
            tcpServer.start();
        } catch (IOException e) {
            System.err.println("Server failed to start!");
        }
    }

    /**
     * @param newClientSocket
     * @throws IOException
     */
    private void startNewPlayerThread(Socket newClientSocket) throws IOException {

        PrintWriter playerWriter = new PrintWriter(newClientSocket.getOutputStream());
        BufferedReader playerReader = new BufferedReader(
                new InputStreamReader(newClientSocket.getInputStream()));
        Player newPlayer = new Player(playerWriter, playerReader);
        PlayerWrapperThread newPlayerThread = new PlayerWrapperThread(newPlayer, waitingPlayers);
        System.out.println("New client arrived. ClientThread starts");
        new Thread(newPlayerThread).start();
    }

   
}
