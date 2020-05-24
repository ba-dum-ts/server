import com.sun.org.apache.bcel.internal.generic.JsrInstruction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

/*
This program awaits for a client to connect
After connecting, the server creates a fake client which the client is supposed to communicate with
By doing this we can create a back and fourth communication between two different clients
*/

public class server {
    static JFrame frame = new JFrame("server");
    static JPanel panel = new JPanel();
    static JTextArea chatResult = new JTextArea();
    static JTextField chatWrite = new JTextField();
    static ServerSocket ss;
    static Socket s;
    static OutputStream out;
    static InputStream in;
    static byte[] bufferIn, bufferOut;
    static Thread receiveMessage = new Thread(new listener());

    public static void main(String Args[]) { // after client joins, the chat room appears and you can start both send and receive messages
        try{
            ss = new ServerSocket(7777);
            s = ss.accept(); // accepts connection from user

            in = s.getInputStream();
            out = s.getOutputStream();

            System.out.println("Client has connected");
            chatroom(); // opening frame with chat room

            receiveMessage.start(); // starting thread that receive messages
        }

        catch(Exception e){}
    }

    public static void chatroom(){ // chat room appears
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(450, 550);
        frame.add(panel);

        JScrollPane scroll = new JScrollPane(chatResult); // adds scroll function

        panel.setLayout(new BorderLayout());

        panel.add(chatWrite, BorderLayout.PAGE_END);
        panel.add(scroll, BorderLayout.CENTER);

        chatResult.setEditable(false);

        chatWrite.setText("write here...");
        chatWrite.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                chatWrite.setText("");
            }
        });

        chatWrite.addKeyListener(new KeyListener() { // if the use types more than 0 characters and less than 100 characters, and presses ENTER he sends a message
            public void keyTyped(KeyEvent e) {}

            public void keyPressed(KeyEvent e) {}

            public void keyReleased(KeyEvent e) {
                if(chatWrite.getText().length() > 0 && chatWrite.getText().length() < 100){ // if client types right amount of characters
                    if(e.getKeyCode() == KeyEvent.VK_ENTER){ // if client presses ENTER, he sends a message
                        try{
                            chatResult.append("Server: " + chatWrite.getText() + "\n");

                            bufferOut = new byte[1000]; // creates a buffer with a limit of 1000 bytes

                            bufferOut = chatWrite.getText().getBytes(); //checks the amount of bytes used out of the 1000 bytes
                            out.write(bufferOut); // sends right amount of bytes
                            System.out.println("signal givet");

                            chatWrite.setText(""); // reset
                        }

                        catch(Exception r){}
                    }
                }
            }
        });
    }

    public static class listener implements Runnable{ // waits for client to send a message. After having a message receieved, it appears on the chat room
        public void run() {
            try{
                while(true){
                    bufferIn = new byte[1000]; // creates a buffer with a limit of 1000 bytes

                    in.read(bufferIn); // reads the amount of bytes sent by the other client
                    System.out.println("signal taget");
                    String response = new String(bufferIn); // translates using the amount of bytes sent by the other client
                    chatResult.append("Client: " + response + "\n"); // putting message on screen
                }
            }

            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}