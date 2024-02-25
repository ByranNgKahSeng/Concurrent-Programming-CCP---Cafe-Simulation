/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Coffee_shop;
import java.util.Random;
/**
 *
 * @author bryan
 */
public class Coffee_shop {
    public boolean closingTime = false;
    
    public static void main(String[] args) throws InterruptedException{
        //Random random = new Random();
        Cafe cafe=new Cafe("GoGo CoffeeCafe");
        
        Clock clockThread = new Clock(cafe);
        clockThread.start();
        
        // Create baristas and start their threads
        

        
        CustomerGenerator cg = new CustomerGenerator(cafe);
        Thread thcg = new Thread(cg);
	thcg.start();
        
        
    }
}

