package application.OrderTree;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpSymbolSet;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

public class OrderTreeProblem
{
  public static void main(String argv[])
  {
    int level = 5; // default
    Properties expParameter = new Properties();
    expParameter.put("method", "SGP");
    //expParameter.put("method", "PORTS");
    expParameter.put("selector", "tournament");
    expParameter.put("selectionOrder", "reverse");
    expParameter.put("tournamentSize", "6");
    expParameter.put("populationSize", "500");
    expParameter.put("repetitionNumber", "200");
    expParameter.put("portsT", "0.5");
    expParameter.put("portsAlpha", "0.01");
    expParameter.put("portsMutation", "0.05");
    expParameter.put("autoUpdateT", "true");
    expParameter.put("autoUpdateAlpha", "false");
    expParameter.put("level", String.valueOf(level));
    expParameter.put("fitness", "order");
    expParameter.put("sizePrint", "false");
    
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
    for (int i = 0; i < level; i++)
    {
      symbolSet.addSymbol(new OrderTreeNode(String.valueOf(i), 0));
      symbolSet.addSymbol(new OrderTreeNode(String.valueOf(i), 2));
    }

    GpEnvironment<GpIndividual> environment = new GpEnvironment<GpIndividual>();
    environment.setSymbolSet(symbolSet);
    environment.setCrossoverRatio(0.9);
    environment.setEliteSize(0);
    environment.setMutationRatio(0.1);
    environment.setNumberOfMaxInitialDepth(6);
    environment.setNumberOfMaxDepth(20);
    environment.setNumberOfMinimumDepth(1);
    environment.setRepetitionNumber(200);
    environment.setPopulation(new ArrayList<GpIndividual>());
    
    environment.loadProperties(expParameter);
    
    // print experimental parameters
    System.out.println(expParameter);
    System.out.println(environment.getAttributes());

    // run
    OrderTreeEvolutionModel model = new OrderTreeEvolutionModel(environment);
    model.run();
    model.finish();
    // if( expParameter.getProperty("method").equals("SGP") )
    // {
    // OrderTreeEvolutionModel model = new OrderTreeEvolutionModel(environment);
    // model.run();
    // }
    // else if( expParameter.getProperty("method").equals("PORTE") )
    // {
    // OrderTreeEvolutionModelPORTE model = new
    // OrderTreeEvolutionModelPORTE(environment);
    // model.run();
    // }

  }
}
