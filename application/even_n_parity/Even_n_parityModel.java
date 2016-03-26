package application.even_n_parity;

import geneticProgramming.GPEvolutionModel;
import geneticProgramming.GpEnvironment;
import geneticProgramming.GpTreeManager;

public class Even_n_parityModel extends GPEvolutionModel<Even_n_parityIndividual, GpEnvironment<Even_n_parityIndividual>>
{
    public int level = 0;

    public Even_n_parityModel(GpEnvironment<Even_n_parityIndividual> environment)
    {
        super(Even_n_parityIndividual.class, environment);

        if (_environment.getAttribute("tournamentSize") != null)
        {
            _tournamentSize = Integer.valueOf(_environment.getAttribute("tournamentSize"));
        }

        level = Integer.valueOf(_environment.getAttribute("level"));
    }

    @Override
    public void evaluate()
    {
        bestIndividual = _environment.getPopulation().get(0);
        double averageDepth = 0;
        double averageFitness = 0;
        for (Even_n_parityIndividual individual : _environment.getPopulation())
        {
            // System.out.println( GpTreeManager.getDepth(individual.getRootNode()) + ": " + GpTreeManager.getS_Expression(individual.getRootNode()) );
            // System.out.println( GpTreeManager.getS_Expression(individual.getRootNode()) );
            int hitNum = 0;
            for (int i = 0; i < Math.pow(2, level); i++)
            {
                String binaryStr = Integer.toBinaryString(i);
                StringBuilder str = new StringBuilder();
                while (str.length() != level - binaryStr.length())
                {
                    str.append("0");
                }
                str.append(binaryStr);
                binaryStr = str.toString();
                Boolean[] xList = new Boolean[level];
                int numberOfTrue = 0;
                for (int j = 0; j < xList.length; j++)
                {
                    xList[j] = (binaryStr.charAt(j) == '1');
                    if( xList[j] )
                    {
                        numberOfTrue++;
                    }
                }
                individual.setXList(xList);
                Boolean result = (Boolean) (individual.evaluate());
                if (result && (numberOfTrue % 2 == 0)) 
                {
                    hitNum++;
                }
                if (!result && (numberOfTrue % 2 != 0)) 
                {
                    hitNum++;
                }
            }

            // Boolean result = (Boolean) individual.evaluate();
            averageDepth += individual.getRootNode().getDepthFromHere();
            individual.setFitnessValue(hitNum);
            averageFitness += individual.getFitnessValue();
            if (bestIndividual.getFitnessValue() < individual.getFitnessValue())
            {
                bestIndividual = individual;
            }
        }
        // averageFitness = averageFitness / _environment.getPopulationSize();
        // System.out.println("Generation " + _environment.getGenerationCount() + " ");
        // System.out.println("Average Fitness = " + averageFitness);
        // System.out.println("Best Individual = " + bestIndividual.getFitnessValue() + ": " + GpTreeManager.getS_Expression(bestIndividual.getRootNode()));
        // System.out.println( "Best Individual = " +
        // bestIndividual.getFitnessValue());
        // System.out.println("Average Depth = " + averageDepth / _environment.getPopulationSize());

        if (Math.pow(2, level) == bestIndividual.getFitnessValue())
        {
            System.out.println("success!");
            System.out.println(bestIndividual.getFitnessValue());
            System.out.println(GpTreeManager.getS_Expression(bestIndividual.getRootNode()));
            _finished = true;
        }
    }
}
