package util;

import mpi.MPI;
import ecCore.Individual;

/** 並列処理用の
 * @author tanji */
public class IndividualIdentifier<T extends Individual>
{
  private int _processingElementID;
  private int _index;
  private double _fitness;
  
  public IndividualIdentifier(int id, int index, double fitness)
  {
    _processingElementID = id;
    _index = index;
    _fitness = fitness;
  }

  public T getIndividual()
  {
    int[] getMessage = new int[]{_index};
    MPI.COMM_WORLD.Send(getMessage, 0, 1, MPI.INT, _processingElementID, 1);
    Object[] individual = new Object[1];
    MPI.COMM_WORLD.Recv(individual, 0, 1, MPI.OBJECT, _processingElementID, 1);
    return (T)individual[0];
  }
  
  public int getProcessingElementID()
  {
    return _processingElementID;
  }

  public void setProcessingElementID(int processingElementID)
  {
    _processingElementID = processingElementID;
  }

  public int getIndex()
  {
    return _index;
  }

  public void setIndex(int index)
  {
    _index = index;
  }

  public double getFitness()
  {
    return _fitness;
  }

  public void setFitness(double fitness)
  {
    _fitness = fitness;
  }
}
