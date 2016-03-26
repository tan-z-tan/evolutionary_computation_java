package perce;

import geneticProgramming.DefaultGpIndividual;
import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpNode;
import geneticProgramming.GpSymbolSet;
import geneticProgramming.GpTreeManager;
import geneticProgramming.symbols.DefaultSymbolType;
import geneticProgramming.symbols.SymbolType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import markovNet.Clique;
import markovNet.MarkovNetManager;
import random.RandomManager;

public class CliqueDivisor
{
    public static void divideCliqueN_times(MarkovNetManager mn, int N)
    {
        initialize(mn);
        
        Set<String> cutSet = new HashSet<String>();
        
        for( int n = 0; n < N; n++ )
        {
            //System.out.println("n = " + n);
            // select a pair to be divided
            int[] cutPair = getRandomIndexPair(mn, cutSet);
            double d = mn.dependencyTest(cutPair[0], cutPair[1]);
            System.out.println("dependency = " + cutPair[0] + " " + cutPair[1] + " = " + d);
            if( d < 0 )
            {
                divide(mn, cutPair[0], cutPair[1]);
                String cutStr = cutString(cutPair[0], cutPair[1]);
                
                cutSet.add(cutStr);
                //System.out.println("cutting " + cutStr);
                //System.out.println(mn.getCliqueList());
            }
        }
        
        for( int i = 0; i < mn.getCliqueList().size(); i++ )
        {
            mn.getCliqueList().get(i).setWeight(1);
        }
    }
    
    private static int[] getRandomIndexPair(MarkovNetManager mn, Set<String> cutSet)
    {
        int i = (int) (RandomManager.getRandom() * mn.getNodeSize());
        int j = (int) (RandomManager.getRandom() * mn.getNodeSize());
        while (i == j)
        {
            j = (int) (RandomManager.getRandom() * mn.getNodeSize());
        }
        String cutStr = cutString(i, j);
        while( cutSet.contains(cutStr) )
        {
            i = (int) (RandomManager.getRandom() * mn.getNodeSize());
            while (i == j)
            {
                j = (int) (RandomManager.getRandom() * mn.getNodeSize());
            }
            cutStr = cutString(i, j);
        }
        return new int[]{i, j};
    }
    
    private static String cutString(int i, int j)
    {
        return (i > j)? cutString(j, i): new StringBuilder(String.valueOf(i)).append(" ").append(String.valueOf(j)).toString();
    }
    
    private static Clique<SymbolType>[] cut(MarkovNetManager mn, int i, int j, Clique<SymbolType> c)
    {
        BigInteger codeA = c.getCode().clearBit(i);
        BigInteger codeB = c.getCode().clearBit(j);
        int candidateSizeA = c.getCandidateSize() / mn.getNodeList().get(i).getCandidateList().size();
        int candidateSizeB = c.getCandidateSize() / mn.getNodeList().get(j).getCandidateList().size();

        Clique<SymbolType> childA = new Clique<SymbolType>(codeA, new ArrayList<List<SymbolType>>(), candidateSizeA);
        Clique<SymbolType> childB = new Clique<SymbolType>(codeB, new ArrayList<List<SymbolType>>(), candidateSizeB);

        return new Clique[] { childA, childB };
    }

    private static void initialize(MarkovNetManager mn)
    {
        // initialize: create a fully connected graph
        List<Clique<SymbolType>> cList = new ArrayList<Clique<SymbolType>>();
        BigInteger rootCode = new BigInteger("0");
        for (int i = 0; i < mn.getNodeSize(); i++)
        {
            rootCode = rootCode.setBit(i);
        }
                
        List<List<SymbolType>> candidateListList = new ArrayList<List<SymbolType>>();
        int candidateSize = 1;
        for (int index = 0; index < rootCode.bitLength(); index++)
        {
            if (rootCode.testBit(index))
            {
                candidateSize *= mn.getNodeList().get(index).getCandidateList().size();
                candidateListList.add(mn.getNodeList().get(index).getCandidateList());
            }
        }
        Clique<SymbolType> clique = new Clique<SymbolType>(rootCode, new ArrayList<List<SymbolType>>(), candidateSize);
        cList.add(clique);

        mn.setCliqueList(cList);
    }

