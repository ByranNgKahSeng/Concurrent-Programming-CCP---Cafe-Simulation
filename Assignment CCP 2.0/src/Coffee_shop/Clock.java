/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Coffee_shop;

/**
 *
 * @author bryan
 */
public class Clock extends Thread
{
    //private CustomerGenerator cg;
    private Cafe cafe;
    public Clock(Cafe cafe)
    {
        this.cafe = cafe;
    }
    
    public void run()
    {
        try
        {
            Thread.sleep(20000);
            //System.out.println("/t/tClock : Tick Tock...It's Closing Time！");
            cafe.setClosingTime();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
