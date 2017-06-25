package com.mycompany.dish;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Restaurant {
    String id;
    String name;
    Location location;
    Categories[] categories;
    double rating;
    List<Dish> dishes;
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public boolean foundNullDishesList() {
        return dishes == null;
    }
    public void addDishes(List<Dish> newDishes) {
    	if (dishes == null) dishes = new ArrayList<>();
        if (newDishes == null) return;
        for (Dish d: newDishes) {
            d.whichRestaurant(this);
        }
    	dishes.addAll(newDishes);
    }
    
    public int getDishesCount() {
        if (dishes == null) {
            return 0;
        }
        return dishes.size();
    }
    
    public List<Dish> getDishes() {
        if (dishes == null) {
            dishes = new ArrayList<>();
        }
        return dishes;
    }
    
    public ArrayList<String> getDishNames() {
        if (dishes == null) {
            return null;
        }
        ArrayList<String> list = new ArrayList<>();
        for(Dish d : dishes) {
            list.add(d.getName());
        }
        return list;
    }
    
    @Override
    public String toString() {
        StringBuilder ans = new StringBuilder();
//        ans.append("id = ").append(id).append('\n');
        ans.append("name = ").append(name).append('\n');
//        ans.append("location = ").append(location).append('\n');
//        ans.append("tag = ").append(categories[0]).append('\n');
//        ans.append("rating = ").append(rating).append('\n');
        if (getDishesCount() != 0) {
            for (Dish dish : dishes)
            	ans.append(dish);
        }
        else {
            ans.append("Menu not available\n");
        }
        return ans.toString();
    }
}

class Location {
	double lat;
	double lng;
	String[] formattedAddress;
	
        @Override
	public String toString() {
		StringBuilder ans = new StringBuilder();
		ans.append("lat = ").append(lat).append(", ");
		ans.append("lng = ").append(lng).append('\n');
		ans.append("Address = ").append(Arrays.toString(formattedAddress));
		return ans.toString();
	}
}

class Categories {
	String name;
	
        @Override
	public String toString() {
		return name;
	}
}
