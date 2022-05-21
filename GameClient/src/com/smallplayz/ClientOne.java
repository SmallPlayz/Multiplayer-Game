package com.smallplayz;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ClientOne implements Runnable {

    private static Socket clientSocket = null;

    public static PrintStream os = null;

    private static DataInputStream is = null;

    private static BufferedReader inputLine = null;
    private static boolean closed = false;

    public static void main(String[] args) {

        Game game = new Game();

        int portNumber = 10101;
        String host = "localhost";

        if (args.length < 2) {
            System.out
                    .println("Usage: java MultiThreadChatClient <host> <portNumber>\n"
                            + "Now using host=" + host + ", portNumber=" + portNumber);
        } else {
            host = args[0];
            portNumber = Integer.valueOf(args[1]).intValue();
        }
        try {
            clientSocket = new Socket(host, portNumber);
            inputLine = new BufferedReader(new InputStreamReader(System.in));
            os = new PrintStream(clientSocket.getOutputStream());
            is = new DataInputStream(clientSocket.getInputStream());
            os.println("Player1");
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host "
                    + host);
        }
        if (clientSocket != null && os != null && is != null) {
            try {
                new Thread(new ClientOne()).start();
                while (!closed) {
                    os.println(inputLine.readLine().trim());
                }
                os.close();
                is.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
    }
    public void run() {
        String responseLine;
        try {
            while ((responseLine = is.readLine()) != null) {
                System.out.println(responseLine);
                if(responseLine.charAt(7) == '2')
                    Game.label1.setLocation(returnCord(responseLine, 'x'), returnCord(responseLine, 'y'));
            }
            closed = true;
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }
    public static int returnCord(String str, char c){
        String x = "";
        if(c == 'x' || c == 'X')
            x = str.substring(11, 17).trim();
        else if(c == 'y' || c == 'Y')
            x = str.substring(17, str.length()-1).trim();
        return Integer.parseInt(x);
    }
}
class Game {

    JFrame frame;
    static JLabel label1;
    static JLabel label;
    Action upAction;
    Action downAction;
    Action leftAction;
    Action rightAction;

    Game() {

        frame = new JFrame("Game 1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 420);
        frame.setLayout(null);

        label1 = new JLabel();
        label1.setBackground(Color.blue);
        label1.setBounds(200, 100, 100, 100);
        label1.setOpaque(true);

        label = new JLabel();
        label.setBackground(Color.red);
        label.setBounds(100, 100, 100, 100);
        label.setOpaque(true);

        upAction = new UpAction();
        downAction = new DownAction();
        leftAction = new LeftAction();
        rightAction = new RightAction();

        label.getInputMap().put(KeyStroke.getKeyStroke('w'), "upAction");
        label.getActionMap().put("upAction", upAction);
        label.getInputMap().put(KeyStroke.getKeyStroke('s'), "downAction");
        label.getActionMap().put("downAction", downAction);
        label.getInputMap().put(KeyStroke.getKeyStroke('a'), "leftAction");
        label.getActionMap().put("leftAction", leftAction);
        label.getInputMap().put(KeyStroke.getKeyStroke('d'), "rightAction");
        label.getActionMap().put("rightAction", rightAction);

        ImageIcon img = new ImageIcon("C:\\Users\\900ra\\IdeaProjects\\GameClient\\clientpfp.png");
        frame.setIconImage(img.getImage());

        frame.add(label);
        frame.add(label1);
        frame.setVisible(true);
    }

    public class UpAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {

            label.setLocation(label.getX(), label.getY() - 10);
            ClientOne.os.println(label.getX() + "      " + label.getY() + "      ");
        }
    }

    public class DownAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {

            label.setLocation(label.getX(), label.getY() + 10);
            ClientOne.os.println(label.getX() + "      " + label.getY() + "      ");
        }
    }

    public class LeftAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {

            label.setLocation(label.getX() - 10, label.getY());
            ClientOne.os.println(label.getX() + "      " + label.getY() + "      ");
        }
    }

    public class RightAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {

            label.setLocation(label.getX() + 10, label.getY());
            ClientOne.os.println(label.getX() + "      " + label.getY() + "      ");
        }
    }
}