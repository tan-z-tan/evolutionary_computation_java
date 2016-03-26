package geneticProgramming;

import java.io.Serializable;

import ecCore.Individual;

/**
 * The Individual class of GP.
 * 
 * @author tanji
 */
public class GpIndividual extends Individual implements Serializable
{
  private static final long serialVersionUID = 5418532985405694636L;
  protected GpNode _rootNode;

  public GpIndividual()
  {
    _rootNode = null;
    _fitnessValue = 0;
  }

  public GpIndividual(GpNode root)
  {
    _rootNode = root;
    _fitnessValue = 0;
  }
  
  @Override
  public Object evaluate()
  {
    return _rootNode.evaluate(this);
  }

  public GpNode getRootNode()
  {
    return _rootNode;
  }

  public void setRootNode(GpNode node)
  {
    _rootNode = node;
  }
}