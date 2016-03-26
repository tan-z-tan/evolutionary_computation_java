package application.max;

import geneticProgramming.GPEvolutionModel;
import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;

public class MaxEvolutionModel extends GPEvolutionModel<GpIndividual, GpEnvironment<GpIndividual>>
{
  public MaxEvolutionModel(GpEnvironment<GpIndividual> environment)
  {
      super(environment);
      _environment = environment;
      if (_environment.getAttribute("tournamentSize") != null)
      {
          _tournamentSize = Integer.valueOf(_environment.getAttribute("tournamentSize"));
      }
  }
  @Override
  public void evaluateIndividual(GpIndividual individual)
  {
      Double value = (Double) individual.evaluate();
      individual.setFitnessValue(value);
  }
  
  @Override
  public boolean isTerminal(GpIndividual bestIndividual)
  {
      if( bestIndividual.getFitnessValue() == Math.pow(2, Math.pow(2, _environment.getNumberOfMaxDepth() - 3)) )
      {
          return true;
      }
      return false;
  }
}
