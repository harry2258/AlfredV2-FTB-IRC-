package com.harry2258.Alfred.runnables;

/**
 * Created by Hardik on 1/26/14.
 */

import org.pircbotx.PircBotX;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatSocketHandler extends Thread {
    PircBotX bot;
    Socket socket;
    BufferedReader reader;
    BufferedWriter writer;

    public ChatSocketHandler(Socket socket, PircBotX bot) {
        this.socket = socket;
        this.bot = bot;
    }

    private static volatile boolean isRunning = true;

    @Override
    public void run() {
        while (isRunning) {
            try {
                System.out.println("New connection from " + socket.getRemoteSocketAddress());
                this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                writer = new BufferedWriter(new PrintWriter(socket.getOutputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isEmpty()) {
                        bot.sendRaw().rawLineNow(line);
                    } else {
                        writer.write("Invalid format!\r\n");
                        writer.flush();
                    }
                }
                writer.close();
                reader.close();
                socket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void kill() {
        isRunning = false;
    }
}
