package markovNet;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/** 条件付確率表を持つ確率変数 */
public class MarkovNetNode<T>
{
    private BigInteger neighbors;
    private List<T> candidateList;
    private int state;
    
    /** コンストラクタ
     * @param candidateList
     * @param probabilityList
     */
    public MarkovNetNode(List<T> candidateList)
    {
        this(candidateList, new ArrayList<Double>());
    }
    
    /** コンストラクタ
     * @param candidateList
     * @param probabilityList
     */
    public MarkovNetNode(List<T> candidateList, List<Double> probabilityList)
    {
        this.candidateList = candidateList;
        this.neighbors = new BigInteger("0");
        this.state = -1;
    }
    
    public void setNeighbor(int n)
    {
        neighbors = neighbors.setBit(n);
    }
    
    public void removeNeighbor(int n)
    {
        neighbors = neighbors.clearBit(n);
    }
    // ---------- getter and setter methods ----------
    public List<T> getCandidateList()
    {
        return candidateList;
    }

    public void setCandidateList(List<T> candidateList)
    {
        this.candidateList = candidateList;
    }

    public int getState()
    {
        return state;
    }

    public void setState(int state)
    {
        this.state = state;
    }
    
    public BigInteger getNeighbors()
    {
        return neighbors;
    }
    
    public void setNeighbors(BigInteger neighbors)
    {
        this.neighbors = neighbors;
    }
}
