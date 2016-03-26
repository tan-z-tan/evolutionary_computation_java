package application.oneMax;

import java.util.ArrayList;
import java.util.List;

import random.RandomManager;
import ecCore.EvolutionModel;
import ecCore.selector.AbstractSelector;
import ecCore.selector.RouletteSelector;
import geneticAlgorithm.ArrayManager;
import geneticAlgorithm.GaEnvironment;
import geneticAlgorithm.GaIndividual;

public class OneMax_Model extends EvolutionModel<GaIndividual<Integer>, GaEnvironment<GaIndividual<Integer>>>
{
	int populationSize = 200;
	int geneLength = 50;
	double crossoverRatio = 0.8;
	double mutationRatio = 0.05;
	List<GaIndividual<Integer>> generation;
	
	@Override
	public void initialize()
	{
		generation = new ArrayList<GaIndividual<Integer>>(populationSize);
		for( int i = 0; i < populationSize; i++ )
		{
			GaIndividual<Integer> individual = new GaIndividual<Integer>();
			individual.setGene(createRandomGene());
			generation.add(individual);
		}
	}
	
	@Override
	public void evaluateIndividual(GaIndividual<Integer> individual)
	{
		// TODO Auto-generated method stub
		double value = OneMaxFunction.evaluate(individual.getGene());
		individual.setFitnessValue(value);
	}
	
	private List<Integer> createRandomGene()
	{
		//Integer[] gene = new Integer[geneLength];
		List<Integer> gene = new ArrayList<Integer>();
		for( int i = 0; i < geneLength; i++ )
		{
			if( RandomManager.getRandom() > 0.5 )
			{
				gene.add(1);
			}
			else
			{
				gene.add(0);
			}
		}
		return gene;
	}
	
	@Override
	public void updateGeneration()
	{
		// creates new generation by sampling from estimated distribution
		AbstractSelector<GaIndividual<Integer>> selector = new RouletteSelector<GaIndividual<Integer>>(generation);
		int index = 0;
		List<GaIndividual<Integer>> newPopulation = new ArrayList<GaIndividual<Integer>>();
		
		for( ; index < populationSize * crossoverRatio; index+=2 )
		{
			GaIndividual<Integer> parentA = (GaIndividual<Integer>)selector.getRandomPType();
			GaIndividual<Integer> parentB = (GaIndividual<Integer>)selector.getRandomPType();
			GaIndividual<Integer>[] children = ArrayManager.crossoverrrr(parentA, parentB);
			newPopulation.add(children[0]);
			newPopulation.add(children[1]);
		}
		//System.out.println(index + " " + generation.size());
		for( ; newPopulation.size() < populationSize; index++ )
		{
			GaIndividual<Integer> parent = (GaIndividual<Integer>)selector.getRandomPType();
			GaIndividual<Integer> child = ArrayManager.mutation(parent, mutationRatio);
			newPopulation.add(child);
		}
		generation = newPopulation;
	}
	
	@Override
	public List<GaIndividual<Integer>> getPopulation()
	{
		return generation;
	}
}
