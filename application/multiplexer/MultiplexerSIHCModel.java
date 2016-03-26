package application.multiplexer;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpNode;
import geneticProgramming.GpTreeManager;

import java.util.List;

import SIHC.StochasticIteratedHillClimbing;

public class MultiplexerSIHCModel extends MultiplexerEvolutionModel
{
  private int maxSizeForAbandoning;
  
  public MultiplexerSIHCModel(GpEnvironment<MultiplexerIndividual> environment)
  {
    super(environment);
    maxSizeForAbandoning = Integer.valueOf(_environment.getAttribute("maxSizeForAbandoning"));
  }
  
  @Override
  public void run()
  {
    long startTime = System.currentTimeMillis();
    
    //_environment.putAttribute("initialisation", "full");
    
    initialize();
    List<MultiplexerIndividual> population = _environment.getPopulation();
    int totalNumberOfEvaluation = 0;
    double bestFitness = 0;
    GpIndividual bestIndividual = population.get(0);
    int hillClimCount = 0;
    double averageStep = 0;
    
    while( totalNumberOfEvaluation < _environment.getPopulationSize() * _environment.getRepetitionNumber() )
    {
      int stepSize = 0;
      
      // start point
      MultiplexerIndividual individual = population.get( (int)(Math.random() * population.size()));
      evaluate(individual);
      totalNumberOfEvaluation++;  
      
      // do hill climbing
      int stuckCount = 0;
      System.out.println("Hill Climb " + hillClimCount + " start: " + individual.getFitnessValue());
      System.out.println( "Start from " + GpTreeManager.getS_Expression(individual.getRootNode()) );
      while(stuckCount < maxSizeForAbandoning && totalNumberOfEvaluation < _environment.getPopulationSize() * _environment.getRepetitionNumber())
      {
        GpNode candidateNode = StochasticIteratedHillClimbing.randomChange(individual.getRootNode(), _environment);
        MultiplexerIndividual candidateIndividual = new MultiplexerIndividual();
        candidateIndividual.setRootNode(candidateNode);
        evaluate(candidateIndividual);
        
        if( candidateIndividual.getFitnessValue() > individual.getFitnessValue() )
        {
          if( candidateIndividual.getFitnessValue() != individual.getFitnessValue() )
          {
            stepSize++;
            //System.out.println("step " + individual.getFitnessValue());
            //System.out.println( "  " + GpTreeManager.getS_Expression(individual.getRootNode()) );
            stuckCount = 0;
          }
          individual = candidateIndividual;
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
        if ( Math.pow(2, level) == individual.getFitnessValue() )
        {
          System.out.println("success!");
          System.out.println(GpTreeManager.getS_Expression(individual.getRootNode()));
          System.out.println(individual.getFitnessValue() / Math.pow(2, level));
          System.out.println("eval / maxEval = " + totalNumberOfEvaluation / (double)(_environment.getPopulationSize() * _environment.getRepetitionNumber()) );
          System.out.println("Average Step Size = " + averageStep / hillClimCount);
          System.exit(0);
        }
        totalNumberOfEvaluation++;  
      }
      if( individual.getFitnessValue() > bestIndividual.getFitnessValue() )
      {
        bestIndividual = individual;
      }
      System.out.println("Hill Climb " + hillClimCount + " top: " + individual.getFitnessValue());
      System.out.println("step size = " + stepSize);
      System.out.println(GpTreeManager.getS_Expression(individual.getRootNode()));
      System.out.println();
      hillClimCount++;
      averageStep += stepSize;
    }
    
    System.out.println("----------------------");
    System.out.println("Best Individual");
    System.out.println(GpTreeManager.getS_Expression(bestIndividual.getRootNode()));
    System.out.println(bestIndividual.getFitnessValue() / Math.pow(2, level));
    System.out.println("eval / maxEval = " + totalNumberOfEvaluation / (double)(_environment.getPopulationSize() * _environment.getRepetitionNumber()) );
    System.out.println("Average Step Size = " + averageStep / hillClimCount);
    finish();
  }
  
  public void evaluate(MultiplexerIndividual individual)
  {
    int hitNum = 0;
    for( int i = 0; i < Math.pow(2, level); i++ )
    {
      String binaryStr = Integer.toBinaryString(i);
      StringBuilder str = new StringBuilder();
      while( str.length() != 6 - binaryStr.length() )
      {
        str.append("0");
      }
      str.append(binaryStr);
      binaryStr = str.toString();
      Boolean[] xList = new Boolean[level];
      for( int j = 0; j < xList.length; j++ )
      {
        xList[j] = (binaryStr.charAt(j) == '1');
      }
      individual.setXList(xList);
      Boolean result = (Boolean)(individual.evaluate());
      if( result == correctList[i] )
      {
        hitNum++;
      }
    }
    
    individual.setFitnessValue(hitNum);
//    if (bestIndividual.getFitnessValue() < individual.getFitnessValue())
//    {
//      bestIndividual = individual;
//    }
  }
}
