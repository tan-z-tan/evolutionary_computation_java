package application.Order;

import ecCore.selector.AbstractSelector;
import ecCore.selector.RouletteSelector;
import ecCore.selector.TournamentSelector;
import ecCore.selector.TruncateSelector;
import geneticProgramming.GPEvolutionModel;
import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpNode;
import geneticProgramming.GpTreeManager;

import java.util.ArrayList;
import java.util.List;

import application.multiplexer.MultiplexerIndividual;

import ports.Porte;

public class OrderEvolutionModel extends GPEvolutionModel<GpIndividual, GpEnvironment<GpIndividual>>
{
	GpIndividual bestIndividual;
	int _tournamentSize = 10; // default
	int _level = 15; // default
	String _fitness = "";
	int _k = 3;
	double _delta = 0.25;
	//private List<Double> _averageSizeList = new ArrayList<Double>();
	private List<Double> _averageFitnessList = new ArrayList<Double>();
	
	// broot parameters
	private List<Double> _offspringSizeList;
	private double _averageFragmentSize;
	
	public OrderEvolutionModel(GpEnvironment<GpIndividual> environment)
	{
        super(GpIndividual.class, environment);
        
        _level = Integer.valueOf(_environment.getAttribute("level"));
		_fitness = _environment.getAttribute("fitness");
		_k = Integer.valueOf(_environment.getAttribute("k"));
		_delta = Double.valueOf(_environment.getAttribute("delta"));
		
		if( _environment.getAttribute("tournamentSize") != null )
		{
			_tournamentSize = Integer.valueOf(_environment.getAttribute("tournamentSize"));
		}
	}
    
	@Override
    public void evaluateIndividual(GpIndividual individual)
    {
        double fitness = 0;
        if( _fitness.equals("order") )
        {
            fitness = FitnessFunctions.fitnessFunction_OrderTree( individual.getRootNode(), _level );
        }
        if( _fitness.equals("deceptiveOrder") )
        {
            fitness = FitnessFunctions.fitnessFunction_DeceptiveOrderTree( individual.getRootNode(), _level, _k, _delta );
        }
        individual.setFitnessValue(fitness);
    }
	
    @Override
    public boolean isTerminal(GpIndividual bestIndividual)
    {
        if ( (_fitness.equals("order") && bestIndividual.getFitnessValue() == _level) || 
                (_fitness.equals("deceptiveOrder") && (bestIndividual.getFitnessValue() == _level/_k)) )
        {
            System.out.println("success!");
            System.out.println("Evaluation Count = " + evaluationCount);
            System.out.println(bestIndividual.getFitnessValue());
            System.out.println(GpTreeManager.getS_Expression(bestIndividual.getRootNode()));
            _finished = true;
        }
        return false;
    }

//    @Override
//    public void finish()
//    {
//        super.finish();
//        System.out.println("Evaluation Count = " + evaluationCount);
//        System.exit(0);
//    }
//	@Override
//	public void evaluate()
//	{
//		bestIndividual = _environment.getPopulation().get(0);
//		double averageDepth = 0;
//		double averageFitness = 0;
//		
//		for( GpIndividual individual: _environment.getPopulation() )
//		{
//		    evaluationCount++;
//			//System.out.println( GpTreeManager.getS_Expression(individual.getRootNode()) );
//			double depth = individual.getRootNode().getDepthFromHere() - 1;
//			//
//			double fitness = 0;
//			if( _fitness.equals("order") )
//			{
//				fitness = FitnessFunctions.fitnessFunction_OrderTree( individual.getRootNode(), _level );
//			}
//			if( _fitness.equals("deceptiveOrder") )
//			{
//				fitness = FitnessFunctions.fitnessFunction_DeceptiveOrderTree( individual.getRootNode(), _level, _k, _delta );
//			}
//			
//			averageDepth += depth;
//			individual.setFitnessValue( fitness );
//			averageFitness += individual.getFitnessValue();
//			if( bestIndividual.getFitnessValue() < individual.getFitnessValue() )
//			{
//				bestIndividual = individual;
//			}
//		}		
//		averageFitness = averageFitness / _environment.getPopulationSize();
//		_averageFitnessList.add(averageFitness);
////		System.out.println("Generation " + _environment.getGenerationCount() + " ");
////		System.out.println( "Average Fitness = " + averageFitness );
//		System.out.println( "Best Individual = "  + bestIndividual.getFitnessValue() + ": " + GpTreeManager.getS_Expression(bestIndividual.getRootNode()) );
////		System.out.println("Average Depth = " + averageDepth / _environment.getPopulationSize());
////		
//        if ( (_fitness.equals("order") && bestIndividual.getFitnessValue() == _level) || 
//                (_fitness.equals("deceptiveOrder") && (bestIndividual.getFitnessValue() == _level/_k)) )
//        {
//            System.out.println("success!");
//            System.out.println("Evaluation Count = " + evaluationCount);
//            System.out.println(bestIndividual.getFitnessValue());
//            System.out.println(GpTreeManager.getS_Expression(bestIndividual.getRootNode()));
//            _finished = true;
//        }
//	}
}
