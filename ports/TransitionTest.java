package ports;

import java.util.Arrays;

import random.RandomManager;

public class TransitionTest
{
    public static void main(String args[])
    {
        int n = 10;
        double pt = 0.5;
        double repetition = 1000000;
        int transitionSum = 0;
        for( int i = 0; i < repetition; i++ )
        {   
            //int length = 1 + (int)(RandomManager.getRandom() * (n-1));
            int length = n;
            boolean[] treeModel = new boolean[length];
            Arrays.fill(treeModel, false);
            //treeModel[length/2] = true;
            treeModel[length-1] = true;
            
            transitionSum += sample(treeModel, pt, length);
            //transitionSum += isJump(pt, (int)(RandomManager.getRandom() * n), n);
        }
        
        System.out.println("Transition Count = " + transitionSum / repetition);
    }
    
    static int poisson(double lambda)
    {
        int k;
        lambda = Math.exp(lambda) * RandomManager.getRandom();
        for(k = 0; lambda > 1.0; k++)
        {
            lambda *= RandomManager.getRandom();
        }
        return k;
    }
    
    public static int isJump(double pt, int position, int n)
    {
        //System.out.println("First Position = " + position);
        //System.out.println(position + " " + n);
        while(position != n-1)
        {
            if( RandomManager.getRandom() < pt )
            {
                return 0;
                // transition
                //position = (int)(RandomManager.getRandom() * n);
                //transitionCount ++;
            }
            else
            {
                position += 1;
            }
            //System.out.println(position);
        }
        return 1;
    }
    
    public static int sample(boolean[] treeModel, double pt, int n)
    {
        int position = (int)(Math.random() * n);
        int transitionCount = 0;
        
        while( !treeModel[position] )
        //while(position != n-1)
        {
            if( Math.random() < pt )
            {
                // transition
                position = (int)(Math.random() * n);
                transitionCount ++;
            }
            else
            {
                position += 1;
            }
            //System.out.println(position);
        }
        return transitionCount;
    }
}
