/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Coffee_shop;

/**
 *
 * @author bryan
 */
public enum Drink {
    JUICE(7),
    CAPPUCCINO(9),
    ESPRESSO(6);

    private int price;

    Drink(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
    
}