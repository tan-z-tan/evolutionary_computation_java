package application.multiplexer;

import geneticProgramming.GPEvolutionModel;
import geneticProgramming.GpEnvironment;
import geneticProgramming.GpTreeManager;

public class MultiplexerEvolutionModel extends GPEvolutionModel<MultiplexerIndividual, GpEnvironment<MultiplexerIndividual>>
{
    public Boolean[] correctList;
    public int level = 0;

    public MultiplexerEvolutionModel(GpEnvironment<MultiplexerIndividual> environment)
    {
        super(MultiplexerIndividual.class, environment);

        if (_environment.getAttribute("tournamentSize") != null)
        {
            _tournamentSize = Integer.valueOf(_environment.getAttribute("tournamentSize"));
        }

        level = Integer.valueOf(_environment.getAttribute("level"));
        correctList = new Boolean[(int) Math.pow(2, level)];
        //
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
            for (int j = 0; j < xList.length; j++)
            {
                xList[j] = (binaryStr.charAt(j) == '1');
            }

            if (level == 11)
            {
                if (xList[0])
                {
                    if (xList[2])
                    {
                        if (xList[1])
                        {
                            correctList[i] = xList[10];
                        } else
                        {
                            correctList[i] = xList[8];
                        }
                    } else
                    {
                        if (xList[1])
                        {
                            correctList[i] = xList[6];
                        } else
                        {
                            correctList[i] = xList[4];
                        }
                    }
                } else
                {
                    if (xList[2])
                    {
                        if (xList[1])
                        {
                            correctList[i] = xList[9];
                        } else
                        {
                            correctList[i] = xList[7];
                        }
                    } else
                    {
                        if (xList[1])
                        {
                            correctList[i] = xList[5];
                        } else
                        {
                            correctList[i] = xList[3];
                        }
                    }
                }
            } else
            {
                if (xList[1])
                {
                    if (xList[0])
                    {
                        correctList[i] = xList[5];
                    } else
                    {
                        correctList[i] = xList[4];
                    }
                } else
                {
                    if (xList[0])
                    {
                        correctList[i] = xList[3];
                    } else
                    {
                        correctList[i] = xList[2];
                    }
                }
            }
        }
    }

    @Override
    public void evaluate()
    {
        bestIndividual = _environment.getPopulation().get(0);
        double averageDepth = 0;
        double averageFitness = 0;
        for (MultiplexerIndividual individual : _environment.getPopulation())
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
                for (int j = 0; j < xList.length; j++)
                {
                    xList[j] = (binaryStr.charAt(j) == '1');
                }
                individual.setXList(xList);
                Boolean result = (Boolean) (individual.evaluate());
                if (result == correctList[i])
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
