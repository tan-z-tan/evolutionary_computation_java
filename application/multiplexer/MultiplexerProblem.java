package application.multiplexer;

import geneticProgramming.GpEnvironment;
import geneticProgramming.GpSymbolSet;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import application.multiplexer.symbols.And;
import application.multiplexer.symbols.If;
import application.multiplexer.symbols.Not;
import application.multiplexer.symbols.Or;
import application.multiplexer.symbols.X;

public class MultiplexerProblem
{
  public static void main(String argv[])
  {
    int level = 6; // default
    Properties expParameter = new Properties();
    //expParameter.put("method", "SGP");
    //expParameter.put("method", "PORTS");
    //expParameter.put("method", "SIHC");
    expParameter.put("method", "PPT_PERCE");
    expParameter.put("PPT", "true");
    expParameter.put("PPTArity", "3");
    expParameter.put("PPTDepth", "5");
    expParameter.put("PPTMaxCliqueSize", "5");
    expParameter.put("PPTSignificanceLevel", "0.9");
    //expParameter.put("PPT_dependency", "mutualInformation");
    expParameter.put("PPT_dependency", "chiSquare");
    expParameter.put("PPTSmoothingParameter", "0.04");
    expParameter.put("PPT_MN_order", "roulette");
    expParameter.put("peedSampling", "relatedCliqueBased");
    expParameter.put("peedCatProbability", "0.05");
    expParameter.put("truncationRate", "0.5");
    
    expParameter.put("selector", "tournament");
    expParameter.put("selectionOrder", "normal");
    expParameter.put("crossover", "90/10");
    expParameter.put("tournamentSize", "6");
    expParameter.put("portsT", "0.35");
    expParameter.put("portsAlpha", "0.1");
    expParameter.put("autoUpdateT", "true");
    expParameter.put("autoUpdateAlpha", "false");
    expParameter.put("portsStart", "root");
    expParameter.put("portsMutation", "0.05");
    expParameter.put("portsUpdate", "");
    expParameter.put("peedCatProbability", "0.05");
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
    symbolSet.addSymbol(new If(), "If", 3);
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
    
    GpEnvironment<MultiplexerIndividual> environment = new GpEnvironment<MultiplexerIndividual>();
    environment.setSymbolSet(symbolSet);
    environment.setCrossoverRatio(0.9);
    environment.setMutationRatio(0.1);
    environment.setEliteSize(0);
    environment.setNumberOfMaxInitialDepth(5);
    environment.setNumberOfMaxDepth(5);
    //environment.setNumberOfMinimumDepth(level + 1);
    environment.setPopulationSize(500);
    environment.setRepetitionNumber(50);
    //environment.setPopulation(new ArrayList<MultiplexerIndividual>());
    
    environment.loadProperties(expParameter);

    // print experimental parameters
    System.out.println(expParameter);
    System.out.println(environment.getAttributes());
    
    // run
    if( environment.getAttribute("method").equals("SIHC") )
    {
      MultiplexerEvolutionModel model = new MultiplexerSIHCModel(environment);
      model.run();
    }
    else
    {
      MultiplexerEvolutionModel model = new MultiplexerEvolutionModel(environment);
      model.run();
    }
  }
}
