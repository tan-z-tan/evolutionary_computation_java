package differentialEvolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.omg.CORBA._PolicyStub;

import random.RandomManager;
import ecCore.EvolutionModel;
import ecCore.selector.AbstractSelector;
import ecCore.selector.TournamentSelector;
import ecCore.selector.TruncateSelector;

/**
 * GA進化のプロトタイプモデル，個々の問題はこのクラスを継承することで簡潔に書ける． 自動でやってくれるところは以下のメソッド． "initialise":　個体の生成・初期化．
 * "updateGeneration":　世代の更新,[method=SGP]でGP
 * @author tanji
 * @param <T>
 *            Type of Individuals
 * @param <E>
 *            Type of Environment
 */
public class DEEvolutionModel<T extends DEIndividual, E extends DEEnvironment<T>> extends EvolutionModel<T, E>
{
	private static double scalingRate = 0.1;
	private static double columnCrossoverRate = 0.5;
	
    protected Class<T> _individualClass;
    protected int generationForAuto = 10;
    protected int geneSize = 0;
    protected double initialMin= -1;
    protected double initialMax= 1;
    protected double valueMin= -100000;
    protected double valueMax= 100000;
    
    protected boolean _isIndividualPrint = false;
        
    public DEEvolutionModel(E environment)
    {
        this((Class<T>) DEIndividual.class, environment);
    }
    
    public DEEvolutionModel(Class<T> individualClass, E environment)
    {
        super(environment);
        System.out.println( "IndividualClass" );
        System.out.println( individualClass );
        System.out.println( (Class<T>) DEIndividual.class );
        
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
        if (_environment.getAttribute("initialMin") != null)
        {
            initialMin = Double.valueOf(_environment.getAttribute("initialMin"));
        }
        if (_environment.getAttribute("initialMax") != null)
        {
            initialMax = Double.valueOf(_environment.getAttribute("initialMax"));
        }
        if (_environment.getAttribute("valueMin") != null)
        {
        	valueMin = Double.valueOf(_environment.getAttribute("valueMin"));
        }
        if (_environment.getAttribute("valueMax") != null)
        {
        	valueMax = Double.valueOf(_environment.getAttribute("valueMax"));
        }
        
        geneSize = _environment.getChromosomeLength();
    }

