package markovNet;

import geneticProgramming.symbols.SymbolType;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import random.RandomManager;

/**
 * マルコフネットのクリークを表現するクラス．
 * @author tanji
 */
public class Clique<T> implements Comparable<Clique<T>> 
{
    private BigInteger code;
    private double weight;
    private double[] probabilityList;
    private List<List<T>> candidateListList;
    private List<Integer> indexes;
    private int candidateSize;
    private List<T> mostProbableStructure;
    
    public Clique(BigInteger code, List<List<T>> candidateListList, int candidateSize)
    {
        this(code, candidateListList, candidateSize, null);
    }
    
    public Clique(BigInteger code, List<List<T>> candidateListList, int candidateSize, double[] probabilityList)
    {
        this.code = code;
        this.candidateListList = candidateListList;
        this.probabilityList = probabilityList;
        this.candidateSize = candidateSize;
        
        indexes = new LinkedList<Integer>();
        for( int i = 0; i < code.bitLength(); i++ )
        {
            if( code.testBit(i) )
            {
                indexes.add(i);
            }
        }
    }
        
    /** ノード数を返す */
    public int size()
    {
        return candidateListList.size();
    }
    
    /**
     * 指定された状態の確率を返す．
     * @param state
     * @return
     */
    public double getProbability(int[] state)
    {
        int startIndex = 0;
        int range = getCandidateSize();
        
        for( int i= 0; i< candidateListList.size(); i++ )
        {
            int oneCandidateSize = getCandidateListList().get(i).size();
            startIndex += (range / oneCandidateSize) * state[i];
            range /= oneCandidateSize;
        }
        //System.out.println("getProbability Index = " + Arrays.toString(state) + " " + startIndex + " / " + probabilityList.length);
        return probabilityList[startIndex];
    }
    
    public String toString()
    {
        return new StringBuilder(new StringBuilder(code.toString(2)).reverse().toString()).append(":[").append(weight).append("]").toString();
    }

    @Override
    public int compareTo(Clique<T> o)
    {
        if( this.weight - o.getWeight() > 0 ) return -1;
        else if( this.weight - o.getWeight() < 0 ) return 1;
        else
        {
            if( this.code.bitCount() - o.getCode().bitCount() > 0 ) return -1;
            else if( this.code.bitCount() - o.getCode().bitCount() < 0 ) return 1;
            return 0;
        }
    }
    
    public int getRandomState(int sampleIndex)
    {
        int bit = 0;
        for( int i = 0; i <= sampleIndex; i++ )
        {
            if( code.testBit(i) )
            {
                bit++;
            }
        }
        return (int)(RandomManager.getRandom() * candidateListList.get(bit-1).size());
    }
    
    // ---------- getter and setter methods ----------
    public BigInteger getCode()
    {
        return code;
    }

    public void setCode(BigInteger code)
    {
        this.code = code;
    }

    public double getWeight()
    {
        return weight;
    }

    public void setWeight(double weight)
    {
        this.weight = weight;
    }
    
    public double[] getProbabilityList()
    {
        return probabilityList;
    }

    public void setProbabilityList(double[] probabilityList)
    {
        this.probabilityList = probabilityList;
    }

    public List<List<T>> getCandidateListList()
    {
        return candidateListList;
    }

    public void setCandidateListList(List<List<T>> candidateListList)
    {
        this.candidateListList = candidateListList;
    }

    public int getCandidateSize()
    {
        return candidateSize;
    }

    public void setCandidateSize(int candidateSize)
    {
        this.candidateSize = candidateSize;
    }
    /** ビットが立っているインデックスのリストを返す */
    public List<Integer> getIndexes()
    {
        return indexes;
    }
    /** ビットが立っているインデックスのリストを設定する */
    public void setIndexes(List<Integer> indexes)
    {
        this.indexes = indexes;
    }
    
    public static String toS_Expression_recursive(int currentIndex, int depth, int maxDepth, int arity, int[] data)
    {
    	if (depth > maxDepth)
    	{
    		return "";
    	}
    	String s;
    	
    	//    		System.out.println( "currentIndex = " + currentIndex + " " + data[currentIndex]  + " arity = " + arity);
    	if( depth != maxDepth ) {
    		s = "( " + data[currentIndex] + " ";
    		for ( int i = 1; i <= arity; i++ )
    		{
    			s = s + toS_Expression_recursive(currentIndex * arity + i, depth+1, maxDepth, arity, data) + " ";
    		}
    		s = s + ")";
    	}
    	else {
    		s = String.valueOf(data[currentIndex]);
    	}
    	return s;
    }
    
    public String toS_Expression(int maxDepth, int arity)
    {
    	int size = 0;
    	for( int d = 1; d <= maxDepth; d++ )
    	{
    		size += Math.pow(arity, d);
    	}
    	
    	StringBuilder code = new StringBuilder(this.code.toString(2)).reverse();
    	for(int i = code.length(); i < size; i++)
    	{
    		code.append("0");
    	}
    	
    	int[] data = new int[code.length()];
    	int i = 0;
    	for( ; i < code.length(); i++ )
    	{
    		data[i] = Integer.valueOf(code.substring(i, i+1));
    	}
    	for( ; i < size; i++ )
    	{
    		data[i] = 0;
    	}
    	//System.out.println( data.length );
    	//System.out.println( Arrays.toString(data) );
    	return toS_Expression_recursive(0, 1, maxDepth, arity, data);
    }
}
