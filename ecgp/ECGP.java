package ecgp;

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
import java.util.List;

import perce.PERCE;

import markovNet.Clique;
import markovNet.MarkovNetManager;

public class ECGP
{
    private MarkovNetManager mn;
    private GpSymbolSet _symbolSet;
    private GpEnvironment<? extends GpIndividual> _environment;
    
    /**
     * constructor
     * 
     * @param promisingSolutions
     * @param environment
     */
    public ECGP(List<? extends GpIndividual> promisingIndividual, GpEnvironment<? extends GpIndividual> environment)
    {
        _symbolSet = environment.getSymbolSet();
        _environment = environment;
        
        constructModel(promisingIndividual);
    }
    
    private void constructModel(List<? extends GpIndividual> promisingIndividuals)
    {
        mn = new MarkovNetManager(promisingIndividuals, _environment);
        
        List<Clique<SymbolType>> cliqueList = new ArrayList<Clique<SymbolType>>();
        for( int i = 0; i < mn.getNodeSize(); i++ )
        {
            List<List<SymbolType>> candidateListList = new ArrayList<List<SymbolType>>();
            int candidateSize = 1;
            BigInteger code = BigInteger.valueOf(0);
            code = code.setBit(i);
            
            candidateSize *= mn.getNodeList().get(i).getCandidateList().size();
            candidateListList.add( mn.getNodeList().get(i).getCandidateList() );
                        
            Clique<SymbolType> clique = new Clique<SymbolType>(code, candidateListList, candidateSize);
            cliqueList.add( clique );
        }
        
        mn.setCliqueList(cliqueList);
        mn.calculateProbabilityTable();
        
        optimizePartition(mn);
        
        
//        System.out.println( "MDL = " + calculateMDL(mn) );
//        System.out.println(mn.getCliqueList());
//        
//        MarkovNetManager newMn = merge(mn, 0, 3);
//        System.out.println( "MDL = " + calculateMDL(newMn) );
//        System.out.println(newMn.getCliqueList());
//        System.out.println( "diff = " + calculateMDLDiff(mn, 0, 3));
//        
//        newMn = merge(mn, 3, 6);
//        System.out.println( "MDL = " + calculateMDL(newMn) );
//        System.out.println(newMn.getCliqueList());
//        System.out.println( "diff = " + calculateMDLDiff(mn, 3, 6));
        
    }
    
    private MarkovNetManager optimizePartition(MarkovNetManager mn)
    {
        int index_i = -1;
        int index_j = -1;
        System.out.println("start " + mn.getCliqueList().size() );
        
        while( true )
        {
            double minimumScore = Double.MAX_VALUE;

            for( int i = 0; i < mn.getCliqueList().size() ; i++ )
            {
                for( int j = i+1; j < mn.getCliqueList().size() ; j++ )
                {
                    double score = calculateMDLDiff(mn, i, j);
                    if( minimumScore > score )
                    {
                        minimumScore = score;
                        index_i = i;
                        index_j = j;
                    }
                }
            }

            if( minimumScore >= 0 )
            {
                break;
            }
            
            mn = merge(mn, index_i, index_j);
            System.out.println( "merge " + index_i + " " + index_j + " -> " + mn.getCliqueList().size() + " " + mn.getCliqueList().get(mn.getCliqueList().size()-1).size() );
        }
        return mn;
    }
    
    private MarkovNetManager merge(MarkovNetManager mn, int i, int j)
    {
        MarkovNetManager newMn = new MarkovNetManager(new ArrayList<GpIndividual>(), _environment);
        newMn.setDataList(mn.getDataList());
        
        BigInteger code = new BigInteger("0");
        code = code.or(mn.getCliqueList().get(i).getCode()).or(mn.getCliqueList().get(j).getCode());
        
        List<Clique<SymbolType>> cliqueList = new ArrayList<Clique<SymbolType>>(mn.getCliqueList());
        if( i > j )
        {
            cliqueList.remove(i);
            cliqueList.remove(j);
        }
        else {
            cliqueList.remove(j);
            cliqueList.remove(i);
        }
        
        // construct merged clique
        List<List<SymbolType>> candidateListList = new ArrayList<List<SymbolType>>();
        int candidateSize = 1;
        for( int index = 0; index < code.bitLength(); index++ )
        {
            if( code.testBit(index) )
            {
                candidateSize *= mn.getNodeList().get(index).getCandidateList().size();
                candidateListList.add( mn.getNodeList().get(index).getCandidateList() );
            }
        }    
        Clique<SymbolType> clique = new Clique<SymbolType>(code, candidateListList, candidateSize);
        cliqueList.add(clique);
        
        newMn.setCliqueList(cliqueList);
        newMn.calculateProbabilityTable();
        
        return newMn;
    }
    
