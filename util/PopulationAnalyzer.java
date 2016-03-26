package util;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpTreeManager;

//public class PopulationAnalyzer<T extends GpIndividual, E extends GpEnvironment<T>>
//public class PopulationAnalyzer<E extends GpEnvironment<?>>
public class PopulationAnalyzer
{
    private GpEnvironment<?> _environment;
    
    public PopulationAnalyzer(GpEnvironment<?> environment)
    {
        _environment = environment;
    }
    
    /** returns the average size of individual tree */
    public double calculateAverageTreeSize()
    {
        double sum = 0;
        for( GpIndividual individual: _environment.getPopulation() )
        {
            int size = GpTreeManager.getNodeSize(individual.getRootNode());
            //System.out.println("size = " + size);
            sum += size;
        }
        return sum / _environment.getPopulationSize();
    }
}
