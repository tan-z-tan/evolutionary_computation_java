package math.distance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermutationDistance
{
    public static void main(String[] args)
    {
        List<Integer> alpha = Arrays.asList(new Integer[]{1,2,3,4,5,6});
        //List<Integer> beta =  Arrays.asList(new Integer[]{2,4,3,7,5,6,1});
        List<Integer> beta =  Arrays.asList(new Integer[]{3,6,2,1,5,4,9});
        System.out.println("NumberOfCross     = " + permutationDistance_NumberOfCross(alpha, beta) );
        System.out.println("Spearman Footrule = " + permutationDistance_SpaermanFootrule(alpha, beta) );
    }
    
    public static int permutationDistance_SpaermanFootrule(List<Integer> alpha, List<Integer> beta)
    {
    	if( alpha.size() < beta.size() )
    	{
    		return permutationDistance_SpaermanFootrule(beta, alpha);
    	}
    	
        int sum = 0;
        for(int i = 0; i < alpha.size(); i++)
        {
            sum += Math.abs( index(alpha, alpha.get(i)) - index(beta, alpha.get(i)) );
        }
        
        //System.out.println("sum = " + sum);
        return sum;
    }
    
    /** バグがある．計算量がオーダー一つ大きい．　*/
    public static int permutationDistance_NumberOfCross(List<Integer> alpha, List<Integer> beta)
    {
    	if( alpha.size() < beta.size() )
    	{
    		return permutationDistance_NumberOfCross(beta, alpha);
    	}
    	
        int sum = 0;
        for(int i = 0; i < alpha.size(); i++)
        {
        	sum += d( alpha.get(i), alpha, beta ); 
        }
        
        return sum;
    }
    
    public static int d(int e, List<Integer> alpha, List<Integer> beta)
    {
        int sum = 0;
        for( int i = 0; i < alpha.size(); i++ )
        {
            int y = alpha.get(i);
            if( alpha.indexOf(e) < alpha.indexOf(y) && beta.indexOf(e) > beta.indexOf(y))
            {
                sum++;
            }
            else if( alpha.indexOf(e) > alpha.indexOf(y) && beta.indexOf(e) < beta.indexOf(y))
            {
                sum++;
            }
            
        }
        
        return sum;
    }
    
    public static int index(List<Integer> alpha, int e)
    {
        return alpha.indexOf(e);
    }
}