    private static double calculateMDL(MarkovNetManager markovNet)
    {
        double MDL = 0;
        
        // model complexity
        double modelComplexity = 0;
        for( int i = 0; i < markovNet.getCliqueList().size(); i++ )
        {
            Clique<SymbolType> clique = markovNet.getCliqueList().get(i);
            double r = clique.getCandidateSize();
            modelComplexity += r - 1;
        }
        
        // likelihood
        double likelihood = 0;
        for( int i = 0; i < markovNet.getCliqueList().size(); i++ )
        {
            Clique<SymbolType> clique = markovNet.getCliqueList().get(i);
            double[] probbiliatyList = clique.getProbabilityList();
            //System.out.println("i = " + i);
            
            for( int j = 0; j < probbiliatyList.length; j++ )
            {
                //System.out.println( j + " " + probbiliatyList[j] );
                
                if( probbiliatyList[j] != 0 )
                {
                    likelihood += -probbiliatyList[j] * Math.log(probbiliatyList[j]) / Math.log(2);
                }
            }
        }
        //likelihood = markovNet.getDataList().size() * likelihood;
        
        MDL = (Math.log(markovNet.getDataList().size()) / Math.log(2)) * modelComplexity + markovNet.getDataList().size() * likelihood;
        //MDL = modelComplexity + markovNet.getDataList().size() * likelihood;
        
        //System.out.println( "MDL " + MDL + " (" + modelComplexity + " + " + likelihood + ")");
        return MDL;
    }
    
    // iとjをマージしたときのMDLの差分をかえす．
    private static double calculateMDLDiff(MarkovNetManager markovNet, int i, int j)
    {
        // model complexity
        double modelComplexity = 0;
        Clique<SymbolType> clique_i = markovNet.getCliqueList().get(i);
        Clique<SymbolType> clique_j = markovNet.getCliqueList().get(j);
        double r_i = clique_i.getCandidateSize();
        double r_j = clique_j.getCandidateSize();
        modelComplexity -= (Math.log(markovNet.getDataList().size()) / Math.log(2)) * (r_i - 1 + r_j -1);
        modelComplexity += (Math.log(markovNet.getDataList().size()) / Math.log(2)) * (clique_i.getCandidateSize() * clique_j.getCandidateSize() -1);
                
        // likelihood
        double likelihood = 0;
        
        double[] probbiliatyList_i = clique_i.getProbabilityList();
        double[] probbiliatyList_j = clique_j.getProbabilityList();
        
        for( int s = 0; s < probbiliatyList_i.length; s++ )
        {
            if( probbiliatyList_i[s] != 0 )
            {
                likelihood -= markovNet.getDataList().size() * -probbiliatyList_i[s] * Math.log(probbiliatyList_i[s]) / Math.log(2);
            }
        }
        for( int s = 0; s < probbiliatyList_j.length; s++ )
        {
            if( probbiliatyList_j[s] != 0 )
            {
                likelihood -= markovNet.getDataList().size() * -probbiliatyList_j[s] * Math.log(probbiliatyList_j[s]) / Math.log(2);
            }
        }
        
        // calculate the probabilities of new clique
        BigInteger code = new BigInteger("0");
        code = code.or(clique_i.getCode()).or(clique_j.getCode());
        List<List<SymbolType>> candidateListList = new ArrayList<List<SymbolType>>();
        
        for( int index = 0; index < code.bitLength(); index++ )
        {
            if( code.testBit(index) )
            {
                candidateListList.add( markovNet.getNodeList().get(index).getCandidateList() );
            }
        }
        
        Clique<SymbolType> clique = new Clique<SymbolType>(code, candidateListList, clique_i.getCandidateSize() * clique_j.getCandidateSize());
        
        double[] probabilityArray = new double[clique.getCandidateSize()];
        int count = 0;
        
        for(int[] ind: markovNet.getDataList())
        {
            MarkovNetManager.countDataOnClique(ind, clique, probabilityArray);
            count++;
        }
        for( int probabilityIndex = 0; probabilityIndex < probabilityArray.length; probabilityIndex ++ )
        {
            probabilityArray[probabilityIndex] = probabilityArray[probabilityIndex] / count;
        }
        for( int s = 0; s < probabilityArray.length; s++ )
        {
            //System.out.println( j + " " + probbiliatyList[j] );
            
            if( probabilityArray[s] != 0 )
            {
                likelihood += markovNet.getDataList().size() * -probabilityArray[s] * Math.log(probabilityArray[s]) / Math.log(2);
            }
        }
        
        //clique.setProbabilityList(probabilityArray);
        
        //System.out.println( "MDL " + MDL + " (" + modelComplexity + " + " + likelihood + ")");
        return modelComplexity + likelihood;
    }
    
