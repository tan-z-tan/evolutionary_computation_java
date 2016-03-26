package markovNet;

import geneticProgramming.symbols.DefaultSymbolType;
import geneticProgramming.symbols.SymbolType;

import java.util.ArrayList;
import java.util.List;

import random.RandomManager;

/** 条件付確率表を持つ確率変数 */
public class StochasticVariable<T>
{
    private List<StochasticVariable<T>> parentList;
    private List<StochasticVariable<T>> childList;
    private List<T> candidateList;
    private List<Double> probabilityList;
    private int state;
    
    public static void main(String[] args)
    {
        DefaultSymbolType symbolA = new DefaultSymbolType("A", 0);
        DefaultSymbolType symbolB = new DefaultSymbolType("B", 1);
        DefaultSymbolType symbolC = new DefaultSymbolType("C", 0);
        DefaultSymbolType symbolD = new DefaultSymbolType("D", 1);
        DefaultSymbolType symbolE = new DefaultSymbolType("E", 0);
        DefaultSymbolType symbolF = new DefaultSymbolType("F", 1);
        DefaultSymbolType symbolG = new DefaultSymbolType("G", 2);
        
        List<SymbolType> candidateListA = new ArrayList<SymbolType>();
        List<SymbolType> candidateListB = new ArrayList<SymbolType>();
        List<SymbolType> candidateListC = new ArrayList<SymbolType>();
        candidateListA.add(symbolA);
        candidateListA.add(symbolB);
        candidateListB.add(symbolC);
        candidateListB.add(symbolD);
        candidateListC.add(symbolE);
        candidateListC.add(symbolF);
        candidateListC.add(symbolG);
        
        List<Double> probabilityListA = new ArrayList<Double>();
        List<Double> probabilityListB = new ArrayList<Double>();
        List<Double> probabilityListC = new ArrayList<Double>();
        
        probabilityListA.add(0.2);
        probabilityListA.add(0.8);
        
        probabilityListB.add(0.05);
        probabilityListB.add(0.5);
        probabilityListB.add(0.4);
        probabilityListB.add(0.05);
        
        probabilityListC.add(0.1);
        probabilityListC.add(0.1);
        probabilityListC.add(0.1);
        probabilityListC.add(0.1);
        probabilityListC.add(0.05);
        probabilityListC.add(0.1);
        probabilityListC.add(0.1);
        probabilityListC.add(0.05);
        probabilityListC.add(0.05);
        probabilityListC.add(0.1);
        probabilityListC.add(0.05);
        probabilityListC.add(0.1);
        
        StochasticVariable<SymbolType> nodeA = new StochasticVariable<SymbolType>(candidateListA, probabilityListA);
        StochasticVariable<SymbolType> nodeB = new StochasticVariable<SymbolType>(candidateListB, probabilityListB);
        StochasticVariable<SymbolType> nodeC = new StochasticVariable<SymbolType>(candidateListC, probabilityListC);
        
        nodeB.addParent(nodeA);
        nodeC.addParent(nodeA);
        nodeC.addParent(nodeB);
        
        int stateA = nodeA.sampling();
        nodeA.setState(stateA);
        System.out.println(nodeA.getCandidateList().get(stateA));
        
        int stateB = nodeB.sampling();
        nodeB.setState(stateB);
        System.out.println(nodeB.getCandidateList().get(stateB));
        
        int stateC = nodeC.sampling();
        nodeB.setState(stateC);
        System.out.println(nodeC.getCandidateList().get(stateC));
    }
    
    /** コンストラクタ
     * @param candidateList
     * @param probabilityList
     */
    public StochasticVariable(List<T> candidateList)
    {
        this(candidateList, new ArrayList<Double>());
    }
    
    /** コンストラクタ
     * @param candidateList
     * @param probabilityList
     */
    public StochasticVariable(List<T> candidateList, List<Double> probabilityList)
    {
        this.candidateList = candidateList;
        this.probabilityList = probabilityList;
        this.parentList = new ArrayList<StochasticVariable<T>>();
        this.childList = new ArrayList<StochasticVariable<T>>();
        this.state = -1;
    }
    
    /** ランダムにサンプリングする */
    public int sampling()
    {
        int conditionStartIndex = 0;
        int range = 1;
        
        for( int i = parentList.size() - 1; i >= 0; i-- )
        {
            int parentState = parentList.get(i).getState();
            int parentStateSize = parentList.get(i).getCandidateList().size();
            //System.out.println("parent = " + parentState);
            conditionStartIndex += (parentStateSize * parentState) * range;
            range *= parentStateSize;
        }
        //System.out.println("Condition index  = " + conditionStartIndex + " " + probabilityList);
        
        double r = 0;
        for( int i = conditionStartIndex; i < conditionStartIndex + this.candidateList.size(); i++ )
        {
            r += probabilityList.get(i);
        }
        double randomValue = r * RandomManager.getRandom();
        r = 0;
        for( int i = conditionStartIndex; i < conditionStartIndex + this.candidateList.size(); i++ )
        {
            r += probabilityList.get(i);
            if ( r >= randomValue )
            {
                return i - conditionStartIndex;
            }
        }
        return -1;
    }
    
    public void addParent(StochasticVariable<T> parent)
    {
        if( !parentList.contains(parent) )
        {
            this.parentList.add(parent);
            parent.getChildList().add(this);
        }
    }
    
    public void setChild(StochasticVariable<T> child)
    {
        if( !childList.contains(child) )
        {
            childList.add(child);
            child.getParentList().add(this);
        }
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

    public List<StochasticVariable<T>> getParentList()
    {
        return parentList;
    }

    public void setParentList(List<StochasticVariable<T>> parentList)
    {
        this.parentList = parentList;
    }

    public List<StochasticVariable<T>> getChildList()
    {
        return childList;
    }

    public void setChildList(List<StochasticVariable<T>> childList)
    {
        this.childList = childList;
    }

    public List<Double> getProbabilityList()
    {
        return probabilityList;
    }

    public void setProbabilityList(List<Double> probabilityList)
    {
        this.probabilityList = probabilityList;
    }
}