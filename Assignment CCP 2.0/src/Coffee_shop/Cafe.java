/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Coffee_shop;

import static Coffee_shop.Drink.CAPPUCCINO;
import static Coffee_shop.Drink.ESPRESSO;
import static Coffee_shop.Drink.JUICE;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.*;
import java.util.Random;


/**
 *
 * @author bryan
 */
public class Cafe {
    String name;
    int countCappuccino, countEspresso, countJuice, countCustomer, countBarista, totalCustomer;
    
    BlockingQueue<Customer> customerQueue;
    private final Semaphore[] tables;
    
    Lock milkfrothinglock = new ReentrantLock(true);
    Lock espressolock = new ReentrantLock(true);
    Lock juicelock = new ReentrantLock(true);
    
    
    private volatile boolean closingTime = false;
    
    //constructor
    public Cafe(String name){
        this.name=name;
        countCappuccino = 0;
        countEspresso = 0;
        countJuice = 0;
        countCustomer = 0;  //customer in cafe
        countBarista = 0;
        
        customerQueue = new LinkedBlockingQueue<>();
        System.out.println("Setting Customer Waiting Queue...");
        
        tables = new Semaphore[5]; // 5 tables
        System.out.println("Setting Table...");
        for (int i = 0; i < tables.length; i++) {
            tables[i] = new Semaphore(2); // Each table has 2 seats
            System.out.println("Table No." + i + " Ready!");
        }
        
        System.out.println("Waiting for baristas...");
        Barista barista1 = new Barista(1, this);
        Barista barista2 = new Barista(2, this);
        Barista barista3 = new Barista(3, this);
        barista1.start();
        barista2.start();
        barista3.start();
        
        System.out.println(name+": Open Now!");
        
    }
    
    // ============================= Cafe ====================================
    public String getName(){
        return name;
    }
    
    public synchronized void setClosingTime() {
        closingTime = true;
        System.out.println("===================== Clock: Tick Tock... It's Closing Time! =====================");
        
    }

    public boolean isClosingTime() {
        return closingTime;
    }
    
    public void salesReport(int customercount)
    {
        if(countCustomer == 0 && countBarista ==0){
            System.out.println("========== Sales Report ==========");
            System.out.println("Juice Sold\t:" + countJuice + " \t\tEarned : RM " + countJuice*7);
            System.out.println("Espresso Sold\t:" + countEspresso + " \t\tEarned : RM " + countEspresso*6);
            System.out.println("Cappuccino Sold:"+ countCappuccino + " \t\tEarned : RM " + countCappuccino*9);
            System.out.println("Total customer served: " + customercount);
        }
    }
    
