/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.dish;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author liyou
 */
public class TopRun {

    String apiToken = "3HN5ZNTIV4EBCK5SVFJVFCHIJCBVVI4Q5CFJPYIPXLRUW0Y0&v=20170625";

    public List<Restaurant> getRestaurantList() throws IOException {

        JsonParser parser = new JsonParser();
        List<Restaurant> restaurantList = new ArrayList<>();

        for (int offset = 0; offset < 241; offset += 30) {

            String file = String.format("./rawJson/RawJsonRestaurants_offset_%d.txt", offset);

            String link = String.format("https://api.foursquare.com/v2/venues/explore?near=Austin,TX&section=Food&offset=%d&oauth_token=%s", offset, apiToken);
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

    public Map<String, Map<DishType, Integer>> countDish(List<Restaurant> restaurantList) throws IOException {

        JsonParser parser = new JsonParser();

        // Query menus of each reataurant
        for (int i = 0; i < restaurantList.size(); i++) {
            Restaurant cur = restaurantList.get(i);

            String menuLink = String.format("https://api.foursquare.com/v2/venues/%s/menu?oauth_token=%s", cur.id, apiToken);
            String menuFile = "./rawJson/Menu_" + cur.id + ".txt";

//            String menuJSON = parser.readFromUrl(menuLink, menuFile);
            String menuJSON = parser.readFromFile(menuFile);
            Menus menus = parser.parseMenu(menuJSON);

            List<Dish> dishList = menus.flaten();
            cur.addDishes(dishList);
        }

        // Count summary of all dishes
        Map<String, Map<DishType, Integer>> counter = new HashMap<>();
        for (Restaurant r : restaurantList) {
            for (Dish d : r.getDishes()) {
                String name = d.getName();
                if (!counter.containsKey(name)) {
                    counter.put(name, new HashMap<>());
                }
                DishType curType = d.getType();
                Map<DishType, Integer> curMap = counter.get(name);
                if (!curMap.containsKey(curType)) {
                    curMap.put(curType, 1);
                } else {
                    curMap.put(curType, curMap.get(curType) + 1);
                }
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
    Set<String> stringBreakdown(String str) {
        Set<String> set = new HashSet<>();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ' ') {
                set.addAll(stringBreakdown(str.substring(i + 1)));
            } else {
                set.add(str.substring(0, i + 1));
            }
        }
        return set;
    }

    Map<String, List<Frequency>> countSummary(Map<String, Map<DishType, Integer>> counter) throws IOException {
        Map<String, List<Frequency>> summary = new HashMap<>();
        for (String cur : counter.keySet()) {
            Set<String> list = stringBreakdown(cur);
            Map<DishType, Integer> subMap = counter.get(cur);
            for (DishType type : subMap.keySet()) {
                Frequency curFreq = new Frequency(cur, type, subMap.get(type));
                for (String k : list) {
                    if (!summary.containsKey(k)) {
                        summary.put(k, new ArrayList<>());
                    }
                    summary.get(k).add(curFreq);
                }
            }
        }

        Comparator<Frequency> freqComparator = new Comparator<Frequency>() {
            @Override
            public int compare(Frequency op1, Frequency op2) {
                return op2.freq - op1.freq;
            }
        };

        PrintWriter out = new PrintWriter("./restaurantList/SortedDishCount.txt");

        for (String k : summary.keySet()) {
            summary.get(k).sort(freqComparator);
            out.println(k + ' ' + summary.get(k));
        }
        out.close();
        return summary;
    }

    public static void main(String[] args) throws IOException {
        TopRun top = new TopRun();

        List<Restaurant> restaurantList = top.getRestaurantList();

        Map<String, Map<DishType, Integer>> counter = top.countDish(restaurantList);

        Map<String, List<Frequency>> sorted = top.countSummary(counter);


        System.out.println(String.format("\nTotal %d restaurants.\n", restaurantList.size()));

        System.out.println(counter.size() + " dishes in total.\n");

        System.out.println(sorted.size() + " keys in total.\n");


        JedisMain test = new JedisMain();

//        System.out.println("\nRedis build up begins.\n");
//        test.build(sorted);
//        System.out.println("Redis build up completes.\n");
        String key = "pot";
        System.out.println(key + ":");
        System.out.println(test.query(key, 10));



//        DatabaseAndSearchConnect conn = new DatabaseAndSearchConnect("test", "127.0.0.1", "austin", true);
//        conn.cleanup();
//        conn.close();
//
//        DatabaseAndSearchConnect connet = new DatabaseAndSearchConnect("test", "127.0.0.1", "austin", true);
//        connet.InitOrUpdate(restaurantList);
//        connet.close();

        System.out.println("excution finished");
        final String dir = System.getProperty("user.dir");
        System.out.println("current dir = " + dir);
    }

}
