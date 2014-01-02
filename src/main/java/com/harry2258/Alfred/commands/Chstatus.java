package com.harry2258.Alfred.commands;

import com.harry2258.Alfred.api.Command;
import com.harry2258.Alfred.api.Config;
import com.harry2258.Alfred.api.PermissionManager;
import com.harry2258.Alfred.api.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pircbotx.hooks.events.MessageEvent;


public class Chstatus extends Command {
    
    private Config config;
    private PermissionManager manager;

    public Chstatus() {
        super("ChStatus", "Shows status of CreeperHost repos", ":3");
    }

    @Override
    public boolean execute(MessageEvent event) {
        /*
        //---------------------------
        String Offline = "Offline";
        String Online = "Online";
        Socket socket = null;
        //---------------------------
        String maidencheck = null;
        String nottingcheck = null;
        String grantcheck = null;
        String atlantacheck = null;
        String atlanta2check = null;
        String chicagocheck = null;
        String chicago2check = null;
        String LAcheck = null;
        boolean Maiden = false;
        boolean Notting = false;
        boolean Grant = false;
        boolean Atlant = false;
        boolean Atlant2 = false;
        boolean Chicag = false;
        boolean Chicag2 = false;
        boolean Los = false;
        
//----------------------
        try {
              socket = new Socket("england1.creeperrepo.net", 80);
              Maiden = true;
            } finally {            
        if (socket != null) try { socket.close(); }
        catch(IOException M) {System.out.println(M);}
        }
//----------------------
        try {
              socket = new Socket("england2.creeperrepo.net", 80);
              Notting = true;
            } finally {            
        if (socket != null) try { socket.close(); }
        catch(IOException M) {System.out.println(M);}
        }
//----------------------
        try {
              socket = new Socket("england3.creeperrepo.net", 80);
              Grant = true;
            } finally {            
        if (socket != null) try { socket.close(); }
        catch(IOException M) {System.out.println(M);}
        }
//----------------------
        try {
              socket = new Socket("atlanta1.creeperrepo.net", 80);
              Atlant = true;
            } finally {            
        if (socket != null) try { socket.close(); }
        catch(IOException M) {System.out.println(M);}
        }
//----------------------
        try {
              socket = new Socket("atlanta2.creeperrepo.net", 80);
              Atlant2 = true;
            } finally {            
        if (socket != null) try { socket.close(); }
        catch(IOException M) {System.out.println(M);}
        }
//----------------------
        try {
              socket = new Socket("chicago1.creeperrepo.net", 80);
              Chicag = true;
            } finally {            
        if (socket != null) try { socket.close(); }
        catch(IOException C) {System.out.println(C);}
        }
//----------------------
        try {
              socket = new Socket("chicago2.creeperrepo.net", 80);
              Chicag2 = true;
            } finally {            
        if (socket != null) try { socket.close(); }
        catch(IOException C2) {System.out.println(C2);}
        }
//----------------------
        try {
              socket = new Socket("losangeles1.creeperrepo.net", 80);
              Los = true;
            } finally {            
        if (socket != null) try { socket.close(); }
        catch(IOException LS) {
        System.out.println(LS);}
        }
//----------------------
           
       if (Maiden) {
           maidencheck = Colors.DARK_GREEN + Online + Colors.NORMAL + " | ";
       } else {
           maidencheck = Colors.RED + Offline + Colors.NORMAL + " | ";
       }
       if (Notting) {
           nottingcheck = Colors.DARK_GREEN + Online + Colors.NORMAL + " | ";
       } else {
           nottingcheck = Colors.RED + Offline + Colors.NORMAL + " | ";
       }
       if (Grant) {
           grantcheck = Colors.DARK_GREEN + Online + Colors.NORMAL + " | ";
       } else {
           grantcheck = Colors.RED + Offline + Colors.NORMAL + " | ";
       }
       if (Atlant) {
           atlantacheck = Colors.DARK_GREEN + Online + Colors.NORMAL + " | ";
       } else {
           atlantacheck = Colors.RED + Offline + Colors.NORMAL + " | ";
       }
       if (Atlant2) {
           atlanta2check = Colors.DARK_GREEN + Online + Colors.NORMAL + " | ";
       } else {
           atlanta2check = Colors.RED + Offline + Colors.NORMAL + " | ";
       }
       if (Chicag) {
           chicagocheck = Colors.DARK_GREEN + Online + Colors.NORMAL + " | ";
       } else {
           chicagocheck = Colors.RED + Offline + Colors.NORMAL + " | ";
       }
       if (Chicag2) {
           chicago2check = Colors.DARK_GREEN + Online + Colors.NORMAL + " | ";
       } else {
           chicago2check = Colors.RED + Offline + Colors.NORMAL + " | ";
       }
       if (Los) {
           LAcheck = Colors.DARK_GREEN + Online;
       } else {
           LAcheck = Colors.RED + Offline;
       }
       
         String status = "Maidenhead: "
                       + maidencheck 
                       + "Nottingham: "
                       + nottingcheck 
                       + "Grantham: "
                       + grantcheck 
                       + "Atlanta: "
                       + atlantacheck 
                       + "Atlanta 2: "
                       + atlanta2check 
                       + "Chicago: " 
                       + chicagocheck 
                       + "Chicago 2: " 
                       + chicago2check 
                       + "LA: "
                       + LAcheck
                       ;
         */
       
       String list = null;
       String status = null;
        try {
                URL url;
                url = new URL("http://new.creeperrepo.net/edges.json");
                BufferedReader re = new BufferedReader(new InputStreamReader(url.openStream()));
                String st;
                while ((st = re.readLine()) != null) {
                    String a = st.replace("[", "").replace("]", "");
                    String b = a.replace("{", "").replace("}", "").replace(":", " Repo url: ").replace("\"", "").replaceAll(",", " | ");
                    list = b;
                }
            } catch (IOException E) {
                if (E.getMessage().contains("503")) {
                }
                if (E.getMessage().contains("404")) {
                }
            }
        String[] test = list.split(" ");
        
        for (int i = 1; i < test.length; i++) {
            if (test[i].contains("creeperrepo.net")) {
                boolean tests = Utils.pingUrl(test[i]);
                String st = test[i];
                String[] moretest = st.split(".");
                /*
                if (tests) {
                    e.respond(test[i] + " is Online!");
                } else {
                    e.respond(test[i] + " is Offline!");
                }
                */
            }
        }
        
        return true;
        }
        

    @Override
    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public void setManager(PermissionManager manager) {
        this.manager = manager;
    }

}