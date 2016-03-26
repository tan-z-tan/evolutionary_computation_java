package geneticProgramming;

import ecCore.EvolutionModel;
import ecCore.selector.AbstractSelector;
import ecCore.selector.TournamentSelector;
import ecCore.selector.TruncateSelector;
import ecgp.ECGP;
import geneticProgramming.symbols.PPT_Symbol;
import geneticProgramming.symbols.SymbolL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import markovNet.MarkovNetManager;
import perce.PERCE;
import pipe.PIPE;
import ports.PORTS_Cut;
import random.RandomManager;

/**
 * GP進化のプロトタイプモデル，個々の問題はこのクラスを継承することで簡潔に書ける． 自動でやってくれるところは以下のメソッド． "initialise":　個体の生成・初期化．[initialisation=rampedHalfAndHalf]でramped half & half, 他に[initialisation=full][initialization=grow]など．
 * "updateGeneration":　世代の更新,[method=SGP]でGP,[method=PORTS]でPORTSが使われる． [sizePrint=true]で木構造断片のサイズを出力する．
 * @author tanji
 * @param <T>
 *            Type of Individuals
 * @param <E>
 *            Type of Environment
 */
public class GPEvolutionModel<T extends GpIndividual, E extends GpEnvironment<T>> extends EvolutionModel<T, E>
{
    protected Class<T> _individualClass;
    // protected T bestIndividual;
    protected int generationForAuto = 10;
    protected int sizeOfIEC = 10;
        
    protected List<Double> _offspringSizeList;
    protected double _averageFragmentSize;
    protected double _averageTreeSize = 0;
    protected double _averageTransitionCount;
    protected PORTS_Cut _ports;
    protected PERCE _perce;
    protected List<HashSet<String>> _uniqueStructureList;
    protected boolean _isPPT = false;
    protected boolean _isUniqueStructureRecorded = false;
    protected boolean _isIgnoreContent = false;
    protected boolean _isEntropyRecord = false;
    protected boolean _isIndividualPrint = false;
    protected int _maximumSizeForRecord = 1000;
    
    public GPEvolutionModel(E environment)
    {
        this((Class<T>) GpIndividual.class, environment);
    }
    
    public GPEvolutionModel(Class<T> individualClass, E environment)
    {
        super(environment);
        _individualClass = individualClass;
        _environment = environment;
        _uniqueStructureList = new ArrayList<HashSet<String>>();
        for( int i = 0; i < _maximumSizeForRecord; i++ )
        {
            _uniqueStructureList.add(new HashSet<String>());
        }
        readAttributes();
    }

    @Override
    protected void readAttributes()
    {
        super.readAttributes();
        if (_environment.getAttribute("tournamentSize") != null)
        {
            _tournamentSize = Integer.valueOf(_environment.getAttribute("tournamentSize"));
        }
        if (_environment.getAttribute("PPT") != null)
        {
            _isPPT = Boolean.valueOf(_environment.getAttribute("PPT"));
        }
        if (_environment.getAttribute("isUniqueStructureRecorded") != null)
        {
            _isUniqueStructureRecorded = Boolean.valueOf(_environment.getAttribute("isUniqueStructureRecorded"));
        }
        if (_environment.getAttribute("isIgnoreContent") != null)
        {
            _isIgnoreContent = Boolean.valueOf(_environment.getAttribute("isIgnoreContent"));
        }
        if (_environment.getAttribute("isEntropyRecord") != null)
        {
            _isEntropyRecord = Boolean.valueOf(_environment.getAttribute("isEntropyRecord"));
        }
        if (_environment.getAttribute("isIndividualPrint") != null)
        {
            _isIndividualPrint = Boolean.valueOf(_environment.getAttribute("isIndividualPrint"));
        }
    }

    @Override
    public void run()
    {
        System.out.println("*** Experiment Parameters ");
        System.out.println("*** " + _environment.getAttributes());
        long startTime = System.currentTimeMillis();
        long cummulativeEvaluationTime = 0;
        initialize();
        for (int i = 0; i < _environment.getRepetitionNumber(); i++)
        {
            System.out.println("***** Generation " + _environment.getGenerationCount() + " ");
            
            long start = System.currentTimeMillis();
            evaluate();
            //cummulativeEvaluationTime += System.currentTimeMillis() - start;
            cummulativeEvaluationTime = System.currentTimeMillis() - start;
            System.out.println("### Evaluation time = " + cummulativeEvaluationTime);
            recordStructure();
            if( _finished || i == _environment.getRepetitionNumber() -1 )
            {
                break;
            }
            start = System.currentTimeMillis();
            updateGeneration();
            cummulativeEvaluationTime = System.currentTimeMillis() - start;
            System.out.println("### Update time = " + cummulativeEvaluationTime);
        }
        _takenTime = System.currentTimeMillis() - startTime;
        
        finish();
    }
    
