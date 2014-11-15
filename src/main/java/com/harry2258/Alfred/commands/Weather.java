package com.harry2258.Alfred.commands;

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

    public Weather() {
        super("Weather", "Gets the Weather for the city", "Weather [City name], (State)");
    }

    private Config config;
    private PermissionManager manager;

    @Override
    public boolean execute(MessageEvent event) throws Exception {

        String[] args = event.getMessage().split(" ");
        String City = "";
        String State = "";
        try {
            if (args.length == 1) {
                String ip = java.net.InetAddress.getByName(event.getUser().getHostmask()).getHostAddress();
                URL url = new URL("http://freegeoip.net/json/" + ip);
                BufferedReader re = new BufferedReader(new InputStreamReader(url.openStream()));
                String jsonstring = re.readLine();
                JsonObject jsonObj = JsonUtils.getJsonObject(jsonstring);
                City = "/" + jsonObj.get("city").getAsString();
                State = "/" + jsonObj.get("region_code").getAsString();
            }

            if (args.length > 1) {
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
            URL url = new URL(("http://api.wunderground.com/api/" + config.WeatherKey() + "/conditions/q" + State + City + ".json").replaceAll(" ", "_"));
            System.out.println(url.toString());
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String jsonstring = "";
            String tmp;
            while ((tmp = br.readLine()) != null) {
                jsonstring += tmp;
            }
            System.out.println(jsonstring.replaceAll("\n", "").replaceAll("\t","").replaceAll("\r",""));
            if (jsonstring.contains(", \"results\": [")) {
                JsonObject jsonObj = JsonUtils.getJsonObject(jsonstring.replaceAll("\n", ""));
                String CityID = jsonObj.getAsJsonObject("response").getAsJsonArray("results").get(0).getAsJsonObject().get("l").toString();
                url = new URL(("http://api.wunderground.com/api/" + config.WeatherKey() + "/conditions" + CityID + ".json").replaceAll(" ", "_").replaceAll("\"", ""));
                br = new BufferedReader(new InputStreamReader(url.openStream()));
                jsonstring = "";
                while ((tmp = br.readLine()) != null) {
                    jsonstring += tmp;
                }
            }
            JsonObject jsonObj = JsonUtils.getJsonObject(jsonstring.replaceAll("\n", "")).getAsJsonObject("current_observation");
            String city = jsonObj.getAsJsonObject("display_location").get("city").getAsString();
            String state = jsonObj.getAsJsonObject("display_location").get("state").getAsString();
            String Weather = jsonObj.get("weather").getAsString();
            String Temp = jsonObj.get("temperature_string").getAsString();
            String Wind = jsonObj.get("wind_string").getAsString() + ", Gusting at " + jsonObj.get("wind_gust_mph").getAsString() +" MPH";
            String Humidity = jsonObj.get("relative_humidity").getAsString();

            MessageUtils.sendChannel(event, city + ", " + state + ": " + Weather + " | " + Temp + " | " + Colors.BOLD + "Humidity" + Colors.NORMAL + ": " + Humidity + " | " + Colors.BOLD + "Winds" + Colors.NORMAL + ": " + Wind);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

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
