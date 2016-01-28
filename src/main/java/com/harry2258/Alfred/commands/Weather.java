package com.harry2258.Alfred.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.harry2258.Alfred.api.*;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Hardik at 4:33 PM on 8/1/2014.
 */
public class Weather extends Command {

    private Config config;
    private PermissionManager manager;

    public Weather() {
        super("Weather", "Gets the Weather for the city", "Weather [City name], (State)");
    }

    @Override
    public boolean execute(MessageEvent event) throws Exception {

        String[] args = event.getMessage().split(" ");
        String City = "";
        String State = "";
        String ip = null;
        String jsonstring = "";
        String alerts = "";
        String Alert = "None";
        Boolean IPAddress = false;
        String UndergroundURL = "http://api.wunderground.com/api/" + config.getWeatherKey() + "/conditions/q";
        String UndergroundAlerts = "http://api.wunderground.com/api/" + config.getWeatherKey() + "/alerts/q";
        //If not args are provided, defaults to the user running the command
        if (args.length == 1) {
            if (event.getUser() == null) {
                return false;
            }
            ip = java.net.InetAddress.getByName(event.getUser().getHostmask().replaceAll(".*(?:@)", "")).getHostAddress();
            URL url = new URL("http://ip-api.com/json/" + ip);
            BufferedReader re = new BufferedReader(new InputStreamReader(url.openStream()));
            String IPjsonstring = re.readLine();
            JsonObject jsonObj = JsonUtils.getJsonObject(IPjsonstring);
            if (jsonObj == null) {
                return false;
            }
            City = "/" + jsonObj.get("city").getAsString();
            State = "/" + jsonObj.get("regionName").getAsString();
        }

        //If args happens to be a User connected to the IRC
        if (args.length == 2 && event.getChannel().getUsers().asList().contains(event.getBot().getUserChannelDao().getUser(args[1]))) {
            ip = Utils.getIP(args[1], event);
            IPAddress = true;
        } else if (args.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            City = "/" + sb.toString().replace(",", "");
        }

        if (event.getMessage().contains(",")) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            City = "/" + sb.toString().replaceAll("(?:,).*", "").trim();
            State = "/" + event.getMessage().replaceAll(".*(?:, )", "");
        }

        if (IPAddress) {
            URL IPUrl = new URL(UndergroundURL + "/autoip.json?geo_ip=" + ip);
            BufferedReader br = new BufferedReader(new InputStreamReader(IPUrl.openStream()));
            String tmp;
            while ((tmp = br.readLine()) != null) {
                jsonstring += tmp;
            }
            URL AlertsURL = new URL(UndergroundAlerts + "/autoip.json?geo_ip=" + ip);
            br = new BufferedReader(new InputStreamReader(AlertsURL.openStream()));
            while ((tmp = br.readLine()) != null) {
                alerts += tmp;
            }
            br.close();
        } else {
            URL url = new URL((UndergroundURL + State + City + ".json").replaceAll(" ", "_"));
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String tmp;
            while ((tmp = br.readLine()) != null) {
                jsonstring += tmp;
            }

            URL AlertsURL = new URL((UndergroundAlerts + State + City + ".json").replaceAll(" ", "_"));
            br = new BufferedReader(new InputStreamReader(AlertsURL.openStream()));
            while ((tmp = br.readLine()) != null) {
                alerts += tmp;
            }
            br.close();
        }

        if (jsonstring.contains(", \"results\": [")) {
            JsonObject jsonObj = JsonUtils.getJsonObject(jsonstring.replaceAll("\n", ""));
            if (jsonObj == null) {
                return false;
            }
            String CityID = jsonObj.getAsJsonObject("response").getAsJsonArray("results").get(0).getAsJsonObject().get("l").toString();
            URL url = new URL(("http://api.wunderground.com/api/" + config.getWeatherKey() + "/conditions" + CityID + ".json").replaceAll(" ", "_").replaceAll("\"", ""));
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            jsonstring = "";
            String tmp;
            while ((tmp = br.readLine()) != null) {
                jsonstring += tmp;
            }
            br.close();
        }

        JsonObject jsonObj = JsonUtils.getJsonObject(jsonstring.replaceAll("\n", ""));
        if (jsonObj == null) {
            return false;
        }
        jsonObj = jsonObj.getAsJsonObject("current_observation");
        String city = jsonObj.getAsJsonObject("display_location").get("city").getAsString();
        String state = jsonObj.getAsJsonObject("display_location").get("state").getAsString();
        String Weather = jsonObj.get("weather").getAsString();
        String Temp = jsonObj.get("temperature_string").getAsString();
        String Wind = jsonObj.get("wind_string").getAsString().contains("Gusting to") ? jsonObj.get("wind_string").getAsString() : jsonObj.get("wind_string").getAsString() + ", Gusting at " + jsonObj.get("wind_gust_mph").getAsString() + " MPH";
        String Humidity = jsonObj.get("relative_humidity").getAsString();
        JsonObject obj = JsonUtils.getJsonObject(alerts.replaceAll("\n", ""));
        if (obj == null) {
            return false;
        }
        Alert = (obj.getAsJsonArray("alerts").get(0).getAsJsonObject().get("description")
          .getAsString().isEmpty()) ? "None" : Colors.BOLD + Colors.RED + obj.getAsJsonArray
          ("alerts").get(0).getAsJsonObject().get("description").getAsString() + Colors
          .NORMAL + " till " + obj.getAsJsonArray("alerts").get(0).getAsJsonObject().get
          ("expires").getAsString();

        MessageUtils.sendChannel(event, city + ", " + state + ": " + Weather + " | " + Temp + " | " + Colors.BOLD + "Humidity" + Colors.NORMAL + ": " + Humidity + " | " + Colors.BOLD + "Winds" + Colors.NORMAL + ": " + Wind + " | Alerts: " + Alert);


        return true;
    }

    @Override
    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public void setManager(PermissionManager manager) {

    }
}
