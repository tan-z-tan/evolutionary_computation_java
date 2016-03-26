package application.breastCancer;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpSymbolSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class BreastCancerProblem
{
    private BreastCancerEvolutionModel model;
    public static long time;
    
    public BreastCancerProblem(String[] argv)
    {
        Properties expParameter = new Properties();
        //expParameter.put("method", "SGP");
        expParameter.put("method", "PPT_PERCE");
        expParameter.put("selector", "tournament");
        //expParameter.put("selector", "truncation");
        expParameter.put("selectionOrder", "normal");
        expParameter.put("tournamentSize", "2");
        expParameter.put("sizePrint", "false");
        expParameter.put("isUniqueStructureRecorded", "false");
        expParameter.put("maxDepth", "4");
        expParameter.put("trainingRatio", "0.7");
        
        expParameter.put("PPT", "true");
        expParameter.put("PPTArity", "3");
        expParameter.put("PPTDepth", "4");
        expParameter.put("PPTMaxCliqueSize", "13");
        expParameter.put("PPTSignificanceLevel", "0.05");
        expParameter.put("PPTSmoothingParameter", "0.05");
        expParameter.put("PPT_MN_order", "roulette");
        expParameter.put("PPT_dependency", "mutualInformation");
        expParameter.put("initialization", "full");
        expParameter.put("peedSampling", "relatedCliqueBased");
        expParameter.put("truncationRate", "1.0");
        expParameter.put("portsT", "0.05");
        expParameter.put("peedCatProbability", "0.05");
	        
        
        if (argv.length > 0)
        {
            try
            {
                expParameter.load(new FileInputStream(new File(argv[0])));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        InputStream stream = BreastCancerProblem.class.getResourceAsStream("GP_Node_Type");

        // creates environment
        GpSymbolSet symbolSet = GpSymbolSet.getSymbolSet(stream);
        GpEnvironment<BreastCancerIndividual> environment = new GpEnvironment<BreastCancerIndividual>();
        environment.setRepetitionNumber(100);
        environment.setPopulationSize(1000);
        environment.setCrossoverRatio(0.9);
        environment.setMutationRatio(0.1);
        environment.setEliteSize(1);
        environment.setNumberOfMaxInitialDepth(4);
        environment.setSymbolSet(symbolSet);

        // this method may override parameters above
        environment.loadProperties(expParameter);

        // prints experimental parameters
        // System.out.println(expParameter);
        System.out.println(environment.getAttributes());

        // constructs evolution model and run
        BreastCancerEvolutionModel model = new BreastCancerEvolutionModel(environment);
        model.run();
    }

    public static void main(String args[])
    {
        BreastCancerProblem problem = new BreastCancerProblem(args);
    }
}
