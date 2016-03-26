package geneticProgramming;

import java.util.ArrayList;
import java.util.Properties;

import ecCore.Environment;

public class GpEnvironment<T extends GpIndividual> extends Environment<T>
{
  protected GpSymbolSet _symbolSet;
  protected int _numberOfMaxInitialDepth;
  protected int _numberOfMinimumDepth;
  protected int _numberOfMaxDepth;
  protected double _nonterminalRate;
  
  public GpEnvironment()
  {
    _numberOfMaxDepth = Integer.MAX_VALUE;
    _numberOfMaxInitialDepth = Integer.MAX_VALUE;
    _numberOfMinimumDepth = 0;
    _symbolSet = new GpSymbolSet();
    _nonterminalRate = 0.2;
    loadDefaultAttributes();
    setPopulation(new ArrayList<T>());
  }
  
  protected void loadDefaultAttributes()
  {
    super.loadDefaultAttributes();
    _attributes.put("initialization", "rampedHalfAndHalf");
    _attributes.put("nonTerminalRate", "0.2");
  }
  
  @Override
  public void loadProperties(Properties properties)
  {
    super.loadProperties(properties);
    if (this.getAttribute("maxDepth") != null)
    {
      _numberOfMaxDepth = Integer.valueOf(getAttribute("maxDepth"));
    }
    if (this.getAttribute("maxInitialDepth") != null)
    {
      _numberOfMaxInitialDepth = Integer.valueOf(getAttribute("maxInitialDepth"));
    }
    if (this.getAttribute("nonTerminalRate") != null)
    {
      _nonterminalRate = Double.valueOf(getAttribute("nonTerminalRate"));
    }
  }

  // --- getter and setter ---
  public int getNumberOfMaxInitialDepth()
  {
    return _numberOfMaxInitialDepth;
  }

  public void setNumberOfMaxInitialDepth(int ofMaxDepth)
  {
    _numberOfMaxInitialDepth = ofMaxDepth;
  }

  public GpSymbolSet getSymbolSet()
  {
    return _symbolSet;
  }

  public void setSymbolSet(GpSymbolSet set)
  {
    _symbolSet = set;
  }

  public int getNumberOfMinimumDepth()
  {
    return _numberOfMinimumDepth;
  }

  public void setNumberOfMinimumDepth(int numberOfMinimumTreeSize)
  {
    _numberOfMinimumDepth = numberOfMinimumTreeSize;
  }

  public int getNumberOfMaxDepth()
  {
    return _numberOfMaxDepth;
  }

  public void setNumberOfMaxDepth(int numberOfMaxDepth)
  {
    _numberOfMaxDepth = numberOfMaxDepth;
  }
  
  public double getNonterminalRate()
  {
      return _nonterminalRate;
  }
  
  public void setNonterminalRate(double nonterminalRate)
  {
      _nonterminalRate = nonterminalRate;
  }
}