    @Override
    public void run()
    {
        System.out.println("*** Experiment Parameters ");
        System.out.println("*** " + _environment.getAttributes());
        long startTime = System.currentTimeMillis();
        initialize();
        
        evaluate();
        
        for (int i = 0; i < _environment.getRepetitionNumber(); i++)
        {
        	//System.out.println("***** Generation " + _environment.getGenerationCount() + " ");
            
        	//cummulativeEvaluationTime = System.currentTimeMillis() - start;
            //System.out.println("### Evaluation time = " + cummulativeEvaluationTime);
            //recordStructure();
            if( _finished || i == _environment.getRepetitionNumber() -1 )
            {
                break;
            }
            //long start = System.currentTimeMillis();
            updateGeneration();
            //cummulativeEvaluationTime = System.currentTimeMillis() - start;
            //System.out.println("### Update time = " + cummulativeEvaluationTime);
        }
        _takenTime = System.currentTimeMillis() - startTime;
        
        finish();
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
		System.out.println("Best Individual " + bestIndividual.getFitnessValue());
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
    
    public int getGeneSize()
	{
		return geneSize;
	}

	public void setGeneSize(int geneSize)
	{
		this.geneSize = geneSize;
	}

	@Override
    public void initialize()
    {
        _environment.setPopulation(new ArrayList<T>());
        int index = 0;
        
        List<T> population = new ArrayList<T>();
        for (int p = index; p < _environment.getPopulationSize(); p++)
        {
        	T individual = createNewIndividual();
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
            newInstance.setGene( MatrixUtils.createRealVector(new double[this.geneSize]) );
            for( int i = 0; i < this.geneSize; i++ )
            {
            	newInstance.getGene().setEntry(i, RandomManager.getRandom(initialMax, initialMin));;
            }
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
    	List<Integer> alreadySelected = new ArrayList<Integer>();
		int index_A = randomSelect(0, _environment.getPopulationSize(), alreadySelected);
		alreadySelected.add(index_A);
		int index_B = randomSelect(0, _environment.getPopulationSize(), alreadySelected);
		alreadySelected.add(index_B);
		int index_C = randomSelect(0, _environment.getPopulationSize(), alreadySelected);
		alreadySelected.add(index_C);
		int index_D = randomSelect(0, _environment.getPopulationSize(), alreadySelected);
		
		RealVector mutatedMatrix = mutation(_environment.getPopulation().get(index_A), _environment.getPopulation().get(index_B), _environment.getPopulation().get(index_C));
		RealVector newGene = crossover(mutatedMatrix, _environment.getPopulation().get(index_D).getGene());
		
		T newIndividual = createNewIndividual();
		newIndividual.setGene(newGene);
		evaluateIndividual(newIndividual);
		//System.out.println(newIndividual.getFitnessValue() + " " + newGene);
		double newFitness = newIndividual.getFitnessValue();
		
		if( _environment.getSelectionOrder() && _environment.getPopulation().get(index_D).getFitnessValue() <= newFitness )
		{
			_environment.getPopulation().set(index_D, newIndividual);
			if( bestIndividual.getFitnessValue() <= newFitness )
			{
				bestIndividual = newIndividual;
				System.out.println(_environment.getGenerationCount() + " Best Individual Updated " + bestIndividual.getFitnessValue());
			}
		} else if( (!_environment.getSelectionOrder()) && _environment.getPopulation().get(index_D).getFitnessValue() >= newFitness )
		{
			_environment.getPopulation().set(index_D, newIndividual);
			if( bestIndividual.getFitnessValue() >= newFitness )
			{
				bestIndividual = newIndividual;
				System.out.println( _environment.getGenerationCount() + " Best Individual Updated " + bestIndividual.getFitnessValue());
			}
		}
		_environment.setGenerationCount( _environment.getGenerationCount() + 1);
    }
    
	/** 選択されていないランダムなインデックスを返す．*/
	public static int randomSelect(int start, int end, List<Integer> alreadySelected)
	{
		int index = RandomManager.getRandom(start, end);
		while( alreadySelected.contains(index) )
		{
			index = RandomManager.getRandom(start, end);
		}
		
		return index;
	}
	
	/**
	 * 突然変異オペレータ
	 * @return
	 */
	public static RealVector mutation(DEIndividual parentA, DEIndividual parentB, DEIndividual parentC)
	{
		return parentA.getGene().add( parentB.getGene().subtract(parentC.getGene()).mapMultiply(scalingRate) );
	}
	
	/**
	 * 交差オペレータ
	 * @return
	 */
	public static RealVector crossover(RealVector mutatedVector, RealVector targetParent)
	{
		RealVector targetVector = targetParent.copy();
		int columnSize = targetVector.getDimension();
		int columnMutateLength = exponentialRandomLength(columnCrossoverRate);
		int columnMutateIndex = RandomManager.getRandom(0, columnSize);
		columnMutateLength = Math.min(columnMutateLength, columnSize);
		
		for( int j = 0; j < columnMutateLength; j++ )
		{
			int index_j = (columnMutateIndex + j) % columnSize;
			targetVector.setEntry(index_j, mutatedVector.getEntry(index_j) );
		}
		
		return targetVector;
	}
	
	public static int exponentialRandomLength(double rate)
	{
		if( rate >= 1 )
		{
			return Integer.MAX_VALUE;
		}
		
		int length = 0;
		while( RandomManager.getRandom() < columnCrossoverRate )
		{
			length++;
		}
		return length + 1;
	}
	
    public void finish()
    {
        super.finish();
    }
    
    static class TestInd extends DEIndividual
	{
		@Override
		public Object evaluate()
		{
			return getGene().getNorm();
		}
	};
	
    public static void main(String[] args)
    {
    	DEEnvironment<TestInd> env = new DEEnvironment<TestInd>();
    	env.setChromosomeLength(7);
    	env.setPopulationSize(10);
    	env.setGenerationCount(5);
    	
    	class Model extends DEEvolutionModel<TestInd, DEEnvironment<TestInd>>
    	{
    		public Model(DEEnvironment<TestInd> env)
    		{
    			super(env);
    		}
    	}
    	
    	Model model = new Model(env);
    	
    	model.run();
    }
}