    public static void divide(MarkovNetManager mn, int i, int j)
    {
        List<Clique<SymbolType>> newCList = new ArrayList<Clique<SymbolType>>();
        for (int s = 0; s < mn.getCliqueList().size(); s++)
        {
            Clique<SymbolType> c = mn.getCliqueList().get(s);
            if (c.getCode().testBit(i) && c.getCode().testBit(j))
            {
                Clique<SymbolType>[] result = cut(mn, i, j, c);
                newCList.add(result[0]);
                newCList.add(result[1]);
            }
            else
            {
                newCList.add(c);
            }
        }
        mn.setCliqueList(newCList);
    }

    public static void main(String[] args)
    {
        GpEnvironment<DefaultGpIndividual> env = new GpEnvironment<DefaultGpIndividual>();
        GpSymbolSet symbolSet = new GpSymbolSet();
        SymbolType X = new DefaultSymbolType("X", 0);
        SymbolType Y = new DefaultSymbolType("Y", 0);
        SymbolType Z = new DefaultSymbolType("Z", 0);
        SymbolType A = new DefaultSymbolType("A", 2);
        SymbolType B = new DefaultSymbolType("B", 2);
        SymbolType C = new DefaultSymbolType("C", 2);
        SymbolType L = new DefaultSymbolType("L", 2);
        symbolSet.addSymbol(X);
        symbolSet.addSymbol(Y);
        symbolSet.addSymbol(Z);
        symbolSet.addSymbol(A);
        symbolSet.addSymbol(B);
        symbolSet.addSymbol(C);
        symbolSet.addSymbol(L);
        env.setSymbolSet(symbolSet);
        env.setNumberOfMaxDepth(3);
        env.setNumberOfMaxInitialDepth(3);

        env.putAttribute("portsT", "0.0");
        env.putAttribute("portsAlpha", "0.0");
        env.putAttribute("PPTArity", "2");
        env.putAttribute("PPTDepth", "3");
        env.putAttribute("PPTMaxCliqueSize", "3");
        env.putAttribute("PPTSignificanceLevel", "0.05");
        env.putAttribute("PPTSmoothingParameter", "0");
        env.putAttribute("PPT_MN_order", "root");
        // env.putAttribute("peedSampling", "cliqueBased");
        env.putAttribute("peedSampling", "relatedCliqueBased");
        env.putAttribute("PPT_MN_order", "roulette");
        env.putAttribute("PPT_dependency", "mutualInformation");
        
        // env.putAttribute("peedSampling", "dependencyBased");

        env.setPopulationSize(4);

        // create sample tree
        GpNode tree1 = GpTreeManager.constructGpNodeFromString("(A (B X Z) (C X X))", symbolSet);
        GpNode tree2 = GpTreeManager.constructGpNodeFromString("(A (C Y Y) (B Y Z))", symbolSet);
        GpNode tree3 = GpTreeManager.constructGpNodeFromString("(B (B Y X) (C X Y))", symbolSet);
        GpNode tree4 = GpTreeManager.constructGpNodeFromString("(B (A Z X) (B Y Z))", symbolSet);
        //GpNode tree1 = GpTreeManager.constructGpNodeFromString("(A (B (C X Z) (A Y Z)) (C (B X X) (A X X)))", symbolSet);
        //GpNode tree2 = GpTreeManager.constructGpNodeFromString("(A (B (B Y Y) (A X Z)) (C (A Y Z) (A Z Y)))", symbolSet);
        //GpNode tree3 = GpTreeManager.constructGpNodeFromString("(A (B (C Y X) (A X Z)) (C (B X Y) (B Z Y)))", symbolSet);
        //GpNode tree4 = GpTreeManager.constructGpNodeFromString("(A (B (A Z X) (C Y Y)) (C (C Y Z) (C X Z)))", symbolSet);
        
        
        // GpNode tree5 = GpTreeManager.constructGpNodeFromString("(C (C Y Y) (B Y X))", symbolSet);

        List<GpIndividual> population = new ArrayList<GpIndividual>();
        population.add(new GpIndividual(tree1));
        population.add(new GpIndividual(tree2));
        population.add(new GpIndividual(tree3));
        population.add(new GpIndividual(tree4));
        // population.add( new GpIndividual(tree5) );

        MarkovNetManager mn = new MarkovNetManager(population, env);
        
        mn.extractDependencyCliqueFromFullTree(5);
        System.out.println(mn.getCliqueList());
        /*
        CliqueDivisor.divideCliqueN_times(mn, 3);
        System.out.println();
        System.out.println("Result ");
        for( int i = 0; i < mn.getCliqueList().size(); i++ )
        {
            System.out.println(mn.getCliqueList().get(i));
        }
        */
    }
}
