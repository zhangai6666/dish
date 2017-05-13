/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dish;

/**
 *
 * @author liyou
 */
public class Frequency {
    private int total;
    private int entree;
    private int starter;
    private int dessert;
    private int drink;
    private int vegetarian;
    private int kids;
    
    public Frequency(){
        total = 0;
        entree = 0;
        starter = 0;
        dessert = 0;
        drink = 0;
        vegetarian = 0;
        kids = 0;
    }
    
    public void increment(DishType type) {
        switch (type) {
            case Starter:
                ++starter;
                break;
            case Dessert:
                ++dessert;
                break;
            case Drink:
                ++drink;
                break;
            case Vegetarian:
                ++vegetarian;
                break;
            case Kids:
                ++kids;
                break;
            default:
                ++entree;
        }
        ++total;
    }
    
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Total = ");
        str.append(total);
        if (entree > 0) {
            str.append("; Entree = ").append(entree);
        }
        if (starter > 0) {
            str.append("; Starter = ").append(starter);
        }
        if (dessert > 0) {
            str.append("; Dessert = ").append(dessert);
        }
        if (drink > 0) {
            str.append("; Drink = ").append(drink);
        }
        if (vegetarian > 0) {
            str.append("; Vegetarian = ").append(vegetarian);
        }
        if (kids > 0) {
            str.append("; Kids = ").append(kids);
        }
        return str.toString();
    }
}
