/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.dish;

/**
 *
 * @author liyou
 */
public class Frequency {

    String dishName;
    DishType type;
    int freq;
    
    public Frequency(String _n, DishType _t, int _f){
        dishName = _n;
        type = _t;
        freq = _f;
    }
    

    @Override
    public String toString() {
        return dishName + ' ' + type + ' ' + freq;
    }
}
