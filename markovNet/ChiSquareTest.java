package markovNet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;


public class ChiSquareTest
{
	/** このカイ二乗値以上の値が，自由度degreeOfFreedomのカイ二乗分布上で起こる確率を返す． */
    public static double getChiSquare(double x, double degreeOfFreedom)
    {
        if( degreeOfFreedom == 1 && x > 1000 )
        {
            //System.out.println("afo");
            return 0;
        }
        
        if( degreeOfFreedom == 0 )
        {
            return 1;
        }
        ChiSquaredDistribution chiSquareDist = new ChiSquaredDistribution(degreeOfFreedom);
        
        try{
        	double value = chiSquareDist.cumulativeProbability(x);
        	if( Double.isInfinite(value) )
        	{
        	    //System.out.println( "value = " + value );
        		return 0;
        	}
        	return 1 - value;
        	//return chiSquareDist.inverseCumulativeProbability(x);
        //}catch(Exception e){e.printStackTrace();}
        }catch(Exception e){ System.out.println("ChiSquareTest(x, df) = " + x + " " + degreeOfFreedom); e.printStackTrace(); return 0.0001; }
    }
    
    /** calculates chi square value from data A and B (in dataSet).
     * @param dataSet
     * @param symbolListA
     * @param symbolListB
     * @return chi square value
     */
    public static double calculateChiSquareProbability(List<int[]> dataSet, List<Integer> symbolListA, List<Integer> symbolListB)
    {
        List<List<Integer>> table = new ArrayList<List<Integer>>();
        
        // creates initial table
        for( int i = 0; i < symbolListA.size() + 1; i++ )
        {
            Integer[] initialRow = new Integer[symbolListB.size() + 1];
            Arrays.fill(initialRow, 0);
            table.add( Arrays.asList(initialRow) );
        }
              
        for( int[] data: dataSet )
        {
            int indexA = symbolListA.indexOf( data[0] );
            int indexB = symbolListB.indexOf( data[1] );
            table.get(indexA).set(indexB, table.get(indexA).get(indexB) + 1);
            
            table.get(indexA).set(symbolListB.size(), table.get(indexA).get(symbolListB.size()) + 1);
            table.get(symbolListA.size()).set(indexB, table.get(symbolListA.size()).get(indexB) + 1);
            table.get(symbolListA.size()).set(symbolListB.size(), table.get(symbolListA.size()).get(symbolListB.size()) + 1);
        }
        
        double sum = table.get(symbolListA.size()).get(symbolListB.size());
        double chisq = 0;
        for( int i = 0; i < symbolListA.size(); i++ )
        {
            for( int j = 0; j < symbolListB.size(); j++ )
            {
                double e = table.get(i).get(symbolListB.size()) * table.get(symbolListA.size()).get(j) / sum;
                if( e!= 0 )
                {
                    double a = table.get(i).get(j);
                    chisq += Math.pow((a - e), 2) / e;
                }
            }
        }
        //System.out.println("Chi square value = " + chisq);
        return getChiSquare(chisq, (symbolListA.size() -1) * (symbolListB.size() -1));
    }

    /** calculates chi square value from data A and B (in dataSet).
     * @param dataSet
     * @param symbolListA
     * @param symbolListB
     * @return chi square value
     */
    public static double calculateChiSquareProbability(List<int[]> dataSet, int i, int j, List<Integer> symbolListA, List<Integer> symbolListB)
    {
        List<List<Integer>> table = new ArrayList<List<Integer>>();
        //Integer[][] table = new Integer[symbolListA.size() + 1][symbolListB.size() + 1];
        
        // creates initial table
        for( int s = 0; s < symbolListA.size() + 1; s++ )
        {
            Integer[] initialRow = new Integer[symbolListB.size() + 1];
            Arrays.fill(initialRow, 0);
            table.add( Arrays.asList(initialRow) );
        }
              
        for( int[] data: dataSet )
        {
        	int indexA = data[i];
            int indexB = data[j];
            //System.out.println(indexA + " " + indexB);
            table.get(indexA).set(indexB, table.get(indexA).get(indexB) + 1);
            table.get(indexA).set(symbolListB.size(), table.get(indexA).get(symbolListB.size()) + 1);
            table.get(symbolListA.size()).set(indexB, table.get(symbolListA.size()).get(indexB) + 1);
            table.get(symbolListA.size()).set(symbolListB.size(), table.get(symbolListA.size()).get(symbolListB.size()) + 1);
            //table[indexA][indexB] ++;
            //table[indexA][symbolListB.size()] ++;
            //table[symbolListA.size()][indexB] ++;
            //table[symbolListA.size()][symbolListB.size()] ++;
        }
        
//        for( int[] data: dataSet )
//        {
//            int indexA = symbolListA.indexOf( data[0] );
//            int indexB = symbolListB.indexOf( data[1] );
//            table.get(indexA).set(indexB, table.get(indexA).get(indexB) + 1);
//            
//            table.get(indexA).set(symbolListB.size(), table.get(indexA).get(symbolListB.size()) + 1);
//            table.get(symbolListA.size()).set(indexB, table.get(symbolListA.size()).get(indexB) + 1);
//            table.get(symbolListA.size()).set(symbolListB.size(), table.get(symbolListA.size()).get(symbolListB.size()) + 1);
//        }
        
        double sum = table.get(symbolListA.size()).get(symbolListB.size());
        //double sum = table[symbolListA.size()][symbolListB.size()];
        double chisq = 0;
        for( int s = 0; s < symbolListA.size(); s++ )
        {
            for( int k = 0; k < symbolListB.size(); k++ )
            {
                double e = table.get(s).get(symbolListB.size()) * table.get(symbolListA.size()).get(k) / sum;
                //double e = table[s][symbolListB.size()] * table[symbolListA.size()][k] / sum;
                if( e!= 0 )
                {
                    double a = table.get(s).get(k);
                    //double a = table[s][k];
                    chisq += Math.pow((a - e), 2) / e;
                }
            }
        }
        //System.out.println("Chi square value = " + chisq);
        return getChiSquare(chisq, (symbolListA.size() -1) * (symbolListB.size() -1));
    }
    
    // main method for check
    // try following command in R.
    // chisq.test( matrix( c(228, 863, 284, 851), nrow=2 ), correct=FALSE )
    public static void main(String[] args)
    {
        List<int[]> dataSet = new ArrayList<int[]>();
        //List<Integer> sListA = Arrays.asList(1, 2, 3, 4);
        //List<Integer> sListB = Arrays.asList(4, 5, 6);
        List<Integer> sListA = Arrays.asList(1, 2);
        List<Integer> sListB = Arrays.asList(3, 4, 5);
        
        for( int i = 0; i < 100; i++ )
        {
            int a = (int)(Math.random() * 4 + 1);
            int b = (int)(Math.random() * 3 + 4);
            //System.out.println(a + " " + b);
            //dataSet.add(new int[]{a, b});
        }
        for( int i = 0; i < 228; i++ )
            dataSet.add(new int[]{1, 3});
        for( int i = 0; i < 863; i++ )
            dataSet.add(new int[]{1, 4});
        for( int i = 0; i < 284; i++ )
            dataSet.add(new int[]{2, 3});
        for( int i = 0; i < 851; i++ )
            dataSet.add(new int[]{2, 4});
        
        // additional
        for( int i = 0; i < 200; i++ )            
            dataSet.add(new int[]{1, 5});
        for( int i = 0; i < 203; i++ )
            dataSet.add(new int[]{2, 5});
        
        System.out.println("Chi Square value = " + calculateChiSquareProbability(dataSet, sListA, sListB) );
    }
}