    private GpNode convertPPT_to_normalForm(GpNode node)
    {
        return GpTreeManager.trimL(node);
    }

    /** records and prints the size of unique structures for each node size */
    protected void recordStructure()
    {
        double averageNodeSize = 0;
        double averageDepth = 0;
        double averageFitness = 0;
        for( GpIndividual ind: _environment.getPopulation() )
        {
            String s_expression = GpTreeManager.getS_Expression(ind.getRootNode());
            if( _isIgnoreContent )
            {
                s_expression = s_expression.replaceAll("[a-zA-Z|\\d]+", "X");
            }
            averageDepth += ind.getRootNode().getDepthFromHere();
            averageFitness += ind.getFitnessValue();
            
            int nodeSize = GpTreeManager.getNodeSize(ind.getRootNode());
            averageNodeSize += nodeSize;
            if( nodeSize < _maximumSizeForRecord )
            {
                HashSet<String> structureSet = _uniqueStructureList.get(nodeSize);
                structureSet.add(s_expression);
            }
        }
        averageDepth = averageDepth / _environment.getPopulationSize();
        averageFitness = averageFitness / _environment.getPopulationSize();
        averageNodeSize = averageNodeSize / _environment.getPopulationSize();
        
        if( _isUniqueStructureRecorded )
        {
            System.out.println("*** unique structures");
            for(int i = 0; i < _maximumSizeForRecord; i++)
            {
                HashSet<String> set = _uniqueStructureList.get(i);
                System.out.println(i + " " + set.size());
            }
        }
        if( _isEntropyRecord )
        {
        	for( T individual: _environment.getPopulation() )
        	{
        		GpNode root = individual.getRootNode();
        		List<GpNode> bfs = GpTreeManager.breadthFirstSearch(root);
        		// TODO
        	}
        	// calculate entropy of PPT nodes
        }
        System.out.println("***");
        System.out.println("Average Fitness = " + averageFitness);
        System.out.println("Average NodeSize = " + averageNodeSize);
        System.out.println("Average Depth = " + averageDepth);
        if( bestIndividual != null )
        {
            System.out.println("Best Individual = " + bestIndividual.getFitnessValue() + " : " + GpTreeManager.getS_Expression(GpTreeManager.trimL(bestIndividual.getRootNode())));
        	if( this._isPPT )
        	{
        		System.out.println("Original Solution = " + GpTreeManager.getS_Expression(bestIndividual.getRootNode()));
        	}
        }
    }
    
    @Override
    public void evaluate()
    {
        bestIndividual = _environment.getPopulation().get(0);
        double averageDepth = 0;
        double averageFitness = 0;
        for (T individual : _environment.getPopulation())
        {
            evaluationCount++;
            
            // evaluation
            //if( _environment.getAttributes().containsKey("PPT") && Boolean.valueOf(_environment.getAttribute("PPT")) )
            if( _isPPT )
            {
            	GpNode originalNode = individual.getRootNode();
                //System.out.println("PPT " + GpTreeManager.getS_Expression(individual.getRootNode()));
                GpNode evaluateNode = convertPPT_to_normalForm(originalNode);
                individual.setRootNode(evaluateNode);
                evaluateIndividual(individual);
                individual.setRootNode(originalNode);
            }
            else
            {
                evaluateIndividual(individual);
                //System.out.println(individual.getFitnessValue() + " " + GpTreeManager.getS_Expression(individual.getRootNode()));
            }
            
            if( _isIndividualPrint )
            {
            	System.out.println( GpTreeManager.getS_Expression(individual.getRootNode()) );
            }
            
            averageDepth += individual.getRootNode().getDepthFromHere();
            averageFitness += individual.getFitnessValue();
            
            if( _environment.getSelectionOrder() && bestIndividual.getFitnessValue() < individual.getFitnessValue() )
            {
                bestIndividual = individual;
            }
            else if( !_environment.getSelectionOrder() && bestIndividual.getFitnessValue() > individual.getFitnessValue() )
            {
                bestIndividual = individual;
            }
        }
        if( isTerminal(bestIndividual) )
        {
            System.out.println("success!");
            _finished = true;
        }
    }
    
