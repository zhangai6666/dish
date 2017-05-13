package dish;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuRaw {

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

    public List<Dish> flaten() {
        if (count == 0) {
            return null;
        }
        List<Dish> ans = new ArrayList<Dish>();
        for (int i = 0; i < items.length; i++) {
            String category = items[i].name;
            MenuSection[] sections = items[i].entries.items;
            for (int j = 0; j < sections.length; j++) {
                String section = sections[j].name;
                SingleDish[] dishes = sections[j].entries.items;
                for (int k = 0; k < dishes.length; k++) {
                    dishes[k].uniformNameToLowerCase();
                    ans.add(new Dish(dishes[k], category, section));
                }
            }
        }
        return ans;
    }

    public String toString() {
        if (count == 0) {
            return "Menu not available";
        }
        return count + "menus in total : \n" + Arrays.toString(items) + "\n";
    }
}

class MenuCategory {

    String menuId;
    String name;
    String description;
    MenuSections entries;

    public String toString() {
        return name + ": \n" + entries;
    }
}

class MenuSections {

    int count;
    MenuSection[] items;

    public String toString() {
        return Arrays.toString(items) + "\n";
    }
}

class MenuSection {

    String name;
    DishGroup entries;

    public String toString() {
        return name + ": \n" + entries;
    }
}

class DishGroup {

    int count;
    SingleDish[] items;

    public String toString() {
        return Arrays.toString(items) + "\n";
    }
}
