package ecCore.selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ecCore.Individual;
import geneticAlgorithm.GaIndividual;

public class TruncateSelector<T extends Individual> extends AbstractSelector<T>
{
    private int index = 0;

    /** construct Truncate Selector with normal order */
    public TruncateSelector(List<T> generation)
    {
        _population = generation;
        Collections.sort(_population);
    }

    /** construct Truncate Selector with specified order */
    public TruncateSelector(List<T> generation, boolean reverse)
    {
        _reverse = reverse;
        _population = generation;
        Collections.sort(_population);
    }

    @Override
    public List<T> getRandomPTypeList(int selectionSize)
    {
        if (_reverse == NORMAL)
        {
            return _population.subList(_population.size() - selectionSize, _population.size());
        } else
        {
            return _population.subList(0, selectionSize);
        }
    }

    @Override
    public T getRandomPType()
    {
        if (!_reverse)
        {
            return _population.get(_population.size() - 1 - index++);
        } else
        {
            return _population.get(0 + index++);
        }
    }

    public static void main(String args[])
    {
        List<Individual> population = new ArrayList<Individual>();
        Individual individual1 = new GaIndividual<Integer>();
        Individual individual2 = new GaIndividual<Integer>();
        Individual individual3 = new GaIndividual<Integer>();
        Individual individual4 = new GaIndividual<Integer>();
        Individual individual5 = new GaIndividual<Integer>();
        individual1.setFitnessValue(1);
        individual2.setFitnessValue(2);
        individual3.setFitnessValue(3);
        individual4.setFitnessValue(4);
        individual5.setFitnessValue(5);
        population.add(individual1);
        population.add(individual2);
        population.add(individual3);
        population.add(individual4);
        population.add(individual5);
        AbstractSelector<Individual> selector = new TruncateSelector<Individual>(population, AbstractSelector.NORMAL);

        System.out.println(selector.getRandomPType());
        System.out.println(selector.getRandomPType());
        System.out.println(selector.getRandomPType());
        System.out.println(selector.getRandomPType());
        System.out.println(selector.getRandomPTypeList(3));
    }
}
