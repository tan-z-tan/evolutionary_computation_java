package application.oneMax;

import util.DataRecorder;
import ecCore.EvolutionModel;
import geneticAlgorithm.GaEnvironment;
import geneticAlgorithm.GaIndividual;

public class OneMax_Problem
{
  public static void main(String args[])
  {
    EvolutionModel<GaIndividual<Integer>, GaEnvironment<GaIndividual<Integer>>> model = new OneMax_Model();
    GaEnvironment<GaIndividual<Integer>> environment = new GaEnvironment<GaIndividual<Integer>>();

    model.setEnvironment(environment);

    model.initialize();
    for (int i = 0; i < 30; i++)
    {
      System.out.println("Generation " + i);
      model.evaluate();
      // System.out.println( "Population Fitness = " + model.getGeneration() );
      System.out.println("Average Fitness = " + DataRecorder.averageFitness(model.getPopulation()));
      model.updateGeneration();
    }
  }
}
