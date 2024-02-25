/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Coffee_shop;

import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 *
 * @author bryan
 */
public class Barista extends Thread{
    Cafe cafe;
    private boolean hasCalledSalesReport = false;
    public Barista(int ID, Cafe cafe){
        this.cafe=cafe;
        this.setName("Barista-" + ID);
        System.out.println(getName()+": Arrive at "+cafe.getName()+"!");
        cafe.countBarista++;
    }
    

    
    @Override
    public void run(){
        while(!cafe.isClosingTime())
        {
            cafe.work(this);
        }
        if (cafe.isClosingTime()) { 
            
            while(!cafe.customerQueue.isEmpty() || cafe.countCustomer != 0)
            {//check if there are any customers in the shop
                if(!cafe.customerQueue.isEmpty()){
                    System.out.println("\t\tLooklikes there's "+cafe.customerQueue.size()+
                        " customers left. Next customer serving by " + getName());
                    cafe.work(this);
                } else {
                    try{
                        cafe.clean(this);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
//            synchronized(cafe.customerQueue){
//                cafe.customerQueue.notify();
//            }
            if(cafe.customerQueue.isEmpty() && cafe.countCustomer == 0){
                System.out.println("\t\tLooklikes there's "+cafe.countCustomer + " customers left." + getName() + " leave the shop");
                cafe.countBarista--;
            }
        }
        if (cafe.countBarista==0) {
            cafe.salesReport(cafe.totalCustomer);
        }
    }
}
