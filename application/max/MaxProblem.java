package application.max;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpSymbolSet;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import application.max.symbols.Multiply;
import application.max.symbols.Plus;
import application.max.symbols.Variable;

public class MaxProblem
{
    public static void main(String argv[])
    {
        int level = 7; // default
        Properties expParameter = new Properties();
        //expParameter.put("method", "SGP");
        //expParameter.put("method", "PORTS");
        expParameter.put("method", "PPT_MN");
        expParameter.put("PPT", "true");
        expParameter.put("PPTArity", "2");
        expParameter.put("PPTDepth", String.valueOf(level));
        expParameter.put("PPTMaxCliqueSize", "5");
        expParameter.put("PPTSignificanceLevel", "0.9");
        expParameter.put("PPTSmoothingParameter", "0.15");
        expParameter.put("truncationRate", "1.0");
        
        //expParameter.put("method", "SIHC");
        expParameter.put("selector", "tournament");
        expParameter.put("selectionOrder", "normal");
        expParameter.put("tournamentSize", "10");
        expParameter.put("crossover", "90/10");
        expParameter.put("portsT", "0.1");
        expParameter.put("portsAlpha", "0.5");
        expParameter.put("portsMutation", "0.05");
        expParameter.put("portsStart", "root");
        expParameter.put("portsDepthDependency", "0.8");
        expParameter.put("portsUpdate", "adaptive");
        expParameter.put("portsTUpdate", "true");
        //expParameter.put("portsUpdateParameter", "0.99");
        expParameter.put("autoUpdateT", "true");
        expParameter.put("autoUpdateAlpha", "false");
        expParameter.put("sizePrint", "false");
        expParameter.put("isUniqueStructureRecorded", "false");
        expParameter.put("level", String.valueOf(level));
        
        if (argv.length > 0)
        {
          try
          {
            expParameter.load(new FileInputStream(new File(argv[0])));
            if (expParameter.containsKey("level"))
            {
              level = Integer.valueOf(expParameter.getProperty("level"));
            }
          } catch (Exception e)
          {
            e.printStackTrace();
          }
        }
        
        GpSymbolSet symbolSet = new GpSymbolSet();
        symbolSet.addSymbol(new Variable());
        symbolSet.addSymbol(new Multiply());
        symbolSet.addSymbol(new Plus());

        GpEnvironment<GpIndividual> environment = new GpEnvironment<GpIndividual>();
        environment.setSymbolSet(symbolSet);
        environment.setCrossoverRatio(0.9);
        environment.setEliteSize(1);
        environment.setMutationRatio(0.1);
        environment.setNumberOfMaxInitialDepth(level);
        environment.setNumberOfMaxDepth(level);
        environment.setNumberOfMinimumDepth(2);
        environment.setPopulationSize(400);
        environment.setRepetitionNumber(100);
        
        environment.loadProperties(expParameter);
        
        MaxEvolutionModel model = new MaxEvolutionModel(environment);
        model.run();
    }
}
