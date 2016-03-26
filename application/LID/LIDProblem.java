package application.LID;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpSymbolSet;
import geneticProgramming.GpTreeManager;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

public class LIDProblem
{
  public static void main(String argv[])
  {
    int level = 7; // default
    Properties expParameter = new Properties();
    expParameter.put("method", "SGP");
    //expParameter.put("method", "PORTS");
    expParameter.put("selector", "tournament");
    expParameter.put("selectionOrder", "normal");
    expParameter.put("tournamentSize", "4");
    expParameter.put("populationSize", "100");
    expParameter.put("repetitionSize", "200");
    expParameter.put("portsT", "0.05");
    expParameter.put("portsAlpha", "1.0");
    //expParameter.put("portsUpdate", "pl");
    expParameter.put("portsDepthDependency", "0.4");
    expParameter.put("autoUpdateT", "true");
    expParameter.put("level", String.valueOf(level));
    expParameter.put("targetDepth", "8");
    expParameter.put("targetTerminal", "100");
    expParameter.put("weightDepth", "50");
    expParameter.put("weightTerminal", "50");
    
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
    symbolSet.addSymbol(new LIDNode("leaf", 0));
    symbolSet.addSymbol(new LIDNode("JOIN", 2));

    GpEnvironment<GpIndividual> environment = new GpEnvironment<GpIndividual>();
    environment.setSymbolSet(symbolSet);
    environment.setCrossoverRatio(0.9);
    environment.setEliteSize(0);
    environment.setMutationRatio(0.1);
    environment.setNumberOfMaxInitialDepth(6);
    environment.setNumberOfMaxDepth(512);
    environment.setNumberOfMinimumDepth(1);
    environment.setRepetitionNumber(200);
    environment.setPopulation(new ArrayList<GpIndividual>());

    environment.loadProperties(expParameter);

    // print experimental parameters
    System.out.println(expParameter);
    System.out.println(environment.getAttributes());
    
    // run
    LIDEvolutionModel model = new LIDEvolutionModel(environment);
    //System.exit(0);
    model.run();
    }
}
