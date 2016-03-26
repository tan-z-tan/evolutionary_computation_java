package application.royalTree;

import mpi.GatherPackerInt;
import geneticProgramming.GPEvolutionModel;
import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpTreeManager;
import application.royalTree.RoyalTreeNode.RoyalTreeNodeEntity;

public class RoyalTreeEvolutionModel extends GPEvolutionModel<GpIndividual, GpEnvironment<GpIndividual>>
{
    public RoyalTreeEvolutionModel(GpEnvironment<GpIndividual> environment)
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
        if ( (level == 2 && bestIndividual.getFitnessValue() == 32.0)
                || (level == 3 && bestIndividual.getFitnessValue() == 384.0)
                || (level == 4 && bestIndividual.getFitnessValue() == 6144.0)
                || (level == 5 && bestIndividual.getFitnessValue() == 122880.0) )
        {
            return true;
        }
        return false;
    }
    
//    @Override
//    public void evaluate()
//    {
//        bestIndividual = _environment.getPopulation().get(0);
//        double averageDepth = 0;
//        double averageFitness = 0;
//        for (GpIndividual individual : _environment.getPopulation())
//        {
//            System.out.println(GpTreeManager.getS_Expression(individual.getRootNode()));
//            RoyalTreeNodeEntity entity = (RoyalTreeNodeEntity) individual.evaluate();
//            averageDepth += individual.getRootNode().getDepthFromHere();
//            individual.setFitnessValue(entity.getNodeValue());
//            averageFitness += entity.getNodeValue();
//            if (bestIndividual.getFitnessValue() < individual.getFitnessValue())
//            {
//                bestIndividual = individual;
//            }
//        }
//        averageFitness = averageFitness / _environment.getPopulationSize();
//        // System.out.println("Generation " + _environment.getGenerationCount() + " ");
//        // System.out.println("Average Fitness = " + averageFitness);
//        // System.out.println("Best Individual = " + bestIndividual.getFitnessValue() + ": " + GpTreeManager.getS_Expression(bestIndividual.getRootNode()));
//        // System.out.println( "Best Individual = " +
//        // bestIndividual.getFitnessValue());
//        // System.out.println("Average Depth = " + averageDepth / _environment.getPopulationSize());
//
//        int level = Integer.valueOf(_environment.getAttribute("level"));
//        if ((level == 2 && bestIndividual.getFitnessValue() == 32.0) || (level == 3 && bestIndividual.getFitnessValue() == 384.0) || (level == 4 && bestIndividual.getFitnessValue() == 6144.0)
//                || (level == 5 && bestIndividual.getFitnessValue() == 122880.0))
//        {
//            System.out.println("success!");
//            _finished = true;
//        }
//    }

    //@Override
    //public void finish()
    //{
        //System.exit(0);
    //}
}
