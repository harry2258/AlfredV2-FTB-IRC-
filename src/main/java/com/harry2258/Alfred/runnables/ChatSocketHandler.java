package com.harry2258.Alfred.runnables;

/**
 * Created by Hardik on 1/26/14.
 */


import com.harry2258.Alfred.api.Utils;
import org.pircbotx.PircBotX;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ChatSocketHandler extends Thread {
    private PircBotX bot;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public ChatSocketHandler(Socket socket, PircBotX bot) {
        this.socket = socket;
        this.bot = bot;
    }

    private static String Uptime() {
        return Utils.getUptime();
    }

    @Override
    public void run() {
        try {
            System.out.println("New connection from " + socket.getRemoteSocketAddress());
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            writer = new BufferedWriter(new PrintWriter(socket.getOutputStream()));
            writer.write("Connected to " + bot.getNick() + "\r\n");
            writer.write("The first line you send will be ignored! (no idea why)\r\n");
            writer.write("Currently connect to: " + bot.getServerHostname() + "\r\n");
            writer.flush();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equalsIgnoreCase("uptime")) {
                    writer.write(Uptime() + "\r\n");
                    writer.flush();
                }
                if (line.startsWith("raw ")) {
                    bot.sendRaw().rawLineNow(line.replace("raw ", ""));
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
