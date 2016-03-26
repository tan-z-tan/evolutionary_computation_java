package application.OrderTree;

import geneticProgramming.GPEvolutionModel;
import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpNode;
import geneticProgramming.GpTreeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderTreeEvolutionModel extends GPEvolutionModel<GpIndividual, GpEnvironment<GpIndividual>>
{
    GpIndividual bestIndividual;
    int _tournamentSize = 10; // default
    int _level = 15; // default
    String _fitness = "";
    int _k = 3;
    double _delta = 0.25;
    private List<Double> _averageSizeList = new ArrayList<Double>();
    private List<Double> _averageFitnessList = new ArrayList<Double>();

    // broot parameters
    private List<Double> _offspringSizeList;
    private double _averageFragmentSize;
    private double _averageTreeSize = 0;

    public OrderTreeEvolutionModel(GpEnvironment<GpIndividual> environment)
    {
        super(environment);
        _environment = environment;
        _level = Integer.valueOf(_environment.getAttribute("level"));
        _fitness = _environment.getAttribute("fitness");

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
        double maxFitness = Math.pow(2, _level) - 2;
        
        for (GpIndividual individual : _environment.getPopulation())
        {
            double depth = individual.getRootNode().getDepthFromHere() - 1;
            //
            double fitness = 0;
            if (_fitness.equals("order"))
            {
                fitness = FitnessFunctions.fitnessFunction_OrderTree(individual.getRootNode());
                fitness = maxFitness - fitness;
            }
            
            averageDepth += depth;
            individual.setFitnessValue(fitness);
            averageFitness += individual.getFitnessValue();
            if (bestIndividual.getFitnessValue() > individual.getFitnessValue())
            {
                bestIndividual = individual;
            }
        }
        averageFitness = averageFitness / _environment.getPopulationSize();
        _averageFitnessList.add(averageFitness);
        System.out.println("Average Fitness = " + averageFitness);
        System.out.println("Best Individual = " + bestIndividual.getFitnessValue() + ": " + GpTreeManager.getS_Expression(bestIndividual.getRootNode()));
        System.out.println("Average Depth = " + averageDepth / _environment.getPopulationSize());

        if ((bestIndividual.getFitnessValue() == 0))
        //if ((bestIndividual.getFitnessValue() == Math.pow(2, _level) - 2))
        {
            System.out.println("success!");
            System.out.println(bestIndividual.getFitnessValue());
            System.out.println(GpTreeManager.getS_Expression(bestIndividual.getRootNode()));
            _finished = true;
            _finished = true;
            System.exit(0);
        }
    }
    
    // @Override
    // public void updateGeneration()
    // {
    // if (_environment.getAttribute("method").equals("SGP"))
    // {
    // updateGeneration_SGP();
    // } else if (_environment.getAttribute("method").equals("PORTE"))
    // {
    // updateGeneration_PORTE();
    // }
    // }
    //
    // public void updateGeneration_SGP()
    // {
    // // reproduction
    // AbstractSelector<GpIndividual> selector = new TournamentSelector<GpIndividual>(_environment.getPopulation(),
    // _tournamentSize, AbstractSelector.NORMAL);
    // if (_environment.getAttribute("selector").equals("tournament"))
    // {
    // selector = new TournamentSelector<GpIndividual>(_environment.getPopulation(), _tournamentSize,
    // AbstractSelector.NORMAL);
    // } else if (_environment.getAttribute("selector").equals("truncation"))
    // {
    // selector = new TruncateSelector<GpIndividual>(_environment.getPopulation(), AbstractSelector.NORMAL);
    // } else if (_environment.getAttribute("selector").equals("roulette"))
    // {
    // selector = new RouletteSelector<GpIndividual>(_environment.getPopulation(), AbstractSelector.NORMAL);
    // }
    //
    // int phenotypeNum = _environment.getPopulationSize();
    // Map<Integer, Integer> fragmentSizeMap = new HashMap<Integer, Integer>();
    // List<GpIndividual> nextPopulation = new ArrayList<GpIndividual>(phenotypeNum);
    // for (int i = 0; i < _environment.getEliteSize(); i++)
    // {
    // nextPopulation.add(bestIndividual);
    // }
    // for (; nextPopulation.size() < phenotypeNum;)
    // {
    // if (nextPopulation.size() < (phenotypeNum) * _environment.getCrossoverRatio())
    // {
    // GpIndividual parentA = selector.getRandomPType();
    // GpIndividual parentB = selector.getRandomPType();
    // while (parentB == parentA)
    // {
    // parentB = (GpIndividual) selector.getRandomPType();
    // }
    //
    // GpNode[] childrenTree = GpTreeManager.crossover(parentA.getRootNode(), parentB.getRootNode(),
    // (GpEnvironment<GpIndividual>) _environment);
    // List<Integer> fragmentSizeList = GpTreeManager.getSizeOfLastTimeCrossover();
    // sumFragmentSize(fragmentSizeMap, fragmentSizeList);
    //
    // GpIndividual childA = new GpIndividual();
    // GpIndividual childB = new GpIndividual();
    // childA.setRootNode(childrenTree[0]);
    // childB.setRootNode(childrenTree[1]);
    // nextPopulation.add(childA);
    // if (nextPopulation.size() + 1 < phenotypeNum)
    // {
    // nextPopulation.add(childB);
    // }
    // } else if (nextPopulation.size() < (phenotypeNum)
    // * (_environment.getCrossoverRatio() + _environment.getMutationRatio()))
    // {
    // GpIndividual child = new GpIndividual();
    // child.setRootNode(GpTreeManager.mutation(selector.getRandomPType().getRootNode(),
    // (GpEnvironment<GpIndividual>) _environment));
    // nextPopulation.add(child);
    // List<Integer> fragmentSizeList = GpTreeManager.getSizeOfLastTimeMutation();
    // sumFragmentSize(fragmentSizeMap, fragmentSizeList);
    // } else
    // // just copy
    // {
    // GpIndividual child = new GpIndividual(selector.getRandomPType().getRootNode());
    // nextPopulation.add(child);
    // }
    // }
    // _environment.setPopulation(nextPopulation);
    // // show size distribution
    // for (Entry<Integer, Integer> entry : fragmentSizeMap.entrySet())
    // {
    // System.out.println("Size = " + entry.getKey() + " " + entry.getValue());
    // }
    // }

    /** fragment のサイズを加算する */
    public void sumFragmentSize(Map<Integer, Integer> sizeMap, List<Integer> fragmentList)
    {
        for (Integer size : fragmentList)
        {
            if (sizeMap.containsKey(size))
            {
                sizeMap.put(size, sizeMap.get(size) + 1);
            } else
            {
                sizeMap.put(size, 1);
            }
        }
    }

    public void finish()
    {
        int i = 0;
        for (Double averageFitness : _averageFitnessList)
        {
            System.out.println(i + " " + averageFitness + " " + _averageSizeList);
        }
    }

    // public void updateGeneration_PORTE()
    // {
    // // reproduction
    // AbstractSelector<GpIndividual> selector = new TournamentSelector<GpIndividual>(_environment.getPopulation(),
    // _tournamentSize, AbstractSelector.NORMAL);
    // if (_environment.getAttribute("selector").equals("tournament"))
    // {
    // selector = new TournamentSelector<GpIndividual>(_environment.getPopulation(), _tournamentSize,
    // AbstractSelector.NORMAL);
    // } else if (_environment.getAttribute("selector").equals("truncation"))
    // {
    // selector = new TruncateSelector<GpIndividual>(_environment.getPopulation(), AbstractSelector.NORMAL);
    // } else if (_environment.getAttribute("selector").equals("roulette"))
    // {
    // selector = new RouletteSelector<GpIndividual>(_environment.getPopulation(), AbstractSelector.NORMAL);
    // }
    //
    // List<GpNode> parents = new ArrayList<GpNode>();
    // int populationSize = _environment.getPopulationSize();
    // List<GpIndividual> nextPopulation = new ArrayList<GpIndividual>(populationSize);
    // List<GpIndividual> tmp = selector.getRandomPTypeList((int) (populationSize * 1));
    // for (GpIndividual individual : tmp)
    // {
    // parents.add(individual.getRootNode());
    // }
    //
    // // jan. 15, 2009
    // double averageAllFragmentSize = 0;
    // if (_offspringSizeList != null)
    // {
    // for (int i = 0; i < _offspringSizeList.size(); i++)
    // {
    // averageAllFragmentSize += _offspringSizeList.get(i);
    // }
    // averageAllFragmentSize = averageAllFragmentSize / _offspringSizeList.size();
    // }
    // _averageFragmentSize = 0;
    // for (GpIndividual individual : tmp)
    // {
    // parents.add(individual.getRootNode());
    // if (_offspringSizeList != null)
    // {
    // _averageFragmentSize += _offspringSizeList.get(_environment.getPopulation().indexOf(individual));
    // }
    // }
    // _averageFragmentSize = _averageFragmentSize / tmp.size();
    // if (averageAllFragmentSize != _averageFragmentSize)
    // {
    // double leastProbability = 1 / _averageTreeSize;
    // double ratio = ((1 / (_averageFragmentSize)) - leastProbability)
    // / ((1 / (averageAllFragmentSize)) - leastProbability);
    // // double ratio = (1 / (_averageFragmentSize)) / (1 /
    // // (averageAllFragmentSize));
    // System.out.println("Least Probability = " + leastProbability);
    // System.out.println("porteT = " + _environment.getAttribute("porteT"));
    // _environment.putAttribute("porteT", String.valueOf(leastProbability
    // + (Double.valueOf(_environment.getAttribute("porteT")) - leastProbability) * ratio));
    // }
    //
    // PORTS_Cut porte = new PORTS_Cut(parents, (GpEnvironment<GpIndividual>) _environment);
    //
    // // _averageSizeList.add((double)porte.getAverageNodeSize());
    //
    // for (int i = 0; i < _environment.getPopulationSize(); i++)
    // {
    // GpNode childNode = porte.getRandomSample();
    // while (childNode == null)
    // {
    // childNode = porte.getRandomSample();
    // }
    // GpIndividual child = new GpIndividual();
    // child.setRootNode(childNode);
    // nextPopulation.add(child);
    // }
    // _offspringSizeList = porte.getOffspringSizeList();
    // _averageTreeSize = porte.getSumOfTreeSize() / nextPopulation.size();
    //
    // // fragment test
    // Map<Integer, Integer> fragmentSizeMap = porte.getAllConstructionSizeMap();
    // List<String> fragmentList = new ArrayList<String>();
    // StringBuilder str = new StringBuilder();
    // for (Entry<Integer, Integer> entry : fragmentSizeMap.entrySet())
    // {
    // str.append(entry.getKey()).append(" ").append(entry.getValue());
    // fragmentList.add(str.toString());
    // str.delete(0, str.length());
    // }
    // Collections.sort(fragmentList);
    // for (String line : fragmentList)
    // {
    // System.out.println("Size = " + line);
    // }
    // // test end
    //
    // _environment.setPopulation(nextPopulation);
    // }
}
