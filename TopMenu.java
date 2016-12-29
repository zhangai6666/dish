package dish;

import java.util.Arrays;

public class TopMenu {
	ResponseMenu response;
}

class ResponseMenu {
	MenuInAll menu;
}

class MenuInAll {
	Menus menus;
}

class Menus {
	int count;
	MenuCategory[] items;
	public String toString () {
		if (count == 0)
			return "Menu not available";
		return count + "menus in total : \n" + Arrays.toString(items) + "\n";
	}
}

class MenuCategory {
	String menuId;
	String name;
	String description;
	MenuSections entries;
	public String toString () {
		return name + ": \n" + entries;
	}
}

class MenuSections {
	int count;
	MenuSection[] items;
	public String toString () {
		return Arrays.toString(items) + "\n";
	}
}

class MenuSection {
	String name;
	DishGroup entries;
	public String toString () {
		return name + ": \n" + entries;
	}
}

class DishGroup {
	int count;
	SingleDish[] items;
	public String toString () {
		return Arrays.toString(items) + "\n";
	}
}
