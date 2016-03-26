package markovNet;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class BronKerboschAlgorithm
{
    public static void main(String[] args)
    {
        MarkovNetNode<Integer> n1 = new MarkovNetNode<Integer>(null);
        MarkovNetNode<Integer> n2 = new MarkovNetNode<Integer>(null);
        MarkovNetNode<Integer> n3 = new MarkovNetNode<Integer>(null);
        MarkovNetNode<Integer> n4 = new MarkovNetNode<Integer>(null);
        MarkovNetNode<Integer> n5 = new MarkovNetNode<Integer>(null);
        MarkovNetNode<Integer> n6 = new MarkovNetNode<Integer>(null);
        
        n1.setNeighbor(1);
        n2.setNeighbor(0);
        
        n1.setNeighbor(4);
        n5.setNeighbor(0);
        
        n2.setNeighbor(2);
        n3.setNeighbor(1);
        
        n2.setNeighbor(4);
        n5.setNeighbor(1);
        
        n3.setNeighbor(3);
        n4.setNeighbor(2);
        
        n4.setNeighbor(4);
        n5.setNeighbor(3);
        
        n4.setNeighbor(5);
        n6.setNeighbor(3);
        
        List<MarkovNetNode<Integer>> data = new ArrayList<MarkovNetNode<Integer>>();
        data.add(n1);
        data.add(n2);
        data.add(n3);
        data.add(n4);
        data.add(n5);
        data.add(n6);
        
        System.out.println(n1.getNeighbors().toString(2));
        System.out.println(n2.getNeighbors().toString(2));
        System.out.println(n3.getNeighbors().toString(2));
        System.out.println(n4.getNeighbors().toString(2));
        System.out.println(n5.getNeighbors().toString(2));
        System.out.println(n6.getNeighbors().toString(2));
        
        List<BigInteger> cliqueList = bronKerboschAlgorithm(data);
        for( int i = 0; i < cliqueList.size(); i++ )
        {
            System.out.println( "Clique " + i + " = " + cliqueList.get(i).toString(2) );
        }
    }
    
    public static <T> List<BigInteger> bronKerboschAlgorithm(List<MarkovNetNode<T>> data)
    {
        BigInteger R = new BigInteger("0");
        BigInteger P = new BigInteger("0");
        BigInteger X = new BigInteger("0");
        
        for(int i = 0; i < data.size(); i++)
        {
            P = P.setBit(i);
        }
        
        List<BigInteger> cliqueList = new ArrayList<BigInteger>();
        
        BK(R, P, X, data, cliqueList);
        
        return cliqueList;
    }
    
    private static <T> void BK(BigInteger R, BigInteger P, BigInteger X, List<MarkovNetNode<T>> data, List<BigInteger> cliqueList)
    {
        //System.out.println(indent(depth) + "BR(" + R.toString(2) + ", " + P.toString(2) + ", " + X.toString(2) + ", " + cliqueList + ")");
        if( P.bitCount() == 0 && X.bitCount() == 0 )
        {
            //System.out.println(indent(depth) + "-------- output " + R.toString(2));
            cliqueList.add(R);
            return;
        }
        
        int pivotIndex = 0;
        BigInteger pivotCandidate = P.or(X);
        while( !pivotCandidate.testBit(pivotIndex) )
        {
            pivotIndex++;
        }
        
        BigInteger neighbor = data.get(pivotIndex).getNeighbors();
                
        BigInteger forElements = P.andNot(neighbor);
        int v = 0;
        for( int i = 0; i < forElements.bitCount(); i++ )
        {
            while( !forElements.testBit(v) )
            {
                v++;
            }
            
            BK(R.setBit(v), P.and(data.get(v).getNeighbors()), X.and(data.get(v).getNeighbors()), data, cliqueList);
            P = P.clearBit(v);
            X = X.setBit(v);
            v++;
        }
    }
}