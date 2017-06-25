package com.mycompany.dish;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.google.gson.Gson;


public class JsonParser {

    /*
    Return JSON response of url and write response to local file for future use
    @param link URL to get response from
    @param file Path to write JSON response to
    @return JSON response
    */
    public String readFromUrl (String link, String file) throws IOException {
    	PrintWriter out = null;
        StringBuilder jsonString = new StringBuilder();
        try {
            URL url = new URL(link);
            //read from the url
            Scanner scan = new Scanner(url.openStream());
            while (scan.hasNext()) {
                jsonString.append(scan.nextLine());
            }
            scan.close();
//            System.out.println(jsonString);
            out = new PrintWriter(file);
            out.println(jsonString);
            out.close();
        }
        catch (MalformedURLException e) {
            System.err.println("Invalid URL");
        }
        catch (IOException e) {
        }
        return jsonString.toString();
    }

    /*
    @param file Path to file to be read
    @return Content read from local file
    */
    public String readFromFile (String file) throws IOException{
        BufferedReader br = null;
        StringBuilder jsonString = new StringBuilder();
        try {
        	br = new BufferedReader(new FileReader(file));
            String line = br.readLine();

            while (line != null) {
            	jsonString.append(line);
            	jsonString.append("\n");
                line = br.readLine();
            }
        }
        catch (IOException e) {
            System.err.println("File not Found");
        }
        finally {
            try { br.close();} catch(IOException e) {}
        }
        return jsonString.toString();
    }
    
    public List<Restaurant> parseRestaurant (String jsonString) {
    	Gson gson = new Gson();
    	RestaurantRaw restaurantRaw = gson.fromJson(jsonString, RestaurantRaw.class);
    	Group[] buckets = restaurantRaw.response.groups;
    	List<Restaurant> restaurant = new ArrayList<>();
    	for (int i = 0; i < buckets.length; i++) {
    		Item[] item = buckets[i].items;
    		for (int j = 0; j < item.length; j++) {   		
    			restaurant.add(item[j].venue);
    		}
    	}
    	return restaurant;
    }
    
    public Menus parseMenu (String jsonString) {
    	Gson gson = new Gson();
    	MenuRaw topMenu = gson.fromJson(jsonString, MenuRaw.class);
    	return topMenu.response.menu.menus;
    }
}
