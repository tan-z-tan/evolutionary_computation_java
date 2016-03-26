package application.regression;

import geneticProgramming.GPEvolutionModel;
import geneticProgramming.GpEnvironment;
import geneticProgramming.GpTreeManager;

public class RegressionEvolutionModel extends GPEvolutionModel<RegressionIndividual, GpEnvironment<RegressionIndividual>>
{
  private double[] _correctData;
  private double[] _xPlotData;
  
  public RegressionEvolutionModel(GpEnvironment<RegressionIndividual> environment)
  {
    super(RegressionIndividual.class, environment);

    _correctData = new double[20];
    _xPlotData = new double[20];
    for (int i = 0; i < 20; i++)
    {
      _xPlotData[i] = -1 + i * 2.0 / 19.0;
      // _xPlotData[i] = i / 19.0 * 2.0 * Math.PI;
      _correctData[i] = targetFunction(_xPlotData[i], Integer.valueOf(_environment.getAttribute("level")));
    }
  }
  
  // target function
  private double targetFunction(double x, int level)
  {
    double result = 0;
    for (int i = 1; i <= level; i++)
    {
      double term = 1;
      for (int j = 1; j <= i; j++)
      {
        term *= x;
      }
      result += term;
    }
    return result;
  }

  private double rlog(double x)
  {
    if (x == 0)
    {
      return 0;
    } else
    {
      return Math.log(Math.abs(x));
    }
  }

  @Override
  public void evaluate()
  {
    bestIndividual = _environment.getPopulation().get(0);
    double averageDepth = 0;
    double averageFitness = 0;
    double averageTreeSize = 0;

    for (RegressionIndividual individual : _environment.getPopulation())
    {
      double[] testData = (double[]) individual.evaluate();
      double errorSum = 0;
      int hit = 0;
      averageTreeSize += GpTreeManager.getNodeSize(individual.getRootNode());

      for (int i = 0; i < _correctData.length; i++)
      {
        double error = Math.abs(testData[i] - _correctData[i]);
        if (Double.isNaN(error))
        {
          error = Double.POSITIVE_INFINITY;
        }
        errorSum += error;
        if (error < 0.01)
        {
          hit++;
        }
      }
      if (hit == _correctData.length)
      {
        System.out.println(_environment.getGenerationCount());
        System.out.println("success!");
        System.out.println(GpTreeManager.getS_Expression(individual.getRootNode()));
        System.exit(0);
      }
      individual.setFitnessValue(errorSum);
      if (bestIndividual.getFitnessValue() > individual.getFitnessValue())
      {
        bestIndividual = individual;
      }
      averageDepth += individual.getRootNode().getDepthFromHere();
      averageFitness += individual.getFitnessValue();
    }
    System.out.println("Generation " + _environment.getGenerationCount());
    System.out.println("Best Individual " + bestIndividual.getFitnessValue() + ": "
        + GpTreeManager.getS_Expression(bestIndividual.getRootNode()));
    System.out.println("Average Depth = " + averageDepth / _environment.getPopulationSize());
    System.out.println("Average Tree Size = " + averageTreeSize / _environment.getPopulationSize());
  }
}
