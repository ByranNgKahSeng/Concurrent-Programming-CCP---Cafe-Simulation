/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Coffee_shop;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
/**
 *
 * @author bryan
 */
public class Customer extends Thread{
    public int customerId;
    public Drink order;
    public Cafe cafe;
    Date inTime;
    Random random;
    
    public Customer(int customerId, Cafe cafe) {
        random = new Random();
        this.cafe = cafe;
        this.customerId = customerId;
        this.setName("Customer-" + customerId);
        
        int drinkindex=random.nextInt(10);//0-6 Cappucino, 7-8 Espresso, 9 Juice
        if(drinkindex<7){
            this.order=Drink.CAPPUCCINO;
        }
        else if(drinkindex<9){
            this.order=Drink.ESPRESSO;
        }
        else{
            this.order=Drink.JUICE;
        }
    }
    
    

    public int getCustomerId() {
        return customerId;
    }

    public Drink getOrder() {
        return order;
    }
    
    public Date getInTime() {
        return inTime;
    }
    
    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }
    
    public void drinkingTime() throws InterruptedException{
        System.out.println("\t" + getName() + " Start drinking " + getOrder());
        random = new Random();
        int totalTime = 0;
        for (int i = 1; i <= 3; i++) {
            int randomSleep = random.nextInt(3000) + 1000;
            Thread.sleep(randomSleep);  // Random sleep duration between 1 to 3 seconds
            System.out.println("\t" + getName() + " " + (i * 25) + "% consumed in " + randomSleep + " MILLISECONDS."); 
            totalTime += randomSleep;
        }
        System.out.println("\t" + getName() + " Finished Drinking. Total Time: " + totalTime + " MILLISECONDS.");
        cafe.leaveSeat(this);
    }
    
    private synchronized void goCafe()
    {
        cafe.add(this);
    }
    
    @Override
    public void run() {
        // Add more behavior here as needed
        goCafe();
    }
}
