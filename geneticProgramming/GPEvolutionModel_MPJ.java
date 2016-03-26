package geneticProgramming;

import ecCore.EvolutionModel;
import ecCore.selector.AbstractSelector;
import ecCore.selector.TournamentSelector;
import ecCore.selector.TruncateSelector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import mpi.MPI;
import ports.PORTS_Cut;
import random.RandomManager;

/**
 * GP進化のプロトタイプモデル，個々の問題はこのクラスを継承することで簡潔に書ける． 自動でやってくれるところは以下のメソッド． "initialise":　個体の生成・初期化．[initialisation=rampedHalfAndHalf]でramped half & half, 他に[initialisation=full][initialization=grow]など．
 * "updateGeneration":　世代の更新,[method=SGP]でGP,[method=PORTS]でPORTSが使われる． [sizePrint=true]で木構造断片のサイズを出力する． [parallel=true]でMPJによる並列化. [id=x]で並列化の際のidをxで指定する． [np=y]で並列化の際のprocessing elementsの数をyで指定する．
 * 
 * @author tanji
 * @param <T>
 *            Type of Individuals
 * @param <E>
 *            Type of Environment
 */
public class GPEvolutionModel_MPJ<T extends GpIndividual, E extends GpEnvironment<T>> extends EvolutionModel<T, E>
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
    protected List<HashSet<String>> _uniqueStructureList;
    protected boolean _isUniqueStructureRecorded = false;
    protected boolean _isIgnoreContent = false;
    protected int _maximumSizeForRecord = 1000;
    protected boolean _finished = false;
    
    public GPEvolutionModel_MPJ(E environment)
    {
        this((Class<T>) GpIndividual.class, environment);
    }
    
    public GPEvolutionModel_MPJ(Class<T> individualClass, E environment)
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
        if (_environment.getAttribute("isUniqueStructureRecorded") != null)
        {
            _isUniqueStructureRecorded = Boolean.valueOf(_environment.getAttribute("isUniqueStructureRecorded"));
        }
        if (_environment.getAttribute("isIgnoreContent") != null)
        {
            _isIgnoreContent = Boolean.valueOf(_environment.getAttribute("isIgnoreContent"));
        }
    }

    @Override
    public void run()
    {
        long startTime = System.currentTimeMillis();
        // if( _parallel )
        // {
        // run_parallel();
        // System.out.println("Time = " + (System.currentTimeMillis() - startTime));
        // return;
        // }

        initialize();
        for (int i = 0; i < _environment.getRepetitionNumber(); i++)
        {
            System.out.println("***** Generation " + _environment.getGenerationCount() + " ");
            evaluate();
            recordStructure();
            if( _finished || i == _environment.getRepetitionNumber() -1 )
            {
                break;
            }
            updateGeneration();
        }
        _takenTime = System.currentTimeMillis() - startTime;
        finish();
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
        System.out.println("***");
        System.out.println("Average Fitness = " + averageFitness);
        System.out.println("Average NodeSize = " + averageNodeSize);
        System.out.println("Average Depth = " + averageDepth);
        if( bestIndividual != null )
        {
            System.out.println("Best Individual = " + bestIndividual.getFitnessValue() + " : " + GpTreeManager.getS_Expression(bestIndividual.getRootNode()));
        }
    }

    /**
     * 並列処理用のモデル. ID=0のマシンがInitialization,Reproductionを行う．Evaluationだけ並列化.
     */
    public void run_parallel()
    {
        int wholeSize = _environment.getPopulationSize();
        if (_id == 0)
        {
            initialize();
        } else
        {
            _environment.setPopulation(new ArrayList<T>());
        }

        _environment.setPopulationSize(_environment.getPopulationSize() / _size);

        for (int i = 0; i < _environment.getRepetitionNumber(); i++)
        {
            // GpNode[] buf = new GpNode[_environment.getPopulationSize()];
            String[] buf = new String[_environment.getPopulationSize()];
            // GpIndividual[] buf = new GpIndividual[_environment.getPopulationSize()];
            // System.out.println("Scattering id = " + _id);

            String[] populationString = new String[0];
            if (_id == 0)
            {
                // encode population to String
                populationString = new String[wholeSize];
                for (int individualIndex = 0; individualIndex < wholeSize; individualIndex++)
                {
                    populationString[individualIndex] = GpTreeManager.getS_Expression(_environment.getPopulation().get(individualIndex).getRootNode());
                }
                MPI.COMM_WORLD.Scatter(populationString, 0, _environment.getPopulationSize(), MPI.OBJECT, buf, 0, buf.length, MPI.OBJECT, 0);
            } else
            {
                MPI.COMM_WORLD.Scatter(populationString, 0, _environment.getPopulationSize(), MPI.OBJECT, buf, 0, buf.length, MPI.OBJECT, 0);
            }
            // decode String into individual
            List<T> populationFragment = new ArrayList<T>();
            for (int individualIndex = 0; individualIndex < buf.length; individualIndex++)
            {
                T individual = createNewIndividual();
                individual.setRootNode(GpTreeManager.constructGpNodeFromString(buf[individualIndex], _environment.getSymbolSet()));
                populationFragment.add(individual);
            }
            _environment.setPopulation(populationFragment);

            // parallel evaluation
            evaluate();
            // System.out.println("Evaluation was done " + _id);

            if (_id == 0)
            {
                GpIndividual[] wholePopulation = new GpIndividual[wholeSize];
                MPI.COMM_WORLD.Gather(_environment.getPopulation().toArray(), 0, _environment.getPopulationSize(), MPI.OBJECT, wholePopulation, 0, _environment.getPopulationSize(), MPI.OBJECT, 0);
                _environment.setPopulation((List<T>) Arrays.asList(wholePopulation));
                // System.out.println("Gather " + _id);
                updateGeneration();
                // System.out.println("Updated " + _id + " " + _environment.getPopulation());
            } else
            {
                MPI.COMM_WORLD.Gather(_environment.getPopulation().toArray(), 0, _environment.getPopulationSize(), MPI.OBJECT, null, 0, _environment.getPopulationSize(), MPI.OBJECT, 0);
                // System.out.println("Gather " + _id);
            }
            _environment.setGenerationCount(_environment.getGenerationCount() + 1);
        }
        finish();
        MPI.Finalize();
    }

    @Override
    public void evaluate()
    {
    }
    
    @Override
    public void initialize()
    {
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
                GpNode individualRoot = GpTreeManager.grow(_environment);
                individual.setRootNode(individualRoot);
                population.add(individual);
            }
            _environment.setPopulation(population);
        }
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
        } else
        {
            System.out.println("One evolution method such as \"SGP\" or \"PORTS\" must be specified.");
            System.exit(0);
        }
        _environment.setGenerationCount(_environment.getGenerationCount() + 1);
        long endTime = System.currentTimeMillis();
        System.out.println("# update time = " + (endTime - startTime));
    }

    public void updateGeneration_SGP_DPP()
    {
        if (_id == 0)
        {

        }
        // MPI.COMM_WORLD.Gather(sendbuf, sendoffset, sendcount, sendtype, recvbuf, recvoffset, recvcount, recvtype, root);

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
            nextPopulation.add(bestIndividual);
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
            nextPopulation.add(bestIndividual);
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

	@Override
	public void evaluateIndividual(T individual)
	{
		
	}
}