    /**
     * Override this method to implements own GP system.
     * @param individual
     */
    public void evaluateIndividual(T individual)
    {
        
    }
    
    /** subclass must implement this method. */
    public boolean isTerminal(T bestIndividual)
    {
        return false;
    }
    
    @Override
    public void initialize()
    {
        if( _environment.getAttributes().containsKey("PPT") && Boolean.valueOf(_environment.getAttributes().get("PPT")) )
        {
        	convertSymbolTypes();
        	System.out.println("converted");
        }
        
        _environment.setPopulation(new ArrayList<T>());
        int index = 0;
        // read the first population if it is specified.
        if( _environment.getAttribute("initialPopulation") != null )
        {
            System.out.println("initial population");
            File file = new File(_environment.getAttribute("initialPopulation"));
            System.out.println("File = " + file);
            try{
                BufferedReader reader = new BufferedReader(new FileReader(file));
                while(reader.ready() && _environment.getPopulation().size() < _environment.getPopulationSize())
                {
                    String line = reader.readLine();
                    _environment.getPopulation().add(createNewIndividual());
                    GpNode root = GpTreeManager.constructGpNodeFromString(line, _environment.getSymbolSet());
                    GpTreeManager.calculateDepth( root, 1 );
                    _environment.getPopulation().get(index++).setRootNode( root );
                }
            }catch(Exception e){ e.printStackTrace(); }
        }
        // ramped half & half
        if (_environment.getAttribute("initialization").equals("rampedHalfAndHalf"))
        {
            List<GpNode> genotypes = GpTreeManager.rampedHalfAndHalf(_environment);
            if (genotypes.size() != _environment.getPopulationSize())
            {
                System.out.println(genotypes.size());
                System.out.println(_environment.getPopulationSize());
                System.out.println("サイズが違いますよ．Ramped-half-and-half");
                System.exit(0);
            }
            for (int i = index; i < _environment.getPopulationSize(); i++)
            {
                _environment.getPopulation().add(createNewIndividual());
                _environment.getPopulation().get(i).setRootNode(genotypes.get(i));
                //System.out.println("i = " + i);
            }
        }
        // full
        else if (_environment.getAttribute("initialization").equals("full"))
        {
            List<T> population = new ArrayList<T>();
            for (int p = index; p < _environment.getPopulationSize(); p++)
            {
                T individual = createNewIndividual();
                GpNode individualRoot = GpTreeManager.full(_environment);
                if( _environment.getAttributes().containsKey("PPT") && Boolean.valueOf(_environment.getAttribute("PPT")) )
                {
                    individualRoot = GpTreeManager.full_PPT(_environment, _environment.getNumberOfMaxInitialDepth(), _environment.getNumberOfMaxDepth()); 
                }
                individual.setRootNode(individualRoot);
                population.add(individual);
            }
            _environment.setPopulation(population);
        } else
        // grow
        {
            List<T> population = new ArrayList<T>();
            for (int i = index; i < _environment.getPopulationSize(); i++)
            {
                T individual = createNewIndividual();
                GpNode individualRoot;
                if( _environment.getAttributes().containsKey("PPT") && Boolean.valueOf(_environment.getAttribute("PPT")) )
                {
                    individualRoot = GpTreeManager.grow_PPT(_environment); 
                }
                else
                {
                    individualRoot = GpTreeManager.grow(_environment);
                }
                individual.setRootNode(individualRoot);
                population.add(individual);
            }
            _environment.setPopulation(population);
        }
        
        // if PPT, add SymbolL to environment. Then replace all "L".
        if( _environment.getAttributes().containsKey("PPT") && Boolean.valueOf(_environment.getAttributes().containsKey("PPT")) )
        {
            SymbolL L = new SymbolL("L", Integer.valueOf(_environment.getAttribute("PPTArity")));
            _environment.getSymbolSet().addSymbol( L );
            //int i = 0;
            for( GpIndividual ind: _environment.getPopulation() )
            {
                //System.out.println(i++ + " " + GpTreeManager.getS_Expression(ind.getRootNode()));
                replaceAll_L(ind.getRootNode(), L);
            }
        }
    }
    
    private void replaceAll_L(GpNode node, SymbolL L)
    {
        if( node.getNodeType().getSymbolName().equals("L") )
        {
            node.setNodeType(L);
        }
        for( GpNode child: node.getChildren() )
        {
            replaceAll_L(child, L);
        }
    }

