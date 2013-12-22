/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

/**
 *
 * @author Zack
 */
public class Picroma extends Command 
{
     private Config config;
    
    @Override
    public void setConfig(Config conf)
    {
        this.config = conf;
    }
    @Override
    public void run(MessageEvent event)
    {
        String status = "";
         try
         {
             
             HttpURLConnection conn = (HttpURLConnection) new URL("http://direct.cyberkitsune.net/canibuycubeworld/").openConnection();
             BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
             String line = "";
             boolean up = true;
             while((line = in.readLine()) != null){
                 if(line.toLowerCase().contains("site:true")){
                     up = false;
                 }
             }
             status += "Shop: ";
             status += up ? Colors.GREEN + "Online!"  + Colors.NORMAL + " " : Colors.RED + "Offline" + Colors.NORMAL + " ";
             conn.disconnect();
             in.close();
             //--------------------------------------------------------
             conn = (HttpURLConnection) new URL("http://direct.cyberkitsune.net/canibuycubeworld/").openConnection();  
             in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
             up = true;
             while((line = in.readLine()) != null){
                 if(line.toLowerCase().contains("registration is down")){
                     up = false;
                 }
             }
             status += "Accounts: ";
             status += up ? Colors.GREEN + "Online!"  + Colors.NORMAL + " " : Colors.RED + "Offline" + Colors.NORMAL + " ";
             conn.disconnect();
             in.close();
             //--------------------------------------------------------
             conn = (HttpURLConnection) new URL("http://direct.cyberkitsune.net/canibuycubeworld/").openConnection();  
             in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
             up = true;
             while((line = in.readLine()) != null){
                 if(line.toLowerCase().contains("picroma is up")){
                     up = false;
                 }
             }
             status += "Picorama: ";
             status += up ? Colors.GREEN + "Online!"  + Colors.NORMAL  + " " : Colors.RED + "Offline" + Colors.NORMAL + " ";
             conn.disconnect();
             in.close();
             //--------------------------------------------------------
         } catch (IOException ex)
         {
             Logger.getLogger(Picroma.class.getName()).log(Level.SEVERE, null, ex);
         }
         event.getBot().sendMessage(event.getChannel(), status.trim());
    }
    @Override
    public String getDescription()
    {
        return "Cheks if Picroma store/registration is online";
    }

    @Override
    public String getSyntax()
    {
        return config.getTrigger() + "Kill";
    }

    @Override
    public String getName()
    {
        return "Picroma";
    }

    @Override
    public boolean isSecret()
    {
        return false;
    }
}