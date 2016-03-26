package application.LID;

import geneticProgramming.GPEvolutionModel;
import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpTreeManager;

public class LIDEvolutionModel extends GPEvolutionModel<GpIndividual, GpEnvironment<GpIndividual>>
{
    double _targetDepth = 5;
    double _targetTerminal = 30;
    double _weightDepth = 50;
    double _weightTerminal = 50;

    public LIDEvolutionModel(GpEnvironment<GpIndividual> environment)
    {
        super(environment);

        _environment = environment;
        _targetDepth = Double.valueOf(_environment.getAttribute("targetDepth"));
        _targetTerminal = Double.valueOf(_environment.getAttribute("targetTerminal"));
        _weightDepth = Double.valueOf(_environment.getAttribute("weightDepth"));
        _weightTerminal = Double.valueOf(_environment.getAttribute("weightTerminal"));

        if (_environment.getAttribute("tournamentSize") != null)
        {
            _tournamentSize = Integer.valueOf(_environment.getAttribute("tournamentSize"));
        }
    }

    @Override
    public void evaluate()
    {
        bestIndividual = _environment.getPopulation().get(0);
        double averageDepth = 0;
        double averageFitness = 0;
        for (GpIndividual individual : _environment.getPopulation())
        {
            // System.out.println(
            // GpTreeManager.getS_Expression(individual.getRootNode()) );
            double depth = individual.getRootNode().getDepthFromHere() - 1;
            double terminalNodeSize = GpTreeManager.getTerminalNodeSize(individual.getRootNode());

            double metric_depth = _weightDepth * (1 - Math.abs(_targetDepth - depth) / _targetDepth);
            double metric_term = _weightTerminal * (1 - Math.abs(_targetTerminal - terminalNodeSize) / _targetTerminal);
            double fitness = 0;

            if (depth == _targetDepth)
            {
                fitness = metric_depth + metric_term;
            } else
            {
                fitness = metric_depth;
            }

            averageDepth += depth;
            individual.setFitnessValue(fitness);
            averageFitness += individual.getFitnessValue();
            if (bestIndividual.getFitnessValue() < individual.getFitnessValue())
            {
                bestIndividual = individual;
            }
        }
        averageFitness = averageFitness / _environment.getPopulationSize();
        System.out.println("Generation " + _environment.getGenerationCount() + " ");
        System.out.println("Average Fitness = " + averageFitness);
        System.out.println("Best Individual = " + bestIndividual.getFitnessValue() + ": " + GpTreeManager.getS_Expression(bestIndividual.getRootNode()));
        // System.out.println( "Best Individual = " +
        // bestIndividual.getFitnessValue());
        System.out.println("Average Depth = " + averageDepth / _environment.getPopulationSize());

        if ((bestIndividual.getFitnessValue() == 100))
        {
            System.out.println("success!");
            System.exit(0);
        }
    }
}
