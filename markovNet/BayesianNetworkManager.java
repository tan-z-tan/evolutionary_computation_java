package markovNet;

import geneticProgramming.DefaultGpIndividual;
import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpNode;
import geneticProgramming.GpSymbolSet;
import geneticProgramming.GpTreeManager;
import geneticProgramming.symbols.DefaultSymbolType;
import geneticProgramming.symbols.SymbolType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 最大深さ = depthの分岐数 = arityの完全木のプロトタイプ木上でマルコフネットを構築する．
 * @author tanji
 */
public class BayesianNetworkManager
{
    private int arity;
    private int depth;
    private int nodeSize;
    private List<int[]> edgeList;
    private int maxCliqueSize;
    private double sigfinicanceLevel;
    private GpEnvironment<? extends GpIndividual> environment;
    private List<StochasticVariable<SymbolType>> nodeList;
      
    class Edge
    {
        private int start;
        private int end;
        private double weight;
    }
    
    public BayesianNetworkManager(GpEnvironment<? extends GpIndividual> environment)
    {
        this.environment = environment;
        depth = Integer.valueOf( environment.getAttribute("PPTDepth") );
        arity = Integer.valueOf( environment.getAttribute("PPTArity") );
        maxCliqueSize = Integer.valueOf( environment.getAttribute("PPTMaxCliqueSize") );
        
        initialize();
    }
    
    private void initialize()
    {
        int size = 0;
        for( int i = 0; i < depth; i++ )
        {
            size += (int)Math.pow(arity, i);
        }
        nodeSize = size;
        
        nodeList = new ArrayList<StochasticVariable<SymbolType>>();
        for( int i = 0; i < nodeSize; i++ )
        {
            StochasticVariable<SymbolType> node;
            //System.out.println("i = " + i);
            if( isTerminal(i) )
            {
                node = new StochasticVariable<SymbolType>(environment.getSymbolSet().getTerminalList());
            }
            else
            {
                node = new StochasticVariable<SymbolType>(environment.getSymbolSet().getFunctionList());
            }
            //System.out.println(node.getCandidateList());
            Double[] probabilityList = new Double[node.getCandidateList().size()];
            Arrays.fill(probabilityList, new Double(0));
            
            for(GpIndividual ind: environment.getPopulation())
            {
                //System.out.println( node.getCandidateList().indexOf(getNodeSymbol(ind, i).getNodeType()) );
                probabilityList[node.getCandidateList().indexOf(getNodeSymbol(ind, i).getNodeType())] ++;
            }
            //System.out.println("Size " + environment.getPopulationSize());
            for( int p = 0; p < probabilityList.length; p++ )
            {
                probabilityList[p] /= environment.getPopulationSize();
            }
            node.setProbabilityList(Arrays.asList(probabilityList));
            nodeList.add(node);
        }
    }
    
    private GpNode sampleNewTree()
    {
        //System.out.println( "Root sampling " + nodeList.get(0).sampling() );
        GpNode root = new GpNode(nodeList.get(0).getCandidateList().get(nodeList.get(0).sampling()), 1);
        List<GpNode> frontLine = new ArrayList<GpNode>();
        List<GpNode> newLine = new ArrayList<GpNode>();
        frontLine.add(root);
        
        int nodeIndex = 1;
        for( int i = 1; i < depth; i++ )
        {
            System.out.println("depth = " + i);
            int size = (int)Math.pow(arity, i);
            for( int j = 0; j < size; j++ )
            {
                int parentIndex = (int)(j / arity);
                System.out.println("parent index = " + parentIndex);
                
                StochasticVariable<SymbolType> node = nodeList.get( nodeIndex++ );
                int sampleIndex = node.sampling();
                GpNode newNode = new GpNode(node.getCandidateList().get(sampleIndex), i);
                frontLine.get(parentIndex).addChild(newNode);
                newLine.add(newNode);
            }
            frontLine = newLine;
            newLine = new ArrayList<GpNode>();
        }
        return root;
    }
    
    private void extractDependencyEdge()
    {        
        edgeList = new ArrayList<int[]>();
        
        List<? extends GpIndividual> population = environment.getPopulation();
        List<Integer> terminalSymbolList = new ArrayList<Integer>();
        List<Integer> nonterminalSymbolList = new ArrayList<Integer>();
        
        for( int i = 0; i < nodeSize; i++ )
        {
            for( int j = i+1; j < nodeSize; j++ )
            {
                List<int[]> pairList = new ArrayList<int[]>();
                for( GpIndividual ind: population )
                {
                    pairList.add( new int[]{environment.getSymbolSet().getIndex(getNodeSymbol(ind, i).getNodeType()), environment.getSymbolSet().getIndex(getNodeSymbol(ind, j).getNodeType())} );
                }
                
                List<Integer> candidateListA = nonterminalSymbolList;
                List<Integer> candidateListB = nonterminalSymbolList;
                if( isTerminal(i) )
                {
                    candidateListA = terminalSymbolList;
                }
                if( isTerminal(j) )
                {
                    candidateListB = terminalSymbolList;
                }
                double chiSquareProbability = ChiSquareTest.calculateChiSquareProbability(pairList, candidateListA, candidateListB);
                
                if( chiSquareProbability > sigfinicanceLevel )
                {
                    edgeList.add(new int[]{i, j});
                }
            }
        }
    }
    
    /** return true if and only if the specified index is terminal index.
     * @param i
     * @return
     */
    private boolean isTerminal(int i)
    {
        if( i >= (nodeSize - (int)Math.pow(arity, depth-1)) )
        {
            return true;
        }
        return false;
    }
    
    /** returns node at i-th
     * @param ind
     * @param i
     * @return
     */
    private static GpNode getNodeSymbol(GpIndividual ind, int i)
    {
        GpNode root = ind.getRootNode();
        return GpTreeManager.getNodeAt(root, i);
    }
    
    // main method for test
    public static void main(String[] args)
    {
        GpEnvironment<GpIndividual> env = new GpEnvironment<GpIndividual>();
        GpSymbolSet set = new GpSymbolSet();
        set.addSymbol(new DefaultSymbolType("A", 2));
        set.addSymbol(new DefaultSymbolType("B", 2));
        set.addSymbol(new DefaultSymbolType("C", 2));
        set.addSymbol(new DefaultSymbolType("X", 0));
        set.addSymbol(new DefaultSymbolType("Y", 0));
        
        List<GpIndividual> population = new ArrayList<GpIndividual>();
        population.add( new GpIndividual(GpTreeManager.constructGpNodeFromString("(A (B X X) (C X Y))", set)) );
        population.add( new GpIndividual(GpTreeManager.constructGpNodeFromString("(A (A X Y) (B X X))", set)) );
        population.add( new GpIndividual(GpTreeManager.constructGpNodeFromString("(C (A X Y) (A Y X))", set)) );
        env.setPopulation(population);
        env.setPopulationSize(3);
        env.putAttribute("PPTArity", "2");
        env.putAttribute("PPTDepth", "3");
        env.putAttribute("PPTMaxCliqueSize", "5");
        env.setSymbolSet(set);
        
        BayesianNetworkManager net = new BayesianNetworkManager(env);
        System.out.println( GpTreeManager.getS_Expression(net.sampleNewTree()) );
    }
}