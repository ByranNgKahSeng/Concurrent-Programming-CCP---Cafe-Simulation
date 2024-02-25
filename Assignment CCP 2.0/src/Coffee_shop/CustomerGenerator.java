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
public class CustomerGenerator implements Runnable{
    Cafe cafe;
    int customercount = 1;
    public CustomerGenerator(Cafe cafe)
    {
        this.cafe = cafe;
    }
 
    public void run()
    {
        while(!cafe.isClosingTime())
        {
            try
            {   //add counter that to ensure customer not more than 20
                //implement break to check the closing time
                Random random = new Random();
                if(customercount<=20){
                    int randomSeconds = random.nextInt(3);
                    Customer customer = new Customer(customercount, cafe);
                    customer.setInTime(new Date());
                    customer.start();
                    customercount++;
                    Thread.sleep(randomSeconds * 1000);
                }
                
            }
            catch(InterruptedException iex)
            {
                iex.printStackTrace();
            }
        }

    }
    
}

