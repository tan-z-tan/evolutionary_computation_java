package util;

import java.util.List;

import ecCore.Individual;

public class DataRecorder
{
	public static double averageFitness(List<? extends Individual> population)
	{
		double average = 0;
		for( int i = 0; i < population.size(); i++ )
		{
			average += population.get(i).getFitnessValue();
		}
		return average/population.size();
	}
}
