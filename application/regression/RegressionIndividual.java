package application.regression;

import geneticProgramming.GpIndividual;

public class RegressionIndividual extends GpIndividual
{
  private double _x;
  
  @Override
  public Object evaluate()
  {
    double[] result = new double[20];
    for (int i = 0; i < 20; i++)
    {
      _x = -1 + i * 2.0 / 19.0;
      result[i] = (Double) _rootNode.evaluate(this);
    }
    return result;
  }

  public Object getVariable()
  {
    return _x;
  }
}
