package com.harry2258.Alfred.runnables;

/**
 * Created by Hardik on 1/26/14.
 */

import org.pircbotx.PircBotX;

import java.net.ServerSocket;

public class ChatSocketListener extends Thread {
    private ServerSocket server;
    private int port;
    private PircBotX bot;

    public ChatSocketListener(PircBotX bot, int port) {
        this.port = port;
        this.bot = bot;
        System.out.println("Starting chat socket listener on port " + port);
    }

    private static volatile boolean isRunning = true;

    public void run() {
        try {
            this.server = new ServerSocket(port);
            System.out.println("Accepting connections!");
            while (isRunning) {
                new Thread(new ChatSocketHandler(server.accept(), bot)).start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void kill() {
        isRunning = false;
    }
}