package geneticAlgorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import ecCore.EvolutionModel;
import ecCore.selector.AbstractSelector;
import ecCore.selector.TournamentSelector;
import ecCore.selector.TruncateSelector;
import geneticProgramming.GpNode;
import geneticProgramming.GpTreeManager;

/**
 * GA進化のプロトタイプモデル，個々の問題はこのクラスを継承することで簡潔に書ける． 自動でやってくれるところは以下のメソッド． "initialise":　個体の生成・初期化．
 * "updateGeneration":　世代の更新,[method=SGP]でGP
 * @author tanji
 * @param <T>
 *            Type of Individuals
 * @param <E>
 *            Type of Environment
 */
public class GAEvolutionModel<T extends GaIndividual<Number>, E extends GaEnvironment<T>> extends EvolutionModel<T, E>
{
    protected Class<T> _individualClass;
    protected int generationForAuto = 10;
    protected int sizeOfIEC = 10;
        
    protected List<Double> _offspringSizeList;
    protected boolean _isIndividualPrint = false;
    protected int _maximumSizeForRecord = 1000;
    
    public GAEvolutionModel(E environment)
    {
        this((Class<T>) GaIndividual.class, environment);
    }
    
    public GAEvolutionModel(Class<T> individualClass, E environment)
    {
        super(environment);
        _individualClass = individualClass;
        _environment = environment;
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
    
    /** records and prints the size of unique structures for each node size */
    protected void recordStructure()
    {
    	double averageFitness = 0;
        for( GaIndividual ind: _environment.getPopulation() )
        {
            averageFitness += ind.getFitnessValue();
        }
        averageFitness = averageFitness / _environment.getPopulationSize();
        
        System.out.println("***");
        System.out.println("Average Fitness = " + averageFitness);
        if( bestIndividual != null )
        {
        	System.out.println("Best Individual = " + bestIndividual.getFitnessValue() + " : " + bestIndividual.getGene());
        }
    }
    
    @Override
    public void evaluate()
    {
        bestIndividual = _environment.getPopulation().get(0);
        double averageFitness = 0;
        for (T individual : _environment.getPopulation())
        {
            evaluationCount++;
            
            // evaluation
            evaluateIndividual(individual);
            
            if( _isIndividualPrint )
            {
            	System.out.println( individual.getGene() );
            }
            
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
                    //GpNode root = GpTreeManager.constructGpNodeFromString(line, _environment.getSymbolSet());
                    //GpTreeManager.calculateDepth( root, 1 );
                    //_environment.getPopulation().get(index++).setRootNode( root );
                }
            }catch(Exception e){ e.printStackTrace(); }
        }
        
        
        List<T> population = new ArrayList<T>();
        for (int p = index; p < _environment.getPopulationSize(); p++)
        {
        	T individual = createNewIndividual();
        	List<Number> gene = new ArrayList<Number>();
        	individual.setGene(gene);
        	population.add(individual);
        }
        _environment.setPopulation(population);
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
        if (_environment.getAttribute("method").equals("GA"))
        {
            updateGeneration_SGP();
        }
        _environment.setGenerationCount(_environment.getGenerationCount() + 1);
        long endTime = System.currentTimeMillis();
        System.out.println("# update time = " + (endTime - startTime));
    }
    
    public void updateGeneration_SGP()
    {
    }
    
    public void finish()
    {
        super.finish();
    }
}
