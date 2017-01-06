package dish;

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
    
    public void addDishes (List<Dish> newDishes) {
    	if (newDishes == null) return;
    	if (dishes == null) dishes = new ArrayList<>();
    	dishes.addAll(newDishes);
    }
    
    @Override
    public String toString() {
        StringBuilder ans = new StringBuilder();
        ans.append("id = ").append(id).append('\n');
        ans.append("name = ").append(name).append('\n');
        ans.append("location = ").append(location).append('\n');
        ans.append("tag = ").append(categories[0]).append('\n');
        ans.append("rating = ").append(rating).append('\n');
        if (dishes != null) {
        	for (Dish dish : dishes)
            	ans.append(dish).append("    ");
        }
        else
        	ans.append("Menu not available");
        ans.append("\n");
        return ans.toString();
    }
}

class Location {
	double lat;
	double lng;
	String[] formattedAddress;
	
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
	
	public String toString() {
		return name;
	}
}
