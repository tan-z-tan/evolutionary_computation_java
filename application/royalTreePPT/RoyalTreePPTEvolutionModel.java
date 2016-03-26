package application.royalTreePPT;

import geneticProgramming.GPEvolutionModel;
import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import application.royalTreePPT.RoyalTreePPTNode.RoyalTreeNodeEntity;

public class RoyalTreePPTEvolutionModel extends GPEvolutionModel<GpIndividual, GpEnvironment<GpIndividual>>
{
    public RoyalTreePPTEvolutionModel(GpEnvironment<GpIndividual> environment)
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
        RoyalTreeNodeEntity entity = (RoyalTreeNodeEntity) individual.evaluate();
        individual.setFitnessValue(entity.getNodeValue());
        //System.out.println("inidividusl " + GpTreeManager.getS_Expression(individual.getRootNode()));
    }
    
    @Override
    public boolean isTerminal(GpIndividual bestIndividual)
    {
        int level = Integer.valueOf(_environment.getAttribute("level"));
        if ( (level == 2 && bestIndividual.getFitnessValue() == 64.0)
                || (level == 3 && bestIndividual.getFitnessValue() == 512.0)
                || (level == 4 && bestIndividual.getFitnessValue() == 4096.0)
                || (level == 5 && bestIndividual.getFitnessValue() == 32768.0) )
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
