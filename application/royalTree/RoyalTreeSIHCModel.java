package application.royalTree;

import geneticProgramming.GPEvolutionModel;
import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpNode;
import geneticProgramming.GpTreeManager;

import java.util.List;

import SIHC.StochasticIteratedHillClimbing;
import application.royalTree.RoyalTreeNode.RoyalTreeNodeEntity;

public class RoyalTreeSIHCModel extends GPEvolutionModel<GpIndividual, GpEnvironment<GpIndividual>>
{
  private int maxSizeForAbandoning;
  
  public RoyalTreeSIHCModel(GpEnvironment<GpIndividual> environment)
  {
    super(environment);
    _environment = environment;
    if (_environment.getAttribute("tournamentSize") != null)
    {
      _tournamentSize = Integer.valueOf(_environment.getAttribute("tournamentSize"));
    }
    maxSizeForAbandoning = Integer.valueOf(_environment.getAttribute("maxSizeForAbandoning"));
  }
  
  @Override
  public void run()
  {
    long startTime = System.currentTimeMillis();
    
    //_environment.putAttribute("initialisation", "full");
    
    initialize();
    List<GpIndividual> population = _environment.getPopulation();
    int totalNumberOfEvaluation = 0;
    double bestFitness = 0;
    GpIndividual bestIndividual = population.get(0);
    int hillClimCount = 0;
    
    while( totalNumberOfEvaluation < _environment.getPopulationSize() * _environment.getRepetitionNumber() )
    {
      // start point
      GpIndividual individual = population.get( (int)(Math.random() * population.size()));
      evaluate(individual);
      totalNumberOfEvaluation++;  
      
      // do hill climbing
      int stuckCount = 0;
      System.out.println("Hill Climb " + hillClimCount + " start: " + individual.getFitnessValue());
      System.out.println( "Start " + GpTreeManager.getS_Expression(individual.getRootNode()) );
      while(stuckCount < maxSizeForAbandoning && totalNumberOfEvaluation < _environment.getPopulationSize() * _environment.getRepetitionNumber())
      {
        GpNode candidateNode = StochasticIteratedHillClimbing.randomChange(individual.getRootNode(), _environment);
        GpIndividual candidateIndividual = new GpIndividual(candidateNode);
        evaluate(candidateIndividual);
        
        if( candidateIndividual.getFitnessValue() >= individual.getFitnessValue() )
        {
          if( candidateIndividual.getFitnessValue() != individual.getFitnessValue() )
          {
            System.out.println("step " + individual.getFitnessValue());
            System.out.println( "  " + GpTreeManager.getS_Expression(individual.getRootNode()) );
            stuckCount = 0;
          }
          individual = candidateIndividual;
          //System.out.println("step " + individual.getFitnessValue());
          //System.out.println( "  " + GpTreeManager.getS_Expression(individual.getRootNode()) );
        }
        else
        {
          stuckCount++;
        }
        
        // record
        if( individual.getFitnessValue() >= bestFitness )
        {
          bestFitness = individual.getFitnessValue();
        }
        // is complete solution?
        int level = Integer.valueOf(_environment.getAttribute("level"));
        if ((level == 2 && individual.getFitnessValue() == 32.0)
            || (level == 3 && individual.getFitnessValue() == 384.0)
            || (level == 4 && individual.getFitnessValue() == 6144.0)
            || (level == 5 && individual.getFitnessValue() == 122880.0))
        {
          System.out.println("success!");
          System.out.println(GpTreeManager.getS_Expression(individual.getRootNode()));
          System.out.println(individual.getFitnessValue());
          System.exit(0);
        }
        totalNumberOfEvaluation++;  
      }
      if( individual.getFitnessValue() > bestIndividual.getFitnessValue() )
      {
        bestIndividual = individual;
      }
      System.out.println("Hill Climb " + hillClimCount + " top: " + individual.getFitnessValue());
      System.out.println(GpTreeManager.getS_Expression(individual.getRootNode()));
      System.out.println();
      hillClimCount++;
    }
    
    System.out.println("----------------------");
    System.out.println("Best Individual");
    System.out.println(GpTreeManager.getS_Expression(bestIndividual.getRootNode()));
    System.out.println(bestIndividual.getFitnessValue());
    finish();
  }
  
  public void evaluate(GpIndividual individual)
  {
    // System.out.println( GpTreeManager.getDepth(individual.getRootNode()) +
    // ": " + GpTreeManager.getS_Expression(individual.getRootNode()) );
    RoyalTreeNodeEntity entity = (RoyalTreeNodeEntity) individual.evaluate();
    individual.setFitnessValue(entity.getNodeValue());
  }
}
