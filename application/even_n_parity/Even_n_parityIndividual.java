package application.even_n_parity;

import geneticProgramming.GpIndividual;

public class Even_n_parityIndividual extends GpIndividual
{
  private Boolean[] xList;
  
  public Even_n_parityIndividual()
  {
  }
  
  public Boolean[] getXList()
  {
    return xList;
  }
  
  public void setXList(Boolean[] list)
  {
    xList = list;
  }
  
  public Boolean getX(int index)
  {
    return xList[index];
  }
}
