package com.harry2258.Alfred.runnables;

/**
 * Created by Hardik on 1/26/14.
 */

import org.pircbotx.PircBotX;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

public class ChatSocketListener extends Thread {
    private static volatile boolean isRunning = true;
    private int port;
    private PircBotX bot;

    public ChatSocketListener(PircBotX bot, int port) {
        this.port = port;
        this.bot = bot;
        System.out.println("Starting chat socket listener on port " + port);
    }

    public static void kill() {
        isRunning = false;
    }

    public void run() {
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("Accepting connections!");
            while (isRunning) {
                new Thread(new ChatSocketHandler(server.accept(), bot)).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}