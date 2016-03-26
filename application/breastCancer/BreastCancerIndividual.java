package application.breastCancer;

import java.util.List;

import geneticProgramming.GpIndividual;

public class BreastCancerIndividual extends GpIndividual
{
    private double[] feature;
    private List<double[]> dataSet;
    
    @Override
    public Object evaluate()
    {
        double[] result = new double[dataSet.size()];
        //for (int i = 0; i < 20; i++)
        for (int i = 0; i < dataSet.size(); i++)
        {
            feature = dataSet.get(i);
            result[i] = (Double) _rootNode.evaluate(this);
        }
        return result;
    }

    public void setDataSet(List<double[]> dataSet)
    {
        this.dataSet = dataSet;
    }
    
    public Object getVariable(int i)
    {
        return feature[i];
    }
}
