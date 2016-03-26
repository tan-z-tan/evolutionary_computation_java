package application.DMAX;

import geneticProgramming.GPEvolutionModel;
import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;

import org.apache.commons.math3.complex.Complex;

public class DMaxEvolutionModel extends GPEvolutionModel<GpIndividual, GpEnvironment<GpIndividual>>
{
    public DMaxEvolutionModel(GpEnvironment<GpIndividual> environment)
    {
        super(environment);
        _environment = environment;
        if (_environment.getAttribute("tournamentSize") != null)
        {
            _tournamentSize = Integer.valueOf(_environment.getAttribute("tournamentSize"));
        }
    }
    
    @Override
    public void evaluateIndividual(GpIndividual individual)
    {
        Complex entity = (Complex) individual.evaluate();
        individual.setFitnessValue(entity.getReal());
        //System.out.println("inidividusl " + GpTreeManager.getS_Expression(individual.getRootNode()));
    }
    
    @Override
    public boolean isTerminal(GpIndividual bestIndividual)
    {
    	int level = Integer.valueOf(_environment.getAttribute("level"));
    	if( _environment.getAttribute("fitness") != null &&
    			_environment.getAttribute("fitness").equals("normal") &&
    			level == 3 && bestIndividual.getFitnessValue() == 19683)
    	{
    		return true;
    	}
    	else if ( (level == 3 && bestIndividual.getFitnessValue() == 18698.85)
                || (level == 2 && bestIndividual.getFitnessValue() == 25.65) )
        {
            return true;
        }
        return false;
    }
    
    @Override
    public void finish()
    {
        super.finish();
        System.out.println("Evaluation Count = " + evaluationCount);
        System.exit(0);
    }
}
