package application.breastCancer;

import geneticProgramming.GPEvolutionModel;
import geneticProgramming.GpEnvironment;
import geneticProgramming.GpTreeManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BreastCancerEvolutionModel extends
		GPEvolutionModel<BreastCancerIndividual, GpEnvironment<BreastCancerIndividual>>
{
	private List<double[]> trainingDataSet;
	private List<double[]> testDataSet;

	public BreastCancerEvolutionModel(GpEnvironment<BreastCancerIndividual> environment)
	{
		super(BreastCancerIndividual.class, environment);
		
		List<double[]> dataSet = readDataSet();
		Collections.shuffle(dataSet);
		double trainingRatio = Double.valueOf( environment.getAttribute("trainingRatio") );
		trainingDataSet = dataSet.subList(0, (int)(trainingRatio * dataSet.size()));
		testDataSet = dataSet.subList((int)(trainingRatio * dataSet.size()), dataSet.size());
	}

	private List<double[]> readDataSet()
	{
		// InputStream dataFile =
		// BreastCancerEvolutionModel.class.getResourceAsStream("./breast-cancer-wisconsin.data");
		InputStream stream = BreastCancerEvolutionModel.class.getResourceAsStream("breast-cancer-wisconsin.data");

		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			List<double[]> dataList = new ArrayList<double[]>();
			Pattern digitPattern = Pattern.compile("\\d+");

			while (reader.ready())
			{
				String line = reader.readLine();
				String[] token = line.split(",");
				double[] data = new double[token.length - 1];
				boolean validData = true;
				for (int i = 1; i < token.length; i++) // ignore the first token
														// (ID number).
				{
					Matcher matcher = digitPattern.matcher(token[i]);

					if (matcher.matches())
					{
						data[i - 1] = Double.valueOf(token[i]);
					} else
					{
						validData = false;
						break;
					}
				}
				if (validData)
				{
					dataList.add(data);
				}
			}
			return dataList;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public BreastCancerIndividual createNewIndividual()
	{
		BreastCancerIndividual ind = super.createNewIndividual();
		ind.setDataSet(trainingDataSet);
		return ind;
	}

	@Override
	public void evaluate()
	{
		bestIndividual = _environment.getPopulation().get(0);
		double averageDepth = 0;
		double averageFitness = 0;
		double averageTreeSize = 0;

		for (BreastCancerIndividual individual : _environment.getPopulation())
		{
			//System.out.println( GpTreeManager.getS_Expression(individual.getRootNode()) );
			double[] testData = (double[]) individual.evaluate();
			double errorSum = 0;
			double tp = 0;
			double tn = 0;
			double fp = 0;
			double fn = 0;
			
			averageTreeSize += GpTreeManager.getNodeSize(individual.getRootNode());
			
			for (int i = 0; i < trainingDataSet.size(); i++)
			{
				boolean gpOutput = (0 >= testData[i]);
				boolean answer = 3 >= trainingDataSet.get(i)[9];

				// double error = Math.abs(testData[i] - dataSet.get(i)[9]);
				if( gpOutput && answer )
				{
					tp ++;
				}
				else if( !gpOutput && !answer )
				{
					tn ++;
				}
				else if( gpOutput && !answer )
				{
					fp ++;
				}
				else if( !gpOutput && answer )
				{
					fn ++;
				}
				
				if (gpOutput != answer)
				{
					errorSum++;
					// error ++;
				}
			}
			
			double precision = tp / (tp + fp);
			if( tp + fp == 0 )
			{
				precision = 0;
			}
			double recall = tp / (tp + fn);
			if( tp + fn == 0 )
			{
				recall = 0;
			}
			
			double f_measure = 2 * precision * recall / (precision + recall);
			if( precision + recall == 0 )
			{
				f_measure = 0;
			}
			//individual.setFitnessValue(errorSum / dataSet.size());
			individual.setFitnessValue(f_measure);
			
			if (bestIndividual.getFitnessValue() < individual.getFitnessValue())
			{
				bestIndividual = individual;
			}
			averageDepth += individual.getRootNode().getDepthFromHere();
			averageFitness += individual.getFitnessValue();
		}
		
		// evaluate using the test data
		
		bestIndividual.setDataSet(testDataSet);
		double[] testData = (double[]) bestIndividual.evaluate();
		double errorSum = 0;
		double tp = 0;
		double tn = 0;
		double fp = 0;
		double fn = 0;
		
		averageTreeSize += GpTreeManager.getNodeSize(bestIndividual.getRootNode());
		
		for (int i = 0; i < testDataSet.size(); i++)
		{
			boolean gpOutput = (0 >= testData[i]);
			boolean answer = 3 >= testDataSet.get(i)[9];
			
			// double error = Math.abs(testData[i] - dataSet.get(i)[9]);
			if( gpOutput && answer )
			{
				tp ++;
			}
			else if( !gpOutput && !answer )
			{
				tn ++;
			}
			else if( gpOutput && !answer )
			{
				fp ++;
			}
			else if( !gpOutput && answer )
			{
				fn ++;
			}
				
			if (gpOutput != answer)
			{
				errorSum++;
				// error ++;
			}
		}
		
		double precision = tp / (tp + fp);
		if( tp + fp == 0 )
		{
			precision = 0;
		}
		double recall = tp / (tp + fn);
		if( tp + fn == 0 )
		{
		recall = 0;
		}
		
		double f_measure = 2 * precision * recall / (precision + recall);
		if( precision + recall == 0 )
		{
			f_measure = 0;
		}
		System.out.println("best f_measure = " + f_measure);
		bestIndividual.setDataSet(trainingDataSet);
		// System.out.println("Generation " +
		// _environment.getGenerationCount());
		// System.out.println("Best Individual " +
		// bestIndividual.getFitnessValue() + ": " +
		// GpTreeManager.getS_Expression(bestIndividual.getRootNode()));
		// System.out.println("Average Depth = " + averageDepth /
		// _environment.getPopulationSize());
		// System.out.println("Average Tree Size = " + averageTreeSize /
		// _environment.getPopulationSize());
	}
}
