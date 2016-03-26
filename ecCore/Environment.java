package ecCore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import ecCore.selector.AbstractSelector;

public class Environment<T extends Individual>
{
  protected int _populationSize;
  protected int _repetitionNumber;
  protected double _crossoverRatio;
  protected double _mutationRatio;
  protected List<T> _population;
  protected int _eliteSize;
  protected AbstractSelector<T> _selector;
  protected int _generationCount;
  protected Map<String, String> _attributes;
  protected boolean _selectionOrder; // true = large value is good, false = small value is good.
  
  public Environment()
  {
    _attributes = new HashMap<String, String>(8);
    loadDefaultAttributes();
    _generationCount = 1;
    _selectionOrder = true;
  }
  
  protected void loadDefaultAttributes()
  {
    _attributes.put("selector", "tournament");
    _attributes.put("selectionOrder", "normal");
    _attributes.put("tournamentSize", "4");
    _attributes.put("parallel", "false");
  }
  
  public void loadProperties(Properties properties)
  {
    for (Entry<Object, Object> entry : properties.entrySet())
    {
      String key = (String) entry.getKey();
      String value = (String) entry.getValue();

      _attributes.put(key, value);

      if (key.equals("populationSize"))
      {
        _populationSize = Integer.valueOf(value);
      } else if (key.equals("repetitionNumber"))
      {
        _repetitionNumber = Integer.valueOf(value);
      } else if (key.equals("eliteSize"))
      {
        _eliteSize = Integer.valueOf(value);
      } else if (key.equals("crossoverRatio"))
      {
        _crossoverRatio = Double.valueOf(value);
      } else if (key.equals("mutationRatio"))
      {
        _mutationRatio = Double.valueOf(value);
      } else if (key.equals("selectionOrder"))
      {
          _selectionOrder = value.equals("normal");
      }
    }
  }

  public void putAttribute(String key, String value)
  {
    _attributes.put(key, value);
  }

  public String getAttribute(String key)
  {
    return _attributes.get(key);
  }

  public Map<String, String> getAttributes()
  {
    return _attributes;
  }

  // --- getter and setter ---
  public int getPopulationSize()
  {
    return _populationSize;
  }

  public void setPopulationSize(int populationSize)
  {
    _attributes.put("populationSize", String.valueOf(populationSize));
    _populationSize = populationSize;
  }

  public int getRepetitionNumber()
  {
    return _repetitionNumber;
  }

  public void setRepetitionNumber(int repetitionNumber)
  {
    _attributes.put("repetitionNumber", String.valueOf(repetitionNumber));
    _repetitionNumber = repetitionNumber;
  }

  public double getCrossoverRatio()
  {
    return _crossoverRatio;
  }

  public void setCrossoverRatio(double crossoverRatio)
  {
    _attributes.put("crossoverRatio", String.valueOf(crossoverRatio));
    _crossoverRatio = crossoverRatio;
  }

  public double getMutationRatio()
  {
    return _mutationRatio;
  }

  public void setMutationRatio(double mutationRatio)
  {
    _attributes.put("mutationRatio", String.valueOf(mutationRatio));
    _mutationRatio = mutationRatio;
  }

  public List<T> getPopulation()
  {
    return _population;
  }

  public void setPopulation(List<T> population)
  {
    _population = population;
  }

  public int getEliteSize()
  {
    return _eliteSize;
  }

  public void setEliteSize(int eliteSize)
  {
    _attributes.put("eliteSize", String.valueOf(eliteSize));
    _eliteSize = eliteSize;
  }

  public AbstractSelector<T> getSelector()
  {
    return _selector;
  }

  public void setSelector(AbstractSelector<T> selector)
  {
    _selector = selector;
  }

  public int getGenerationCount()
  {
    return _generationCount;
  }

  public void setGenerationCount(int generationCount)
  {
    _generationCount = generationCount;
  }
  
  public boolean getSelectionOrder()
  {
      return _selectionOrder;
  }
  
  public void setSelectionOrder(boolean selectionOrder)
  {
      _selectionOrder = selectionOrder;
  }  
}