    // ============================= Customer ====================================
    public void add(Customer customer)
    {
        System.out.println("\t"+customer.getName()+ " entering the shop at "+customer.getInTime());
        
        synchronized (customerQueue){
            if (customerQueue.size() == 5) {
                System.out.println("\t\tMore than 5 customers waiting. Customer " + customer.getName() + " leaves.");
                return;
            }
            ((LinkedBlockingQueue<Customer>)customerQueue).offer(customer);
            System.out.println("\t"+customer.getName()+ " wait for order. Waiting Queue: " + customerQueue.size());
           
            if(!customerQueue.isEmpty())
                customerQueue.notify();
        }
        try{
            countCustomer ++;
            totalCustomer ++;
            System.out.println("\t\tTotal customer in cafe: " + countCustomer);
            // Barista take the order first
            Thread.sleep(1000);
            synchronized(customer) {
                //waiting for drink
                customer.wait();
            }
            takeSeat(customer);
            customer.drinkingTime();
            leaveSeat(customer);  
            countCustomer --;
            System.out.println("\t\tRemaining customer in cafe: " + countCustomer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public synchronized void takeSeat(Customer customer) throws InterruptedException {
        for (int i = 0; i < tables.length; i++) {
            if (tables[i].tryAcquire()) { // Try to acquire seats at a table
                System.out.println("\t" + customer.getName() + " took a seat at table " + i + ". Seat available: " + tables[i].availablePermits());
                return;
            }
        }
        System.out.println("\t" + customer.getName() + " couldn't find an available seat. Waiting...");
        synchronized (customerQueue) {
            while (true) {
                try {
                    customerQueue.wait(); // Wait until a seat becomes available
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Try to take a seat again after waking up
                for (int i = 0; i < tables.length; i++) {
                    if (tables[i].tryAcquire()) {
                        System.out.println("\t" + customer.getName() + " took a seat at table " + i + " after waiting.");
                        return;
                    }
                }
            }
        }
    }
    
    public void leaveSeat(Customer customer) {
        for (int i = 0; i < tables.length; i++) {
            if (tables[i].availablePermits() == 1) { // Check if there's at least one seat occupied
                tables[i].release(); // Release a seat
                System.out.println("\t" + customer.getName() + " left the seat at table " + i + ". Seat available: " + tables[i].availablePermits());
                return;
            }
        }
    }   

    // ============================= Barista ====================================
        public void work(Barista barista)
        {
            Customer customer = checkWaitingQueue(barista);
            if(customer!=null){
                try{    
                    getorder(customer, barista);  
                }catch(InterruptedException iex){
                    iex.printStackTrace();
                }
            }
            
        }
        
        public void clean(Barista barista) throws InterruptedException
        {
            System.out.println(barista.getName() + ": Cleaning the cafe");
            Thread.sleep(8000);
            
        }

        public Customer checkWaitingQueue(Barista barista){
            Customer customer = null;
            boolean isSleep = false;
            synchronized (customerQueue){
                while(customerQueue.isEmpty() && !isClosingTime()) //no waiting customer
                {
                    System.out.println(barista.getName()+": Fall Asleep");
                    isSleep = true;
                    try{
                        customerQueue.wait();
                    }catch(InterruptedException iex){
                        iex.printStackTrace();
                    }
                }
                if(customerQueue.isEmpty() && isClosingTime()){
                    customerQueue.notify();
                }
                customer = customerQueue.poll();
                if (isSleep){
                    System.out.println(barista.getName() + ": Awake and serve " + customer.getName() 
                            +" in the queue. Remaining Waiting Queue: " + customerQueue.size());
                } else {
                    System.out.println(barista.getName() + ": Serve the next customer (" + customer.getName() 
                            +") in the queue. Remaining Waiting Queue: " + customerQueue.size());
                }
                return customer;
            }
        }
    
    public void getorder(Customer customer, Barista barista) throws InterruptedException{
        
        Drink order = customer.getOrder();
        Thread.sleep(3000);
        System.out.println(barista.getName()+": Start Making "+order+" for " + customer.getName()+"!");
        switch(order){
            case CAPPUCCINO:
                makeCappuccino(barista);
                countCappuccino++;
                break;
            case ESPRESSO:    
                makeEspresso(barista);
                countEspresso++;
                break;
            case JUICE:
                makeJuice(barista);
                countJuice++;
                break;
        }
        System.out.println(barista.getName()+": Finished " + order + " for " + customer.getName()+"!");
        synchronized(customer) {
            customer.notify();
        }
    }
    
    public void makeJuice(Barista barista) throws InterruptedException{
        boolean done=false;
        while(!done){
            if(juicelock.tryLock()){
                System.out.println(barista.getName()+": Uses Juice Tap!");
                Thread.sleep(8000);
                done=true;
                juicelock.unlock();
                System.out.println(barista.getName()+" Done: Juice Tap is available now!");
            }
            else{
                System.out.println(barista.getName()+": try to use Juice Tap but it is not available now!");
                Thread.sleep(4000);
            }
        }
    }
    public void makeEspresso(Barista barista) throws InterruptedException{
        boolean done=false;
        while(!done){
            if(espressolock.tryLock()){
                System.out.println(barista.getName()+": Uses Espresso Machine!");
                Thread.sleep(5000);
                done=true;
                espressolock.unlock();
                System.out.println(barista.getName()+" Done: Espresso Machine is available now!");
            }
            else{
                System.out.println(barista.getName()+": try to use Espresso Machine but it is not available now!");
                Thread.sleep(2000);
            }
        }
    }
    public void makeCappuccino(Barista barista) throws InterruptedException{
        boolean done1=false;
        boolean done2=false;
        while(!done1||!done2){
            if(!done1){
                if(espressolock.tryLock()){
                    System.out.println(barista.getName()+" Uses Espresso Machine!");
                    Thread.sleep(5000);
                    done1=true;
                    espressolock.unlock();
                    System.out.println(barista.getName()+" Done: Espresso Machine is available now!");
                }
                else{
                    System.out.println(barista.getName()+": try to use Espresso Machine but it is not available now!");
                    Thread.sleep(2000);
                }
            }
            if(!done2){
                if(milkfrothinglock.tryLock()){
                    System.out.println(barista.getName()+": Uses Milk Frothing Machine!");
                    Thread.sleep(5000);
                    done2=true;
                    milkfrothinglock.unlock();
                    System.out.println(barista.getName()+" Done: Milk Frothing Machine is available now!");
                }
                else{
                    System.out.println(barista.getName()+": try to use Milk Frothing Machine but it is not available now!");
                    Thread.sleep(2000);
                }
            }
        }
    }
}
