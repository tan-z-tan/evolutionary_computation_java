package ecCore;

import java.util.ArrayList;
import java.util.List;

import util.DataRecorder;
import ecCore.selector.AbstractSelector;

/**
 * Evolutionary Computationのプロトタイプモデル．<BR>
 * GAとGPは共通のこのクラスを使用する．<BR>
 * 以下,並列化に関する設定.<BR>
 * [parallel=true]でMPJによる並列化.<BR>
 * [id=x]で並列化の際のidをxで指定する．<BR>
 * [np=y]で並列化の際のprocessing elementsの数をyで指定する．
 */
public abstract class EvolutionModel<T extends Individual, E extends Environment<T>> implements Runnable
{
  protected E _environment;
  protected boolean _selectionOrder = true;
  protected int _tournamentSize;
  protected T bestIndividual;
  protected boolean _parallel = false;
  protected int evaluationCount = 0;
  protected int _id;
  protected int _size;
  protected long _takenTime;
  protected boolean _finished = false;
  protected int activeThreadNum = 0;
  
  class Evaluator implements Runnable
  {
	  int startIndex;
	  int endIndex;
	  List<T> population;
	  
	  public void set(List<T> population, int startIndex, int endIndex)
	  {
		  this.population = population;
		  this.startIndex = startIndex;
		  this.endIndex = endIndex;
	  }
	  
	  @Override
	  public void run()
	  {
		  for (int i = startIndex; i < endIndex; i ++)
		  {
			  evaluationCount++;
			  T individual = population.get(i);
			  //System.out.println("evaluate");
			  evaluateIndividual(individual);
			  //averageFitness += individual.getFitnessValue();
			  synchronized (bestIndividual) {    	        	  
				  if( _environment.getSelectionOrder() && bestIndividual.getFitnessValue() < individual.getFitnessValue() )
					  bestIndividual = individual;
				  else if( !_environment.getSelectionOrder() && bestIndividual.getFitnessValue() > individual.getFitnessValue() )
					  bestIndividual = individual;
    	      }
    	  }
		  activeThreadNum--;
      }
  }
  
  public EvolutionModel()
  {
  }

  public EvolutionModel(E environment)
  {
    _environment = environment;
    readAttributes();
  }

  protected void readAttributes()
  {
    if (_environment == null)
    {
      return;
    }

    if (_environment.getAttribute("selectionOrder").equals("normal"))
    {
      _selectionOrder = AbstractSelector.NORMAL;
    } else if (_environment.getAttribute("selectionOrder").equals("reverse"))
    {
      _selectionOrder = AbstractSelector.REVERSE;
    }

    if (_environment.getAttribute("parallel").equals("true"))
    {
      _parallel = true;
      _id = Integer.valueOf(_environment.getAttribute("id"));
      _size = Integer.valueOf(_environment.getAttribute("np"));
      System.out.println("NP Size = " + _size);
    }

    _tournamentSize = Integer.valueOf(_environment.getAttribute("tournamentSize"));
  }

  public abstract void initialize();

  public void evaluate()
  {
      bestIndividual = _environment.getPopulation().get(0);
      double averageFitness = 0;
      
      if( _environment.getAttribute("thread") != null )
      {
    	  evaluateParallel( Integer.valueOf(_environment.getAttribute("thread")) );
      }
      else
      {
    	  for (T individual : _environment.getPopulation())
    	  {
    		  evaluationCount++;
    		  
    		  evaluateIndividual(individual);
    		  averageFitness += individual.getFitnessValue();
    		  
    		  if( _environment.getSelectionOrder() && bestIndividual.getFitnessValue() < individual.getFitnessValue() )
    		  {
    			  bestIndividual = individual;
    		  }
    		  else if( !_environment.getSelectionOrder() && bestIndividual.getFitnessValue() > individual.getFitnessValue() )
    		  {
    			  bestIndividual = individual;
    		  }
    	  }
      }
      
      endOfEvaluation();
      
      if( isTerminal(bestIndividual) )
      {
          System.out.println("success!");
          _finished = true;
      }
  }
  
  protected void evaluateParallel(int threadNum)
  {
	  activeThreadNum = threadNum;
	  int givenSize = _environment.getPopulationSize() / threadNum;
	  int remainder = _environment.getPopulationSize() % threadNum;
	  
	  int index = 0;
	  List<Thread> threadList = new ArrayList<Thread>();
	  for( int i = 0; i < threadNum; i++ )
	  {
		  Evaluator evaluator = new Evaluator();
		  int plus = remainder != 0 ? 1:0;
		  evaluator.set(_environment.getPopulation(), index, index + givenSize + plus);
		  remainder = Math.max(0, remainder - 1);
		  Thread th = new Thread(evaluator);
		  threadList.add(th);
		  th.start();
		  //System.out.println("run " + i);
	  }
	  //System.out.println("Active Thread = " + activeThreadNum);
	  
	  // 同期オブジェクトの待ち合わせ（子がすべて終了するまで待つ）
	  while (activeThreadNum > 0) {
		  //System.out.println("running... Active Thread = " + activeThreadNum);
		  try {
			  threadList.get(0).join();
			  threadList.remove(0);
		  } catch (Exception ex) {
			  ex.printStackTrace();
		  }
	  }
  }
  
  /** This method does nothing by default.
   * User can overwrite this method to calculate some statistics or to show some GUI and so on. */
  protected void endOfEvaluation()
  {
	  
  }
  
  /** subclass must implement this method. */
  public boolean isTerminal(T bestIndividual)
  {
      return false;
  }
  
  /**
   * Override this method to implements own GP system.
   * @param individual
   */
  public abstract void evaluateIndividual(T individual);
  
  public abstract void updateGeneration();

  public void finish()
  {
      System.out.println("Time Taken by the Evolution = " + _takenTime);
  }

  /**
   * returns current population.
   * 
   * @return
   */
  public List<T> getPopulation()
  {
    return _environment.getPopulation();
  }

  @Override
  public void run()
  {
	  System.out.println("*** Experiment Parameters ");
      System.out.println("*** " + _environment.getAttributes());
      long startTime = System.currentTimeMillis();
      
      initialize();
      for (int i = 0; i < _environment.getRepetitionNumber(); i++)
      {
          System.out.println("***** Generation " + _environment.getGenerationCount() + " ");
          evaluate();
          System.out.println("Average Fitness = " + DataRecorder.averageFitness(_environment.getPopulation()));
          if( _finished || i == _environment.getRepetitionNumber() -1 )
          {
              break;
          }
          updateGeneration();
          _environment.setGenerationCount(_environment.getGenerationCount() + 1);
      }
      _takenTime = System.currentTimeMillis() - startTime;
      finish();
  }

  // --- getter and setter methods ---
  /**
   * returns environment object that contains some variables of Evolutionary
   * Computation
   */
  public E getEnvironment()
  {
    return _environment;
  }

  /** sets environment */
  public void setEnvironment(E environment)
  {
    _environment = environment;
    readAttributes();
  }
  public T getBestIndividual()
  {
	  return bestIndividual;
  }
  
  public void setBestIndividual(T bestIndividual)
  {
	 this.bestIndividual = bestIndividual;
  }
}