    /** 定義されているSymbolTypeを全てPPT_Symbolに変換する．Arityを合わせるのが目的．また，SymbolLを追加する．
     */
    private void convertSymbolTypes()
    {
        System.out.println("convert symbol types to PPT symbols");
        GpSymbolSet PPT_set = new GpSymbolSet();
        int arity = Integer.valueOf(_environment.getAttribute("PPTArity"));
        
        for( int i = 0; i < _environment.getSymbolSet().getSymbolSize(); i++ )
        {
        	PPT_set.addSymbol(new PPT_Symbol(_environment.getSymbolSet().getSymbolList().get(i), arity));
        }
        //PPT_set.addSymbol(new SymbolL("L", arity));
        _environment.setSymbolSet(PPT_set);
    }

    /** creates a new individual using reflection */
    protected T createNewIndividual()
    {
        try
        {
            T newInstance = _individualClass.newInstance();
            return newInstance;
        } catch (InstantiationException e)
        {
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateGeneration()
    {
        long startTime = System.currentTimeMillis();
        if (_environment.getAttribute("method").equals("SGP"))
        {
            updateGeneration_SGP();
        } else if (_environment.getAttribute("method").equals("PORTS"))
        {
            updateGeneration_PORTS();
        } else if (_environment.getAttribute("method").equals("PPT_MN"))
        {
            updateGeneration_PPT_MN();
        } else if (_environment.getAttribute("method").equals("PPT_PERCE") || _environment.getAttribute("method").equals("PPT_PEED") || _environment.getAttribute("method").equals("PERCE"))
        {
            updateGeneration_PPT_PERCE();
        } else if (_environment.getAttribute("method").equals("PPT_ECGP") || _environment.getAttribute("method").equals("ECGP"))
        {
            updateGeneration_PPT_ECGP();
        } else if (_environment.getAttribute("method").equals("PPT_PIPE") || _environment.getAttribute("method").equals("PIPE"))
        {
            updateGeneration_PPT_PIPE();
        } else
        {
            System.out.println("One evolution method such as \"SGP\", \"PORTS\" or \"PPT_MN\"must be specified.");
            System.exit(0);
        }
        _environment.setGenerationCount(_environment.getGenerationCount() + 1);
        long endTime = System.currentTimeMillis();
        System.out.println("# update time = " + (endTime - startTime));
    }
    
    public void updateGeneration_SGP()
    {
        // reproduction
        AbstractSelector<T> selector = new TournamentSelector<T>(_environment.getPopulation(), _tournamentSize, _selectionOrder);
        if (_environment.getAttribute("selector").equals("tournament"))
        {
            selector = new TournamentSelector<T>(_environment.getPopulation(), _tournamentSize, _selectionOrder);
        } else if (_environment.getAttribute("selector").equals("truncation"))
        {
            selector = new TruncateSelector<T>(_environment.getPopulation(), _selectionOrder);
        }
        
        int phenotypeNum = _environment.getPopulationSize();
        List<T> nextPopulation = new ArrayList<T>(phenotypeNum);
        //Map<Integer, Integer> fragmentSizeMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> fragmentSizeMap = new TreeMap<Integer, Integer>();
        for (int i = 0; i < _environment.getEliteSize(); i++)
        {
        	//nextPopulation.add(bestIndividual);
        	T elite = createNewIndividual();
        	elite.setRootNode(bestIndividual.getRootNode());
        	nextPopulation.add(elite);
        }
        for (; nextPopulation.size() < phenotypeNum;)
        {
            if (nextPopulation.size() < (phenotypeNum) * _environment.getCrossoverRatio())
            {
                GpIndividual parentA = selector.getRandomPType();
                GpIndividual parentB = selector.getRandomPType();
                while (parentB == parentA)
                {
                    parentB = (GpIndividual) selector.getRandomPType();
                }

                GpNode[] childrenTree;
                if (_environment.getAttribute("crossover") == null)
                {
                    childrenTree = GpTreeManager.crossover(parentA.getRootNode(), parentB.getRootNode(), _environment);
                } else if (_environment.getAttribute("crossover").equals("normal"))
                {
                    childrenTree = GpTreeManager.crossover(parentA.getRootNode(), parentB.getRootNode(), _environment);
                } else if (_environment.getAttribute("crossover").equals("depth-dependent"))
                {
                    childrenTree = GpTreeManager.crossoverDepthDependent(parentA.getRootNode(), parentB.getRootNode(), _environment);
                } else if (_environment.getAttribute("crossover").equals("90/10"))
                {
                    childrenTree = GpTreeManager.crossover90_10(parentA.getRootNode(), parentB.getRootNode(), _environment);
                } else if (_environment.getAttribute("crossover").equals("depth-fair"))
                {
                    childrenTree = GpTreeManager.crossover(parentA.getRootNode(), parentB.getRootNode(), _environment);
                } else
                {
                    childrenTree = GpTreeManager.crossover(parentA.getRootNode(), parentB.getRootNode(), _environment);
                }

                if (_environment.getAttribute("sizePrint") != null && _environment.getAttribute("sizePrint").equals("true"))
                {
                    List<Integer> fragmentSizeList = GpTreeManager.getSizeOfLastTimeCrossover();
                    sumFragmentSize(fragmentSizeMap, fragmentSizeList);
                }
                
                T childA = createNewIndividual();
                T childB = createNewIndividual();
                childA.setRootNode(childrenTree[0]);
                childB.setRootNode(childrenTree[1]);
                nextPopulation.add(childA);
                if (nextPopulation.size() + 1 < phenotypeNum)
                {
                    nextPopulation.add(childB);
                }
            } else
            {
                T child = createNewIndividual();
                T parent = selector.getRandomPType();
                child.setRootNode(GpTreeManager.mutation(parent.getRootNode(), _environment));
                if (_environment.getAttribute("sizePrint") != null && _environment.getAttribute("sizePrint").equals("true"))
                {
                    List<Integer> fragmentSizeList = GpTreeManager.getSizeOfLastTimeMutation();
                    sumFragmentSize(fragmentSizeMap, fragmentSizeList);
                }

                nextPopulation.add(child);
            }
        }
        _environment.setPopulation(nextPopulation);
        // show size distribution
        if (_environment.getAttribute("sizePrint") != null && _environment.getAttribute("sizePrint").equals("true"))
        {
            System.out.println("*** fragment size");
            for (Entry<Integer, Integer> entry : fragmentSizeMap.entrySet())
            {
                System.out.println(entry.getKey() + " " + entry.getValue());
            }
            System.out.println("***");
        }
    }

    /** fragment のサイズを加算する */
    public void sumFragmentSize(Map<Integer, Integer> sizeMap, List<Integer> fragmentList)
    {
        for (Integer size : fragmentList)
        {
            if (sizeMap.containsKey(size))
            {
                sizeMap.put(size, sizeMap.get(size) + 1);
            } else
            {
                sizeMap.put(size, 1);
            }
        }
    }

    public void updateGeneration_PORTS()
    {
        // reproduction
        // System.out.println("PORTS: Order = " + _selectionOrder);
        AbstractSelector<T> selector = new TournamentSelector<T>(_environment.getPopulation(), _tournamentSize, _selectionOrder);
        if (_environment.getAttribute("selector").equals("tournament"))
        {
            selector = new TournamentSelector<T>(_environment.getPopulation(), _tournamentSize, _selectionOrder);
        } else if (_environment.getAttribute("selector").equals("truncation"))
        {
            selector = new TruncateSelector<T>(_environment.getPopulation(), _selectionOrder);
        }

        List<GpNode> parents = new ArrayList<GpNode>();
        int populationSize = _environment.getPopulationSize();
        List<T> nextPopulation = new ArrayList<T>(populationSize);
        List<T> promisingSolutions = selector.getRandomPTypeList((int) (populationSize * 1));
        for (GpIndividual individual : promisingSolutions)
        {
            if( _environment.getAttribute("portsMutation") != null && RandomManager.getRandom() < Double.valueOf(_environment.getAttribute("portsMutation")) )
            {
                GpNode randomTree = GpTreeManager.grow(_environment, _environment.getNumberOfMaxInitialDepth());
                parents.add(randomTree);
            }
            else
            {
                parents.add(individual.getRootNode());
            }
        }

        // jan. 15, 2009
        double averageAllFragmentSize = 0;
        if (_offspringSizeList != null)
        {
            for (int i = 0; i < _offspringSizeList.size(); i++)
            {
                averageAllFragmentSize += _offspringSizeList.get(i);
            }
            averageAllFragmentSize = averageAllFragmentSize / _offspringSizeList.size();
        }
        _averageFragmentSize = 0;
        if( _offspringSizeList != null )
        {
            System.out.println("Offspring size = " + _offspringSizeList.size());
        }
        for (GpIndividual individual : promisingSolutions)
        {
            parents.add(individual.getRootNode());
            if (_offspringSizeList != null)
            {
                //_averageFragmentSize += _offspringSizeList.get(_environment.getPopulation().indexOf(individual));
            }
        }
        //_averageFragmentSize = _averageFragmentSize / promisingSolutions.size();
        
        // start sampling
        if( _ports == null )
        {
            _ports = new PORTS_Cut(promisingSolutions, _environment);
        }
        _ports.update(promisingSolutions);
        
        int phenotypeNum = _environment.getPopulationSize();
        for (int i = 0; i < _environment.getEliteSize(); i++)
        {
            //nextPopulation.add(bestIndividual);
        	T elite = createNewIndividual();
        	elite.setRootNode(bestIndividual.getRootNode());
        	nextPopulation.add(elite);
        }
        for (; nextPopulation.size() < phenotypeNum;)
        // for (int i = 0; i < _environment.getPopulationSize(); i++)
        {
            GpNode childNode = _ports.getRandomSample();
            while (childNode == null)
            {
                childNode = _ports.getRandomSample();
            }
            T child = createNewIndividual();
            child.setRootNode(childNode);
            nextPopulation.add(child);
        }
        _offspringSizeList = _ports.getOffspringSizeList();
        _averageTreeSize = _ports.getSumOfTreeSize() / (double) nextPopulation.size();
        _averageTransitionCount = _ports.getSumOfTransitionCount() / (double) nextPopulation.size();
        
        // fragment test
        if (_environment.getAttribute("sizePrint") != null && _environment.getAttribute("sizePrint").equals("true"))
        {
            Map<Integer, Integer> fragmentSizeMap = _ports.getAllConstructionSizeMap();
            List<String> fragmentList = new ArrayList<String>();
            StringBuilder str = new StringBuilder();
            for (Entry<Integer, Integer> entry : fragmentSizeMap.entrySet())
            {
                str.append(entry.getKey()).append(" ").append(entry.getValue());
                fragmentList.add(str.toString());
                str.delete(0, str.length());
            }
            //Collections.sort(fragmentList);
            System.out.println("*** fragment size");
            for (String line : fragmentList)
            {
                System.out.println(line);
            }
            System.out.println("***");
            
        }

        // System.out.println("Average Tree Size = " + _averageTreeSize);
        System.out.println("Average Tree Size = " + _averageTreeSize);
        System.out.println("Average Transition Count = " + _averageTransitionCount);
        System.out.println("Average Branch Size = " + _ports.getAverageBranchSize() / _ports.getCutCount());
        _environment.setPopulation(nextPopulation);
    }
    
    /**
     * PPT表現のPERCEで世代交代する
     */
    public void updateGeneration_PPT_PERCE()
    {
        AbstractSelector<T> selector = new TournamentSelector<T>(_environment.getPopulation(), _tournamentSize, _selectionOrder);
        if (_environment.getAttribute("selector").equals("tournament"))
        {
            selector = new TournamentSelector<T>(_environment.getPopulation(), _tournamentSize, _selectionOrder);
        } else if (_environment.getAttribute("selector").equals("truncation"))
        {
            selector = new TruncateSelector<T>(_environment.getPopulation(), _selectionOrder);
        }

        List<GpNode> parents = new ArrayList<GpNode>();
        int populationSize = _environment.getPopulationSize();
        List<T> nextPopulation = new ArrayList<T>(populationSize);
        List<T> promisingSolutions = selector.getRandomPTypeList( (int)(Double.valueOf(_environment.getAttribute("truncationRate")) * _environment.getPopulationSize()) );
        for (GpIndividual individual : promisingSolutions)
        {
            if( _environment.getAttribute("portsMutation") != null && RandomManager.getRandom() < Double.valueOf(_environment.getAttribute("portsMutation")) )
            {
                GpNode randomTree = GpTreeManager.grow(_environment, _environment.getNumberOfMaxInitialDepth());
                parents.add(randomTree);
            }
            else
            {
                parents.add(individual.getRootNode());
            }
        }
        
        // start sampling
        System.out.println("MN construction...");
        //MarkovNetManager mn_manager = new MarkovNetManager(promisingSolutions, _environment);
        if( _perce == null )
        {
            _perce = new PERCE(promisingSolutions, _environment);
        }
        else
        {
        	_perce.update(promisingSolutions);
        }
                
        System.out.println("end.");
        
        int phenotypeNum = _environment.getPopulationSize();
        for (int i = 0; i < _environment.getEliteSize(); i++)
        {
            //nextPopulation.add(bestIndividual);
        	T elite = createNewIndividual();
        	elite.setRootNode(bestIndividual.getRootNode());
        	nextPopulation.add(elite);
        }
        System.out.println(" sampling start...");
        for (; nextPopulation.size() < phenotypeNum;)
        {
            //System.out.println("sampling " + nextPopulation.size());
            //GpNode childNode = mn_manager.sampleNewTree();
        	GpNode childNode = _perce.getRandomSample();
            T child = createNewIndividual();
            child.setRootNode(childNode);
            nextPopulation.add(child);
        }
        System.out.println(" sampling end.");
        
        // System.out.println("Average Tree Size = " + _averageTreeSize);
        System.out.println("Average Tree Size = " + _averageTreeSize);
        _environment.setPopulation(nextPopulation);
    }
    
    /**
     * PPT表現のPIPEで世代交代する
     */
    public void updateGeneration_PPT_PIPE()
    {
        AbstractSelector<T> selector = new TournamentSelector<T>(_environment.getPopulation(), _tournamentSize, _selectionOrder);
        if (_environment.getAttribute("selector").equals("tournament"))
        {
            selector = new TournamentSelector<T>(_environment.getPopulation(), _tournamentSize, _selectionOrder);
        } else if (_environment.getAttribute("selector").equals("truncation"))
        {
            selector = new TruncateSelector<T>(_environment.getPopulation(), _selectionOrder);
        }

        List<GpNode> parents = new ArrayList<GpNode>();
        int populationSize = _environment.getPopulationSize();
        List<T> nextPopulation = new ArrayList<T>(populationSize);
        List<T> promisingSolutions = selector.getRandomPTypeList( (int)(Double.valueOf(_environment.getAttribute("truncationRate")) * _environment.getPopulationSize()) );
        for (GpIndividual individual : promisingSolutions)
        {
            if( _environment.getAttribute("portsMutation") != null && RandomManager.getRandom() < Double.valueOf(_environment.getAttribute("portsMutation")) )
            {
                GpNode randomTree = GpTreeManager.grow(_environment, _environment.getNumberOfMaxInitialDepth());
                parents.add(randomTree);
            }
            else
            {
                parents.add(individual.getRootNode());
            }
        }
        
        // start sampling
        System.out.println("MN construction...");
        PIPE pipe= new PIPE(promisingSolutions, _environment);
        System.out.println("end");
        
        int phenotypeNum = _environment.getPopulationSize();
        for (int i = 0; i < _environment.getEliteSize(); i++)
        {
            //nextPopulation.add(bestIndividual);
        	T elite = createNewIndividual();
        	elite.setRootNode(bestIndividual.getRootNode());
        	nextPopulation.add(elite);
        }
        System.out.println(" sampling start...");
        for (; nextPopulation.size() < phenotypeNum;)
        {
            //System.out.println("sampling " + nextPopulation.size());
            //GpNode childNode = mn_manager.sampleNewTree();
            GpNode childNode = pipe.getRandomSample();
            T child = createNewIndividual();
            child.setRootNode(childNode);
            nextPopulation.add(child);
        }
        System.out.println(" sampling end.");
        
        // System.out.println("Average Tree Size = " + _averageTreeSize);
        System.out.println("Average Tree Size = " + _averageTreeSize);
        _environment.setPopulation(nextPopulation);
    }
    
    /**
     * PPT表現のECGPで世代交代する
     */
    public void updateGeneration_PPT_ECGP()
    {
        AbstractSelector<T> selector = new TournamentSelector<T>(_environment.getPopulation(), _tournamentSize, _selectionOrder);
        if (_environment.getAttribute("selector").equals("tournament"))
        {
            selector = new TournamentSelector<T>(_environment.getPopulation(), _tournamentSize, _selectionOrder);
        } else if (_environment.getAttribute("selector").equals("truncation"))
        {
            selector = new TruncateSelector<T>(_environment.getPopulation(), _selectionOrder);
        }

        List<GpNode> parents = new ArrayList<GpNode>();
        int populationSize = _environment.getPopulationSize();
        List<T> nextPopulation = new ArrayList<T>(populationSize);
        List<T> promisingSolutions = selector.getRandomPTypeList( (int)(Double.valueOf(_environment.getAttribute("truncationRate")) * _environment.getPopulationSize()) );
        for (GpIndividual individual : promisingSolutions)
        {
            if( _environment.getAttribute("portsMutation") != null && RandomManager.getRandom() < Double.valueOf(_environment.getAttribute("portsMutation")) )
            {
                GpNode randomTree = GpTreeManager.grow(_environment, _environment.getNumberOfMaxInitialDepth());
                parents.add(randomTree);
            }
            else
            {
                parents.add(individual.getRootNode());
            }
        }
        
        // start sampling
        System.out.println("MN construction...");
        ECGP ecgp = new ECGP(promisingSolutions, _environment);
        System.out.println("end");
        
        int phenotypeNum = _environment.getPopulationSize();
        for (int i = 0; i < _environment.getEliteSize(); i++)
        {
            //nextPopulation.add(bestIndividual);
        	T elite = createNewIndividual();
        	elite.setRootNode(bestIndividual.getRootNode());
        	nextPopulation.add(elite);
        }
        System.out.println(" sampling start...");
        for (; nextPopulation.size() < phenotypeNum;)
        {
            //System.out.println("sampling " + nextPopulation.size());
            //GpNode childNode = mn_manager.sampleNewTree();
            GpNode childNode = ecgp.getRandomSample();
            T child = createNewIndividual();
            child.setRootNode(childNode);
            nextPopulation.add(child);
        }
        System.out.println(" sampling end.");
        
        // System.out.println("Average Tree Size = " + _averageTreeSize);
        System.out.println("Average Tree Size = " + _averageTreeSize);
        _environment.setPopulation(nextPopulation);
    }
    
    /**
     * PPT表現のマルコフネットで世代交代する
     */
    public void updateGeneration_PPT_MN()
    {
        // System.out.println("PPT_MN: Order = " + _selectionOrder);
        AbstractSelector<T> selector = new TournamentSelector<T>(_environment.getPopulation(), _tournamentSize, _selectionOrder);
        if (_environment.getAttribute("selector").equals("tournament"))
        {
            selector = new TournamentSelector<T>(_environment.getPopulation(), _tournamentSize, _selectionOrder);
        } else if (_environment.getAttribute("selector").equals("truncation"))
        {
            selector = new TruncateSelector<T>(_environment.getPopulation(), _selectionOrder);
        }
        
        List<GpNode> parents = new ArrayList<GpNode>();
        int populationSize = _environment.getPopulationSize();
        List<T> nextPopulation = new ArrayList<T>(populationSize);
        List<T> promisingSolutions = selector.getRandomPTypeList( (int)(Double.valueOf(_environment.getAttribute("truncationRate")) * _environment.getPopulationSize()) );
        for (GpIndividual individual : promisingSolutions)
        {
            if( _environment.getAttribute("portsMutation") != null && RandomManager.getRandom() < Double.valueOf(_environment.getAttribute("portsMutation")) )
            {
                GpNode randomTree = GpTreeManager.grow(_environment, _environment.getNumberOfMaxInitialDepth());
                parents.add(randomTree);
            }
            else
            {
                parents.add(individual.getRootNode());
            }
        }
        
        // start sampling
        System.out.println("MN construction...");
        MarkovNetManager mn_manager = new MarkovNetManager(promisingSolutions, _environment);
        mn_manager.extractDependencyEdge_chiSquare();
        mn_manager.extractCliqueList();
        mn_manager.calculateProbabilityTable();
        
        System.out.println("end.");
        
        int phenotypeNum = _environment.getPopulationSize();
        for (int i = 0; i < _environment.getEliteSize(); i++)
        {
            //nextPopulation.add(bestIndividual);
        	T elite = createNewIndividual();
        	elite.setRootNode(bestIndividual.getRootNode());
        	nextPopulation.add(elite);
        }
        System.out.println(" sampling start...");
        for (; nextPopulation.size() < phenotypeNum;)
        {
            //System.out.println("sampling " + nextPopulation.size());
            GpNode childNode = mn_manager.sampleNewTree();
            T child = createNewIndividual();
            child.setRootNode(childNode);
            nextPopulation.add(child);
        }
        System.out.println(" sampling end.");
        
        // System.out.println("Average Tree Size = " + _averageTreeSize);
        System.out.println("Average Tree Size = " + _averageTreeSize);
        _environment.setPopulation(nextPopulation);
    }
    
    public void finish()
    {
        super.finish();
    }
}
