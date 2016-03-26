package ecCore.selector;

import java.util.List;

import ecCore.Individual;

/**
 * The Selector class works to chose PTypes by fitness values in Evolutionary
 * Computation.
 * 
 * @author Makoto Tanji
 */
public abstract class AbstractSelector<T extends Individual>
{
  /** the flag which means fitness value should be evaluated by normal order */
  public static final boolean NORMAL = true;
  /** the flag which means fitness value should be evaluated by reverse order */
  public static final boolean REVERSE = false;
  /** generation of PType */
  protected List<T> _population;
  /** average of adaptation values in a generation */
  protected double _averageValue;
  /** sum of adaptation values in a generation */
  protected double _sumValue;
  /** max fitness value */
  protected double _max;
  /**
   * Inverse flag which is true if low fitness value one is good one, otherwise
   * false
   */
  protected boolean _reverse;

  /** calculates average of fitness value in generation */
  protected void calcAverageValue()
  {
    if (_reverse == REVERSE)
    {
      _max = 0;
      for (Individual pType : _population)
        if (_max < pType.getFitnessValue())
          _max = pType.getFitnessValue();

      _sumValue = 0;
      for (Individual pType : _population)
        _sumValue += _max - pType.getFitnessValue();

      _averageValue = _sumValue / _population.size();
    } else
    // normal order
    {
      _sumValue = 0;
      for (Individual pType : _population)
      {
        _sumValue += pType.getFitnessValue();
      }
      _averageValue = _sumValue / _population.size();
    }
  }

  /**
   * returns a random PType in generation, secection way is depended to
   * subclasses
   */
  public abstract T getRandomPType();

  /**
   * returns a List of random PType in generation, secection way is depended to
   * subclasses
   */
  public abstract List<T> getRandomPTypeList(int selectionSize);
}
