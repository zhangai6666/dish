package dish;

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
import java.util.Collections;

/**
 * Created by liyou on 11/24/16.
 */
public class JsonParser {

    public void readFromUrl (String link, String file, StringBuilder jsonString) throws IOException{
    	PrintWriter out = null;
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
    }

    public void readFromFile (String file, StringBuilder jsonString) throws IOException{
        BufferedReader br = null;
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
    }
    
    public List<Restaurant> parse (String jsonString) {
    	Gson gson = new Gson();
    	Top top = gson.fromJson(jsonString, Top.class);
    	Group[] buckets = top.response.groups;
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
    	TopMenu topMenu = gson.fromJson(jsonString, TopMenu.class);
    	return topMenu.response.menu.menus;
    }
    
    
    public static void main(String[] args) throws IOException{
    	List<Restaurant> list = new ArrayList<>();
    	
    	int menuNotAvailCount = 0;
        int totalDishesCount = 0;

    	
    	for(int offset = 0; offset < 241; offset += 30) {
        	JsonParser parser = new JsonParser();
        	StringBuilder jsonString = new StringBuilder();
        	String file = String.format("./rawJson/RawJsonRestaurants_offset_%d.txt", offset);
        	
        	// Get Restaurant info from FourSquare API
//      	String link = String.format("https://api.foursquare.com/v2/venues/explore?near=Austin,TX&section=Food&offset=%d&oauth_token=3HN5ZNTIV4EBCK5SVFJVFCHIJCBVVI4Q5CFJPYIPXLRUW0Y0&v=20161230", offset);
//            parser.readFromUrl(link, file, jsonString);
        	parser.readFromFile(file, jsonString);
                 
            
            file = String.format("./restaurantList/RestaurantList_offset_%d.txt", offset);
            List<Restaurant> cur = parser.parse(jsonString.toString());
            int count = cur.size();
            PrintWriter out = new PrintWriter(file);  
            
            // Get menus of each restaurant 
            for (Restaurant r : cur) {
            	if (r.categories[0].name.equals("Fast Food Restaurant")) {
            		count--;
            		continue;
            	}
                list.add(r);
            	
//            	String menuLink = String.format("https://api.foursquare.com/v2/venues/%s/menu?oauth_token=3HN5ZNTIV4EBCK5SVFJVFCHIJCBVVI4Q5CFJPYIPXLRUW0Y0&v=20161230", r.id);
            	String menuFile = "./rawJson/Menu_" + r.name + ".txt";
            	StringBuilder jsonMenu = new StringBuilder();
//            	parser.readFromUrl(menuLink, menuFile, jsonMenu);
            	parser.readFromFile(menuFile, jsonMenu);
            	Menus menus = parser.parseMenu(jsonMenu.toString());
            	
            	r.addDishes(menus.flaten());
            	
            	out.println(r);
            	//System.out.println(r);
//                System.out.println(r.getDishesCount());

                if (r.foundNullDishesList()) {
                    System.out.println("Dishes List is NULL!!!");
                }

                if (r.getDishesCount() == 0) {
                    menuNotAvailCount++;
                }
                totalDishesCount += r.getDishesCount();
            	
            }
            out.close();
        
//            System.out.println(String.format("Current size %d", count));
    	}
        System.out.println(String.format("\nTotal %d restaurants.\n", list.size()));
 //       System.out.println(menuNotAvailCount + " restaurants don't menu info.");
        System.out.println(totalDishesCount + " dishes in total.");
    	
    	DatabaseAndSearchConnect conn = new DatabaseAndSearchConnect("dishtest", "127.0.0.1", "austin", true);
    	//conn.cleanup();
        conn.InitOrUpdate(list);
    	conn.close();
    	
    	System.out.println("excution finished" );
        final String dir = System.getProperty("user.dir");
        System.out.println("current dir = " + dir);
    }
}