    /**
     * returns random GpNode by method of PORTS
     * 
     * @return
     */
    public GpNode getRandomSample()
    {
        return mn.sampleNewTree();
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
        env.putAttribute("PPTSignificanceLevel", "0.1");
        env.putAttribute("PPTSmoothingParameter", "0.1");
        env.putAttribute("PPT_MN_order", "root");
        //env.putAttribute("peedSampling", "cliqueBased");
        env.putAttribute("peedSampling", "relatedCliqueBased");
        env.putAttribute("PPT_MN_order", "roulette");

        //env.putAttribute("peedSampling", "dependencyBased");
        
        env.setPopulationSize(8);
        
        // create sample tree
        GpNode tree1 = GpTreeManager.constructGpNodeFromString("(A (B X X) (C Y Y))", symbolSet);
        GpNode tree2 = GpTreeManager.constructGpNodeFromString("(A (B Y Y) (B Y X))", symbolSet);
        GpNode tree3 = GpTreeManager.constructGpNodeFromString("(B (A X X) (A X Y))", symbolSet);
        GpNode tree4 = GpTreeManager.constructGpNodeFromString("(B (C X Y) (C Y Y))", symbolSet);
        System.out.println( GpTreeManager.getS_Expression(tree1) );
        System.out.println( GpTreeManager.getS_Expression(tree2) );
        System.out.println( GpTreeManager.getS_Expression(tree3) );
        System.out.println( GpTreeManager.getS_Expression(tree4) );
        //GpNode tree5 = GpTreeManager.constructGpNodeFromString("(C (C Y Y) (B Y X))", symbolSet);
        
        List<GpIndividual> population = new ArrayList<GpIndividual>();
        population.add( new GpIndividual(tree1) );
        population.add( new GpIndividual(tree2) );
        population.add( new GpIndividual(tree3) );
        population.add( new GpIndividual(tree4) );
        //population.add( new GpIndividual(tree5) );
        //population.add( new GpIndividual(tree6) );
        //population.add( new GpIndividual(tree7) );
        //population.add( new GpIndividual(tree8) );
        //population.add( new GpIndividual(tree5) );
        
        ECGP ecgp = new ECGP(population, env);
        
        System.out.println(" sampled tree = " + GpTreeManager.getS_Expression( ecgp.getRandomSample() ));
        System.out.println(" sampled tree = " + GpTreeManager.getS_Expression( ecgp.getRandomSample() ));
        System.out.println(" sampled tree = " + GpTreeManager.getS_Expression( ecgp.getRandomSample() ));
        System.out.println(" sampled tree = " + GpTreeManager.getS_Expression( ecgp.getRandomSample() ));
        System.out.println(" sampled tree = " + GpTreeManager.getS_Expression( ecgp.getRandomSample() ));
        System.out.println(" sampled tree = " + GpTreeManager.getS_Expression( ecgp.getRandomSample() ));
        
    }
}
