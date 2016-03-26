package markovNet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MutualInformationTest
{
    /** calculates mutual information value from data A and B (in dataSet).
     * @param dataSet
     * @param symbolListA
     * @param symbolListB
     * @return chi square value
     */
    public static double calculateNormalizedRedundancy(List<int[]> dataSet, int i, int j, List<Integer> symbolListA, List<Integer> symbolListB)
    {
        // symbolListA.size() + 1 * symbolListB.size() + 1 
        List<List<Integer>> table = new ArrayList<List<Integer>>();
        
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
            //System.out.println(i + " " + j + " : " + indexA + " " + indexB + " size = " + table.size() + " " + table.get(indexA).size());
            table.get(indexA).set(indexB, table.get(indexA).get(indexB) + 1);
            table.get(indexA).set(symbolListB.size(), table.get(indexA).get(symbolListB.size()) + 1);
            table.get(symbolListA.size()).set(indexB, table.get(symbolListA.size()).get(indexB) + 1);
            table.get(symbolListA.size()).set(symbolListB.size(), table.get(symbolListA.size()).get(symbolListB.size()) + 1);
        }
        
        for( int s = 0; s < table.size(); s++ )
        {
            for( int k = 0; k < table.get(s).size(); k++ )
            {
                //System.out.print( table.get(s).get(k) + ", ");
            }
            //System.out.println();
        }
        
        double sum = table.get(symbolListA.size()).get(symbolListB.size());
        double entropyAB = 0;
        double entropyA = 0;
        double entropyB = 0;
        
        for( int s = 0; s < symbolListA.size(); s++ )
        {
            double value = table.get(s).get(symbolListB.size());
            if( value != 0 )
            {
                entropyA -= (value /sum) * log_2(value /sum);
                //System.out.println("en A " + entropyA);
            }
        }
        for( int s = 0; s < symbolListB.size(); s++ )
        {
            double value = table.get(symbolListA.size()).get(s);
            if( value != 0 )
            {
                entropyB -= (value /sum) * log_2(value /sum);
                //System.out.println("en B " + entropyA);
            }
        }
        
        for( int s = 0; s < symbolListA.size(); s++ )
        {
            for( int k = 0; k < symbolListB.size(); k++ )
            {
                double count = table.get(s).get(k); 
                
                if( count != 0 )
                {
                    entropyAB -= (count /sum ) * log_2(count /sum);
                    //System.out.println("entropy AB = " + entropyAB);
                }
            }
        }
        
        //System.out.println(" new method: A =" + entropyA + " B  = " + entropyB + " AB = " + entropyAB);
        double mutualInformation = entropyA + entropyB - entropyAB;
        double max = Math.min(entropyA, entropyB) / (entropyA + entropyB);
        if( max == 0 )
        {
            return 0;
        }
        //return (mutualInformation / (entropyA + entropyB)) / max;
        //return (mutualInformation / (entropyA + entropyB)) * (entropyA + entropyB);
        //return mutualInformation;
        return (mutualInformation / Math.min(entropyA, entropyB));
    }
    
    /** calculates mutual information value from data A and B (in dataSet).
     * @param dataSet
     * @param symbolListA
     * @param symbolListB
     * @return chi square value
     */
    public static double calculateMutualInformation(List<int[]> dataSet, int i, int j, List<Integer> symbolListA, List<Integer> symbolListB)
    {
        List<List<Integer>> table = new ArrayList<List<Integer>>();
        
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
        }
        for( int s = 0; s < table.size(); s++ )
        {
        	for( int k = 0; k < table.get(s).size(); k++ )
        	{
        		//System.out.print( table.get(s).get(k) + ", ");
        	}
        	//System.out.println();
        }
        double sum = table.get(symbolListA.size()).get(symbolListB.size());
        double mutualInformation = 0;
        //System.out.println("sum = " + sum);
        for( int s = 0; s < symbolListA.size(); s++ )
        {
            for( int k = 0; k < symbolListB.size(); k++ )
            {
                double sum_s = table.get(s).get(symbolListB.size());
                double sum_k = table.get(symbolListA.size()).get(k);
                double count = table.get(s).get(k); 
                double logTerm = (count / sum) / ((sum_s / sum) * (sum_k / sum));
//                System.out.println( "x = (" + s + "," + k + ")");
//                System.out.println(" count = " + count / sum);
//                System.out.println("s = " + sum_s / sum);
//                System.out.println("k = " + sum_k / sum);
//                System.out.println( "log " + logTerm + "  = " + log_2(logTerm) );
//                System.out.println( (count /sum ) * log_2(logTerm) );
                if( logTerm != 0 && !Double.isInfinite(logTerm) && !Double.isNaN(logTerm) )
                {
                    mutualInformation += (count /sum ) * log_2(logTerm);
                }
            }
        }
        
        //System.out.println("Chi square value = " + chisq);
        return mutualInformation;
    }
    
    public static double log_2(double x)
    {
    	return Math.log(x) / Math.log(2);
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
        
        dataSet.add(new int[]{0, 1, 1, 1, 0, 1, 0});
        dataSet.add(new int[]{0, 0, 0, 0, 0, 1, 0});
        dataSet.add(new int[]{0, 0, 1, 1, 1, 0, 0});
        dataSet.add(new int[]{0, 0, 0, 2, 1, 2, 0});
        dataSet.add(new int[]{1, 1, 0, 1, 1, 2, 1});
        dataSet.add(new int[]{1, 1, 1, 2, 1, 1, 0});
        dataSet.add(new int[]{1, 0, 0, 0, 1, 2, 0});
        dataSet.add(new int[]{1, 0, 2, 1, 1, 0, 0});
        
        for( int i = 0; i < dataSet.get(0).length; i++ )
        {
        	for( int j = 0; j < dataSet.get(0).length; j++ )
            {
        	    System.out.println();
                System.out.println("Mutual Information value    (" + i + "," + j + ") = " + calculateMutualInformation(dataSet, i, j, sListB, sListB) );
        	    System.out.println("Normalized Redundancy value (" + i + "," + j + ") = " + calculateNormalizedRedundancy(dataSet, i, j, sListB, sListB) );
            }
        }
        //System.out.println("Mutual Information value (" + 0 + "," + 6 + ") = " + calculateMutualInformation(dataSet, 0, 6, sListA, sListA) );
        //System.out.println("Mutual Information value (" + 0 + "," + 6 + ") = " + calculateNormalizedRedundancy(dataSet, 0, 6, sListA, sListA) );
    }
}
