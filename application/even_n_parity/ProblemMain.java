package application.even_n_parity;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpSymbolSet;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import application.even_n_parity.symbols.And;
import application.even_n_parity.symbols.Not;
import application.even_n_parity.symbols.Or;
import application.even_n_parity.symbols.X;

public class ProblemMain
{
  public static void main(String argv[])
  {
    int level = 3; // default
    Properties expParameter = new Properties();
    expParameter.put("method", "SGP");
    //expParameter.put("method", "PORTS");
    //expParameter.put("method", "SIHC");
    //expParameter.put("method", "PPT_PERCE");
    expParameter.put("PPT", "true");
    expParameter.put("PPTArity", "2");
    expParameter.put("PPTDepth", "7");
    expParameter.put("PPTMaxCliqueSize", "7");
    expParameter.put("PPTSignificanceLevel", "0.95");
    //expParameter.put("PPT_dependency", "mutualInformation");
    expParameter.put("PPT_dependency", "chiSquare");
    expParameter.put("PPTSmoothingParameter", "0.01");
    expParameter.put("PPT_MN_order", "roulette");
    expParameter.put("peedSampling", "cliqueBased");
    expParameter.put("peedCatProbability", "0.04");
    expParameter.put("truncationRate", "0.2");
    
    expParameter.put("selector", "tournament");
    expParameter.put("selectionOrder", "normal");
    expParameter.put("crossover", "90/10");
    expParameter.put("tournamentSize", "10");
    expParameter.put("portsT", "0.01");
    expParameter.put("portsAlpha", "0.1");
    //expParameter.put("autoUpdateT", "true");
    //expParameter.put("autoUpdateAlpha", "false");
    expParameter.put("portsStart", "root");
    expParameter.put("portsMutation", "0.05");
    expParameter.put("portsUpdate", "");
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
    symbolSet.addSymbol(new And(), "And", 2);
    symbolSet.addSymbol(new Or(), "Or", 2);
    //symbolSet.addSymbol(new If(), "If", 3);
    symbolSet.addSymbol(new Not(), "Not", 1);
//    symbolSet.addSymbol(new X(0), 0);
//    symbolSet.addSymbol(new X(1), 0);
//    symbolSet.addSymbol(new X(2), 0);
//    symbolSet.addSymbol(new X(3), 0);
//    symbolSet.addSymbol(new X(4), 0);
//    symbolSet.addSymbol(new X(5), 0);
//    symbolSet.addSymbol(new X(6), 0);
//    symbolSet.addSymbol(new X(7), 0);
//    symbolSet.addSymbol(new X(8), 0);
//    symbolSet.addSymbol(new X(9), 0);
//    symbolSet.addSymbol(new X(10), 0);
    for (int i = 0; i < level; i++)
    {
      symbolSet.addSymbol(new X(i), 0);
    }
    
    GpEnvironment<Even_n_parityIndividual> environment = new GpEnvironment<Even_n_parityIndividual>();
    environment.setSymbolSet(symbolSet);
    environment.setCrossoverRatio(0.9);
    environment.setMutationRatio(0.1);
    environment.setEliteSize(1);
    environment.setNumberOfMaxInitialDepth(5);
    environment.setNumberOfMaxDepth(7);
    //environment.setNumberOfMinimumDepth(level + 1);
    environment.setPopulationSize(1000);
    environment.setRepetitionNumber(50);
    //environment.setPopulation(new ArrayList<MultiplexerIndividual>());
    
    environment.loadProperties(expParameter);

    // print experimental parameters
    System.out.println(expParameter);
    System.out.println(environment.getAttributes());
    
    // run
    Even_n_parityModel model = new Even_n_parityModel(environment);
    model.run();
  }
}
