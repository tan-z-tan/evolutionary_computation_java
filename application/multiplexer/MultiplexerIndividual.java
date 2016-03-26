package application.multiplexer;

import geneticProgramming.GpIndividual;

public class MultiplexerIndividual extends GpIndividual
{
  private Boolean[] xList;
  
  public MultiplexerIndividual()
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
