package perce;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RadixSort
{
    public static List<int[]> sort(List<int[]> data, int[] radixes)
    {
        List<int[]>[] tmp = new List[1];
        tmp[0] = data;
        
        for( int i = 0; i < radixes.length; i++ )
        {
            tmp = radix(tmp, radixes[i], radixes.length - i -1);
        }
        
        List<int[]> result = new ArrayList<int[]>(data.size());
        for( int i = 0; i < tmp.length; i++ )
        {
            result.addAll( tmp[i] );
        }
        return result;
    }
    
    public static List<int[]>[] radix(List<int[]>[] data, int radix, int index)
    {
        System.out.println(data + " " + radix + " " + index);
        List<int[]>[] tmp = new List[radix];
        for( int i = 0; i < radix; i++ )
        {
            tmp[i] = new ArrayList<int[]>();
        }
        
        for( int i = 0; i < data.length; i++ )
        {
            for( int j = 0; j < data[i].size(); j++ )
            {
                tmp[ data[i].get(j)[index] ].add( data[i].get(j) );
            }
        }
        return tmp;
    }
    
    /** i番目の要素だけをソートする */
    public static List<Integer> sort_permutation(List<int[]> data, int radix, int index)
    {
    	List<Integer>[] tmp = new List[radix];
    	for( int i = 0; i < radix; i++ )
    	{
    		tmp[i] = new ArrayList<Integer>( (int)(data.size() / radix) );	
    	}
    	
    	for(int i = 0; i < data.size(); i++ )
    	{
    		tmp[ data.get(i)[index] ] .add( i );
    	}
        
        List<Integer> result = new ArrayList<Integer>(data.size());
        for( int i = 0; i < tmp.length; i++ )
        {
            result.addAll( tmp[i] );
        }
        return result;
    }
    
    public static List<Integer> sort_permutation(List<int[]> data, int[] radixes)
    {
        List<Integer>[] tmp = new List[1];
        tmp[0] = new ArrayList<Integer>(data.size());
        
        for( int i = 0; i < data.size(); i++ )
        {
            tmp[0].add(i);
        }
        
        for( int i = 0; i < radixes.length; i++ )
        {
            tmp = radix_permutation(data, tmp, radixes[i], radixes.length - i -1);
        }
        
        List<Integer> result = new ArrayList<Integer>(data.size());
        for( int i = 0; i < tmp.length; i++ )
        {
            result.addAll( tmp[i] );
        }
        return result;
    }
    
    public static List<Integer>[] radix_permutation(List<int[]> data, List<Integer>[] permutation, int radix, int index)
    {
        System.out.println(permutation + " " + radix + " " + index);
        List<Integer>[] tmp = new List[radix];
        
        for( int i = 0; i < radix; i++ )
        {
            tmp[i] = new ArrayList<Integer>();
        }
        
        for( int i = 0; i < permutation.length; i++ )
        {
            for( int j = 0; j < permutation[i].size(); j++ )
            {
                tmp[ data.get(permutation[i].get(j))[index] ].add( permutation[i].get(j) );
            }
        }
        
        return tmp;
    }
    
    public static void main(String[] args)
    {
        List<int[]> data = new ArrayList<int[]>();
        data.add(new int[]{0,0,0,0});
        data.add(new int[]{1,1,2,4});
        data.add(new int[]{3,0,1,0});
        data.add(new int[]{2,2,1,1});
        data.add(new int[]{1,0,3,1});
        data.add(new int[]{2,0,5,2});
        data.add(new int[]{3,1,2,2});
        data.add(new int[]{2,0,5,2});
        
//        for(int i = 0; i < data.size(); i++)
//        {
//            System.out.println(Arrays.toString(data.get(i)));
//        }
//        List<Integer> permutation = sort_permutation( data, new int[]{5, 6, 3, 4} );
//        data = sort( data, new int[]{5, 6, 3, 4} );
//        
//        for(int i = 0; i < data.size(); i++)
//        {
//            System.out.println(Arrays.toString(data.get(i)));
//        }
//        System.out.println(permutation);
        
        System.out.println();
        System.out.println( sort_permutation(data, 4, 0));
    }
}
