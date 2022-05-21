package com.smallplayz;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;

public class MultiThreadChatServer {

    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;

    private static final int maxClientsCount = 2;
    private static int portNumber = 10101;

    private static final clientThread[] threads = new clientThread[maxClientsCount];

    public static void main(String args[]) {
        try{
            InetAddress ip;
            ip = InetAddress.getLocalHost();
            System.out.println("Your current IP address : " + ip);
            if (args.length < 1) {
                System.out.println("Now using port number : " + portNumber);
                serverSocket = new ServerSocket(portNumber);
            } else {
                portNumber = Integer.parseInt(args[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while (true) {
                    clientSocket = serverSocket.accept();
                    int i = 0;
                    for (i = 0; i < maxClientsCount; i++) {
                        if (threads[i] == null) {
                            (threads[i] = new clientThread(clientSocket, threads)).start();
                            break;
                        }
                    }
                    if (i == maxClientsCount) {
                        PrintStream os = new PrintStream(clientSocket.getOutputStream());
                        os.println("Server too busy. Try later.");
                        os.close();
                        clientSocket.close();
                    }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
class clientThread extends Thread {

    private DataInputStream is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    private final clientThread[] threads;
    private final int maxClientsCount;

    public clientThread(Socket clientSocket, clientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }

    public void run() {
        int maxClientsCount = this.maxClientsCount;
        clientThread[] threads = this.threads;

        try {
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());
            String name = is.readLine().trim();
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] != null && threads[i] != this) {
                    threads[i].os.println("Client " + name + " has connected to the server.");
                }
            }
            while (true) {
                String line = is.readLine();
                if (line.startsWith("exit")) {
                    break;
                }
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null) {
                        threads[i].os.println("<" + name + " : " + line);
                    }
                }
            }
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] != null && threads[i] != this) {
                    threads[i].os.println("Client " + name + " is disconnecting from the server.");
                }
            }
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] == this) {
                    threads[i] = null;
                }
            }
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}