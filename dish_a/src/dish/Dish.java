package dish;

public class Dish {
	String name;
	String description = "";
	String price = "0";
	String category;
	String section;
	
	public Dish (SingleDish dish, String cat, String sec) {
		name = dish.name;
		description = dish.description;
		price = dish.price;
		category = cat;
		section = sec;
	}
	
	public String toString () {
		return String.format("%s  $%s", name, price);
	}
}
