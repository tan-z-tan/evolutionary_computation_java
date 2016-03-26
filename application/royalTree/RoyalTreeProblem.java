package application.royalTree;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpIndividual;
import geneticProgramming.GpSymbolSet;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

public class RoyalTreeProblem
{
  public static void main(String argv[])
  {
    int level = 4; // default
    Properties expParameter = new Properties();
    //expParameter.put("method", "SGP");
    //expParameter.put("method", "PORTS");
    expParameter.put("method", "PPT_MN");
    expParameter.put("PPT", "true");
    expParameter.put("PPTArity", String.valueOf(level));
    expParameter.put("PPTDepth", String.valueOf(level + 1));
    expParameter.put("PPTMaxCliqueSize", "3");
    expParameter.put("PPTSignificanceLevel", "0.75");
    expParameter.put("PPTSmoothingParameter", "0.1");
    
    // expParameter.put("method", "SIHC");
    //expParameter.put("selector", "tournament");
    expParameter.put("selector", "truncation");
    expParameter.put("truncationRate", "0.2");
    expParameter.put("selectionOrder", "normal");
    expParameter.put("tournamentSize", "10");
    expParameter.put("eliteSize", "1");
    expParameter.put("crossover", "90/10");
    expParameter.put("portsT", "0.5");
    expParameter.put("portsAlpha", "0.01");
    expParameter.put("portsMutation", "0.05");
    expParameter.put("portsStart", "root");
    //expParameter.put("portsDepthDependency", "0.0");
    //expParameter.put("portsUpdate", "none");
    //expParameter.put("portsUpdateParameter", "0.99");
    expParameter.put("autoUpdateT", "true");
    //expParameter.put("autoUpdateAlpha", "true");
    //expParameter.put("sizePrint", "false");
    //expParameter.put("isUniqueStructureRecorded", "false");
    expParameter.put("level", String.valueOf(level));
    //expParameter.put("maxSizeForAbandoning", "1000");
    //expParameter.put("crossover", "depth-dependent");
    
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

    char[] levelSymbol = { 'A', 'B', 'C', 'D', 'E', 'F', 'G' };
    GpSymbolSet symbolSet = new GpSymbolSet();
    symbolSet.addSymbol(new RoyalTreeNode("x", 0));
    symbolSet.addSymbol( new RoyalTreeNode("y", 0) );
    for (int i = 1; i <= level; i++)
    {
      symbolSet.addSymbol(new RoyalTreeNode(String.valueOf(levelSymbol[i - 1]), i));
    }
    
    GpEnvironment<GpIndividual> environment = new GpEnvironment<GpIndividual>();
    environment.setSymbolSet(symbolSet);
    environment.setCrossoverRatio(0.9);
    //environment.setEliteSize(1);
    environment.setMutationRatio(0.1);
    environment.setNumberOfMaxInitialDepth(level + 1);
    environment.setNumberOfMaxDepth(level + 1);
    environment.setNumberOfMinimumDepth(level + 1);
    environment.setPopulationSize(2000);
    environment.setRepetitionNumber(50);
    environment.setPopulation(new ArrayList<GpIndividual>());

    environment.loadProperties(expParameter);
    
    // run
    if( environment.getAttribute("method").equals("SIHC") )
    {
      RoyalTreeSIHCModel model = new RoyalTreeSIHCModel(environment);
      model.run();
    }
    else
    {
      RoyalTreeEvolutionModel model = new RoyalTreeEvolutionModel(environment);
      model.run();
    }
  }
}
