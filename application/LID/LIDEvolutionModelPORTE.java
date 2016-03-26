package application.LID;

import java.util.ArrayList;
import java.util.List;

import ports.Porte;
import ecCore.EvolutionModel;
import ecCore.selector.AbstractSelector;
import ecCore.selector.TournamentSelector;
import ecCore.selector.TruncateSelector;
import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpNode;
import geneticProgramming.GpTreeManager;

public class LIDEvolutionModelPORTE extends EvolutionModel<GpIndividual, GpEnvironment<GpIndividual>>
{
	GpIndividual bestIndividual;
	int _tournamentSize = 10; // default
	double _targetDepth = 8;
	double _targetTerminal = 200;
	double _weightDepth = 50;
	double _weightTerminal = 50;

	public LIDEvolutionModelPORTE(GpEnvironment<GpIndividual> environment)
	{
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
	public void evaluateIndividual(GpIndividual individual)
	{
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

		individual.setFitnessValue(fitness);
	}

	@Override
	public void initialize()
	{
		List<GpNode> nodeList = GpTreeManager.rampedHalfAndHalf(_environment);
		for (int i = 0; i < _environment.getPopulationSize(); i++)
		{
			_environment.getPopulation().add(new GpIndividual());
			_environment.getPopulation().get(i).setRootNode(nodeList.get(i));
		}
	}

	@Override
	public void updateGeneration()
	{
		// reproduction
		AbstractSelector<GpIndividual> selector = new TournamentSelector<GpIndividual>(_environment.getPopulation(),
				_tournamentSize, AbstractSelector.NORMAL);
		if (_environment.getAttribute("selector").equals("tournament"))
		{
			selector = new TournamentSelector<GpIndividual>(_environment.getPopulation(), _tournamentSize,
					AbstractSelector.NORMAL);
		} else if (_environment.getAttribute("selector").equals("truncation"))
		{
			selector = new TruncateSelector<GpIndividual>(_environment.getPopulation(), AbstractSelector.NORMAL);
		}

		List<GpNode> parents = new ArrayList<GpNode>();
		int populationSize = _environment.getPopulationSize();
		List<GpIndividual> nextPopulation = new ArrayList<GpIndividual>(populationSize);
		List<GpIndividual> tmp = selector.getRandomPTypeList((int) (populationSize * 0.5));
		for (GpIndividual individual : tmp)
		{
			parents.add(individual.getRootNode());
		}

		Porte porte = new Porte(parents, (GpEnvironment<GpIndividual>) _environment);
		for (int i = 0; i < _environment.getPopulationSize(); i++)
		{
			GpNode childNode = porte.getRandomSample();
			GpIndividual child = new GpIndividual();
			child.setRootNode(childNode);
			nextPopulation.add(child);
		}
		_environment.setPopulation(nextPopulation);
	}
}
