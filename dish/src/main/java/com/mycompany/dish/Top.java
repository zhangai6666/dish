/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dish;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author liyou
 */
public class Top {

    public List<Restaurant> getRestaurantList() throws IOException {

        JsonParser parser = new JsonParser();
        List<Restaurant> restaurantList = new ArrayList<>();

        for (int offset = 0; offset < 241; offset += 30) {

            String file = String.format("./rawJson/RawJsonRestaurants_offset_%d.txt", offset);
            String link = String.format("https://api.foursquare.com/v2/venues/explore?near=Austin,TX&section=Food&offset=%d&oauth_token=3HN5ZNTIV4EBCK5SVFJVFCHIJCBVVI4Q5CFJPYIPXLRUW0Y0&v=20161230", offset);
//            String restaurantJSON = parser.readFromUrl(link, file);
            String restaurantJSON = parser.readFromFile(file);

            List<Restaurant> cur = parser.parseRestaurant(restaurantJSON);

            for (Restaurant r : cur) {
                if (r.categories[0].name.equals("Fast Food Restaurant")) {
                    continue;
                }
                restaurantList.add(r);
            }
        }

        return restaurantList;
    }

    public Map<String, Frequency> countDish(List<Restaurant> restaurantList) throws IOException {

        JsonParser parser = new JsonParser();
        
        // Query menus of each reataurant
        for (int i = 0; i < restaurantList.size(); i++) {
            Restaurant cur = restaurantList.get(i);

//            	String menuLink = String.format("https://api.foursquare.com/v2/venues/%s/menu?oauth_token=3HN5ZNTIV4EBCK5SVFJVFCHIJCBVVI4Q5CFJPYIPXLRUW0Y0&v=20161230", r.id);
            String menuFile = "./rawJson/Menu_" + cur.name + ".txt";

//            	parser.readFromUrl(menuLink, menuFile, jsonMenu);
            String menuJSON = parser.readFromFile(menuFile);
            Menus menus = parser.parseMenu(menuJSON);

            List<Dish> dishList = menus.flaten();
            cur.addDishes(dishList);
        }
        
        // Count summary of all dishes
        Map<String, Frequency> counter = new HashMap<>();
        for (Restaurant r : restaurantList) {
            for (Dish d : r.getDishes()) {
                String name = d.getName();
                if (!counter.containsKey(name)) {
                    counter.put(name, new Frequency());
                }
                counter.get(name).increment(d.getType());
            }
        }
        
        // Write counter to file
        PrintWriter out = new PrintWriter("./restaurantList/DishCount.txt");
        for (String d : counter.keySet()) {
            out.println(d + ' ' + counter.get(d));
        }
        out.close();

        return counter;
    }

    public static void main(String[] args) throws IOException {
        Top top = new Top();

        List<Restaurant> restaurantList = top.getRestaurantList();

        Map<String, Frequency> counter = top.countDish(restaurantList);

        System.out.println(String.format("\nTotal %d restaurants.\n", restaurantList.size()));

        System.out.println(counter.size() + " dishes in total.\n");

        DatabaseAndSearchConnect conn = new DatabaseAndSearchConnect("test", "127.0.0.1", "austin", true);
        conn.cleanup();
        conn.close();

        DatabaseAndSearchConnect connet = new DatabaseAndSearchConnect("test", "127.0.0.1", "austin", true);
        connet.InitOrUpdate(restaurantList);
        connet.close();

        System.out.println("excution finished");
        final String dir = System.getProperty("user.dir");
        System.out.println("current dir = " + dir);
    }

}
